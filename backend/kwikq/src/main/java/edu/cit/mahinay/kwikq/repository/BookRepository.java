package edu.cit.mahinay.kwikq.repository;

import edu.cit.mahinay.kwikq.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    boolean existsByUniqueCode(String uniqueCode);
    Optional<Book> findByUniqueCode(String uniqueCode);

    @Query("select distinct b.category from Book b order by b.category asc")
    List<String> findDistinctCategories();
}
