package ai.omvrti.backend.common.responses;


public class ErrorResponseCode implements ResponseCode {
    private String code;
    private String message;

    public ErrorResponseCode(String code,String message){
        this.code = code;
        this.message = message;
    }
    public String getCode(){
        return this.code;
    }
    public String getMessage(){
        return this.message;
    }
    
}