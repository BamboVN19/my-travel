package com.mytravel.api.exception;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mytravel.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        lenient().when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    @DisplayName("handleApiException - Should return correct status and message")
    void handleApiException_ReturnsStatusAndBody() {
        ApiException ex = new BadRequestException("Invalid input data");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleApiException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("BAD_REQUEST", response.getBody().getError());
        assertEquals("Invalid input data", response.getBody().getMessage());
        assertEquals("/api/v1/test", response.getBody().getPath());
    }

    @Test
    @DisplayName("Test all Custom Exceptions constructors")
    void testCustomExceptions() {
        ResourceNotFoundException notFound = new ResourceNotFoundException("Not found resource");
        assertEquals(HttpStatus.NOT_FOUND, notFound.getStatus());
        assertEquals("Not found resource", notFound.getMessage());

        UnauthorizedException unauthorized = new UnauthorizedException("Unauthorized access");
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorized.getStatus());
        assertEquals("Unauthorized access", unauthorized.getMessage());

        ForbiddenException forbidden = new ForbiddenException("Forbidden access");
        assertEquals(HttpStatus.FORBIDDEN, forbidden.getStatus());
        assertEquals("Forbidden access", forbidden.getMessage());

        DuplicateResourceException duplicate = new DuplicateResourceException("Duplicate key");
        assertEquals(HttpStatus.CONFLICT, duplicate.getStatus());
        assertEquals("Duplicate key", duplicate.getMessage());
    }

    @Test
    @DisplayName("handleValidationException - Should extract field errors properly")
    void handleValidationException_ReturnsFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("userDto", "username", "Tên đăng nhập không được trống");
        FieldError fieldError2 = new FieldError("userDto", "email", "Email sai định dạng");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        doReturn(List.of(fieldError1, fieldError2)).when(bindingResult).getAllErrors();

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dữ liệu đầu vào không hợp lệ", response.getBody().getMessage());
        assertEquals(2, response.getBody().getErrors().size());
        assertEquals("username", response.getBody().getErrors().get(0).getField());
        assertEquals("Tên đăng nhập không được trống", response.getBody().getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("handleHttpMessageNotReadableException - Standard parsing exception")
    void handleHttpMessageNotReadableException_General() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Dữ liệu JSON đầu vào không hợp lệ"));
    }

    @Test
    @DisplayName("handleHttpMessageNotReadableException - Invalid UUID format exception")
    void handleHttpMessageNotReadableException_InvalidUuid() {
        InvalidFormatException cause = new InvalidFormatException(mock(JsonParser.class), "Invalid UUID", "abc", UUID.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON", cause);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Định dạng UUID không hợp lệ"));
    }

    @Test
    @DisplayName("handleDataIntegrityViolationException - Should return 400 Bad Request")
    void handleDataIntegrityViolationException_ReturnsBadRequest() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Constraint violation");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Dữ liệu không hợp lệ"));
    }

    @Test
    @DisplayName("handleAuthenticationException - Should return 401 Unauthorized")
    void handleAuthenticationException_ReturnsUnauthorized() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthenticationException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Xác thực không thành công"));
    }

    @Test
    @DisplayName("handleAccessDeniedException - Should return 403 Forbidden")
    void handleAccessDeniedException_ReturnsForbidden() {
        AccessDeniedException ex = new AccessDeniedException("Access is denied");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccessDeniedException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bạn không có quyền thực hiện thao tác này", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleMultipartException - MultipartException and MissingServletRequestPartException")
    void handleMultipartException_ReturnsBadRequest() {
        MultipartException ex1 = new MultipartException("Current request is not a multipart request");
        ResponseEntity<ErrorResponse> response1 = globalExceptionHandler.handleMultipartException(ex1, request);

        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertTrue(response1.getBody().getMessage().contains("Lỗi định dạng upload file"));

        MissingServletRequestPartException ex2 = new MissingServletRequestPartException("file");
        ResponseEntity<ErrorResponse> response2 = globalExceptionHandler.handleMultipartException(ex2, request);

        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    @DisplayName("handleMaxUploadSizeExceeded - Should return 400 Bad Request")
    void handleMaxUploadSizeExceeded_ReturnsBadRequest() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(50000000L);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMaxUploadSizeExceeded(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Kích thước file vượt quá giới hạn cho phép", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleGeneralException - Fallback 500 Internal Server Error")
    void handleGeneralException_ReturnsInternalServerError() {
        RuntimeException ex = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!", response.getBody().getMessage());
    }
}
