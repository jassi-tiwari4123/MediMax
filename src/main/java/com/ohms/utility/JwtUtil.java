package com.ohms.utility;

import com.ohms.enums.Role;
import com.ohms.exception.AuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JwtUtil — creates, validates and parses JWT tokens.
 *
 * INTERVIEW POINTS:
 *   - JWT = JSON Web Token. It is stateless — no session stored on server.
 *   - Structure: Header.Payload.Signature (Base64 encoded)
 *   - We store userId, email, and role in the payload (claims).
 *   - Token is signed with HMAC-SHA256 using a secret key.
 *   - Each request sends the token in a cookie; AuthFilter validates it.
 *   - Token expiry enforces session timeout without DB lookups.
 *
 * Token Lifecycle:
 *   Login → generateToken() → store in cookie
 *   Request → AuthFilter.doFilter() → validateToken() → extract claims
 *   Logout → remove cookie client-side
 */
public final class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    /** Cookie name used to store the JWT in the browser */
    public static final String COOKIE_NAME = "ohms_jwt";

    /** Claim keys */
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_EMAIL   = "email";
    private static final String CLAIM_ROLE    = "role";

    /** Signing key — derived from secret in application.properties */
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(
        AppConfig.getJwtSecret().getBytes(StandardCharsets.UTF_8)
    );

    /** Token validity in milliseconds (default: 30 minutes) */
    private static final long EXPIRY_MS = AppConfig.getJwtExpiry();

    // Prevent instantiation
    private JwtUtil() {}

    // ── Token Generation ─────────────────────────────────────────

    /**
     * Generates a signed JWT for the given user.
     *
     * @param userId  database user id
     * @param email   user's email address
     * @param role    user's role (ADMIN / DOCTOR / PATIENT)
     * @return signed JWT string
     */
    public static String generateToken(int userId, String email, Role role) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + EXPIRY_MS);

        return Jwts.builder()
                   .setSubject(String.valueOf(userId))  // "sub" claim
                   .claim(CLAIM_USER_ID, userId)
                   .claim(CLAIM_EMAIL,   email)
                   .claim(CLAIM_ROLE,    role.name())
                   .setIssuedAt(now)
                   .setExpiration(expiry)
                   .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                   .compact();
    }

    // ── Token Validation ─────────────────────────────────────────

    /**
     * Validates a JWT string and returns its claims.
     *
     * @param token JWT string from cookie
     * @return Claims map if valid
     * @throws AuthException if token is invalid or expired
     */
    public static Claims validateToken(String token) throws AuthException {
        try {
            return Jwts.parserBuilder()
                       .setSigningKey(SIGNING_KEY)
                       .build()
                       .parseClaimsJws(token)
                       .getBody();

        } catch (ExpiredJwtException e) {
            logger.warn("JWT expired: {}", e.getMessage());
            throw new AuthException("Session expired. Please log in again.",
                                    AuthException.TOKEN_EXPIRED);

        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Invalid JWT: {}", e.getMessage());
            throw new AuthException("Invalid or tampered token.",
                                    AuthException.TOKEN_INVALID);
        }
    }

    // ── Claims Extraction ────────────────────────────────────────

    public static int extractUserId(Claims claims) {
        return claims.get(CLAIM_USER_ID, Integer.class);
    }

    public static String extractEmail(Claims claims) {
        return claims.get(CLAIM_EMAIL, String.class);
    }

    public static Role extractRole(Claims claims) {
        String roleStr = claims.get(CLAIM_ROLE, String.class);
        return Role.fromString(roleStr);
    }

    /**
     * Convenience method: validates token and extracts userId in one call.
     */
    public static int getUserIdFromToken(String token) throws AuthException {
        Claims claims = validateToken(token);
        return extractUserId(claims);
    }

    /**
     * Convenience method: validates token and extracts Role in one call.
     */
    public static Role getRoleFromToken(String token) throws AuthException {
        Claims claims = validateToken(token);
        return extractRole(claims);
    }
}
