package com.ota.travi.Dto.reponse;

public record LoginResponse(
        boolean success,
        String message,
        String token,
        String username
) {}
