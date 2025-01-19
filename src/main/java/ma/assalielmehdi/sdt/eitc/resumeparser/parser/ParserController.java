package ma.assalielmehdi.sdt.eitc.resumeparser.parser;

import ma.assalielmehdi.sdt.eitc.resumeparser.exceptions.BadRequestException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parser")
public class ParserController {
  private final ParserService parserService;

  public ParserController(ParserService parserService) {
    this.parserService = parserService;
  }

  @PostMapping("/resumes/{id}")
  public void parseResume(@PathVariable Long id) {
    if (id == null) {
      throw new BadRequestException("id is required and cannot be null.");
    }

    parserService.parseResume(id);
  }
}
