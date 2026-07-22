package br.com.techchallenge.techchallenge.controllers.handlers;

import br.com.techchallenge.techchallenge.dtos.ResourceNotFoundDTO;
import br.com.techchallenge.techchallenge.dtos.ValidationErrorDTO;
import br.com.techchallenge.techchallenge.services.exceptions.ResourceNotFoundException;
import jakarta.el.MethodNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResourceNotFoundDTO> handlerResourceNotFoundException(ResourceNotFoundException e){
        var status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status.value()).body(new ResourceNotFoundDTO(e.getMessage(), status.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDTO> handlerMethodNotValidException(MethodArgumentNotValidException e){
        var status = HttpStatus.BAD_REQUEST;
        List<String> errors = new ArrayList<String>();
        for (var error: e.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        return ResponseEntity.status(status.value()).body(new ValidationErrorDTO(errors, status.value()));
    }

}
