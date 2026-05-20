package com.membercard.config;

import com.membercard.dto.ErrorResponse;
import com.membercard.exception.MemberNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFoundException(
            MemberNotFoundException e
    ) {
        log.error("[API - ERROR] {}", e.getMessage(), e);

        return createErrorResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        log.error("[API - ERROR] {}", errorMessage, e);

        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e
    ) {
        log.error("[API - ERROR] 예상치 못한 서버 오류가 발생했습니다.", e);

        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "예상치 못한 서버 오류가 발생했습니다."
        );
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(
            HttpStatus status,
            String message
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                message
        );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}
