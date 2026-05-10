package edu.cit.mahinay.kwikq.controller;

import edu.cit.mahinay.kwikq.dto.BookResponse;
import edu.cit.mahinay.kwikq.features.books.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/books")
    public ResponseEntity<?> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            
            // Search by title, author, or category
            Page<BookResponse> response = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                    query, query, query, pageable
            ).map(b -> new BookResponse(b.getId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getUniqueCode()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Search error: " + e.getMessage());
        }
    }
}
