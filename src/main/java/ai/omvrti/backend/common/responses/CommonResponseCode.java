package ai.omvrti.backend.common.responses;

public enum CommonResponseCode implements ResponseCode {

    SUCCESS("SUCCESS", "Request completed successfully"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Internal server error"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Invalid request"),
    UNAUTHORIZED("UNAUTHORIZED", "Authentication required"),
    FORBIDDEN("FORBIDDEN", "Access denied"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Resource not found");

    private final String code;
    private final String message;

    CommonResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public String getCode() { return code; }

    public String getMessage() { return message; }

}