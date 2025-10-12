package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.controller.GenreController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = GenreController.class)
class GenreControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private GenreService genreService;

    @Test
    void list_returnsGenres() {
        var g1 = new GenreDto("g1", "Genre_1");
        when(genreService.findAll()).thenReturn(Flux.just(g1));

        webTestClient.get().uri("/api/genres")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GenreDto.class).hasSize(1)
                .contains(g1);

        verify(genreService).findAll();
    }
}
