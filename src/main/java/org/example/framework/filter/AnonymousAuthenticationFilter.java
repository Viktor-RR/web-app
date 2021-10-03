package org.example.framework.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.AnonymousProvider;

import java.io.IOException;

public class AnonymousAuthenticationFilter extends HttpFilter {
    private AnonymousProvider authentication;
    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        authentication = ((AnonymousProvider) (getServletContext().getAttribute(ContextAttributes.ANON_PROVIDER_ATTR)));
    }
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        final var basicHeader = req.getHeader("Authorization");
        final var token = req.getHeader("X-Token");
        final var cookie = req.getCookies();
        if (basicHeader == null && cookie == null && token == null) {
            final var auth = authentication.provide();
            req.setAttribute(RequestAttributes.AUTH_ATTR, auth);
        }
        super.doFilter(req, res, chain);
    }
}
