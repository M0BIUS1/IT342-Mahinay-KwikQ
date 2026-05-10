package edu.cit.mahinay.kwikq.features.wishlist.repository;

import edu.cit.mahinay.kwikq.features.wishlist.entity.Wishlist;
import edu.cit.mahinay.kwikq.features.books.entity.Book;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserAndBook(User user, Book book);
    void deleteByUserAndBook(User user, Book book);
    Page<Wishlist> findByUser(User user, Pageable pageable);
    Long countByUser(User user);
}
