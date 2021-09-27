package org.example.app.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.Authentication;

public class AuthHelper {
    private AuthHelper() {
    }

    public static Authentication getAuth(HttpServletRequest req) {
      return  ((Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR));
    }

}
