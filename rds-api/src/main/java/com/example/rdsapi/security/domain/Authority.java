package com.example.rdsapi.security.domain;

import com.example.rdscommon.domain.RoleType;
import org.springframework.security.core.GrantedAuthority;

import static java.util.Objects.hash;

/**
 * Spring Security UserPrincipal 이 가지는 권한을 표현하고자 구현
 */
public class Authority extends RoleType implements GrantedAuthority {
    public static final String ROLE_USER = "USER";

    public static final RoleType USER_AUTHORITY = new Authority(ROLE_USER);


    public Authority(String role){
        super.role = role;
    }
    @Override
    public int hashCode() {
        return hash(getAuthority());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Authority objAuthority){
            return this.getAuthority().equals(objAuthority.getAuthority());
        }
        return false;
    }

    @Override
    public String getAuthority() {
        return super.getRole();
    }
}
