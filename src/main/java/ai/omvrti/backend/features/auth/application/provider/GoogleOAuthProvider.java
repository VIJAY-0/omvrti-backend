package ai.omvrti.backend.features.auth.application.provider;

import ai.omvrti.backend.features.auth.api.response.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component("googleAuth")
public class GoogleOAuthProvider implements OAuthProvider {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private final WebClient webClient = WebClient.create();

    @Override
    public String getAuthUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/calendar" +
                "&access_type=offline" +
                "&prompt=consent";
    }

    @Override
    public TokenResponse exchangeCode(String code) {
        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .bodyValue(java.util.Map.of(
                        "code", code,
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "redirect_uri", redirectUri,
                        "grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }
}