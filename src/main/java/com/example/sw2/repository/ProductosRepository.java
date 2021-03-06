package com.example.sw2.repository;


import com.example.sw2.constantes.ProductoId;
import com.example.sw2.entity.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, ProductoId> {

    List<Productos> findProductosByIdCodigolinea(String linea);

}
