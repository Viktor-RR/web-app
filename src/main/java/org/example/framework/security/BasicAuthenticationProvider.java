package org.example.framework.security;

import lombok.RequiredArgsConstructor;
import org.example.app.service.UserService;

@RequiredArgsConstructor
public class BasicAuthenticationProvider implements AuthenticationProvider{
    private final UserService userService;

    @Override
    public boolean supports(Authentication authentication) {
        return authentication instanceof BasicAuthentication;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return userService.authenticate((BasicAuthentication) authentication);
    }
}
