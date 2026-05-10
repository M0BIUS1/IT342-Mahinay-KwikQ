package edu.cit.mahinay.kwikq.features.users.service;

import edu.cit.mahinay.kwikq.dto.UserProfileResponse;
import edu.cit.mahinay.kwikq.dto.UserProfileUpdateRequest;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.users.entity.UserProfile;
import edu.cit.mahinay.kwikq.features.users.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfileResponse getProfile(User user) {
        Optional<UserProfile> opt = userProfileRepository.findByUser(user);
        if (opt.isEmpty()) throw new RuntimeException("Profile not found");
        UserProfile p = opt.get();
        return mapToResponse(p);
    }

    public UserProfileResponse updateProfile(User user, UserProfileUpdateRequest req) {
        UserProfile profile = userProfileRepository.findByUser(user).orElse(new UserProfile(user));
        profile.setPhoneNumber(req.getPhoneNumber());
        profile.setAddress(req.getAddress());
        profile.setBio(req.getBio());
        profile.setProfilePictureUrl(req.getProfilePictureUrl());
        UserProfile saved = userProfileRepository.save(profile);
        return mapToResponse(saved);
    }

    private UserProfileResponse mapToResponse(UserProfile p) {
        return new UserProfileResponse(
                p.getId(),
                p.getUser() != null ? p.getUser().getName() : null,
                p.getUser() != null ? p.getUser().getEmail() : null,
                p.getPhoneNumber(),
                p.getAddress(),
                p.getBio(),
                p.getBorrowingLimit(),
                p.getActiveBorrows(),
                p.getTotalFines(),
                p.getIsBlocked(),
                p.getProfilePictureUrl()
        );
    }
}
