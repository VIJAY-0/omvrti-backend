package ai.omvrti.backend.features.calendar.api.dto.response;

public class DeleteEventResponse {

    private boolean success;

    public DeleteEventResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}