package edu.cit.mahinay.kwikq.service;

import edu.cit.mahinay.kwikq.dto.UserProfileResponse;
import edu.cit.mahinay.kwikq.dto.UserProfileUpdateRequest;
import edu.cit.mahinay.kwikq.entity.User;
import edu.cit.mahinay.kwikq.entity.UserProfile;
import edu.cit.mahinay.kwikq.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Transactional
    public UserProfile createProfile(User user) {
        UserProfile profile = new UserProfile(user);
        return userProfileRepository.save(profile);
    }

    public UserProfileResponse getProfile(User user) {
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        return mapToResponse(profile);
    }

    @Transactional
    public UserProfileResponse updateProfile(User user, UserProfileUpdateRequest request) {
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(request.getAddress());
        profile.setBio(request.getBio());

        if (request.getProfilePictureUrl() != null) {
            profile.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        UserProfile updated = userProfileRepository.save(profile);
        return mapToResponse(updated);
    }

    private UserProfileResponse mapToResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getUser().getName(),
                profile.getUser().getEmail(),
                profile.getPhoneNumber(),
                profile.getAddress(),
                profile.getBio(),
                profile.getBorrowingLimit(),
                profile.getActiveBorrows(),
                profile.getTotalFines(),
                profile.getIsBlocked(),
                profile.getProfilePictureUrl()
        );
    }
}
