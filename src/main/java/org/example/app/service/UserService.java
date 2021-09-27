package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.User;
import org.example.app.dto.LoginRequestDto;
import org.example.app.dto.LoginResponseDto;
import org.example.app.dto.RegistrationRequestDto;
import org.example.app.dto.RegistrationResponseDto;
import org.example.app.exception.PasswordNotMatchesException;
import org.example.app.exception.RegistrationException;
import org.example.app.exception.UserNotFoundException;
import org.example.app.repository.UserRepository;
import org.example.framework.security.*;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserService implements AuthenticationProvider, AnonymousProvider {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final StringKeyGenerator keyGenerator;

    @Override
    public Authentication authenticate(Authentication authentication) {
              var token = (String) authentication.getPrincipal();
        final var user = repository.findByToken(token).orElseThrow(AuthenticationException::new);
        final var role = repository.findByRole(user.getId()).orElseThrow(AuthenticationException::new).getRole();

        final var tokenTime = repository.findTokenDate(user.getId()).orElseThrow(AuthenticationException::new);
        final var tokenCreatedTime = tokenTime.toLocalDateTime();
        final var expirationTime = LocalDateTime.of(tokenCreatedTime.getYear() + TokenLifeTime.expirationTime, tokenCreatedTime.getMonth(), tokenCreatedTime.getDayOfMonth(), tokenCreatedTime.getHour(),
                tokenCreatedTime.getMinute(), tokenCreatedTime.getSecond(), tokenCreatedTime.getNano());
        final var expirationFinalToken = expirationTime.atZone(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli() / 1000;
        final var today = System.currentTimeMillis() / 1000;
        if (expirationFinalToken < today) {
           token = keyGenerator.generateKey();
            final LocalDateTime now = LocalDateTime.now();
            final Timestamp timestamp = Timestamp.valueOf(now);
            repository.updateToken(user.getId(),token,timestamp);
        }

        TokenAuthentication tokenAuthentication = null;
        if (role.equals(Roles.ROLE_USER)) {
            tokenAuthentication = repository.findByToken(token)
                    .map(o -> new TokenAuthentication(o, null, List.of(Roles.ROLE_USER), true))
                    .orElseThrow(AuthenticationException::new);
        } else if (role.equals(Roles.ROLE_ADMIN)) {
            tokenAuthentication = repository.findByToken(token)
                    .map(o -> new TokenAuthentication(o, null, List.of(Roles.ROLE_ADMIN), true))
                    .orElseThrow(AuthenticationException::new);
        }
        return tokenAuthentication;
    }

    @Override
    public Authentication authenticateBasic(Authentication authentication) throws AuthenticationException {
        final var principal = (String) authentication.getPrincipal();
        final byte[] decode = Base64.getDecoder().decode(principal);
        var decodedPrincipal = new String(decode);
        var index = decodedPrincipal.indexOf(":");
        String username = null;
        String password = null;
        if (index != -1) {
            username = decodedPrincipal.substring(0, index).trim();
            password = decodedPrincipal.substring(index + 1).trim();
        }
        final var user = repository.getByUsernameWithPassword(username).orElseThrow(AuthenticationException::new);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchesException();
        }
        final var role = repository.findByRole(user.getId()).orElseThrow(AuthenticationException::new).getRole();

        BasicAuthentication basicAuthentication = null;
        if (role.equals(Roles.ROLE_USER)) {
            basicAuthentication = repository.getByUsername(username)
                    .map(o -> new BasicAuthentication(o, null, List.of(Roles.ROLE_USER), true))
                    .orElseThrow(AuthenticationException::new);
        } else if (role.equals(Roles.ROLE_ADMIN)) {
            basicAuthentication = repository.getByUsername(username)
                    .map(o -> new BasicAuthentication(o, null, List.of(Roles.ROLE_ADMIN), true))
                    .orElseThrow(AuthenticationException::new);
        }
        return basicAuthentication;
    }


    @Override
    public AnonymousAuthentication provide() {
        return new AnonymousAuthentication(new User(
                -1,
                "anonymous"
        ));
    }


    public RegistrationResponseDto register(RegistrationRequestDto requestDto) {
        final var username = requestDto.getUsername().trim().toLowerCase();
        final var password = requestDto.getPassword().trim();
        final var role = Roles.ROLE_USER;
        final var hash = passwordEncoder.encode(password);
        final var token = keyGenerator.generateKey();
        final var saved = repository.save(0, username, hash).orElseThrow(RegistrationException::new);
        repository.saveRole(saved.getId(), role);
        repository.saveToken(saved.getId(), token);
        return new RegistrationResponseDto(saved.getId(), saved.getUsername(), token);
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        final var username = requestDto.getUsername().trim().toLowerCase();
        final var password = requestDto.getPassword().trim();
        final var saved = repository.getByUsernameWithPassword(username).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(password, saved.getPassword())) {
            throw new PasswordNotMatchesException();
        }
        final var maybeToken = repository.findToken(saved.getId()).orElseThrow(UserNotFoundException::new).getText();
        LoginResponseDto loginResponseDto = null;
        if (maybeToken == null) {
            final var token = keyGenerator.generateKey();
            repository.saveToken(saved.getId(), token);
            loginResponseDto = new LoginResponseDto(saved.getId(), saved.getUsername(), token);
        } else {
            loginResponseDto = new LoginResponseDto(saved.getId(), saved.getUsername(), maybeToken);
        }
        return loginResponseDto;
    }

    public int restorePassword(String username) {
        final var user = repository.getByUsername(username).orElseThrow(UserNotFoundException::new);
        final var code = (int) (100000 + Math.random() * 899999);
        repository.saveCode(user.getId(), code);
        return code;
    }

    public void setNewPassword(String username, String password, int code) {
        final var user = repository.getByUsername(username).orElseThrow(UserNotFoundException::new);
        final var dbCode = repository.findCode(user.getId()).orElseThrow(UserNotFoundException::new).getCode();
        if (dbCode == code) {
            final var hashPassword = passwordEncoder.encode(password);
            repository.save(user.getId(), username, hashPassword).orElseThrow(RegistrationException::new);
            repository.deleteCode(user.getId());
        }
    }
}
