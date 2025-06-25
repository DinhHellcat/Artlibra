package org.herukyatto.artlibra.backend.exception;

import org.herukyatto.artlibra.backend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // Đánh dấu đây là một bộ xử lý exception toàn cục
public class GlobalExceptionHandler {

    // Phương thức này sẽ được gọi mỗi khi có ResourceNotFoundException xảy ra
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Sau này chúng ta có thể thêm các @ExceptionHandler khác cho các loại lỗi khác ở đây
}