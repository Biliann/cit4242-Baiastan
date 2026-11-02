package com.khalel.onlinestore;

import com.khalel.onlinestore.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ProductRepository repository;
    private static final String URL = "/api/products";
    private static final String VALID = """
            {"name":"Test Product","description":"A sample","price":29.99,"category":"Test"}
            """;

    @BeforeEach
    void clearDatabase() { repository.deleteAll(); }

    private JsonNode createProduct() throws Exception {
        String body = mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body);
    }

    private void expectError(ResultActions result, int status) throws Exception {
        result.andExpect(status().is(status))
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.errors").isMap());
        JsonNode body = mapper.readTree(result.andReturn().getResponse().getContentAsString());
        assertThat(body.size()).isEqualTo(4);
    }

    @Test
    void completeProductLifecycle() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isOk()).andExpect(content().json("[]"));
        JsonNode created = createProduct();
        long id = created.get("id").asLong();
        assertThat(created.get("inStock").asBoolean()).isTrue();
        assertThat(created.get("createdAt").asText()).isEqualTo(created.get("updatedAt").asText());
        mvc.perform(get(URL + "/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(29.99));
        mvc.perform(get(URL)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        String updated = mvc.perform(put(URL + "/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name":"Updated Product","price":39.99,"category":"Other","inStock":false}
                        """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.inStock").value(false))
                .andExpect(jsonPath("$.description").isEmpty())
                .andReturn().getResponse().getContentAsString();
        JsonNode changed = mapper.readTree(updated);
        assertThat(changed.get("createdAt")).isEqualTo(created.get("createdAt"));
        assertThat(changed.get("updatedAt").asText()).isGreaterThan(created.get("updatedAt").asText());
        mvc.perform(delete(URL + "/" + id)).andExpect(status().isNoContent())
                .andExpect(content().string(""));
        assertThat(repository.existsById(id)).isFalse();
        expectError(mvc.perform(get(URL + "/" + id)), 404);
    }

    @Test
    void creationIncludesLocation() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern(
                        "http://localhost/api/products/[0-9]+")));
    }

    @Test
    void filtersAndSearchHandleMatchesAndNoMatches() throws Exception {
        createProduct();
        mvc.perform(get(URL + "/category/test")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get(URL + "/search").param("name", "pRoD"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        for (String path : List.of("/category/absent", "/search?name=absent")) {
            mvc.perform(get(URL + path)).andExpect(status().isOk()).andExpect(content().json("[]"));
        }
    }

    @Test
    void missingProductReturns404ForReadUpdateDelete() throws Exception {
        expectError(mvc.perform(get(URL + "/999999")), 404);
        expectError(mvc.perform(put(URL + "/999999").contentType(MediaType.APPLICATION_JSON)
                .content(VALID)), 404);
        expectError(mvc.perform(delete(URL + "/999999")), 404);
    }

    @Test
    void invalidCreateAndUpdateNameTheFailingFields() throws Exception {
        long id = createProduct().get("id").asLong();
        String invalid = "{\"name\":\"\",\"price\":-10}";
        for (var request : List.of(post(URL), put(URL + "/" + id))) {
            ResultActions result = mvc.perform(request.contentType(MediaType.APPLICATION_JSON).content(invalid));
            expectError(result, 400);
            result.andExpect(jsonPath("$.errors.name").exists())
                    .andExpect(jsonPath("$.errors.price").exists())
                    .andExpect(jsonPath("$.errors.category").exists());
        }
        assertThat(repository.findById(id).orElseThrow().getName()).isEqualTo("Test Product");
    }

    @Test
    void validatesBoundaries() throws Exception {
        for (String body : List.of(
                "{\"name\":\"A\",\"price\":1,\"category\":\"T\"}",
                "{\"name\":\"" + "x".repeat(101) + "\",\"price\":1,\"category\":\"T\"}",
                "{\"name\":\"Ok\",\"description\":\"" + "x".repeat(1001) + "\",\"price\":1,\"category\":\"T\"}",
                "{\"name\":\"Ok\",\"price\":0,\"category\":\"T\"}",
                "{\"name\":\"Ok\",\"category\":\"T\"}",
                "{\"name\":\"Ok\",\"price\":1,\"category\":\" \"}",
                "{\"name\":\"Ok\",\"price\":0.00001,\"category\":\"T\"}")) {
            expectError(mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body)), 400);
        }
        assertThat(repository.count()).isZero();
    }

    @Test
    void malformedRequestsHaveConsistentErrors() throws Exception {
        expectError(mvc.perform(get(URL + "/abc")), 400);
        expectError(mvc.perform(get(URL + "/search")), 400);
        expectError(mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{")), 400);
        expectError(mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(VALID.replace("29.99", "\"bad\""))), 400);
        expectError(mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(VALID.replace("\"name\"", "\"id\":1,\"name\""))), 400);
        expectError(mvc.perform(post(URL).contentType(MediaType.TEXT_PLAIN).content(VALID)), 415);
        expectError(mvc.perform(patch(URL + "/1")), 405);
        expectError(mvc.perform(get("/missing")), 404);
    }
}
