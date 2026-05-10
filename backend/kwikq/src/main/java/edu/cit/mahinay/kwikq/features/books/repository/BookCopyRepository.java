package edu.cit.mahinay.kwikq.features.books.repository;

import edu.cit.mahinay.kwikq.features.books.entity.BookCopy;
import edu.cit.mahinay.kwikq.features.books.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBook(Book book);
    List<BookCopy> findByBookAndStatus(Book book, BookCopy.CopyStatus status);
    Optional<BookCopy> findByCopyCode(String copyCode);
    Long countByBookAndStatus(Book book, BookCopy.CopyStatus status);
}
