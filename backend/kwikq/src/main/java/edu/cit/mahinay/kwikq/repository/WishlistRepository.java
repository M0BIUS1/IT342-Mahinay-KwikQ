package edu.cit.mahinay.kwikq.repository;

import edu.cit.mahinay.kwikq.entity.Wishlist;
import edu.cit.mahinay.kwikq.entity.User;
import edu.cit.mahinay.kwikq.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Page<Wishlist> findByUser(User user, Pageable pageable);
    Optional<Wishlist> findByUserAndBook(User user, Book book);
    void deleteByUserAndBook(User user, Book book);
    Long countByUser(User user);
}
