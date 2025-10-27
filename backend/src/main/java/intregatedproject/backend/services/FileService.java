package intregatedproject.backend.services;

import intregatedproject.backend.utils.FileStorageProperties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Getter
public class FileService {
    private final Path fileStorageLocation;
    private final FileStorageProperties fileStorageProperties;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            if (!Files.exists(this.fileStorageLocation)) {
                Files.createDirectories(this.fileStorageLocation);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Can’t create the directory where the uploaded files will be stored.", ex);
        }
    }


    public String store(MultipartFile file) {
        if(!isSupportedContentType(file)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Does not support content type: " + file.getContentType());
        }

        // ดึงนามสกุลไฟล์
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if(dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }

        String randomFileName = UUID.randomUUID().toString() + extension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(randomFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return randomFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }


    public List<String> store(List<MultipartFile> files) {
        List<String> fileNames = new ArrayList<>(files.size());
        files.forEach(file   -> fileNames.add(store(file)));
        return fileNames;
    }

    public List<String> multiStore(List<MultipartFile> files) {
        List<String> fileNames = new ArrayList<>(files.size());

        for (MultipartFile file : files) {
            if (!isSupportedContentType(file)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Does not support content type: " + file.getContentType());
            }

            String original = StringUtils.cleanPath(file.getOriginalFilename());
            if (original.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + original);
            }

            // แยกนามสกุลไฟล์ (ถ้ามี)
            int dotIndex = original.lastIndexOf('.');
            String dot = (dotIndex >= 0) ? original.substring(dotIndex) : "";

            // ตั้งชื่อใหม่เสมอ = UUID + ext
            String finalName = UUID.randomUUID().toString() + dot;

            try {
                Files.createDirectories(this.fileStorageLocation);
                Path targetLocation = this.fileStorageLocation.resolve(finalName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                fileNames.add(finalName); // คืนชื่อไฟล์ที่เก็บจริง
            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + finalName + ". Please try again!", ex);
            }
        }

        return fileNames;
    }


    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File operation error: "
                    + fileName, ex);
        }
    }
    public String getFileType(Resource resource) {
        try {
            String type = Files.probeContentType(resource.getFile().toPath());
            return type==null?"application/text":type;
        } catch (IOException ex) {
            throw new RuntimeException("ProbeContentType error: " + resource,ex);
        }
    }
    public void removeFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new ResourceNotFoundException("File not found " + fileName);
            }
        } catch (IOException ex) {
            throw new RuntimeException("File operation (DELETE) error: " + fileName, ex);
        }
    }

    private boolean isSupportedContentType(MultipartFile file) {
        String contentType = file.getContentType();
        List<String> supportFileTypes = Arrays.stream(fileStorageProperties.getSupportFileTypes()).toList();
        return  supportFileTypes.contains(contentType);
    }

}