package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.model.dto.response.FileUploadResponse;
import az.aladdin.blogplatform.services.abstraction.FileLoadService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class FileLoadServiceImpl implements FileLoadService {

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET_NAME;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public FileUploadResponse uploadFile(MultipartFile multipartFile, String id) throws IOException {
        String key = createKey(id, multipartFile);
        createAwsObject(multipartFile, key);
        return new FileUploadResponse(key, getFileUrl(key));
    }

    public String getFileUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    public void deleteFileFromAws(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    public void createAwsObject(MultipartFile multipartFile, String key) throws IOException {
        if (!isValidMediaFile(multipartFile)) {
            throw new InvalidContentTypeException("Incompatible media file type");
        }
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .contentType(multipartFile.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
    }

    public boolean isValidMediaFile(MultipartFile file) {
        String contentType = file.getContentType();

        List<String> allowedMimeTypes = List.of(
                "image/jpeg", "image/png", "image/gif",
                "video/mp4", "video/quicktime", "video/x-msvideo", "video/x-matroska"
        );

        return contentType != null && allowedMimeTypes.contains(contentType);
    }

    public String createKey(String id, MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        String extensionFromContentType = getExtensionFromContentType(Objects.requireNonNull(contentType));
        return id + "_" + extensionFromContentType;
    }

    public String getExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case "audio/mpeg" -> ".mp3";
            case "audio/wav" -> ".wav";
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "video/mp4" -> ".mp4";
            default -> "";
        };
    }


}
