package com.test.library_loan.common.handler;

import com.test.library_loan.common.utils.ResponseUtils;
import com.test.library_loan.common.exception.BusinessException;
import com.test.library_loan.common.exception.ResourceNotFoundException;
import com.test.library_loan.common.model.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<Response<Object>> handleResourceNotFound(ResourceNotFoundException resourceNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtils.toResponse(resourceNotFoundException.getMessage(), null));
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<Response<Object>> handleBusiness(BusinessException businessException){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtils.toResponse(businessException.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Response<Object>> handleGeneral(Exception exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtils.toResponse(exception.getMessage(), null));
    }
}
