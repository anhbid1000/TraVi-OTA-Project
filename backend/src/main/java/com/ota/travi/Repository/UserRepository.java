package com.ota.travi.Repository;

import com.ota.travi.Entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,String> {
    User findByEmail(String email);
    User findByUsername(String username);
    boolean existsUserByUsername(String username);
    boolean existsUserByEmail(String email);
    boolean existsUserBySoDienThoai(String soDienThoai);

    Optional<User> findUserByUsername(String username);
}
