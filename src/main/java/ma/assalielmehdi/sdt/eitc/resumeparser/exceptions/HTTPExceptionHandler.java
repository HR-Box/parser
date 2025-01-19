package ma.assalielmehdi.sdt.eitc.resumeparser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class HTTPExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
    return new ResponseEntity<>(Map.of("success", false, "message", ex.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
    return new ResponseEntity<>(Map.of("success", false, "message", ex.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InternalServerErrorException.class)
  public ResponseEntity<Map<String, Object>> handleInternalServerErrorException(InternalServerErrorException ex) {
    return new ResponseEntity<>(Map.of("success", false, "message", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

