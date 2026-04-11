package edu.cit.mahinay.kwikq.controller;

import edu.cit.mahinay.kwikq.dto.MessageResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class RoleDashboardController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse adminDashboard() {
        return new MessageResponse("Admin dashboard access granted");
    }

    @GetMapping("/librarian")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public MessageResponse librarianDashboard() {
        return new MessageResponse("Librarian dashboard access granted");
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public MessageResponse studentDashboard() {
        return new MessageResponse("Student dashboard access granted");
    }
}
