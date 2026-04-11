package edu.cit.mahinay.kwikq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BookRequest {

    @NotBlank(message = "Book title is required")
    @Size(max = 200, message = "Book title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 150, message = "Author must not exceed 150 characters")
    private String author;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @NotBlank(message = "Unique code is required")
    @Size(max = 80, message = "Unique code must not exceed 80 characters")
    private String uniqueCode;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }
}
