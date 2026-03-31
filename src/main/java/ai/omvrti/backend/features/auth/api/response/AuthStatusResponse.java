package ai.omvrti.backend.features.auth.api.response;

public class AuthStatusResponse {
    public boolean authenticated;

    public AuthStatusResponse(boolean authenticated) {
        this.authenticated = authenticated;
    }
}