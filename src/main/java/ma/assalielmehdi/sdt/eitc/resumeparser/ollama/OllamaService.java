package ma.assalielmehdi.sdt.eitc.resumeparser.ollama;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {
  private final OllamaChatModel chatModel;

  public OllamaService(OllamaChatModel chatModel) {
    this.chatModel = chatModel;
  }

  public String chat(String prompt) {
    return this.chatModel.call(prompt);
  }
}
