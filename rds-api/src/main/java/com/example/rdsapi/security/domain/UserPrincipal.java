package com.example.rdsapi.security.domain;

import com.example.rdsapi.domain.UserAccountDto;
import com.example.rdscommon.domain.RoleType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record UserPrincipal(
        String userId,
        String password,
        String nickname,
        String memo,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    public static UserPrincipal of(String username, String password, String nickname, String memo, Set<RoleType> roleTypes) {
        return new UserPrincipal(
                username,
                password,
                nickname,
                memo,
                roleTypes
                        .stream()
                        .map(RoleType::getRole)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    public static UserPrincipal of(String username, String password, String nickname, String memo, Collection<? extends GrantedAuthority> authorities) {
        return new UserPrincipal(
                username,
                password,
                nickname,
                memo,
                authorities
        );
    }

    public static UserPrincipal of(String userId, Set<RoleType> roleTypes){
        return UserPrincipal.of(
                userId,
                null,
                null,
                null,
                roleTypes
        );
    }


    public static UserPrincipal of(String userId, Collection<? extends GrantedAuthority> authorities){
        return new UserPrincipal(
                userId,
                null,
                null,
                null,
                authorities
        );
    }

    public static UserPrincipal from(UserAccountDto dto){
        return UserPrincipal.of(
                dto.userId(),
                dto.userPassword(),
                dto.nickname(),
                dto.memo(),
                dto.roleTypes()
        );
    }

    public UserAccountDto toDto(){
        return UserAccountDto.of(
                userId,
                password,
                nickname,
                memo,
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(RoleType::new)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
