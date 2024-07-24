package com.example.satto.config.security.handler;

import com.example.satto.config.security.token.TokenBlackList;
import com.example.satto.config.security.token.TokenBlackListRepository;
import com.example.satto.config.security.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final TokenBlackListRepository tokenBlackListRepository;
    private static final Logger logger = LoggerFactory.getLogger(LogoutService.class);

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, "Token is missing or invalid");
            return;
        }

        final String jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);
        if (storedToken == null || storedToken.isExpired()) {
            sendErrorResponse(response, "Token is missing or invalid");
        } else {
            if (!tokenBlackListRepository.existsByToken(jwt) ) {
                TokenBlackList blackList = new TokenBlackList();
                blackList.setToken(jwt);
                tokenBlackListRepository.save(blackList);
                System.out.println("00000000");
                storedToken.setExpired(true);
                storedToken.setRevoked(true);
                tokenRepository.save(storedToken);
                SecurityContextHolder.clearContext();
                System.out.println("here");
                System.out.println("Register token");

                sendSuccessResponse(response);

            } else {
                sendErrorResponse(response, "Can't use this token");
            }
        }
    }


    private void sendErrorResponse(HttpServletResponse response, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"" + message + "\"}");
            response.getWriter().flush();
        } catch (IOException e) {
            logger.error("Error writing response", e);
        }
    }

    private void sendSuccessResponse(HttpServletResponse response) {
        try {
            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"" + "logout" + "\"}");
            response.getWriter().flush();
        } catch (IOException e) {
            logger.error("Error writing response", e);
        }
    }

}
