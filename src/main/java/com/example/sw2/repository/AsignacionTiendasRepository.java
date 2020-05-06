package com.example.sw2.repository;


import com.example.sw2.entity.AsignacionTiendas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionTiendasRepository extends JpaRepository<AsignacionTiendas,Integer> {
}
