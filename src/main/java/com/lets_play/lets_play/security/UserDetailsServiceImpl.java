package com.lets_play.lets_play.security;

import com.lets_play.lets_play.model.User;
import com.lets_play.lets_play.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String authority = "ROLE_" + user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                user.getId(),                               
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(authority))
        );
    }
}