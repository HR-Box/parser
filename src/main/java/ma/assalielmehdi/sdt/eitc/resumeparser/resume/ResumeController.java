package ma.assalielmehdi.sdt.eitc.resumeparser.resume;

import ma.assalielmehdi.sdt.eitc.resumeparser.exceptions.InternalServerErrorException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/resume")
public class ResumeController {
  private final ResumeService resumeService;
  private final ResumeMapper resumeMapper;

  public ResumeController(ResumeService resumeService, ResumeMapper resumeMapper) {
    this.resumeService = resumeService;
    this.resumeMapper = resumeMapper;
  }

  @GetMapping
  public List<ResumeDto> findAll() {
    return resumeService.findAll().stream().map(resumeMapper::toDto).collect(Collectors.toList());
  }

  @PostMapping("/parse")
  public ResumeDto parse(@RequestParam("pdf") MultipartFile pdf) {
    try {
      return resumeMapper.toDto(resumeService.parsePdf(pdf.getBytes()));
    } catch (IOException e) {
      throw new InternalServerErrorException(e);
    }
  }
}
