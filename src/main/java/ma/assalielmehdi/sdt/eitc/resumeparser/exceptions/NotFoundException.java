package ma.assalielmehdi.sdt.eitc.resumeparser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
  public NotFoundException(String resource, String searchCriteria) {
    super(String.format("Resource [%s] not found with search criteria [%s]", resource, searchCriteria));
  }
}
