package com.practica05.init.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("operacion")
public record Operacion(
        @Id Long id,
        TipoOperacion tipoOperacion,
        String simboloAccion,
        BigDecimal precio,
        LocalDateTime fecha
) {

    public static Operacion crear(TipoOperacion tipoOperacion, String simboloAccion, BigDecimal precio, LocalDateTime fecha) {
        return new Operacion(null, tipoOperacion, simboloAccion, precio, fecha);
    }
}
