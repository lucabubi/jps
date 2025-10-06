package it.polito.wa2.g19.document_store
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Declare test instance lifecycle
class DocumentStoreApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private lateinit var testFile: MockMultipartFile

    @BeforeAll
    fun setup() {
        testFile = MockMultipartFile("file", "test.txt", "text/plain", "test".toByteArray())
        mockMvc.multipart("/API/documents/") {
            file(testFile)
        }.andExpect { status { isOk() } }
    }

    @Test
    fun contextLoads() {
    }

    @Test
    fun testGetDocuments_status200() {
        mockMvc.get("/API/documents/")
            .andExpect { status { isOk() } }
    }

    @Test
    fun testGetDocumentMetadata_status200() {
        mockMvc.get("/API/documents/1")
            .andExpect { status { isOk() } }
    }

    @Test
    fun testGetDocumentMetadata_status400() {
        mockMvc.get("/API/documents/25")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun testGetDocumentContent_status200() {
        mockMvc.get("/API/documents/1/data")
            .andExpect { status { isOk() } }
    }

    @Test
    fun testGetDocumentContent_status400() {
        mockMvc.get("/API/documents/25/data")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun testUploadDocument_status200(){
        mockMvc.multipart("/API/documents/") {
            file(MockMultipartFile("file", "test2.txt", "text/plain", "test2".toByteArray()))
        }.andExpect { status { isOk() } }
    }

    @Test
    fun testUploadDocument_status409() {
        mockMvc.multipart("/API/documents/") {
            file(MockMultipartFile("file", "test.txt", "text/plain", "test".toByteArray()))
        }.andExpect { status { isConflict() } }
    }

    @Test
    fun testUpdateDocument_status200(){
        val file = MockMultipartFile("file", "test.txt", "text/plain", "test11".toByteArray())
        mockMvc.perform(multipart("/API/documents/1")
            .file("file", file.bytes)
            .with { request ->
                request.method = "PUT"
                request
            }
            .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect( status().isOk())
    }

    @Test
    fun testUpdateDocument_status400(){
        val file = MockMultipartFile("file", "test.txt", "text/plain", "test".toByteArray())
        mockMvc.perform(multipart("/API/documents/25")
            .file("file", file.bytes)
            .with { request ->
                request.method = "PUT"
                request
            }
            .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect( status().isNotFound())
    }

    @Test
    fun testUpdateDocument_status409(){
        val file = MockMultipartFile("file", "test.txt", "text/plain", "test".toByteArray())
        mockMvc.perform(multipart("/API/documents/1")
            .file("file", file.bytes)
            .with { request ->
                request.method = "PUT"
                request
            }
            .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isConflict())
    }

    @Test
    fun testDeleteDocument_status200(){
        mockMvc.delete("/API/documents/1")
            .andExpect { status { isOk() } }
    }

    @Test
    fun testDeleteDocument_status400(){
        mockMvc.delete("/API/documents/25")
            .andExpect { status { isNotFound() } }
    }

}
