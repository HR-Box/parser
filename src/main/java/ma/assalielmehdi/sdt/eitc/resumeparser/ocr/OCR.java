package ma.assalielmehdi.sdt.eitc.resumeparser.ocr;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
public class OCR {
  private final EurekaClient eurekaClient;
  private final String serviceName;

  public OCR(EurekaClient eurekaClient, @Value("${ocr.service.name}") String serviceName) {
    this.eurekaClient = eurekaClient;
    this.serviceName = serviceName;
  }

  private String getInstanceUrl() {
    var instances = eurekaClient.getApplication(serviceName).getInstances();
    var randomInstance = instances.get(new Random().nextInt(instances.size()));

    return "http://" + randomInstance.getHostName() + ":" + randomInstance.getPort();
  }

  public String pdfToText(byte[] pdfBytes) {
    try {
      var restTemplate = new RestTemplate();

      var headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

      var body = new ByteArrayResource(pdfBytes);

      var request = new HttpEntity<>(body, headers);

      var url = getInstanceUrl() + "/api/v1/ocr/pdf";
      var response = restTemplate.postForEntity(url, request, String.class);

      if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
        throw new OCRException("OCR Error: Empty response");
      }

      return response.getBody();
    } catch (RestClientException e) {
      throw new OCRException("OCR Error: " + e.getMessage() );
    }
  }
}
