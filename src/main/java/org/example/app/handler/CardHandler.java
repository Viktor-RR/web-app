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
//ВЫТАСКИВАЕТ ДАННЫЕ ИЗ ЗАПРОСА И ФОРМИРУЕТ ОТВЕТ В НУЖНОМ НАМ ФОРМАТЕ
// ПО СУТИ ЯВЛЯЕТСЯ АНАЛОГОМ REST CONTROLLER

public class CardHandler {
    private final CardService service;
    private final Gson gson;

    public void getAll(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var authorities = AuthHelper.getAuth(req).getAuthorities();
            if (authorities.contains(Roles.ROLE_USER) || authorities.contains(Roles.ROLE_ADMIN)) {
                final var user = UserHelper.getUser(req);
                final var data = service.getAllByOwnerId(user.getId());
                resp.setHeader("Content-Type", "application/json");
                resp.getWriter().write(gson.toJson(data));
            } else {
                resp.sendError(403);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var authorities = AuthHelper.getAuth(req).getAuthorities();
            if (authorities.contains(Roles.ROLE_USER) || authorities.contains(Roles.ROLE_ADMIN)) {
                final var cardId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group("cardId"));
                final var user = UserHelper.getUser(req);
                final var data = service.getCardById(user.getId(), cardId);
                resp.setHeader("Content-Type", "application/json");
                resp.getWriter().write(gson.toJson(data));
            } else {
                resp.sendError(403);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void order(HttpServletRequest req, HttpServletResponse resp) {
        final var authorities = AuthHelper.getAuth(req).getAuthorities();
        if (authorities.contains(Roles.ROLE_USER) || authorities.contains(Roles.ROLE_ADMIN)) {
            final var user = UserHelper.getUser(req);
            final var hiddenNumber = getHiddenNumber();
            service.createCard(user.getId(), hiddenNumber);
        } else {
            try {
                resp.sendError(403);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void blockById(HttpServletRequest req, HttpServletResponse resp) {
        final var authorities = AuthHelper.getAuth(req).getAuthorities();
        if (authorities.contains(Roles.ROLE_ADMIN)) {
            final var cardId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group("cardId"));
            final var user = UserHelper.getUser(req);
            service.deleteCardById(user.getId(), cardId);
        } else {
            try {
                resp.sendError(403);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void transferById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var authorities = AuthHelper.getAuth(req).getAuthorities();
            if (authorities.contains(Roles.ROLE_USER)) {
                final var user = UserHelper.getUser(req);
                final var transferRequestDto = gson.fromJson(req.getReader(), TransferRequestDto.class);
                if (transferRequestDto.getMoneyValue() < 0) {
                    throw new IncorrectMoneyValueException();
                }
                service.moneyTransfer(transferRequestDto.getMoneyValue(), user.getId(),
                        transferRequestDto.getCardId(), transferRequestDto.getCompanionId(),
                        transferRequestDto.getCompanionCardId());
            } else {
                resp.sendError(403);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getHiddenNumber() {
        final var v = (int) (1000000 + Math.random() * 89999999);
        final var cardNumber = String.valueOf(v).substring(0, 4) + " " + String.valueOf(v).substring(4);
        return cardNumber.replaceAll(cardNumber.substring(0, 6), "**** *");
    }
}
