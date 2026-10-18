package com.techtribe.techtribe_backend.dto;

import com.techtribe.techtribe_backend.model.DeveloperConnection;
import java.time.LocalDateTime;

public class DeveloperConnectionResponse {

    private Long id;
    private Long requesterId;
    private String requesterName;
    private Long receiverId;
    private String receiverName;
    private String status;
    private LocalDateTime createdAt;

    public DeveloperConnectionResponse(
            DeveloperConnection connection) {

        this.id = connection.getId();
        this.requesterId = connection.getRequester().getId();
        this.requesterName = connection.getRequester().getName();
        this.receiverId = connection.getReceiver().getId();
        this.receiverName = connection.getReceiver().getName();
        this.status = connection.getStatus();
        this.createdAt = connection.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}