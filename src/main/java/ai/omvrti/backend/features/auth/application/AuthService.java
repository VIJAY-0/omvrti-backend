package ai.omvrti.backend.features.auth.application;

import ai.omvrti.backend.features.auth.application.provider.OAuthProvider;
import ai.omvrti.backend.features.auth.api.response.TokenResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final Map<String, OAuthProvider> providers;

    public AuthService(Map<String, OAuthProvider> providers) {
        this.providers = providers;
    }

    public String getAuthUrl(String provider) {
        return getProvider(provider).getAuthUrl();
    }

    public TokenResponse exchangeCode(String provider, String code) {
        return getProvider(provider).exchangeCode(code);
    }

private OAuthProvider getProvider(String provider) {

    String beanKey;

    switch (provider.toLowerCase()) {
        case "google":
            beanKey = "googleAuth";
            break;
        case "microsoft":
            beanKey = "microsoftAuth";
            break;
        default:
            throw new IllegalArgumentException("Unsupported provider: " + provider);
    }

    OAuthProvider p = providers.get(beanKey);

    if (p == null) {
        throw new IllegalStateException("Provider not configured: " + beanKey);
    }

    return p;
}
}