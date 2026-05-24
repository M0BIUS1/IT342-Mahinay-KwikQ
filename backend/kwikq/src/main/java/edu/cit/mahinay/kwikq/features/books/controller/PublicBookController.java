package edu.cit.mahinay.kwikq.features.books.controller;

import edu.cit.mahinay.kwikq.dto.PagedResponse;
import edu.cit.mahinay.kwikq.features.books.entity.Book;
import edu.cit.mahinay.kwikq.features.books.service.BookService;
import edu.cit.mahinay.kwikq.features.books.repository.BookCopyRepository;
import edu.cit.mahinay.kwikq.features.books.entity.BookCopy;
import edu.cit.mahinay.kwikq.dto.BookCopyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
public class PublicBookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @GetMapping("/search")
    public PagedResponse<Book> searchBooks(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Book> pageData = bookService.getBooks(query, category, page, size);
        return PagedResponse.from(pageData);
    }

    @GetMapping("/{id}/copies")
    public java.util.List<BookCopyResponse> getBookCopies(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        java.util.List<BookCopy> copies = bookCopyRepository.findByBook(book);
        java.util.List<BookCopyResponse> resp = new java.util.ArrayList<>();
        for (BookCopy c : copies) {
            resp.add(new BookCopyResponse(c.getId(), c.getCopyCode(), c.getStatus().name()));
        }
        return resp;
    }
}
