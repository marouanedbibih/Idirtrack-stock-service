package com.idirtrack.stock_service.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.idirtrack.stock_service.basics.BasicError;
import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.utils.ValidationUtils;

import java.util.List;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BasicResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<BasicError> errors = ValidationUtils.extractErrorsFromBindingResult(ex.getBindingResult());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BasicResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .messageType(MessageType.ERROR)
                        .errors(errors)
                        .build());
    }

    /**
     * Handle all exceptions
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicResponse> handleAllExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BasicResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .messageType(MessageType.ERROR)
                        .message(ex.getMessage())
                        .build());
    }

    /**
     * Handle BasicException exceptions
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(BasicException.class)
    public ResponseEntity<BasicResponse> handleBasicExceptions(BasicException ex) {
        return ResponseEntity
                .status(ex.getResponse().getStatus())
                .body(ex.getResponse());
    }
}