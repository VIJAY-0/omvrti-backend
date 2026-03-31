package ai.omvrti.backend.features.auth.storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import ai.omvrti.backend.features.auth.model.TokenData;

public class TokenStore {

    // userId -> (provider -> token)
    private static final Map<String, Map<String, TokenData>> store = new ConcurrentHashMap<>();

    public static void save(String userId, String provider, TokenData token) {
        if (token == null) {
            Map<String, TokenData> userTokens = store.get(userId);
            if (userTokens != null) {
                userTokens.remove(provider);
            }
            return;
        }

        store
            .computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
            .put(provider, token);
    }

    public static TokenData get(String userId, String provider) {
        Map<String, TokenData> userTokens = store.get(userId);
        if (userTokens == null) return null;

        TokenData token = userTokens.get(provider);

        if (token != null) {
            System.out.println("TOKEN [" + provider + "]: " + token.access_token);
        }

        return token;
    }
}