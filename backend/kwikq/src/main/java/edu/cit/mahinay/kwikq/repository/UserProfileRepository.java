package edu.cit.mahinay.kwikq.repository;

import edu.cit.mahinay.kwikq.entity.UserProfile;
import edu.cit.mahinay.kwikq.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
}
