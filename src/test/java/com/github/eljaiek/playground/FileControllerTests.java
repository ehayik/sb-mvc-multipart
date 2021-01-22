package com.github.eljaiek.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class FileControllerTests {

    MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

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
                        new ObjectMapper().writeValueAsString(new FileMetadata("helloworld")).getBytes(UTF_8));

        // Then
        mockMvc.perform(multipart("/files")
                .file(file).file(metadata))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("helloworld"))
                .andDo(document("upload-with-metadata", requestPartBody("metadata")));
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
                .andExpect(jsonPath("$.name").value("file"))
                .andDo(document("upload-without-metadata"));;
    }
}
