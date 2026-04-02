package ai.omvrti.backend.features.auth.application.provider;

import ai.omvrti.backend.features.auth.api.response.TokenResponse;

public interface OAuthProvider {
    String getAuthUrl();
    TokenResponse exchangeCode(String code);
}