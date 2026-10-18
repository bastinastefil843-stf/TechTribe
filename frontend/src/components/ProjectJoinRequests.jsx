import { useCallback, useEffect, useState } from "react";
import {
  UserPlus,
  CheckCircle,
  XCircle,
  RefreshCw,
} from "lucide-react";

import api from "../services/api";

function getErrorMessage(error, fallback) {
  const data = error.response?.data;

  if (error.response?.status === 401) {
    return "Your session has expired. Please log in again.";
  }

  if (error.response?.status === 403) {
    return "You do not have permission to perform this action.";
  }

  if (typeof data === "string" && data.trim()) {
    return data;
  }

  if (typeof data?.message === "string") {
    return data.message;
  }

  return fallback;
}

function ProjectJoinRequests({ projectId, isOwner, onApproved }) {
  const [requests, setRequests] = useState([]);
  const [message, setMessage] = useState("");

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [processingId, setProcessingId] = useState(null);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================
  // OWNER: LOAD PENDING REQUESTS
  // =====================================

  const loadRequests = useCallback(async () => {
    if (!isOwner) return;

    setLoading(true);
    setError("");

    try {
      const response = await api.get(
        `/api/projects/${projectId}/join-requests`
      );

      setRequests(
        Array.isArray(response.data) ? response.data : []
      );
    } catch (err) {
      console.error("Failed to load join requests:", err);

      setError(
        getErrorMessage(err, "Unable to load join requests.")
      );
    } finally {
      setLoading(false);
    }
  }, [projectId, isOwner]);

  useEffect(() => {
    loadRequests();
  }, [loadRequests]);

  // =====================================
  // DEVELOPER: REQUEST TO JOIN
  // =====================================

  const handleRequestToJoin = async (event) => {
    event.preventDefault();

    setSubmitting(true);
    setError("");
    setSuccess("");

    try {
      await api.post(
        `/api/projects/${projectId}/join-requests`,
        {
          message: message.trim(),
        }
      );

      setMessage("");
      setSuccess("Your join request has been submitted!");
    } catch (err) {
      console.error("Failed to request membership:", err);

      setError(
        getErrorMessage(err, "Unable to submit join request.")
      );
    } finally {
      setSubmitting(false);
    }
  };

  // =====================================
  // OWNER: APPROVE OR REJECT
  // =====================================

  const handleDecision = async (requestId, action) => {
    setProcessingId(requestId);
    setError("");
    setSuccess("");

    try {
      await api.put(
        `/api/projects/${projectId}/join-requests/${requestId}/${action}`
      );

      await loadRequests();

      setSuccess(
        action === "approve"
          ? "Developer approved successfully!"
          : "Join request rejected."
      );

      if (action === "approve" && onApproved) {
        onApproved();
      }
    } catch (err) {
      console.error(`Failed to ${action} request:`, err);

      setError(
        getErrorMessage(
          err,
          `Unable to ${action} join request.`
        )
      );
    } finally {
      setProcessingId(null);
    }
  };

  // =====================================
  // UI
  // =====================================

  return (
    <div className="project-details-card">
      <div className="task-section-header">
        <div>
          <h2>
            <UserPlus size={22} style={{ verticalAlign: "middle" }} />{" "}
            Project Join Requests
          </h2>

          <p>
            {isOwner
              ? "Review developers who want to join your project."
              : "Request to collaborate on this project."}
          </p>
        </div>

        {isOwner && (
          <button
            type="button"
            className="primary-btn"
            onClick={loadRequests}
            disabled={loading}
          >
            <RefreshCw size={17} />
            Refresh
          </button>
        )}
      </div>

      {error && <p className="error">{error}</p>}

      {success && (
        <p className="task-success">{success}</p>
      )}

      {/* DEVELOPER: REQUEST TO JOIN */}
      {!isOwner && (
        <form
          className="create-project-form"
          onSubmit={handleRequestToJoin}
        >
          <label htmlFor="join-message">
            Why do you want to join this project?
          </label>

          <textarea
            id="join-message"
            value={message}
            onChange={(event) => setMessage(event.target.value)}
            maxLength={500}
            placeholder="I would like to contribute to this project..."
          />

          <button
            type="submit"
            className="primary-btn"
            disabled={submitting}
          >
            <UserPlus size={17} />
            {submitting ? "Submitting..." : "Request to Join"}
          </button>
        </form>
      )}

      {/* OWNER: VIEW REQUESTS */}
      {isOwner &&
        (loading ? (
          <p>Loading join requests...</p>
        ) : requests.length === 0 ? (
          <p>No pending join requests.</p>
        ) : (
          <div className="task-list">
            {requests.map((request) => (
              <div className="task-card" key={request.id}>
                <h3>{request.developerName}</h3>

                <p>
                  <strong>Message:</strong>{" "}
                  {request.message || "No message provided."}
                </p>

                {request.bio && (
                  <p>
                    <strong>Bio:</strong> {request.bio}
                  </p>
                )}

                {request.skills && (
                  <p>
                    <strong>Skills:</strong> {request.skills}
                  </p>
                )}

                <p>
                  <strong>Status:</strong> {request.status}
                </p>

                <div className="project-actions">
                  <button
                    type="button"
                    className="primary-btn"
                    onClick={() =>
                      handleDecision(request.id, "approve")
                    }
                    disabled={processingId !== null}
                  >
                    <CheckCircle size={17} />
                    Approve
                  </button>

                  <button
                    type="button"
                    className="cancel-btn"
                    onClick={() =>
                      handleDecision(request.id, "reject")
                    }
                    disabled={processingId !== null}
                  >
                    <XCircle size={17} />
                    Reject
                  </button>
                </div>
              </div>
            ))}
          </div>
        ))}
    </div>
  );
}

export default ProjectJoinRequests;
