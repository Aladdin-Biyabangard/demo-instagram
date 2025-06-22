package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.model.dto.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileLoadService {

    FileUploadResponse uploadFile(MultipartFile multipartFile, String id) throws IOException;

    String getFileUrl(String key);

    void deleteFileFromAws(String key);



}
