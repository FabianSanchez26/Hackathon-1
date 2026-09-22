package com.tuckersoft.branchengine.common;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@RestControllerAdvice
public class ExceptionHandlerAdvice {
 @ExceptionHandler(ApiException.class)
 public ResponseEntity<ApiError> business(ApiException e, HttpServletRequest request) {
  return response(e.status, e.getMessage(), request);
 }
 @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
 public ResponseEntity<ApiError> invalid(Exception e, HttpServletRequest request) {
  return response(HttpStatus.BAD_REQUEST, "Datos de solicitud invalidos", request);
 }
 @ExceptionHandler(DataIntegrityViolationException.class)
 public ResponseEntity<ApiError> conflict(Exception e, HttpServletRequest request) {
  return response(HttpStatus.CONFLICT, "El recurso ya existe o incumple una restriccion", request);
 }
 private ResponseEntity<ApiError> response(HttpStatus status, String message, HttpServletRequest request) {
  return ResponseEntity.status(status).body(new ApiError(status.name(), message, java.time.Instant.now(), request.getRequestURI()));
 }
}
