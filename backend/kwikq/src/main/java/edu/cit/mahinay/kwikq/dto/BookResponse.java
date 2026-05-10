package edu.cit.mahinay.kwikq.dto;

public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String category;
    private String uniqueCode;

    public BookResponse(Long id, String title, String author, String category, String uniqueCode) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.uniqueCode = uniqueCode;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getUniqueCode() { return uniqueCode; }
}
