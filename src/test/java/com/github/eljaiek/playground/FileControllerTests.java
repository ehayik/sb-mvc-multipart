package com.github.eljaiek.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FilesController.class)
class FileControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void uploadShouldReturnMetadataName() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt",
                TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

        MockMultipartFile metadata =
                new MockMultipartFile(
                        "metadata",
                        "metadata",
                        APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsString(new FileMetadata("helloworld")).getBytes(UTF_8));

        // Then
        mockMvc.perform(multipart("/files")
                .file(file).file(metadata))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("helloworld"));
    }

    @Test
    void uploadShouldReturnFilenameWhenMetdataIsNull() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt",
                TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

        // Then
        mockMvc.perform(multipart("/files")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("file"));
    }
}
