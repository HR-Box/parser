package ma.assalielmehdi.sdt.eitc.resumeparser.resume;

import io.minio.GetObjectArgs;
import ma.assalielmehdi.sdt.eitc.resumeparser.exceptions.InternalServerErrorException;
import ma.assalielmehdi.sdt.eitc.resumeparser.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ResumeService {
  private final ResumeRepository resumeRepository;
  private final Minio minio;
  private final String resumeBucket;

  public ResumeService(ResumeRepository resumeRepository, Minio minio, @Value("${resume.bucket}") String resumeBucket) {
    this.resumeRepository = resumeRepository;
    this.minio = minio;
    this.resumeBucket = resumeBucket;
  }

  private Resume findById(Long id) {
    return resumeRepository.findById(id).orElseThrow(() -> new NotFoundException("Resume", "id=" + id));
  }

  public InputStream readPdf(Long id) {
    var resume = findById(id);

    try {
      return minio.client().getObject(GetObjectArgs.builder()
        .bucket(resumeBucket)
        .object(resume.getId().toString() + ".pdf")
        .build()
      );
    } catch (Exception e) {
      throw new InternalServerErrorException("Error reading pdf from MinIO", e);
    }
  }

  public void setState(Long id, Resume.State state) {
    var resume = findById(id);
    resume.setState(state);
    resumeRepository.save(resume);
  }

  public void setOcrResult(Long id, String ocrResult) {
    var resume = findById(id);
    resume.setOcrResult(ocrResult);
    resumeRepository.save(resume);
  }

  public void setParseResult(Long id, String parseResult) {
    var resume = findById(id);
    resume.setParseResult(parseResult);
    resumeRepository.save(resume);
  }
}
