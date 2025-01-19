package ma.assalielmehdi.sdt.eitc.resumeparser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {
  public InternalServerErrorException(Throwable cause) {
    super(cause);
  }

  public InternalServerErrorException(String message, Throwable cause) {
    super(message + ": " + cause.getMessage());
  }
}
