package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "index";
    }

    @GetMapping("/book/{id}")
    public String book(@PathVariable Long id, Model model) {
        var book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("selectedGenreIds",
                book.genres() == null ? java.util.List.of() :
                        book.genres().stream().map(GenreDto::id).toList());
        return "book";
    }

    @GetMapping("/book/new")
    public String newBook(Model model) {
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "book-form";
    }

    @PostMapping("/book")
    public String create(@RequestParam String title,
                         @RequestParam("authorId") Long authorId,
                         @RequestParam("genreIds") List<Long> genreIds,
                         RedirectAttributes ra) {
        var created = bookService.insert(title, authorId, Set.copyOf(genreIds));
        ra.addFlashAttribute("msg", "Книга создана");
        return "redirect:/book/" + created.id();
    }

    @PostMapping("/book/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam("authorId") Long authorId,
                         @RequestParam("genreIds") List<Long> genreIds,
                         RedirectAttributes ra) {
        bookService.update(id, title, authorId, Set.copyOf(genreIds));
        ra.addFlashAttribute("msg", "Книга обновлена");
        return "redirect:/book/" + id;
    }

    @PostMapping("/book/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        bookService.deleteById(id);
        ra.addFlashAttribute("msg", "Книга удалена");
        return "redirect:/";
    }
}
