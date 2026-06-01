package com.ota.travi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UpdatePreferencesRequest(
    @NotEmpty(message = "Danh sach so thich khong duoc de trong")
    List<String> soThichIds
) {}
