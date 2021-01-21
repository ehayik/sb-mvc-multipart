package com.github.eljaiek.playground;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

@Slf4j
@RestController
@RequestMapping("/files")
class FilesController {

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileMetadata> upload(@RequestPart(value = "file") MultipartFile file,
                                               @RequestPart(value = "metadata", required = false) FileMetadata metadata) {

        if (metadata == null) {
            metadata = new FileMetadata(file.getName());
        }

        log.info("Filename: {}", metadata.name);
        metadata.contentType = file.getContentType();
        metadata.length = file.getSize();
        return ResponseEntity.ok(metadata);
    }
}

@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = ANY)
class FileMetadata {
    String name;
    String contentType;
    long length;

    public FileMetadata(String name) {
        this.name = name;
    }
}