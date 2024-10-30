package net.casim.ml.mm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class FileUploadService {

    private static final String UPLOAD_DIR = "D:\\projects\\ML-Model-Management\\uploads";

    public File saveFile(MultipartFile file) throws IOException {
        log.debug("Saving uploaded file: {}", file.getOriginalFilename());

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean dirCreated = uploadDir.mkdirs();
            log.debug("Upload directory created: {}", dirCreated);
        }

        File savedFile = new File(uploadDir, Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(savedFile);

        log.info("File '{}' saved successfully at '{}'", file.getOriginalFilename(), savedFile.getAbsolutePath());
        return savedFile;
    }

}
