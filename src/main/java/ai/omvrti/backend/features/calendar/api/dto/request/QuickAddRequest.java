package ai.omvrti.backend.features.calendar.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class QuickAddRequest {

    @NotBlank(message = "Text is required")
    private String text;

    public QuickAddRequest() {}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}