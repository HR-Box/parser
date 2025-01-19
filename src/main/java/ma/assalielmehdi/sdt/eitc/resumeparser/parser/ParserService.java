package ma.assalielmehdi.sdt.eitc.resumeparser.parser;

import ma.assalielmehdi.sdt.eitc.resumeparser.exceptions.InternalServerErrorException;
import ma.assalielmehdi.sdt.eitc.resumeparser.ocr.OCR;
import ma.assalielmehdi.sdt.eitc.resumeparser.ocr.OCRException;
import ma.assalielmehdi.sdt.eitc.resumeparser.ollama.Ollama;
import ma.assalielmehdi.sdt.eitc.resumeparser.ollama.OllamaException;
import ma.assalielmehdi.sdt.eitc.resumeparser.resume.Resume;
import ma.assalielmehdi.sdt.eitc.resumeparser.resume.ResumeService;
import org.springframework.stereotype.Service;

@Service
public class ParserService {
  private final ResumeService resumeService;
  private final OCR ocr;
  private final Ollama ollama;

  private final String ollamaPrompt = "%s";

  public ParserService(OCR ocr, ResumeService resumeService, Ollama ollama) {
    this.ocr = ocr;
    this.resumeService = resumeService;
    this.ollama = ollama;
  }

  public void parseResume(Long resumeId) {
    try (var resumePdfInputStream = resumeService.readPdf(resumeId)) {
      // OCR
      resumeService.setState(resumeId, Resume.State.OCR_IN_PROGRESS);
      var resumeText = ocr.pdfToText(resumePdfInputStream.readAllBytes());
      resumeService.setOcrResult(resumeId, resumeText);

      // Ollama
      resumeService.setState(resumeId, Resume.State.PARSE_IN_PROGRESS);
      var parseResult = ollama.chat(String.format(ollamaPrompt, resumeText));
      resumeService.setParseResult(resumeId, parseResult);
      resumeService.setState(resumeId, Resume.State.DONE);
    } catch (Exception e) {
      if (e instanceof OCRException) {
        resumeService.setState(resumeId, Resume.State.OCR_FAILED);
      }

      if (e instanceof OllamaException) {
        resumeService.setState(resumeId, Resume.State.PARSE_FAILED);
      }

      throw new InternalServerErrorException(e);
    }
  }
}
