package ai.omvrti.backend.common.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import ai.omvrti.backend.common.responses.ApiResponse;
import ai.omvrti.backend.common.responses.CommonResponseCode;
import ai.omvrti.backend.common.responses.ResponseCode;
import ai.omvrti.backend.common.responses.ErrorResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationError(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        log.warn("Validation error: {}", errorMessage);
        
        ResponseCode responseCode = new ErrorResponseCode(CommonResponseCode.VALIDATION_ERROR.getCode(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(responseCode));
    }

    // Catch-all handler for the "throws Exception" signatures
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        log.error("An unexpected error occurred: ", ex);
        
        // We shouldn't leak raw exception messages to the client in production, 
        // but it's helpful for local development. 
        ResponseCode responseCode = new ErrorResponseCode(CommonResponseCode.INTERNAL_ERROR.getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(responseCode));
    }
    
    // Catch specific RuntimeExceptions (like our missing token logic)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        log.warn("Runtime exception: {}", ex.getMessage());
        
        ResponseCode responseCode = new ErrorResponseCode(CommonResponseCode.UNAUTHORIZED.getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(responseCode));
    }
}