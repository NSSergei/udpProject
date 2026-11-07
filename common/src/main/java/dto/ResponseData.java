package dto;

public class ResponseData {

    private boolean success;
    private String message;
    private Long userId;

    public ResponseData() {
    }

    public ResponseData(
            boolean success,
            String message,
            Long userId
    ) {
        this.success = success;
        this.message = message;
        this.userId = userId;
    }

    public ResponseData(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }
}
