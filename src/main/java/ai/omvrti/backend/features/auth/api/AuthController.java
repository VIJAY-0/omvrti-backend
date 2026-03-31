package ai.omvrti.backend.features.auth.api;

import ai.omvrti.backend.features.auth.api.response.*;
import ai.omvrti.backend.features.auth.application.AuthService;
import ai.omvrti.backend.features.auth.model.TokenData;
import ai.omvrti.backend.features.auth.storage.TokenStore;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    private String getUser() {
        return "user1"; // replace later with real user
    }

    @GetMapping("/{provider}/url")
    public AuthUrlResponse getAuthUrl(@PathVariable String provider) {
        return new AuthUrlResponse(service.getAuthUrl(provider));
    }

    @GetMapping("/{provider}/callback")
    public void handleCallback(
            @PathVariable String provider,
            @RequestParam String code,
            HttpServletResponse response
    ) throws Exception {

        TokenResponse tokenResponse = service.exchangeCode(provider, code);

        TokenData token = new TokenData();
        token.access_token = tokenResponse.accessToken;
        token.refresh_token = tokenResponse.refreshToken;

        TokenStore.save(getUser(), provider, token);

        response.setContentType("text/html");
        response.getWriter().write("""
            <script>
              window.opener.postMessage(
                { type: 'OAUTH_AUTH_SUCCESS' },
                'http://localhost:8080'
              );
              window.close();
            </script>
        """);
    }

    @GetMapping("/{provider}/status")
    public AuthStatusResponse status(@PathVariable String provider) {
        return new AuthStatusResponse(
                TokenStore.get(getUser(), provider) != null
        );
    }

    @PostMapping("/{provider}/logout")
    public LogoutResponse logout(@PathVariable String provider) {
        TokenStore.save(getUser(), provider, null);
        return new LogoutResponse(true);
    }
}