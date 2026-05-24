package edu.cit.mahinay.kwikq;

import edu.cit.mahinay.kwikq.features.books.entity.Book;
import edu.cit.mahinay.kwikq.features.books.entity.BookCopy;
import edu.cit.mahinay.kwikq.features.books.repository.BookCopyRepository;
import edu.cit.mahinay.kwikq.features.books.repository.BookRepository;
import edu.cit.mahinay.kwikq.features.borrowing.entity.Borrowing;
import edu.cit.mahinay.kwikq.features.borrowing.repository.BorrowingRepository;
import edu.cit.mahinay.kwikq.features.queue.entity.BookQueue;
import edu.cit.mahinay.kwikq.features.queue.repository.BookQueueRepository;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.users.entity.UserProfile;
import edu.cit.mahinay.kwikq.features.users.repository.UserProfileRepository;
import edu.cit.mahinay.kwikq.features.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QueueAndBorrowingFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BookQueueRepository bookQueueRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Test
    void studentCanJoinViewAndLeaveQueue() throws Exception {
        User student = createUser("queue-student@example.com", User.Role.STUDENT);
        Book book = createBook("Queue Test Book", "Queue Author", "Fiction", "QUEUE-BOOK-1");

        mockMvc.perform(post("/api/queues/add/{bookId}", book.getId())
                        .with(authentication(studentAuthentication(student))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Queue Test Book"))
                .andExpect(jsonPath("$.queuePosition").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));

        mockMvc.perform(get("/api/queues/my-queues")
                        .param("page", "0")
                        .param("size", "10")
                        .with(authentication(studentAuthentication(student))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        Long queueId = bookQueueRepository.findByBookAndUserAndStatus(book, student, BookQueue.QueueStatus.WAITING)
                .orElseThrow()
                .getId();

        mockMvc.perform(get("/api/queues/book/{bookId}", book.getId())
                        .with(authentication(studentAuthentication(student))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].queuePosition").value(1))
                .andExpect(jsonPath("$[0].bookTitle").value("Queue Test Book"));

        mockMvc.perform(get("/api/queues/position/{bookId}", book.getId())
                        .with(authentication(studentAuthentication(student))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Position: 1"));

        mockMvc.perform(delete("/api/queues/{queueId}", queueId)
                        .with(authentication(studentAuthentication(student))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Queue cancelled"));

        mockMvc.perform(get("/api/queues/my-queues")
                        .param("page", "0")
                        .param("size", "10")
                        .with(authentication(studentAuthentication(student))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void adminCanRemoveQueueEntry() throws Exception {
        User student = createUser("admin-queue-student@example.com", User.Role.STUDENT);
        User admin = createUser("admin-queue-admin@example.com", User.Role.ADMIN);
        Book book = createBook("Admin Queue Book", "Admin Author", "Fiction", "QUEUE-BOOK-2");

        mockMvc.perform(post("/api/queues/add/{bookId}", book.getId())
                        .with(authentication(studentAuthentication(student))))
                .andExpect(status().isOk());

        Long queueId = bookQueueRepository.findByBookAndUserAndStatus(book, student, BookQueue.QueueStatus.WAITING)
                .orElseThrow()
                .getId();

        mockMvc.perform(delete("/api/queues/admin/{queueId}", queueId)
                        .with(authentication(adminAuthentication(admin))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Queue entry cancelled by admin"));

        BookQueue queue = bookQueueRepository.findById(queueId).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(BookQueue.QueueStatus.CANCELLED, queue.getStatus());
    }

    @Test
    void studentCanBorrowAndReturnBookCopy() throws Exception {
        User student = createUser("borrow-student@example.com", User.Role.STUDENT);
        UserProfile profile = userProfileRepository.findByUser(student).orElseThrow();
        Book book = createBook("Borrowing Test Book", "Borrow Author", "Science", "BORROW-BOOK-1");
        BookCopy copy = createBookCopy(book, "COPY-001");

        mockMvc.perform(post("/api/borrowings/borrow/{bookCopyId}", copy.getId())
                        .with(authentication(studentAuthentication(student))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").value("Borrowing Test Book"))
                .andExpect(jsonPath("$.copyCode").value("COPY-001"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        Borrowing borrowing = borrowingRepository.findByUser(student, Pageable.unpaged())
                .stream()
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/api/borrowings/return/{borrowingId}", borrowing.getId())
                        .with(authentication(studentAuthentication(student))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));

        Borrowing returnedBorrowing = borrowingRepository.findById(borrowing.getId()).orElseThrow();
        BookCopy returnedCopy = bookCopyRepository.findById(copy.getId()).orElseThrow();
        UserProfile updatedProfile = userProfileRepository.findById(profile.getId()).orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(Borrowing.BorrowStatus.RETURNED, returnedBorrowing.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(BookCopy.CopyStatus.AVAILABLE, returnedCopy.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(0, updatedProfile.getActiveBorrows());
    }

    private User createUser(String email, User.Role role) {
        User user = new User();
        user.setName(email.split("@")[0]);
        user.setEmail(email);
        user.setPassword("password123");
        user.setRole(role);
        User saved = userRepository.save(user);
        userProfileRepository.save(new UserProfile(saved));
        return saved;
    }

    private Book createBook(String title, String author, String category, String uniqueCode) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setUniqueCode(uniqueCode);
        return bookRepository.save(book);
    }

    private BookCopy createBookCopy(Book book, String copyCode) {
        BookCopy copy = new BookCopy(book, copyCode);
        copy.setStatus(BookCopy.CopyStatus.AVAILABLE);
        return bookCopyRepository.save(copy);
    }

    private Authentication studentAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    private Authentication adminAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}