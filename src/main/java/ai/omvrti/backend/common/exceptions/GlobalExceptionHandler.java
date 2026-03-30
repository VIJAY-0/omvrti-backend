package ai.omvrti.backend.common.exceptions;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import ai.omvrti.backend.common.responses.ApiResponse;
import ai.omvrti.backend.common.responses.CommonResponseCode;
import ai.omvrti.backend.common.responses.ResponseCode;
import ai.omvrti.backend.common.responses.ErrorResponseCode;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationError(
            MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        ResponseCode responseCode = new ErrorResponseCode(CommonResponseCode.VALIDATION_ERROR.getCode(),errorMessage);
        ApiResponse<Object> response = ApiResponse.error(responseCode);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}