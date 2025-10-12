package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.controller.AuthorController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private AuthorService authorService;

    @Test
    void list_returnsAuthors() {
        var a1 = new AuthorDto("a1", "Author_1");
        var a2 = new AuthorDto("a2", "Author_2");
        when(authorService.findAll()).thenReturn(Flux.just(a1, a2));

        webTestClient.get().uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class).hasSize(2)
                .contains(a1, a2);

        verify(authorService).findAll();
    }
}

