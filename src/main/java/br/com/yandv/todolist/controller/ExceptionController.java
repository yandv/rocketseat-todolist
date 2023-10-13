package br.com.yandv.todolist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.yandv.todolist.utils.JsonBuilder;

@ControllerAdvice
public class ExceptionController {
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(new JsonBuilder()
                .addProperty("errorMessage", exception.getMostSpecificCause().getMessage())
                .addProperty("error", exception.getMostSpecificCause().getClass().getSimpleName())
                .toString());
    }

}
