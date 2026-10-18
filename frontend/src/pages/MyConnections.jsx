import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../services/api";

function MyConnections() {
  const [incoming, setIncoming] = useState([]);
  const [outgoing, setOutgoing] = useState([]);
  const [connections, setConnections] = useState([]);
  const [myUserId, setMyUserId] = useState(null);

  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadConnections = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const [
        userRes,
        incomingRes,
        outgoingRes,
        connectionsRes,
      ] = await Promise.all([
        api.get("/api/users/me"),
        api.get("/api/connections/incoming"),
        api.get("/api/connections/outgoing"),
        api.get("/api/connections/my"),
      ]);

      setMyUserId(Number(userRes.data.id));

      setIncoming(
        Array.isArray(incomingRes.data) ? incomingRes.data : []
      );

      setOutgoing(
        Array.isArray(outgoingRes.data) ? outgoingRes.data : []
      );

      setConnections(
        Array.isArray(connectionsRes.data)
          ? connectionsRes.data
          : []
      );
    } catch (err) {
      console.error("Failed to load connections:", err);

      setError(
        err.response?.data?.error ||
          "Unable to load connections. Please try again."
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadConnections();
  }, [loadConnections]);

  const performAction = async (id, action) => {
    setBusyId(id);
    setError("");
    setMessage("");

    try {
      if (action === "accept") {
        await api.put(`/api/connections/${id}/accept`);
        setMessage("Connection request accepted!");
      } else if (action === "reject") {
        await api.put(`/api/connections/${id}/reject`);
        setMessage("Connection request rejected.");
      } else if (action === "remove") {
        await api.delete(`/api/connections/${id}`);
        setMessage("Connection removed successfully.");
      }

      await loadConnections();
    } catch (err) {
      console.error("Connection action failed:", err);

      setError(
        err.response?.data?.error ||
          "Unable to complete this action."
      );
    } finally {
      setBusyId(null);
    }
  };

  const cardStyle = {
    padding: "20px",
    borderRadius: "14px",
    border: "1px solid #e5e7eb",
    backgroundColor: "#ffffff",
    color: "#1f2937",
    marginBottom: "14px",
    boxShadow: "0 3px 10px rgba(0,0,0,0.04)",
  };

  const nameStyle = {
    fontSize: "18px",
    fontWeight: "700",
    color: "#111827",
    marginBottom: "8px",
  };

  const descriptionStyle = {
    fontSize: "14px",
    color: "#4b5563",
    marginBottom: "10px",
  };

  const buttonStyle = {
    padding: "9px 15px",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    marginRight: "8px",
    marginTop: "10px",
    fontWeight: "600",
  };

  const profileLinkStyle = {
    display: "inline-block",
    color: "#2563eb",
    textDecoration: "none",
    fontWeight: "600",
    marginTop: "6px",
  };

  const sectionStyle = {
    marginBottom: "32px",
  };

  return (
    <div className="dashboard">
      {/* PAGE HEADER */}

      <div className="dashboard-heading">
        <div>
          <h1>My Connections</h1>
          <p>Manage your developer network and connection requests.</p>
        </div>

        <div
          style={{
            display: "flex",
            gap: "10px",
            flexWrap: "wrap",
          }}
        >
          <Link
            to="/developers"
            className="primary-btn"
            style={{ textDecoration: "none" }}
          >
            Find Developers
          </Link>

          <button
            className="primary-btn"
            onClick={loadConnections}
            disabled={loading}
          >
            Refresh
          </button>
        </div>
      </div>

      {/* SUCCESS AND ERROR MESSAGES */}

      {error && (
        <div
          style={{
            padding: "12px 16px",
            borderRadius: "8px",
            backgroundColor: "#fef2f2",
            color: "#b91c1c",
            marginBottom: "20px",
          }}
        >
          {error}
        </div>
      )}

      {message && (
        <div
          style={{
            padding: "12px 16px",
            borderRadius: "8px",
            backgroundColor: "#f0fdf4",
            color: "#15803d",
            marginBottom: "20px",
          }}
        >
          {message}
        </div>
      )}

      {/* LOADING */}

      {loading ? (
        <div className="project-details-card">
          <p>Loading your connections...</p>
        </div>
      ) : (
        <>
          {/* INCOMING REQUESTS */}

          <section style={sectionStyle}>
            <h2 style={{ marginBottom: "16px" }}>
              Incoming Requests ({incoming.length})
            </h2>

            {incoming.length === 0 ? (
              <div style={cardStyle}>
                <p style={descriptionStyle}>
                  No incoming connection requests.
                </p>
              </div>
            ) : (
              incoming.map((request) => (
                <div key={request.id} style={cardStyle}>
                  <h3 style={nameStyle}>
                    {request.requesterName || "Developer"}
                  </h3>

                  <p style={descriptionStyle}>
                    Wants to connect with you.
                  </p>

                  <Link
                    to={`/developers/${request.requesterId}`}
                    style={profileLinkStyle}
                  >
                    View Profile
                  </Link>

                  <div>
                    <button
                      style={{
                        ...buttonStyle,
                        backgroundColor: "#16a34a",
                        color: "#ffffff",
                      }}
                      disabled={busyId === request.id}
                      onClick={() =>
                        performAction(request.id, "accept")
                      }
                    >
                      Accept
                    </button>

                    <button
                      style={{
                        ...buttonStyle,
                        backgroundColor: "#dc2626",
                        color: "#ffffff",
                      }}
                      disabled={busyId === request.id}
                      onClick={() =>
                        performAction(request.id, "reject")
                      }
                    >
                      Reject
                    </button>
                  </div>
                </div>
              ))
            )}
          </section>

          {/* OUTGOING REQUESTS */}

          <section style={sectionStyle}>
            <h2 style={{ marginBottom: "16px" }}>
              Outgoing Requests ({outgoing.length})
            </h2>

            {outgoing.length === 0 ? (
              <div style={cardStyle}>
                <p style={descriptionStyle}>
                  No pending outgoing requests.
                </p>
              </div>
            ) : (
              outgoing.map((request) => (
                <div key={request.id} style={cardStyle}>
                  <h3 style={nameStyle}>
                    {request.receiverName || "Developer"}
                  </h3>

                  <p style={descriptionStyle}>
                    Connection request pending.
                  </p>

                  <Link
                    to={`/developers/${request.receiverId}`}
                    style={profileLinkStyle}
                  >
                    View Profile
                  </Link>

                  <div>
                    <button
                      style={{
                        ...buttonStyle,
                        backgroundColor: "#f3f4f6",
                        color: "#374151",
                        border: "1px solid #d1d5db",
                      }}
                      disabled={busyId === request.id}
                      onClick={() =>
                        performAction(request.id, "remove")
                      }
                    >
                      Cancel Request
                    </button>
                  </div>
                </div>
              ))
            )}
          </section>

          {/* CONNECTED DEVELOPERS */}

          <section style={sectionStyle}>
            <h2 style={{ marginBottom: "16px" }}>
              Connected Developers ({connections.length})
            </h2>

            {connections.length === 0 ? (
              <div style={cardStyle}>
                <p style={descriptionStyle}>
                  You haven't connected with anyone yet.
                </p>
              </div>
            ) : (
              connections.map((connection) => {
                const isRequester =
                  Number(connection.requesterId) === myUserId;

                const otherName = isRequester
                  ? connection.receiverName
                  : connection.requesterName;

                const otherId = isRequester
                  ? connection.receiverId
                  : connection.requesterId;

                return (
                  <div key={connection.id} style={cardStyle}>
                    {/* DEVELOPER NAME */}

                    <h3 style={nameStyle}>
                      {otherName || "Developer"}
                    </h3>

                    {/* CONNECTION STATUS */}

                    <p style={descriptionStyle}>
                      <strong style={{ color: "#374151" }}>
                        Status:
                      </strong>{" "}
                      <span
                        style={{
                          color: "#15803d",
                          fontWeight: "600",
                        }}
                      >
                        Connected
                      </span>
                    </p>

                    {/* PROFILE LINK */}

                    <Link
                      to={`/developers/${otherId}`}
                      style={profileLinkStyle}
                    >
                      View Profile
                    </Link>

                    {/* REMOVE CONNECTION */}

                    <div>
                      <button
                        style={{
                          ...buttonStyle,
                          backgroundColor: "#dc2626",
                          color: "#ffffff",
                        }}
                        disabled={busyId === connection.id}
                        onClick={() =>
                          performAction(connection.id, "remove")
                        }
                      >
                        Remove Connection
                      </button>
                    </div>
                  </div>
                );
              })
            )}
          </section>
        </>
      )}
    </div>
  );
}

export default MyConnections;