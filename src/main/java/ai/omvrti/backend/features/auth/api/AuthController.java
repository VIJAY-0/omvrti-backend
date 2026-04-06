package ai.omvrti.backend.features.auth.api;

import ai.omvrti.backend.common.responses.ApiResponse;
import ai.omvrti.backend.common.responses.CommonResponseCode;
import ai.omvrti.backend.features.auth.api.response.*;
import ai.omvrti.backend.features.auth.application.AuthService;
import ai.omvrti.backend.features.auth.model.TokenData;
import ai.omvrti.backend.features.auth.storage.TokenStore;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // TODO: Replace with actual Spring Security context user retrieval
    private String getUser() {
        return "user1"; 
    }

    @GetMapping("/{provider}/url")
    public ResponseEntity<ApiResponse<AuthUrlResponse>> getAuthUrl(@PathVariable String provider) {
        String url = service.getAuthUrl(provider);
        return ResponseEntity.ok(ApiResponse.success(CommonResponseCode.SUCCESS, new AuthUrlResponse(url)));
    }

    @GetMapping("/{provider}/callback")
    public void handleCallback(
            @PathVariable String provider,
            @RequestParam String code,
            HttpServletResponse response
    ) throws IOException {
        try {
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
                    'http://localhost:3000' // Ensure this matches your frontend origin
                  );
                  window.close();
                </script>
            """);
        } catch (Exception e) {
            log.error("Failed to handle OAuth callback for provider: {}", provider, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed");
        }
    }

    @GetMapping("/{provider}/status")
    public ResponseEntity<ApiResponse<AuthStatusResponse>> status(@PathVariable String provider) {
        boolean isAuthenticated = TokenStore.get(getUser(), provider) != null;
        return ResponseEntity.ok(ApiResponse.success(CommonResponseCode.SUCCESS, new AuthStatusResponse(isAuthenticated)));
    }

    @PostMapping("/{provider}/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(@PathVariable String provider) {
        TokenStore.save(getUser(), provider, null);
        return ResponseEntity.ok(ApiResponse.success(CommonResponseCode.SUCCESS, new LogoutResponse(true)));
    }
}