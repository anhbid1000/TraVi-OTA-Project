package com.ota.travi.config;

import com.ota.travi.entity.VaiTro;
import com.ota.travi.repository.VaiTroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedDefaultRoles(VaiTroRepository vaiTroRepository) {
        return args -> {
            createRoleIfMissing(vaiTroRepository, "KHACH_HANG", "Nguoi dung tim kiem va dat dich vu");
            createRoleIfMissing(vaiTroRepository, "DOI_TAC", "Chu khach san/nha hang dang ban dich vu");
            createRoleIfMissing(vaiTroRepository, "QUAN_TRI_VIEN", "Quan tri vien he thong OTA");
        };
    }

    private void createRoleIfMissing(VaiTroRepository vaiTroRepository, String ten, String moTa) {
        vaiTroRepository.findByTen(ten).orElseGet(() -> {
            VaiTro vaiTro = new VaiTro();
            vaiTro.setTen(ten);
            vaiTro.setMoTa(moTa);
            return vaiTroRepository.save(vaiTro);
        });
    }
}
