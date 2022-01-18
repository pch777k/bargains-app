package com.pch777.bargains.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pch777.bargains.model.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserPrincipal implements UserDetails {
	 
   	private static final long serialVersionUID = 1L;
	
	private User user;
     
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
        		.stream()
        		.map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
        		.collect(Collectors.toSet());
    }
 
    @Override
    public String getPassword() {
        return user.getPassword();
    }
 
    @Override
    public String getUsername() {
        return user.getEmail();
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
