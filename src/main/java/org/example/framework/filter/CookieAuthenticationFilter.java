package org.example.framework.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.*;
import org.example.jdbc.DataAccessException;

import java.io.IOException;
import java.util.Arrays;

public class CookieAuthenticationFilter extends HttpFilter {
    private AuthenticationProvider provider;


    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        provider = ((AuthenticationProvider) (getServletContext().getAttribute(ContextAttributes.AUTH_PROVIDER_ATTR)));
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!authenticationIsRequired(req)) {
            super.doFilter(req, res, chain);
            return;
        }
        final var cookie = req.getCookies();
        if (cookie == null) {
            super.doFilter(req, res, chain);
            return;
        }

        final var finalCookie = Arrays.stream(cookie).findFirst().orElseThrow(DataAccessException::new).getValue();
        try {
            final var authentication = provider.authenticate(new TokenAuthentication(finalCookie, null));
            req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
        } catch (AuthenticationException e) {
            res.sendError(401);
        }
        super.doFilter(req, res, chain);
    }

    private boolean authenticationIsRequired(HttpServletRequest req) {
        final var existingAuth = (Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR);

        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return true;
        }

        return AnonymousAuthentication.class.isAssignableFrom(existingAuth.getClass());
    }
}