package edu.cit.mahinay.kwikq.features.queue.service;

import edu.cit.mahinay.kwikq.dto.QueueResponse;
import edu.cit.mahinay.kwikq.features.books.entity.Book;
import edu.cit.mahinay.kwikq.features.queue.entity.BookQueue;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.queue.repository.BookQueueRepository;
import edu.cit.mahinay.kwikq.features.books.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueueService {

    @Autowired
    private BookQueueRepository bookQueueRepository;

    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public QueueResponse addToQueue(User user, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookQueue existing = bookQueueRepository.findByBookAndUserAndStatus(book, user, BookQueue.QueueStatus.WAITING).orElse(null);
        if (existing != null) throw new RuntimeException("User already in queue for this book");

        Integer maxPosition = bookQueueRepository.findMaxQueuePositionByBook(book);
        Integer nextPosition = (maxPosition == null) ? 1 : maxPosition + 1;

        BookQueue queue = new BookQueue(book, user, nextPosition);
        BookQueue saved = bookQueueRepository.save(queue);

        return mapToResponse(saved);
    }

    @Transactional
    public void removeFromQueue(User user, Long queueId) {
        BookQueue queue = bookQueueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue entry not found"));

        if (!queue.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized to cancel this queue");

        queue.setStatus(BookQueue.QueueStatus.CANCELLED);
        bookQueueRepository.save(queue);
        reorderQueue(queue.getBook());
    }

    public Page<QueueResponse> getUserQueue(User user, Pageable pageable) {
        return bookQueueRepository.findByBookAndStatus(null, BookQueue.QueueStatus.WAITING, pageable).map(this::mapToResponse);
    }

    public List<QueueResponse> getBookQueue(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        return bookQueueRepository.findByBookAndStatusOrderByQueuePosition(book, BookQueue.QueueStatus.WAITING).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public Long getQueuePosition(User user, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        BookQueue queue = bookQueueRepository.findByBookAndUserAndStatus(book, user, BookQueue.QueueStatus.WAITING).orElseThrow(() -> new RuntimeException("User not in queue"));
        return bookQueueRepository.countByBookAndStatus(book, BookQueue.QueueStatus.WAITING);
    }

    @Transactional
    private void reorderQueue(Book book) {
        List<BookQueue> queueList = bookQueueRepository.findByBookAndStatusOrderByQueuePosition(book, BookQueue.QueueStatus.WAITING);
        for (int i = 0; i < queueList.size(); i++) {
            queueList.get(i).setQueuePosition(i + 1);
            bookQueueRepository.save(queueList.get(i));
        }
    }

    @Transactional
    public void adminRemoveFromQueue(Long queueId) {
        BookQueue queue = bookQueueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue entry not found"));

        queue.setStatus(BookQueue.QueueStatus.CANCELLED);
        bookQueueRepository.save(queue);
        reorderQueue(queue.getBook());
    }

    private QueueResponse mapToResponse(BookQueue queue) {
        return new QueueResponse(queue.getId(), queue.getBook().getTitle(), queue.getBook().getAuthor(), queue.getQueuePosition(), queue.getStatus().toString(), queue.getQueuedAt());
    }
}
