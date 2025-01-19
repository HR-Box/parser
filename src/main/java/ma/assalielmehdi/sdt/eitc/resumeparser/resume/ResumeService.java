package ma.assalielmehdi.sdt.eitc.resumeparser.resume;

import ma.assalielmehdi.sdt.eitc.resumeparser.exceptions.InternalServerErrorException;
import ma.assalielmehdi.sdt.eitc.resumeparser.minio.MinioException;
import ma.assalielmehdi.sdt.eitc.resumeparser.minio.MinioService;
import ma.assalielmehdi.sdt.eitc.resumeparser.ocr.OCRException;
import ma.assalielmehdi.sdt.eitc.resumeparser.ocr.OCRService;
import ma.assalielmehdi.sdt.eitc.resumeparser.parser.ParserException;
import ma.assalielmehdi.sdt.eitc.resumeparser.parser.ParserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ResumeService {
  private final ResumeRepository resumeRepository;
  private final OCRService ocrService;
  private final ParserService parserService;
  private final String resumeBucket;
  private final MinioService minioService;

  public ResumeService(
    ResumeRepository resumeRepository,
    OCRService ocrService,
    ParserService parserService,
    @Value("${resume.bucket}") String resumeBucket,
    MinioService minioService) {
    this.resumeRepository = resumeRepository;
    this.ocrService = ocrService;
    this.parserService = parserService;
    this.resumeBucket = resumeBucket;
    this.minioService = minioService;
  }

  public Resume parsePdf(byte[] pdfBytes) {
    var resume = save(pdfBytes);

    CompletableFuture.runAsync(() -> {
      try {
        // OCR
        var resumeText = ocrService.pdfToText(pdfBytes);
        resume.setOcrResult(resumeText);

        // Parser
        var parseResult = parserService.parseText(resumeText);
        resume.setParseResult(parseResult);

        resume.setState(Resume.State.DONE);
      } catch (Exception e) {
        if (e instanceof OCRException) {
          resume.setState(Resume.State.OCR_FAILED);
        }

        if (e instanceof ParserException) {
          resume.setState(Resume.State.PARSE_FAILED);
        }

        throw new InternalServerErrorException(e);
      } finally {
        resumeRepository.save(resume);
      }
    });

    return resume;
  }

  public List<Resume> findAll() {
    return resumeRepository.findAll();
  }

  private Resume save(byte[] pdfBytes) {
    var resume = new Resume();
    resume.setState(Resume.State.IN_PROGRESS);
    resume = resumeRepository.save(resume);

    try {
      minioService.putObject(resumeBucket, resume.getId() + ".pdf", pdfBytes);
    } catch (MinioException e) {
      resumeRepository.delete(resume);

      throw new InternalServerErrorException(e);
    }

    return resume;
  }
}
