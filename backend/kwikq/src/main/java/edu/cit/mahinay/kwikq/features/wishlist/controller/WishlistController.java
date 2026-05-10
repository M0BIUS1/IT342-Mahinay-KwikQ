package edu.cit.mahinay.kwikq.features.wishlist.controller;

import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.books.entity.Book;
import edu.cit.mahinay.kwikq.features.books.repository.BookRepository;
import edu.cit.mahinay.kwikq.features.wishlist.repository.WishlistRepository;
import edu.cit.mahinay.kwikq.features.wishlist.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/add/{bookId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> addToWishlist(@PathVariable Long bookId) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
            wishlistService.addToWishlist(user, book);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Added to wishlist"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{bookId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long bookId) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
            wishlistService.removeFromWishlist(user, book);
            return ResponseEntity.ok(new MessageResponse("Removed from wishlist"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-wishlist")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyWishlist(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Pageable pageable = PageRequest.of(page, size);
            Page<?> result = wishlistService.getUserWishlist(user, pageable).map(w -> new Object() {
                public Long id = w.getBook().getId();
                public String title = w.getBook().getTitle();
                public String author = w.getBook().getAuthor();
                public String category = w.getBook().getCategory();
                public Object addedAt = w.getAddedAt();
            });
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getWishlistCount() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long count = wishlistService.getWishlistCount(user);
            return ResponseEntity.ok(new MessageResponse("Count: " + count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
