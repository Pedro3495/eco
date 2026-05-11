package com.eco.auth.service;

import com.eco.common.exception.UnauthorizedException;
import com.eco.user.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final long accessTokenSeconds;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${eco.auth.jwt-secret}") String jwtSecret,
            @Value("${eco.auth.access-token-seconds}") long accessTokenSeconds
    ) {
        this.objectMapper = objectMapper;
        this.secret = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSeconds = accessTokenSeconds;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getId().toString());
        payload.put("email", user.getEmail());
        payload.put("name", user.getName());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plusSeconds(accessTokenSeconds).getEpochSecond());

        String encodedHeader = encodeJson(header);
        String encodedPayload = encodeJson(payload);
        String unsignedToken = encodedHeader + "." + encodedPayload;

        return unsignedToken + "." + sign(unsignedToken);
    }

    public UUID validateAndExtractUserId(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new UnauthorizedException("Token invalido");
        }

        String unsignedToken = parts[0] + "." + parts[1];
        String expectedSignature = sign(unsignedToken);
        if (!constantTimeEquals(expectedSignature, parts[2])) {
            throw new UnauthorizedException("Token invalido");
        }

        Map<String, Object> payload = decodePayload(parts[1]);
        long expiresAt = ((Number) payload.get("exp")).longValue();
        if (Instant.now().getEpochSecond() >= expiresAt) {
            throw new UnauthorizedException("Token expirado");
        }

        return UUID.fromString((String) payload.get("sub"));
    }

    public long getAccessTokenSeconds() {
        return accessTokenSeconds;
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(value);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (Exception exception) {
            throw new IllegalStateException("Falha ao gerar token", exception);
        }
    }

    private Map<String, Object> decodePayload(String encodedPayload) {
        try {
            byte[] json = Base64.getUrlDecoder().decode(encodedPayload);
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception exception) {
            throw new UnauthorizedException("Token invalido");
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception exception) {
            throw new IllegalStateException("Falha ao assinar token", exception);
        }
    }

    private boolean constantTimeEquals(String first, String second) {
        return java.security.MessageDigest.isEqual(
                first.getBytes(StandardCharsets.UTF_8),
                second.getBytes(StandardCharsets.UTF_8)
        );
    }
}
