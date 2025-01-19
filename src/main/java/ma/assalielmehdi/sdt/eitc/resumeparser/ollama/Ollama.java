package ma.assalielmehdi.sdt.eitc.resumeparser.ollama;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class Ollama {
  private final OllamaChatModel chatModel;

  public Ollama(OllamaChatModel chatModel) {
    this.chatModel = chatModel;
  }

  public String chat(String prompt) {
    return this.chatModel.call(prompt);
  }
}
