package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.domain.User;
import org.example.app.dto.ConfirmationDto;
import org.example.app.dto.LoginRequestDto;
import org.example.app.dto.PasswordRestoreDto;
import org.example.app.dto.RegistrationRequestDto;
import org.example.app.service.CardService;
import org.example.app.service.UserService;
import org.example.app.util.AuthHelper;
import org.example.app.util.UserHelper;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.Authentication;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.regex.Matcher;

@Log
@RequiredArgsConstructor
public class UserHandler {
    private final UserService service;
    private final Gson gson;

    public void register(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "register");
            final var requestDto = gson.fromJson(req.getReader(), RegistrationRequestDto.class);
            final var responseDto = service.register(requestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void login(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "register");
            final var requestDto = gson.fromJson(req.getReader(), LoginRequestDto.class);
            final var responseDto = service.login(requestDto);
            if (requestDto.isCookieEnable()) {
                final var cookie = new Cookie("JSESSIONID", responseDto.getToken());
                cookie.setHttpOnly(true);
                resp.addCookie(cookie);
            }
            else {
                resp.setHeader("Content-Type", "application/json");
                resp.getWriter().write(gson.toJson(responseDto));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetPassword(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var passwordRestoreDto = gson.fromJson(req.getReader(), PasswordRestoreDto.class);
            final var code = service.restorePassword(passwordRestoreDto.getUsername());
            log.log(Level.INFO, String.valueOf(code));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void confirmPassword(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var confirmationDto = gson.fromJson(req.getReader(), ConfirmationDto.class);
            service.setNewPassword(confirmationDto.getUsername(), confirmationDto.getPassword(), confirmationDto.getCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
