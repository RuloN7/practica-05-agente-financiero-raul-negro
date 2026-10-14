package com.practica05.init.repository;


import com.practica05.init.model.Operacion;
import org.springframework.data.repository.ListCrudRepository;

public interface OperacionRepository extends ListCrudRepository<Operacion, Long> {
}
