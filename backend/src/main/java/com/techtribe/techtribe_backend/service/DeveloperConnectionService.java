package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.dto.DeveloperConnectionResponse;
import com.techtribe.techtribe_backend.model.DeveloperConnection;
import com.techtribe.techtribe_backend.model.User;
import com.techtribe.techtribe_backend.repository.DeveloperConnectionRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DeveloperConnectionService {

    private final DeveloperConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    public DeveloperConnectionService(
            DeveloperConnectionRepository connectionRepository,
            UserRepository userRepository) {

        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    private DeveloperConnection getConnection(Long connectionId) {
        return connectionRepository.findById(connectionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Connection request not found"));
    }

    private boolean isParticipant(
            DeveloperConnection connection,
            Long userId) {

        return connection.getRequester().getId().equals(userId)
                || connection.getReceiver().getId().equals(userId);
    }

    // SEND CONNECTION REQUEST

    @Transactional
    public DeveloperConnectionResponse sendRequest(
            String email,
            Long receiverId) {

        User requester = getUser(email);

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Developer not found"));

        if (requester.getId().equals(receiver.getId())) {
            throw new IllegalStateException(
                    "You cannot connect with yourself");
        }

        Optional<DeveloperConnection> existing =
                findConnectionBetween(
                        requester.getId(),
                        receiver.getId()
                );

        if (existing.isPresent()) {
            throw new IllegalStateException(
                    "A connection or request already exists");
        }

        DeveloperConnection connection = new DeveloperConnection();

        connection.setRequester(requester);
        connection.setReceiver(receiver);
        connection.setStatus("PENDING");

        return new DeveloperConnectionResponse(
                connectionRepository.save(connection));
    }

    // FIND CONNECTION IN EITHER DIRECTION

    private Optional<DeveloperConnection> findConnectionBetween(
            Long firstUserId,
            Long secondUserId) {

        Optional<DeveloperConnection> direct =
                connectionRepository.findByRequesterIdAndReceiverId(
                        firstUserId,
                        secondUserId
                );

        if (direct.isPresent()) {
            return direct;
        }

        return connectionRepository.findByRequesterIdAndReceiverId(
                secondUserId,
                firstUserId
        );
    }

    // INCOMING REQUESTS

    @Transactional(readOnly = true)
    public List<DeveloperConnectionResponse> getIncomingRequests(
            String email) {

        User user = getUser(email);

        return connectionRepository.findByReceiverIdAndStatus(
                        user.getId(), "PENDING")
                .stream()
                .map(DeveloperConnectionResponse::new)
                .toList();
    }

    // OUTGOING REQUESTS

    @Transactional(readOnly = true)
    public List<DeveloperConnectionResponse> getOutgoingRequests(
            String email) {

        User user = getUser(email);

        return connectionRepository.findByRequesterIdAndStatus(
                        user.getId(), "PENDING")
                .stream()
                .map(DeveloperConnectionResponse::new)
                .toList();
    }

    // MY ACCEPTED CONNECTIONS

    @Transactional(readOnly = true)
    public List<DeveloperConnectionResponse> getMyConnections(
            String email) {

        User user = getUser(email);

        return connectionRepository.findByRequesterIdOrReceiverId(
                        user.getId(), user.getId())
                .stream()
                .filter(connection ->
                        "ACCEPTED".equals(connection.getStatus()))
                .map(DeveloperConnectionResponse::new)
                .toList();
    }

    // CONNECTION STATUS WITH ANOTHER DEVELOPER

    @Transactional(readOnly = true)
    public Optional<DeveloperConnectionResponse> getConnectionStatus(
            String email,
            Long developerId) {

        User user = getUser(email);

        return findConnectionBetween(user.getId(), developerId)
                .map(DeveloperConnectionResponse::new);
    }

    // ACCEPT REQUEST

    @Transactional
    public DeveloperConnectionResponse acceptRequest(
            String email,
            Long connectionId) {

        User user = getUser(email);
        DeveloperConnection connection = getConnection(connectionId);

        if (!connection.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "Only the receiver can accept this request");
        }

        if (!"PENDING".equals(connection.getStatus())) {
            throw new IllegalStateException(
                    "Request has already been processed");
        }

        connection.setStatus("ACCEPTED");

        return new DeveloperConnectionResponse(
                connectionRepository.save(connection));
    }

    // REJECT REQUEST

    @Transactional
    public DeveloperConnectionResponse rejectRequest(
            String email,
            Long connectionId) {

        User user = getUser(email);
        DeveloperConnection connection = getConnection(connectionId);

        if (!connection.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "Only the receiver can reject this request");
        }

        if (!"PENDING".equals(connection.getStatus())) {
            throw new IllegalStateException(
                    "Request has already been processed");
        }

        connection.setStatus("REJECTED");

        return new DeveloperConnectionResponse(
                connectionRepository.save(connection));
    }

    // CANCEL OR REMOVE CONNECTION

    @Transactional
    public void removeConnection(
            String email,
            Long connectionId) {

        User user = getUser(email);
        DeveloperConnection connection = getConnection(connectionId);

        if (!isParticipant(connection, user.getId())) {
            throw new IllegalArgumentException(
                    "You cannot remove this connection");
        }

        if ("PENDING".equals(connection.getStatus())
                && !connection.getRequester().getId()
                        .equals(user.getId())) {

            throw new IllegalStateException(
                    "Only the requester can cancel a pending request");
        }

        connectionRepository.delete(connection);
    }
}