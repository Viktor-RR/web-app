package org.example.framework.security;

public interface AuthenticationProvider {
  boolean supports(Authentication authentication);
  Authentication authenticate(Authentication authentication) throws AuthenticationException;
}
