package ma.assalielmehdi.sdt.eitc.resumeparser.resume;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Minio {
  private final MinioClient minioClient;

  public Minio(
    @Value("${minio.endpoint}") String endpoint,
    @Value("${minio.access-key}") String accessKey,
    @Value("${minio.secret-key}") String secretKey
  ) {
    this.minioClient = MinioClient.builder()
      .endpoint(endpoint)
      .credentials(accessKey, secretKey)
      .build();
  }

  public MinioClient client() {
    return minioClient;
  }
}
