package com.mytravel.api.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mytravel.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
    log.error("API Exception [{}]: {}", ex.getStatus(), ex.getMessage());
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(ex.getStatus().value())
        .error(ex.getStatus().name())
        .message(ex.getMessage())
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(errorResponse, ex.getStatus());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<ErrorResponse.FieldErrorDetail> fieldErrors = ex.getBindingResult().getAllErrors().stream()
        .map(error -> {
          String fieldName = ((FieldError) error).getField();
          String errorMessage = error.getDefaultMessage();
          return new ErrorResponse.FieldErrorDetail(fieldName, errorMessage);
        })
        .collect(Collectors.toList());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.name())
        .message("Dữ liệu đầu vào không hợp lệ")
        .errors(fieldErrors)
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
    log.error("JSON parse error: {}", ex.getMessage());
    String message = "Dữ liệu JSON đầu vào không hợp lệ hoặc sai định dạng.";
    if (ex.getCause() instanceof InvalidFormatException ife) {
      if (ife.getTargetType() != null && ife.getTargetType().equals(UUID.class)) {
        message = "Định dạng UUID không hợp lệ. UUID phải có dạng chuẩn 36 ký tự (ví dụ: 123e4567-e89b-12d3-a456-426614174000).";
      }
    }
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.name())
        .message(message)
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
    log.error("Data Integrity Violation: ", ex);
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.name())
        .message("Dữ liệu không hợp lệ hoặc vượt quá giới hạn độ dài cho phép của hệ thống!")
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.UNAUTHORIZED.value())
        .error(HttpStatus.UNAUTHORIZED.name())
        .message("Xác thực không thành công: " + ex.getMessage())
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.FORBIDDEN.value())
        .error(HttpStatus.FORBIDDEN.name())
        .message("Bạn không có quyền thực hiện thao tác này")
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({MultipartException.class, MissingServletRequestPartException.class})
  public ResponseEntity<ErrorResponse> handleMultipartException(Exception ex, HttpServletRequest request) {
    log.error("Multipart Exception: ", ex);
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.name())
        .message("Lỗi định dạng upload file hoặc thiếu file đính kèm: " + ex.getMessage())
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, HttpServletRequest request) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.name())
        .message("Kích thước file vượt quá giới hạn cho phép")
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
    log.error("Unhandled Exception: ", ex);
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
        .message("Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!")
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
