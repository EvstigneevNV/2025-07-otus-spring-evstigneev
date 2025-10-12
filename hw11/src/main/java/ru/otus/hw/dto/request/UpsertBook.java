package ru.otus.hw.dto.request;

import java.util.List;

public record UpsertBook(String title, String authorId, List<String> genreIds) {}

