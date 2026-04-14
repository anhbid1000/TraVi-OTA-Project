package com.ota.travi.Dto.reponse;

public record ResponseObject(
        String message,
        Integer status,
        Object data
) { }
