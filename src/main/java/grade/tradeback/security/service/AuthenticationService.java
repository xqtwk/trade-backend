package grade.tradeback.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import grade.tradeback.security.dto.AuthenticationRequest;
import grade.tradeback.security.dto.AuthenticationResponse;
import grade.tradeback.security.dto.RegisterRequest;
import grade.tradeback.security.dto.VerificationRequest;
import grade.tradeback.security.jwt.JwtService;
import grade.tradeback.security.tfa.TwoFactorAuthenticationService;
import grade.tradeback.security.jwt.token.Token;
import grade.tradeback.security.jwt.token.TokenRepository;
import grade.tradeback.security.jwt.token.TokenType;
import grade.tradeback.user.entity.Role;
import grade.tradeback.user.entity.User;
import grade.tradeback.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TwoFactorAuthenticationService twoFactorAuthenticationService;

    // REGISTRATION
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .mfaEnabled(request.isMfaEnabled())
                .build();
        // IF MFA is enabled -> generate secret
        if (request.isMfaEnabled()) {
            user.setSecret(twoFactorAuthenticationService.generateNewSecret());
        }
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        System.out.println();
        return AuthenticationResponse.builder()
                .accessToken(jwtToken) // so user doesn't have to log in after registering by himself
                .secretImageUri(twoFactorAuthenticationService.generateQrCodeImageUri(user.getSecret()))
                .refreshToken(refreshToken)
                .mfaEnabled(user.isMfaEnabled())
                .build();
    }

    // AUTHENTICATION
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NoSuchElementException("No user found with username: " + request.getUsername()));
        if (user.isMfaEnabled()) {
            return AuthenticationResponse.builder()
                    .accessToken("")
                    .refreshToken("")
                    .mfaEnabled(true)
                    .build();
        }
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .mfaEnabled(false)
                .build();
    }

    // SAVING TOKEN
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    // REVOKING TOKEN
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    // REFRESHING TOKEN
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow();

            /* TO ALSO REVOKE REFRESH TOKEN
            var isTokenValid = tokenRepository.findByToken(refreshToken)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            */

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .mfaEnabled(false)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public AuthenticationResponse verifyCode(
            VerificationRequest verificationRequest
    ) {
        User user = userRepository.findByUsername(verificationRequest.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(String.format("No user found with %S",
                        verificationRequest.getUsername())));
        if (twoFactorAuthenticationService.isOtpNotValid(user.getSecret(), verificationRequest.getCode())) {
            throw new BadCredentialsException("Code is not correct");
        }
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user,jwtToken);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .mfaEnabled(user.isMfaEnabled())
                .build();
    }
}
