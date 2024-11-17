package dododocs.dododocs.auth.presentation;

import static org.springframework.http.HttpStatus.CREATED;

import dododocs.dododocs.auth.application.AuthService;
import dododocs.dododocs.auth.dto.AccessTokenResponse;
import dododocs.dododocs.auth.dto.LoginRequest;
import dododocs.dododocs.auth.dto.OAuthUriResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/link")
    public ResponseEntity<OAuthUriResponse> generateUri() {
        return ResponseEntity.ok(new OAuthUriResponse(authService.generateUri()));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody final LoginRequest loginRequest) throws Exception {
        final String accessToken = authService.generateTokenWithCode(loginRequest.getCode());
        return ResponseEntity.status(CREATED).body(new AccessTokenResponse(accessToken));
    }
}
