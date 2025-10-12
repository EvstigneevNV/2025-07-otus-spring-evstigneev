package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.otus.hw.controller.CommentController;
import ru.otus.hw.dto.CommentsDto;
import ru.otus.hw.services.CommentService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CommentController.class)
class CommentControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private CommentService commentService;

    private record UpsertComment(String text) {}

    @Test
    void create_comment() {
        var dto = new CommentsDto("c1", "text");
        when(commentService.insert("text", "b1")).thenReturn(Mono.just(dto));

        webTestClient.post().uri("/api/books/{bookId}/comments", "b1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpsertComment("text"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CommentsDto.class).isEqualTo(dto);

        verify(commentService).insert("text", "b1");
    }

    @Test
    void update_comment() {
        var dto = new CommentsDto("c1", "new");
        when(commentService.update("c1", "new", "b1")).thenReturn(Mono.just(dto));

        webTestClient.put().uri("/api/books/{bookId}/comments/{id}", "b1","c1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpsertComment("new"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentsDto.class).isEqualTo(dto);

        verify(commentService).update("c1", "new", "b1");
    }

    @Test
    void delete_comment() {
        when(commentService.deleteById("c1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/books/{bookId}/comments/{id}", "b1","c1")
                .exchange()
                .expectStatus().isNoContent();

        verify(commentService).deleteById("c1");
    }
}
