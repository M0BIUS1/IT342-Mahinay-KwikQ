package edu.cit.mahinay.kwikq.dto;

public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String bio;
    private Integer borrowingLimit;
    private Integer activeBorrows;
    private Double totalFines;
    private Boolean isBlocked;
    private String profilePictureUrl;

    // Constructors
    public UserProfileResponse() {}

    public UserProfileResponse(Long id, String name, String email, String phoneNumber, String address,
                              String bio, Integer borrowingLimit, Integer activeBorrows,
                              Double totalFines, Boolean isBlocked, String profilePictureUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.bio = bio;
        this.borrowingLimit = borrowingLimit;
        this.activeBorrows = activeBorrows;
        this.totalFines = totalFines;
        this.isBlocked = isBlocked;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getBorrowingLimit() {
        return borrowingLimit;
    }

    public void setBorrowingLimit(Integer borrowingLimit) {
        this.borrowingLimit = borrowingLimit;
    }

    public Integer getActiveBorrows() {
        return activeBorrows;
    }

    public void setActiveBorrows(Integer activeBorrows) {
        this.activeBorrows = activeBorrows;
    }

    public Double getTotalFines() {
        return totalFines;
    }

    public void setTotalFines(Double totalFines) {
        this.totalFines = totalFines;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
