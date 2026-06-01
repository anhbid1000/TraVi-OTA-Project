package com.ota.travi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItineraryRequest(
    @NotBlank(message = "Vui long chon diem den")
    String city,
    
    @NotNull(message = "Vui long nhap so ngay")
    @Min(value = 1, message = "So ngay phai lon hon hoac bang 1")
    Integer durationDays,
    
    @NotNull(message = "Vui long nhap so nguoi")
    @Min(value = 1, message = "So nguoi phai lon hon hoac bang 1")
    Integer groupSize,
    
    @NotBlank(message = "Vui long chon ngan sach (Gia re, Trung binh, Cao cap)")
    String budgetLevel
) {}
