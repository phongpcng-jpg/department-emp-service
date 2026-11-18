package ra.departmentempservice.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ra.departmentempservice.dto.response.ApiResponse;
import ra.departmentempservice.dto.response.ErrorResponse;
import ra.departmentempservice.exception.DepartmentNameExistException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiControllerAdviceTest {

    private ApiControllerAdvice advice;

    @BeforeEach
    void setUp() {
        advice = new ApiControllerAdvice();
    }

    @Test
    void handleDepartmentNameExistException_shouldReturnBadRequestResponse() {
        DepartmentNameExistException exception =
                new DepartmentNameExistException("Tên phòng ban đã tồn tại");

        LocalDateTime beforeInvocation = LocalDateTime.now();
        ResponseEntity<ApiResponse<Object>> response =
                advice.handleDepartmentNameExistException(exception);
        LocalDateTime afterInvocation = LocalDateTime.now();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals("Lỗi thêm mới phòng ban", body.getMessage());
        assertNull(body.getData());
        assertEquals(HttpStatus.BAD_REQUEST, body.getStatus());

        ErrorResponse<Object> errors = body.getErrors();
        assertNotNull(errors);
        assertEquals("Tên phòng ban đã tồn tại", errors.getError());
        assertNotNull(errors.getTimestamp());
        assertFalse(errors.getTimestamp().isBefore(beforeInvocation));
        assertFalse(errors.getTimestamp().isAfter(afterInvocation));
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturnBadRequestWithFieldErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError(
                "departmentRequest",
                "departName",
                "Không được để trống tên phòng ban"
        );
        when(exception.getFieldErrors()).thenReturn(List.of(fieldError));

        LocalDateTime beforeInvocation = LocalDateTime.now();
        ResponseEntity<ApiResponse<Object>> response =
                advice.handleMethodArgumentNotValidException(exception);
        LocalDateTime afterInvocation = LocalDateTime.now();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals("Lỗi tham số truyền", body.getMessage());
        assertNull(body.getData());
        assertEquals(HttpStatus.BAD_REQUEST, body.getStatus());

        ErrorResponse<Object> errors = body.getErrors();
        assertNotNull(errors);
        assertNotNull(errors.getTimestamp());
        assertFalse(errors.getTimestamp().isBefore(beforeInvocation));
        assertFalse(errors.getTimestamp().isAfter(afterInvocation));

        assertInstanceOf(Map.class, errors.getError());
        Map<?, ?> fieldErrors = (Map<?, ?>) errors.getError();
        assertEquals(1, fieldErrors.size());
        assertEquals("Không được để trống tên phòng ban", fieldErrors.get("departName"));
    }

    @Test
    void handleMethodArgumentNotValidException_withMultipleFieldErrors_shouldReturnAllErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getFieldErrors()).thenReturn(List.of(
                new FieldError("departmentRequest", "description", "Không được để trống mô tả"),
                new FieldError("departmentRequest", "departName", "Không được để trống tên phòng ban"),
                new FieldError("departmentRequest", "status", "Không được để trống trạng thái")
        ));

        ResponseEntity<ApiResponse<Object>> response =
                advice.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getErrors());

        Map<?, ?> fieldErrors = (Map<?, ?>) body.getErrors().getError();
        assertEquals(3, fieldErrors.size());
        assertEquals("Không được để trống tên phòng ban", fieldErrors.get("departName"));
        assertEquals("Không được để trống mô tả", fieldErrors.get("description"));
        assertEquals("Không được để trống trạng thái", fieldErrors.get("status"));
    }

    @Test
    void handleMethodArgumentNotValidException_withNoFieldErrors_shouldReturnEmptyErrorMap() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<ApiResponse<Object>> response =
                advice.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getErrors());
        assertInstanceOf(Map.class, body.getErrors().getError());
        assertTrue(((Map<?, ?>) body.getErrors().getError()).isEmpty());
    }
}
