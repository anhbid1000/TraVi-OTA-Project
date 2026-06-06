package com.ota.travi.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ProfanityFilterService {
    private static final List<String> BLACKLIST = Arrays.asList("vo_trach_nhiem",
            "lua_dao", "mat_day", "vl", "occho");

    public void kiemDuyetNgonTu(String text) {
        if (text == null) return;
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String textWithoutDiacritics = pattern.matcher(normalized).replaceAll("").toLowerCase();

        for (String badWord : BLACKLIST) {
            // Đảm bảo từ cấm phải đứng độc lập, không nằm dính trong từ khác
            String regex = "\\b" + Pattern.quote(badWord) + "\\b";
            Pattern p = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);

            if (p.matcher(textWithoutDiacritics).find()) {
                // Gửi lỗi cụ thể
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "Nội dung chứa từ ngữ vi phạm tiêu chuẩn cộng đồng: " + badWord
                );
            }
        }
    }
}
