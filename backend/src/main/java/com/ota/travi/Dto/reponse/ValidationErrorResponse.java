package com.ota.travi.Dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationErrorResponse {
    private int status;
    private String error;
    private Map<String, String> fieldErrors;
    private LocalDateTime timestamp;
    private String path;
}
