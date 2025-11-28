package com.sergipeTec.gerenciador_de_veiculos.rest.exception;

import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 – Erro de validação/regra de entrada
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return buildErrorResponse(ex, status, request);
    }

    // 404 – Veículo não encontrado
    @ExceptionHandler(VeiculoNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleVeiculoNaoEncontrado(
            VeiculoNaoEncontradoException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildErrorResponse(ex, status, request);
    }

    // 500 – Qualquer outra exceção não tratada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildErrorResponse(ex, status, request);
    }

    private ResponseEntity<ApiError> buildErrorResponse(
            Exception ex,
            HttpStatus status,
            HttpServletRequest request) {

        ApiError body = ApiError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
