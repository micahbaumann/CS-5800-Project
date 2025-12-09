package com.chachef.repository;

import com.chachef.entity.Chef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChefRepository extends JpaRepository<Chef,UUID> {
    Optional<Chef> findByChefId(UUID chefId);
    Optional<List<Chef>> findChefsByUser_UserId(UUID userUserId);
}
