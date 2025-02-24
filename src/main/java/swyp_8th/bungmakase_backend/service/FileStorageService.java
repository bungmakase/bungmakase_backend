package swyp_8th.bungmakase_backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final AmazonS3 amazonS3;

    @Value("${ncp.s3.bucket}")
    private String bucket;

    @Value("${ncp.s3.endpoint}")
    private String endpoint;

    // 파일 업로드 및 URL 반환
    public String uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(
                    new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                            .withCannedAcl(CannedAccessControlList.PublicRead) // 퍼블릭 읽기 권한 부여
            );

            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            // 파일 이름 추출
            String fileKey = extractFileKey(fileUrl);
            log.info("Deleting file with key: {}", fileKey);  // 파일 키 확인

            // 파일 삭제 요청
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileKey));

            log.info("파일 삭제 성공: {}", fileUrl);
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", fileUrl, e);
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.");
        }
    }

    /**
     * 파일 URL에서 S3 Object Key 추출
     */
    private String extractFileKey(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String encodedPath = url.getPath().substring(1);  // leading '/' 제거
            String decodedPath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.name());  // URL 디코딩
            return decodedPath;
        } catch (Exception e) {
            throw new RuntimeException("파일 키 추출 실패: " + fileUrl, e);
        }
    }
}
