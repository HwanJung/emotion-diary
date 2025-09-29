package hwan.diary.domain.aws.service;

import hwan.diary.domain.aws.dto.PresignedPutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.util.UUID;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${app.s3.bucket}") String bucket;
    @Value("${app.s3.prefix}") String prefix;
    @Value("${app.s3.put-exp-min}") long putExpMin;
    @Value("${app.s3.get-exp-min}") long getExpMin;

    public PresignedPutResponse generatePresignedPutUrl(String fileName, String contentType) {
        // 키 규칙: 접두어 + UUID + 원본 파일명(충돌 방지/운영 편의)
        String key = prefix + UUID.randomUUID() + "-" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest preq = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(putExpMin))
                .putObjectRequest(putObjectRequest)
                .build();

        URL url = s3Presigner.presignPutObject(preq).url();

        return new PresignedPutResponse(url.toString(), key, contentType);
    }

    // 이미 DB에 저장된 key(객체 경로)를 받아 URL만 발급해 준다.
    public String createGetUrl(String key) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest preq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(getExpMin)) // UX 고려해 15~60분 등
                .getObjectRequest(getReq)
                .build();

        return s3Presigner.presignGetObject(preq).url().toString();
    }
}
