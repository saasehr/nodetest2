package com.hospini.api.auth.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hospini.api.auth.model.User;
import com.hospini.api.auth.model.Authority;
import com.hospini.api.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${app.dbschema.name}")
    private String schemaName;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = findUserByUserName(username);

        System.out.println("inside user details service");

        if (user == null) {
            throw new CustomException("User not found");

        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (Authority auth : findAuthorityByUserName(username)) {

            grantedAuthorities.add(new SimpleGrantedAuthority(auth.getAuthority()));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                grantedAuthorities);

    }

    public User findUserByUserName(String userName) {

        String sql = "select username,password,enabled from " + schemaName + "." + "users"
                + " where enabled = true and username = " + "'" + userName + "'";

        try {

           // System.out.println("Query User =" + userName);
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new User(rs.getString("username"),
                    rs.getString("password"), rs.getBoolean("enabled")));

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public Set<Authority> findAuthorityByUserName(String username) {
        String sql = "select  a.username, a.authority" + " from " + schemaName + "." + "authorities a, " + schemaName
                + "." + "users u" + " where u.username = " + "'" + username + "'" + " and u.username = a.username";

        try {
            Set<Authority> authSet = new HashSet<Authority>();
            List<Authority> authList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Authority auth = new Authority();

                auth.setUsername(rs.getString("username"));
                auth.setAuthority(rs.getString("authority"));

                return auth;
            });

            for (Authority auth : authList) {
              //  System.out.println("Authority="+auth.getAuthority());
                authSet.add(auth);
            }

            return authSet;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

}
