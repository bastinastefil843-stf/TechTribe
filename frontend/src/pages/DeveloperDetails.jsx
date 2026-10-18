import { useCallback, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import {
  ArrowLeft,
  UserRound,
  Code2,
  ExternalLink,
} from "lucide-react";

import api from "../services/api";

function DeveloperDetails() {
  const { userId } = useParams();

  const [developer, setDeveloper] = useState(null);
  const [connection, setConnection] = useState(null);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadDeveloper = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const [profileResponse, connectionResponse] =
        await Promise.all([
          api.get(`/api/developers/${userId}`),
          api.get(`/api/connections/status/${userId}`),
        ]);

      setDeveloper(profileResponse.data);

      setConnection(
        connectionResponse.status === 204
          ? null
          : connectionResponse.data
      );
    } catch (err) {
      console.error(err);
      setError("Unable to load developer profile.");
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    loadDeveloper();
  }, [loadDeveloper]);

  const handleConnect = async () => {
    setBusy(true);
    setError("");
    setMessage("");

    try {
      await api.post(`/api/connections/request/${userId}`);

      setMessage("Connection request sent!");
      await loadDeveloper();
    } catch (err) {
      setError(
        err.response?.data?.error ||
          "Unable to send connection request."
      );
    } finally {
      setBusy(false);
    }
  };

  const skills = developer?.skills
    ? developer.skills.split(",").map((s) => s.trim()).filter(Boolean)
    : [];

  return (
    <div className="dashboard">
      <Link to="/developers">
        <ArrowLeft size={17} /> Back to Developer Directory
      </Link>

      <div className="dashboard-heading">
        <div>
          <h1>Developer Profile</h1>
          <p>Explore developer skills and experience.</p>
        </div>
      </div>

      {error && <p style={{ color: "red" }}>{error}</p>}
      {message && <p style={{ color: "green" }}>{message}</p>}

      {loading ? (
        <p>Loading developer profile...</p>
      ) : developer ? (
        <>
          <div className="project-details-card">
            <h2>
              <UserRound size={22} />
              {developer.name}
            </h2>

            <p>
              <strong>About:</strong>{" "}
              {developer.bio || "No bio available."}
            </p>

            {!connection ? (
              <button
                className="primary-btn"
                disabled={busy}
                onClick={handleConnect}
              >
                Connect
              </button>
            ) : (
              <p>
                <strong>Connection Status:</strong>{" "}
                {connection.status}
              </p>
            )}

            <Link
              to="/connections"
              style={{ display: "block", marginTop: "12px" }}
            >
              Manage Connections
            </Link>
          </div>

          <div className="project-details-card">
            <h2>
              <Code2 size={22} />
              Technical Skills
            </h2>

            {skills.map((skill, index) => (
              <span
                key={index}
                style={{
                  display: "inline-block",
                  margin: "6px",
                  padding: "8px 12px",
                  background: "#e0f2fe",
                  borderRadius: "16px",
                }}
              >
                {skill}
              </span>
            ))}
          </div>

          <div className="project-details-card">
            <h2>
              <ExternalLink size={22} />
              Developer Links
            </h2>

            {developer.githubUrl && (
              <a
                href={developer.githubUrl}
                target="_blank"
                rel="noopener noreferrer"
              >
                GitHub
              </a>
            )}

            {developer.linkedinUrl && (
              <a
                href={developer.linkedinUrl}
                target="_blank"
                rel="noopener noreferrer"
                style={{ marginLeft: "15px" }}
              >
                LinkedIn
              </a>
            )}
          </div>
        </>
      ) : null}
    </div>
  );
}

export default DeveloperDetails;