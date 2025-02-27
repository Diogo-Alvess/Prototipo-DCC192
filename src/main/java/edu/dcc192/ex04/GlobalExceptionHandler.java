package edu.dcc192.ex04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception e, Model model) {
        logger.error("Erro capturado: ", e);
        model.addAttribute("error", e.getMessage());
        return "error"; // Nome da página no diretório "templates"
    }
}