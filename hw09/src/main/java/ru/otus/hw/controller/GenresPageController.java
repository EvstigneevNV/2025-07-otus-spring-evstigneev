package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@Controller
public class GenresPageController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public String genres(Model model) {
        model.addAttribute("genres", genreService.findAll());
        return "genres";
    }
}
