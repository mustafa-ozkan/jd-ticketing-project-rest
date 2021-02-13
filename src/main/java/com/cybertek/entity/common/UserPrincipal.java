package com.cybertek.entity.common;

import com.cybertek.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        //i need to convert roles to authorities with below for OneToMany
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(this.user.getRole().getDescription());
        grantedAuthorityList.add(grantedAuthority);

        /*
        If it is ManyToMany
        this.user.getRoles().forEach(role->{
        GrantedAuthority authority = new SimpleGrantedAuthority(this.user.getRole().getDescription());
        authorityList.add(authority)}
         */

        return grantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return this.user.getPassWord();
    }

    @Override
    public String getUsername() {
        return this.user.getUserName();
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
        return this.user.isEnabled();
    }

    public Long getId() {
        return this.user.getId();
    }
}
