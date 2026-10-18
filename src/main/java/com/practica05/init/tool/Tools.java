package com.practica05.init.tool;

import com.practica05.init.model.Cotizacion;
import com.practica05.init.model.Operacion;
import com.practica05.init.model.TipoOperacion;
import com.practica05.init.repository.OperacionRepository;
import com.practica05.init.service.CotizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class Tools {

    public static final String OPERACION_REGISTRADA = "operacionRegistrada";

    private final CotizacionService cotizacionService;
    private final OperacionRepository operacionRepository;

    @Tool(description = "Consulta en Yahoo Finance el precio actual, el precio de cierre anterior y la moneda de una acción. "
            + "Úsala solo cuando le usuario indique el símbolo de una acción. "
            + "No la uses cuando el usuario responde que quiere comprar, vender o mantener.")
    public Cotizacion obtenerCotizacion(@ToolParam(description = "Símbolo bursátil de la acción, por ejemplo AAPL o MSFT") String simbolo) {
        return cotizacionService.obtenerCotizacion(simbolo);
    }

    @Tool(description = "Registra en la base de datos la operación que el usuario ha elegido sobre la acción de la conversación. "
            + "Úsala cuando el usuario responda que quiere comprar, vender o mantener la acción.")
    public Operacion registrarOperacion(
            @ToolParam(description = "Símbolo bursátil de la acción tratada en la conversación") String simbolo,
            @ToolParam(description = "Operación elegida explícitamente por el usuario: COMPRAR, VENDER o MANTENER") TipoOperacion tipoOperacion,
            @ToolParam(description = "Precio actual de la acción que indicaste al usuario en la conversación") BigDecimal precio,
            ToolContext toolContext) {
        Operacion operacion = operacionRepository.save(Operacion.crear(
                tipoOperacion,
                simbolo,
                precio,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        ));
        if (toolContext != null && toolContext.getContext().get(OPERACION_REGISTRADA) instanceof AtomicBoolean operacionRegistrada) {
            operacionRegistrada.set(true);
        }
        return operacion;
    }

}
