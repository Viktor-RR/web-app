package org.example.framework.security;

public interface AuthenticationProvider {
  Authentication authenticate(Authentication authentication) throws AuthenticationException;
  Authentication authenticateBasic(Authentication authentication) throws AuthenticationException;
}
