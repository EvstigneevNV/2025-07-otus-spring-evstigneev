package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentsRepository;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CommentServiceSecurityTest {

    @Autowired
    private CommentService commentService;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private CommentsRepository commentsRepository;

    @Test
    @WithMockUser(roles = "USER")
    void userCanInsertButCannotUpdateOrDelete() {
        assertThatThrownBy(() -> commentService.update(1L, "t", 1L))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> commentService.deleteById(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanInsert() {
        var book = new Book(1L, "B", new Author(1L, "A"), List.of(new Genre(1L, "G")));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(commentsRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        var dto = commentService.insert("txt", 1L);
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.text()).isEqualTo("txt");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUpdate() {
        var book = new Book(1L, "B", new Author(1L, "A"), List.of(new Genre(1L, "G")));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(commentsRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(55L);
            return c;
        });

        var dto = commentService.update(55L, "upd", 1L);
        assertThat(dto.id()).isEqualTo(55L);
        assertThat(dto.text()).isEqualTo("upd");
    }
}
