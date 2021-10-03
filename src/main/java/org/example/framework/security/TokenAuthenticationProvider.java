package org.example.framework.security;


import lombok.RequiredArgsConstructor;
import org.example.app.service.UserService;

@RequiredArgsConstructor
public class TokenAuthenticationProvider implements AuthenticationProvider{
    private final UserService userService;

    @Override
    public boolean supports(Authentication authentication) {
        return authentication instanceof TokenAuthentication;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return userService.authenticate((TokenAuthentication) authentication);
    }
}
