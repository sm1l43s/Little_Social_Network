package com.brausov.social_network.config;


import com.brausov.social_network.models.User;
import com.brausov.social_network.repositories.UserRepository;
import com.brausov.social_network.services.UserService;
import com.brausov.social_network.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Component
public class AuthProviderImpl implements AuthenticationProvider {

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        User user = userService.getByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        String password = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        Collection<GrantedAuthority> authorities =
                AuthorityUtils.createAuthorityList("ROLE_" + user.getRole());
        Date currentDate = Date.valueOf(LocalDate.now());
        user.setLastEntered(currentDate);
        userService.edit(user);
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}

