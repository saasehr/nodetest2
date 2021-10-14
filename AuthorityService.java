package com.hospini.api.auth.service;

import java.util.Set;
import com.hospini.api.auth.model.Authority;

public interface AuthorityService {

    public Set<Authority> getRoles(String userName);

    public int save(Authority auth);
    
}
