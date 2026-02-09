package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/book/{bookId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public String add(@PathVariable Long bookId,
                      @RequestParam String text,
                      RedirectAttributes ra) {
        commentService.insert(text, bookId);
        ra.addFlashAttribute("msg", "Комментарий добавлен");
        return "redirect:/book/" + bookId;
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long bookId,
                         @PathVariable Long id,
                         @RequestParam String text,
                         RedirectAttributes ra) {
        commentService.update(id, text, bookId);
        ra.addFlashAttribute("msg", "Комментарий обновлён");
        return "redirect:/book/" + bookId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long bookId,
                         @PathVariable Long id,
                         RedirectAttributes ra) {
        commentService.deleteById(id);
        ra.addFlashAttribute("msg", "Комментарий удалён");
        return "redirect:/book/" + bookId;
    }
}
