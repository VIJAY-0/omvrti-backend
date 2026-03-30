package ai.omvrti.backend.common.responses;
public class ApiResponse<T> {

    public boolean success;
    public String code;
    public String message;
    public T data;

    public ApiResponse(boolean success, ResponseCode responseCode, T data) {
        this.success = success;
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }

    public static <T> ApiResponse<T> success(ResponseCode code, T data) {
        return new ApiResponse<>(true, code, data);
    }

    public static <T> ApiResponse<T> error(ResponseCode code) {
        return new ApiResponse<>(false, code, null);
    }
}