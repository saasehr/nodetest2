package com.hospini.api.auth.service;

import java.util.Set;

import com.hospini.api.auth.dao.AuthorityDao;
import com.hospini.api.auth.model.Authority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    AuthorityDao authDaoHandle;

    @Override
    public Set<Authority> getRoles(String userName) {
        return authDaoHandle.findAuthorityByUserName(userName);
    }

    public int save(Authority auth) {
        return authDaoHandle.insert(auth);
    }
}
