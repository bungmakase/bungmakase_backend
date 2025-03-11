package swyp_8th.bungmakase_backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

        try {
            String fileName = file.getOriginalFilename();
            long fileSize = file.getSize(); // 파일 크기 확인
            InputStream inputStream = file.getInputStream();

            // 파일 크기가 5MB 이하일 경우, 일반 업로드로 처리
            if (fileSize <= 5 * 1024 * 1024) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(fileSize);
                metadata.setContentType(file.getContentType());

                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead)); // 퍼블릭 읽기 권한 부여

                return amazonS3.getUrl(bucket, fileName).toString();
            }

            // 멀티파트 업로드 시작
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());

            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucket, fileName)
                    .withObjectMetadata(metadata);
            InitiateMultipartUploadResult initResponse = amazonS3.initiateMultipartUpload(initRequest);

            byte[] buffer = new byte[5 * 1024 * 1024]; // 5MB 버퍼
            List<PartETag> partETags = new ArrayList<>();
            int partNumber = 1;

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucket)
                        .withKey(fileName)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(partNumber++)
                        .withInputStream(new ByteArrayInputStream(buffer, 0, bytesRead))
                        .withPartSize(bytesRead);

                partETags.add(amazonS3.uploadPart(uploadRequest).getPartETag());
            }

            // 업로드 완료 요청
            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(
                    bucket, fileName, initResponse.getUploadId(), partETags);
            amazonS3.completeMultipartUpload(completeRequest);

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
