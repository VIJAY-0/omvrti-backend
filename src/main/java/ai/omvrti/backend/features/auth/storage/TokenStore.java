package ai.omvrti.backend.features.auth.storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import ai.omvrti.backend.features.auth.model.TokenData;

public class TokenStore {

    private static final Map<String, TokenData> store = new ConcurrentHashMap<>();

    public static void save(String userId, TokenData token) {
        if (token == null) {
            store.remove(userId);
        } else {
            store.put(userId, token);
        }
    }

    public static TokenData get(String userId) {
        TokenData token = store.get(userId); // ✅ FIX

        if (token != null){

            System.out.println("-------------------------------------------------------");
            System.out.println("-------------------------------------------------------");
            System.out.println("TOKEN: " + token.access_token);
            System.out.println("TOKEN: " + token.refresh_token);
            System.out.println("-------------------------------------------------------");
            System.out.println("-------------------------------------------------------");
        }
            return token;
    }
}