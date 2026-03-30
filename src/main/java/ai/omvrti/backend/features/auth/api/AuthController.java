package ai.omvrti.backend.features.auth.api;

import ai.omvrti.backend.features.auth.model.TokenData;
import ai.omvrti.backend.features.auth.storage.TokenStore;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private final WebClient webClient = WebClient.create();

    @GetMapping("/url")
    public Map<String, String> getAuthUrl() {

        String url = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/calendar" +
                "&access_type=offline" +
                "&prompt=consent";

        return Map.of("url", url);
    }

    @GetMapping("/callback")
    public void handleCallback(
            @RequestParam String code,
            HttpServletResponse response) throws Exception {
        Map tokenResponse = webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .bodyValue(Map.of(
                        "code", code,
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "redirect_uri", redirectUri,
                        "grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        TokenData token = new TokenData();
        token.access_token = (String) tokenResponse.get("access_token");
        token.refresh_token = (String) tokenResponse.get("refresh_token");

        TokenStore.save("user1", token);

        // 👇 Send JS instead of redirect
        response.setContentType("text/html");
        response.getWriter().write("""
                    <html>
                      <body>
                        <script>
                          window.opener.postMessage(
                            { type: 'GOOGLE_AUTH_SUCCESS' },
                            'http://localhost:3000'
                          );
                          window.close();
                        </script>
                      </body>
                    </html>
                """);
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "authenticated", TokenStore.get("user1") != null);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        TokenStore.save("user1", null);
        return Map.of("success", true);
    }
}