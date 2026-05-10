package edu.cit.mahinay.kwikq.repository;

import edu.cit.mahinay.kwikq.entity.BookCopy;
import edu.cit.mahinay.kwikq.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
