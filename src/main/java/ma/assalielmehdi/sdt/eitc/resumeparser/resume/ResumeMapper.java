package ma.assalielmehdi.sdt.eitc.resumeparser.resume;

import org.springframework.stereotype.Component;

@Component
public class ResumeMapper {
  public ResumeDto toDto(Resume resume) {
    return new ResumeDto(resume.getId(), resume.getState());
  }
}
