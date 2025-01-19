package ma.assalielmehdi.sdt.eitc.resumeparser.resume;

import jakarta.persistence.*;

@Entity
public class Resume {
  public enum State {
    IN_PROGRESS,
    OCR_FAILED, PARSE_FAILED,
    DONE
  }

  @Id
  @GeneratedValue
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String ocrResult;

  @Column(columnDefinition = "TEXT")
  private String parseResult;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private State state;

  public Long getId() {
    return id;
  }

  public String getOcrResult() {
    return ocrResult;
  }

  public String getParseResult() {
    return parseResult;
  }

  public State getState() {
    return state;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setOcrResult(String ocrResult) {
    this.ocrResult = ocrResult;
  }

  public void setParseResult(String parseResult) {
    this.parseResult = parseResult;
  }

  public void setState(State state) {
    this.state = state;
  }
}
