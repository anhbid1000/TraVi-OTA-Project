package com.ota.travi.Repository;

import com.ota.travi.Entity.VaiTro;
import org.springframework.data.repository.CrudRepository;

public interface VaitroRepository extends CrudRepository<VaiTro,String> {
        VaiTro findByTen(String ten);
}
