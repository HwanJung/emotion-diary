package hwan.diary.domain.aws.controller;

import hwan.diary.domain.aws.dto.PresignedPutResponse;
import hwan.diary.domain.aws.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping("/put-url")
    public PresignedPutResponse getPutUrl(@RequestBody Map<String, String> req) {
        String fileName = req.getOrDefault("fileName", "noname");
        String contentType = req.getOrDefault("contentType", "application/octet-stream");
        // contentType 검증
        if (!contentType.startsWith("image/") && !contentType.equals("application/octet-stream")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsupported contentType"); //400
        }
        return s3Service.generatePresignedPutUrl(fileName, contentType);
    }

    @PostMapping("/get-url")
    public Map<String, String> getGetUrl(@RequestBody Map<String, String> req) {
        String key = req.get("key");
        if (key == null || key.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "key required"); //400
        }
        if (!key.startsWith("uploads/")) { // S3Service.prefix와 동일하게
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid key"); //400
        }
        String url = s3Service.createGetUrl(key);
        return Map.of("url", url);
    }
}
