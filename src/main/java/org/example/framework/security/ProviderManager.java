package org.example.framework.security;

import lombok.Getter;

import java.util.List;

@Getter
public class ProviderManager implements AuthenticationProvider{
    private final List<AuthenticationProvider> providers;

    public ProviderManager(List<AuthenticationProvider> authenticationProviders) {
        this.providers = authenticationProviders;
    }


    @Override
    public boolean supports(Authentication authentication) {
        return providers.stream().anyMatch(o -> o.supports(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return providers.stream()
                .filter(o -> o.supports(authentication))
                .findAny()
                .map(o -> o.authenticate(authentication))
                .orElse(null)
                ;
    }

    public List<AuthenticationProvider> getProviders() {
        return providers;
    }
}

