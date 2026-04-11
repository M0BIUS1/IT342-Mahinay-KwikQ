package edu.cit.mahinay.kwikq.service;

import edu.cit.mahinay.kwikq.dto.BookRequest;
import edu.cit.mahinay.kwikq.entity.Book;
import edu.cit.mahinay.kwikq.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Page<Book> getBooks(String query, String category, int page, int size) {
        Specification<Book> spec = (root, cq, cb) -> cb.conjunction();

        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            spec = spec.and((root, cq, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + q + "%"),
                    cb.like(cb.lower(root.get("author")), "%" + q + "%"),
                    cb.like(cb.lower(root.get("uniqueCode")), "%" + q + "%")
            ));
        }

        if (category != null && !category.isBlank()) {
            String c = category.trim().toLowerCase();
            spec = spec.and((root, cq, cb) -> cb.equal(cb.lower(root.get("category")), c));
        }

        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "id")
        );

        return bookRepository.findAll(spec, pageable);
    }

    public List<String> getCategories() {
        return bookRepository.findDistinctCategories();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public Book createBook(BookRequest request) {
        String code = normalizeCode(request.getUniqueCode());
        if (bookRepository.existsByUniqueCode(code)) {
            throw new RuntimeException("Unique code is already in use");
        }

        Book book = new Book();
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setCategory(request.getCategory().trim());
        book.setUniqueCode(code);

        return bookRepository.save(book);
    }

    public Book updateBook(Long id, BookRequest request) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        String code = normalizeCode(request.getUniqueCode());
        bookRepository.findByUniqueCode(code)
                .ifPresent(other -> {
                    if (!other.getId().equals(id)) {
                        throw new RuntimeException("Unique code is already in use");
                    }
                });

        existing.setTitle(request.getTitle().trim());
        existing.setAuthor(request.getAuthor().trim());
        existing.setCategory(request.getCategory().trim());
        existing.setUniqueCode(code);

        return bookRepository.save(existing);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    private String normalizeCode(String uniqueCode) {
        return uniqueCode == null ? "" : uniqueCode.trim();
    }
}
