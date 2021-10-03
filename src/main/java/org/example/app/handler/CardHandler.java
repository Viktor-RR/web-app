package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.dto.TransferRequestDto;
import org.example.app.service.CardService;
import org.example.app.util.AuthHelper;
import org.example.app.util.UserHelper;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.Authentication;
import org.example.framework.security.Roles;
import org.example.jdbc.DataAccessException;
import org.example.jdbc.IncorrectMoneyValueException;

import java.io.IOException;
import java.util.regex.Matcher;

@Log
@RequiredArgsConstructor
public class CardHandler {
    private final CardService service;
    private final Gson gson;

    public void getAll(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUser(req);
            final var authorities = AuthHelper.getAuth(req).getAuthorities();
            final var data = service.getAllByOwnerId(user.getId(), authorities);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(data));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUser(req);
            final var authorities = AuthHelper.getAuth(req).getAuthorities();
            final var cardId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group("cardId"));
            final var data = service.getCardById(user.getId(), cardId, authorities);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(data));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void order(HttpServletRequest req, HttpServletResponse resp) {
        final var authorities = AuthHelper.getAuth(req).getAuthorities();
        final var user = UserHelper.getUser(req);
        service.createCard(user.getId(), authorities);
    }

    public void blockById(HttpServletRequest req, HttpServletResponse resp) {
        final var cardId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group("cardId"));
        final var user = UserHelper.getUser(req);
        final var authorities = AuthHelper.getAuth(req).getAuthorities();
        service.deleteCardById(user.getId(), cardId, authorities);
    }

    public void transferByCardNumber(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUser(req);
            final var authorities = AuthHelper.getAuth(req).getAuthorities();
            final var transferRequestDto = gson.fromJson(req.getReader(), TransferRequestDto.class);
            service.moneyTransfer(transferRequestDto, user, authorities);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}