package fr.uga.l3miage.pc.prisonersdilemma.utils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class ApiResponse<T> {

    private int code;
    private String message;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private T data;
    private String type;

    public ApiResponse(int code, String message, String type) {
        this.code = code;
        this.message = message;
        this.type = type;
        this.data = null;
    }

    public ApiResponse(int code, String message, String type, T data) {
        this.code = code;
        this.message = message;
        this.type = type;
        this.data = data;
    }

    public ApiResponse(int i, String ok, String displayResults, String resultsText) {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

