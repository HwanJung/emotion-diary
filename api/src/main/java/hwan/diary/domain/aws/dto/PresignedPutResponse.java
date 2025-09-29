package hwan.diary.domain.aws.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedPutResponse {
    private String url;          // 브라우저가 직접 PUT 업로드할 URL
    private String key;          // 서버/DB에 저장할 객체 키
    private String contentType;  // 업로드 시 전송할 Content-Type(이 값과 동일해야 함)
}
