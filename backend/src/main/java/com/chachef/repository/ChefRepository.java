package com.chachef.repository;

import com.chachef.entity.Chef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChefRepository extends JpaRepository<Chef,Integer> {

//    Optional<List<Chef>> getChefsById(int id);
}
