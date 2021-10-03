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

import java.io.IOException;
import java.util.Base64;

public class BasicAuthenticationFilter extends HttpFilter {
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

        final String header = req.getHeader("Authorization");
        if (header == null) {
            super.doFilter(req,res,chain);
            return;
        }

        var principal = header.substring("Basic ".length());
        final byte[] decode = Base64.getDecoder().decode(principal);
        var decodedPrincipal = new String(decode);
        var index = decodedPrincipal.indexOf(":");
        String username = null;
        String password = null;
        if (index != -1) {
            username = decodedPrincipal.substring(0, index).trim();
            password = decodedPrincipal.substring(index + 1).trim();
        }
        try {
            final var authentication = provider.authenticate(new BasicAuthentication(username, password));
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
