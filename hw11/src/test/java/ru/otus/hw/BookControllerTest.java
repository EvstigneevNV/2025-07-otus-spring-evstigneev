package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.controller.BookController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.request.UpsertBook;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = BookController.class)
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private BookService bookService;

    @Test
    void list_returnsBooks() {
        var a = new AuthorDto("a1", "Author_1");
        var g1 = new GenreDto("g1", "Genre_1");
        var g2 = new GenreDto("g2", "Genre_2");
        var b = new BookDto("b1", "Book_1", a, List.of(g1,g2), List.of());
        when(bookService.findAll()).thenReturn(Flux.just(b));

        webTestClient.get().uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class).hasSize(1).contains(b);

        verify(bookService).findAll();
    }

    @Test
    void get_returnsBookById() {
        var a = new AuthorDto("a1", "Author_1");
        var g1 = new GenreDto("g1", "Genre_1");
        var g2 = new GenreDto("g2", "Genre_2");
        var b = new BookDto("b1", "Book_1", a, List.of(g1,g2), List.of());
        when(bookService.findById("b1")).thenReturn(Mono.just(b));

        webTestClient.get().uri("/api/books/{id}", "b1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class).isEqualTo(b);

        verify(bookService).findById("b1");
    }

    @Test
    void create_insertsBook() {
        var req = new UpsertBook("T", "a1", List.of("g1","g2"));
        var a = new AuthorDto("a1", "Author_1");
        var g1 = new GenreDto("g1", "Genre_1");
        var g2 = new GenreDto("g2", "Genre_2");
        var b = new BookDto("b1", "Book_1", a, List.of(g1,g2), List.of());
        when(bookService.insert("T", "a1", List.of("g1","g2"))).thenReturn(Mono.just(b));

        webTestClient.post().uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookDto.class).isEqualTo(b);

        verify(bookService).insert("T", "a1", List.of("g1","g2"));
    }

    @Test
    void update_updatesBook() {
        var req = new UpsertBook("T2", "a2", List.of("g2"));
        var a = new AuthorDto("a1", "Author_1");
        var g1 = new GenreDto("g1", "Genre_1");
        var g2 = new GenreDto("g2", "Genre_2");
        var b = new BookDto("b1", "Book_1", a, List.of(g1,g2), List.of());
        when(bookService.update("b1", "T2", "a2", List.of("g2"))).thenReturn(Mono.just(b));

        webTestClient.put().uri("/api/books/{id}", "b1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class).isEqualTo(b);

        verify(bookService).update("b1", "T2", "a2", List.of("g2"));
    }

    @Test
    void delete_removesBook() {
        when(bookService.deleteById("b1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/books/{id}", "b1")
                .exchange()
                .expectStatus().isNoContent();

        verify(bookService).deleteById("b1");
    }
}
