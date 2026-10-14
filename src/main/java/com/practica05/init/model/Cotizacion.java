package com.practica05.init.model;

import java.math.BigDecimal;

public record Cotizacion(String simbolo, BigDecimal precioActual, BigDecimal cierreAnterior, String moneda) {}
