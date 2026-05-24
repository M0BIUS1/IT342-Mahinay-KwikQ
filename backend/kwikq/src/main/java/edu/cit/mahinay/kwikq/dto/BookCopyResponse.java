package edu.cit.mahinay.kwikq.dto;

public class BookCopyResponse {
    private Long id;
    private String copyCode;
    private String status;

    public BookCopyResponse() {}

    public BookCopyResponse(Long id, String copyCode, String status) {
        this.id = id;
        this.copyCode = copyCode;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCopyCode() { return copyCode; }
    public void setCopyCode(String copyCode) { this.copyCode = copyCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
