package com.ota.travi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;            // HTTP Status Code (400, 404, 500,...)
    private String error;          // Tên lỗi ngắn gọn (Bad Request, Not Found,...)
    private String message;        // Câu thông báo chi tiết cho Frontend hiển thị
    private LocalDateTime timestamp;
}