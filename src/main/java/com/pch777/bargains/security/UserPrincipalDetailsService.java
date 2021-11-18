package com.pch777.bargains.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pch777.bargains.model.User;
import com.pch777.bargains.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserPrincipalDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.getUserByEmail(email);
        
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user with an email: " + email);
        }

        return new UserPrincipal(user);
    }
}