package edu.cit.mahinay.kwikq.service;

import edu.cit.mahinay.kwikq.features.wishlist.entity.Wishlist;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.books.entity.Book;
import edu.cit.mahinay.kwikq.features.wishlist.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    public Wishlist addToWishlist(User user, Book book) {
        if (wishlistRepository.findByUserAndBook(user, book).isPresent()) {
            throw new RuntimeException("Book already in wishlist");
        }
        return wishlistRepository.save(new Wishlist(user, book));
    }

    public void removeFromWishlist(User user, Book book) {
        wishlistRepository.deleteByUserAndBook(user, book);
    }

    public Page<Wishlist> getUserWishlist(User user, Pageable pageable) {
        return wishlistRepository.findByUser(user, pageable);
    }

    public Long getWishlistCount(User user) {
        return wishlistRepository.countByUser(user);
    }

    public Boolean isInWishlist(User user, Book book) {
        return wishlistRepository.findByUserAndBook(user, book).isPresent();
    }
}
