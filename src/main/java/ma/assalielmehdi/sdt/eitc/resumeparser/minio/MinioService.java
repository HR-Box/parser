package ma.assalielmehdi.sdt.eitc.resumeparser.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class MinioService {
  private final MinioClient client;

  public MinioService(
    @Value("${minio.endpoint}") String endpoint,
    @Value("${minio.access-key}") String accessKey,
    @Value("${minio.secret-key}") String secretKey
  ) {
    this.client = MinioClient.builder()
      .endpoint(endpoint)
      .credentials(accessKey, secretKey)
      .build();
  }

  public void putObject(String bucketName, String objectName, byte[] bytes) {
    try {
      client.putObject(PutObjectArgs.builder()
        .bucket(bucketName)
        .object(objectName)
        .contentType("application/pdf")
        .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
        .build()
      );
    } catch (Exception e) {
      throw new MinioException(e);
    }
  }
}
