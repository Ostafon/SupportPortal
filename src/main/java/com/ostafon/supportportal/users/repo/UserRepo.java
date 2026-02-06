package com.ostafon.supportportal.users.repo;

import com.ostafon.supportportal.users.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.isActive = true")
    List<UserEntity> findAllActive();

    @Query("SELECT u FROM UserEntity u WHERE u.role = :role AND u.isActive = true")
    List<UserEntity> findByRoleAndActive(String role);
}
