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

  private final String ollamaPrompt = """
     You are a highly skilled resume parser. Your task is to extract key information from the provided resume text. Please return the information in a JSON format with the following fields:
    
     *   `name`: The full name of the candidate.
     *   `email`: The candidate's email address.
     *   `phone`: The candidate's phone number.
     *   `linkedin`: The URL of the candidate's LinkedIn profile (if available).
     *   `github`: The URL of the candidate's GitHub profile (if available).
     *   `summary`: A brief professional summary or objective statement.
     *   `experience`: An array of objects, where each object represents a work experience entry. Each experience object should have the following fields:
         *   `title`: The job title.
         *   `company`: The name of the company.
         *   `start_date`: The start date of employment (YYYY-MM or YYYY format if month is not available).
         *   `end_date`: The end date of employment (YYYY-MM or YYYY format or "Present").
         *   `description`: A brief description of the responsibilities and accomplishments.
     *   `education`: An array of objects, where each object represents an education entry. Each education object should have the following fields:
         *   `degree`: The degree earned.
         *   `major`: The major or field of study.
         *   `university`: The name of the university or institution.
         *   `graduation_date`: The graduation date (YYYY-MM or YYYY format).
     *   `skills`: An array of strings representing the candidate's skills.
    
     If a field is not found in the resume text, leave the corresponding JSON field as an empty string (`""`) or an empty array (`[]`). Do not make up information.
    
     Ensure the JSON is valid and parsable. Do not include any explanatory text outside the JSON structure.
    
     Here is the resume text:
    
     %s
    """;

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
