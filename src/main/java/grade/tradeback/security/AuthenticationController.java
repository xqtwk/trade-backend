package grade.tradeback.security;

import grade.tradeback.security.service.AuthenticationService;
import grade.tradeback.security.dto.AuthenticationRequest;
import grade.tradeback.security.dto.AuthenticationResponse;
import grade.tradeback.security.dto.RegisterRequest;
import grade.tradeback.security.dto.VerificationRequest;
import grade.tradeback.security.tfa.MfaSetupResponse;
import grade.tradeback.security.tfa.MfaToggleRequest;
import grade.tradeback.security.tfa.MfaToggleResponse;
import grade.tradeback.security.tfa.TwoFactorAuthenticationService;
import grade.tradeback.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final TwoFactorAuthenticationService twoFactorAuthenticationService;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = authenticationService.register(request);
        if (!request.isMfaEnabled()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .mfaEnabled(true)
                    .secretImageUri(response.getSecretImageUri())
                    .build());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(
            @RequestBody VerificationRequest verificationRequest
    ) {
        return ResponseEntity.ok(authenticationService.verifyCode(verificationRequest));
    }
    @PostMapping("/toggle-mfa")
    public ResponseEntity<?> toggleMfa(@RequestBody MfaToggleRequest request, Principal principal) {
        System.out.println(request.getOtpCode());
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.isEnableMfa()) {
            // If enabling MFA
            String newSecret = request.getSecret();
            if (twoFactorAuthenticationService.isOtpValid(newSecret, request.getOtpCode())) {
                user.setMfaEnabled(true);
                user.setSecret(newSecret);

                userRepository.save(user);
                System.out.println(user.getSecret());
                System.out.println(user.isMfaEnabled());
                System.out.println("DONE");
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            // If disabling MFA, verify the OTP code first
            if (twoFactorAuthenticationService.isOtpValid(user.getSecret(), request.getOtpCode())) {
                user.setMfaEnabled(false);
                user.setSecret(null);
                userRepository.save(user);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
    }

    @GetMapping("/mfa-setup")
    public ResponseEntity<?> getMfaSetup(Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isMfaEnabled()) {
            String newSecret = twoFactorAuthenticationService.generateNewSecret();
            // Do not save the secret yet; save it after user confirms enabling MFA
            String qrCodeImageUri = twoFactorAuthenticationService.generateQrCodeImageUri(newSecret);
            return ResponseEntity.ok(new MfaSetupResponse(qrCodeImageUri, newSecret)); // Include the newSecret in the response
        } else {
            return ResponseEntity.ok(new MfaSetupResponse(null, null));
        }
    }

}
