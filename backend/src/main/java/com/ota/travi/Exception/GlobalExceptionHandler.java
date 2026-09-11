package com.ota.travi.Exception;

import com.ota.travi.Dto.reponse.ResponseObject;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ota.travi.Controller")
public class GlobalExceptionHandler {
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseObject> handleNotFoundException(ChangeSetPersister.NotFoundException ex) {
        return ResponseEntity.badRequest()
                .body(new ResponseObject(
                        "Có lỗi : không tìm thấy tài nguyên",
                        HttpStatus.NOT_FOUND.value(),
                        null
                ));
    }
    // Fallback
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseObject> handleGeneralException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(new ResponseObject(
                        "Có lỗi xảy ra rồi ní ơi",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        null
                ));
    }
}
