package com.example.rdsapi.security.filter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken{
    private static final long serialVersionUID = -8441647194432178255L;

    private final Object principal;
    private final String token;

    public JwtAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities){
        super(authorities);
        this.principal = principal;
        this.token = null;
    }
    public JwtAuthenticationToken(String token){
        super(null);
        this.principal = null;
        this.token = token;
        super.setDetails(null);
        setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
        return this.token == null ? this.principal : token;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public boolean isAuthenticated() {
        return super.isAuthenticated();
    }

    @Override
    public Object getCredentials() {
        return null;
    }
    @Override
    public Object getDetails() {
        return super.getDetails();
    }
}
