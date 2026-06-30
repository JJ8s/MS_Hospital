package com.hospital.ms_inventario.repository;

import com.hospital.ms_inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByNombre(String nombre);

    Optional<Producto> findByNombreIgnoreCase(String nombre);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    Optional<Producto> findByLote(String lote);

    List<Producto> findByStockLessThan(Integer limite);

    boolean existsByLote(String lote);
}
