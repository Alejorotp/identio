package com.identio.mvp.infrastructure.auth.security;
import com.identio.mvp.domain.user.entities.Role;
import com.identio.mvp.domain.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant midnight = LocalDate.now(ZoneId.systemDefault()).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("identio-auth").issuedAt(now).expiresAt(midnight)
                .subject(user.getId().toString()).claim("roles", roles).claim("type", "access").build();
        return jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
    }
    @Override
    public String generateShortLivedAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(3, ChronoUnit.MINUTES);
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("identio-auth").issuedAt(now).expiresAt(expiresAt)
                .subject(user.getId().toString()).claim("roles", roles).claim("type", "access").build();
        return jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
    }
    @Override
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant refreshExp = now.plus(7, ChronoUnit.DAYS);
        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("identio-auth").issuedAt(now).expiresAt(refreshExp)
                .subject(user.getId().toString()).claim("type", "refresh").build();
        return jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();
    }
    @Override
    public String extractSubjectFromRefreshToken(String refreshToken) {
        Jwt jwt = jwtDecoder.decode(refreshToken);
        if (!"refresh".equals(jwt.getClaimAsString("type"))) throw new IllegalArgumentException("Invalid token type");
        return jwt.getSubject();
    }
}