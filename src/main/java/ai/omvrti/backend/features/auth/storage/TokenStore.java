package ai.omvrti.backend.features.auth.storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import ai.omvrti.backend.features.auth.model.TokenData;

public class TokenStore {

    private static final Map<String, TokenData> store = new ConcurrentHashMap<>();

    public static void save(String userId, TokenData token) {
        store.put(userId, token);
    }

    public static TokenData get(String userId) {
        return store.get(userId);
    }
}