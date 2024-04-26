package com.campusconnect.backend.config.aws;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // MultiPartFile을 전달받아 File로 변환 후 S3에 업로드한다.
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

        return upload(uploadFile, dirName);
    }

    private String upload(File uploadFile, String dirName) {
        String originalFileName = uploadFile.getName();
        String fileName = dirName + "/" + "-" + UUID.randomUUID() + "-" +  originalFileName;
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // S3 bucket 내부 user/ 디렉토리에 있는 이미지 삭제 (회원 탈퇴)
    public void deleteToUserProfileImage(String fileName) {
        String key = "user/" + fileName; // board 디렉토리에 있는 파일의 키 생성
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, key));
        log.info("파일이 삭제되었습니다.: {}", fileName);
    }

    // S3 bucket 내부 board/ 디렉토리에 있는 이미지 삭제 (게시글 삭제)
    public void deleteToBoardImage(String fileName) {
        String key = "board/" + fileName; // board 디렉토리에 있는 파일의 키 생성
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, key));
        log.info("파일이 삭제되었습니다.: {}", fileName);
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 않았습니다.");
        }
    }

    private Optional<File> convert(MultipartFile multipartFile) {
        try {
            // 업로드된 파일의 null
            if (multipartFile == null) {
                return Optional.empty();
            }

            // 원본 파일 이름에서 확장자 추출
            String originalFilename = multipartFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));

            // 업로드된 파일의 데이터를 읽어와서 File 객체로 변환
            File convertFile = new File(originalFilename);
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
