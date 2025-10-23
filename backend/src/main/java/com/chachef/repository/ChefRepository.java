package com.chachef.repository;

import com.chachef.entity.Chef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChefRepository extends JpaRepository<Chef,Integer> {

    List<Chef> getChefsById(int id);
}
