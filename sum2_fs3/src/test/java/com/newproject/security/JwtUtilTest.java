package com.newproject.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String secret = "mysecretkeymysecretkeymysecretkeymysecretkey";
    

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken("testUser", "ROLE_USER");
        assertNotNull(token);

        Claims claims = Jwts.parserBuilder().setSigningKey(jwtUtil.getSigningKey()).build().parseClaimsJws(token).getBody();
        assertEquals("testUser", claims.getSubject());
        assertEquals("ROLE_USER", claims.get("role"));
    }


    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken("testUser", "ROLE_USER");
        String username = jwtUtil.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void testExtractExpiration() {
        String token = jwtUtil.generateToken("testUser", "ROLE_USER");
        Date expiration = jwtUtil.extractExpiration(token);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken("testUser", "ROLE_USER");
        assertTrue(jwtUtil.validateToken(token, "testUser"));
    }

    @Test
    void testExtractCustomClaim() {
        String token = jwtUtil.generateToken("testUser", "ROLE_USER");
        Claims claims = Jwts.parserBuilder().setSigningKey(jwtUtil.getSigningKey()).build().parseClaimsJws(token).getBody();
        assertEquals("ROLE_USER", claims.get("role"));
    }

}