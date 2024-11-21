package fr.uga.l3miage.pc.prisonersdilemma.utils;

import lombok.*;

@RequiredArgsConstructor
@Data
public class ApiResponse<T> {

    private int code;
    private String message;
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
}

