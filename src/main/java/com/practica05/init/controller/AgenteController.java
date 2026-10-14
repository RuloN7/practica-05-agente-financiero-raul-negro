package com.practica05.init.controller;

import com.practica05.init.model.PeticionChat;
import com.practica05.init.model.RespuestaChat;
import com.practica05.init.service.AgenteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class AgenteController {

    private final AgenteService agenteService;

    @PostMapping("/chat")
    public RespuestaChat chat(@RequestBody PeticionChat peticionChat, HttpSession session) {
        if (peticionChat.mensaje() == null || peticionChat.mensaje().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mensaje no puede estar vacío");
        }

        return new RespuestaChat(agenteService.chat(peticionChat.mensaje(), session.getId()));
    }

}
