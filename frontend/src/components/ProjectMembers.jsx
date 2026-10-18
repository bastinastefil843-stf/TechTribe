import { useCallback, useEffect, useState } from "react";
import { Users, RefreshCw, UserRound } from "lucide-react";
import api from "../services/api";

function ProjectMembers({ projectId, ownerName }) {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadMembers = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get(
        `/api/projects/${projectId}/members`
      );

      setMembers(
        Array.isArray(response.data) ? response.data : []
      );
    } catch (err) {
      console.error("Failed to load project members:", err);

      if (err.response?.status === 401) {
        setError("Your session has expired. Please log in again.");
      } else if (err.response?.status === 403) {
        setError("You do not have permission to view project members.");
      } else if (err.response?.status === 404) {
        setError("Project not found.");
      } else {
        setError("Unable to load project members.");
      }
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  useEffect(() => {
    loadMembers();
  }, [loadMembers]);

  return (
    <div className="project-details-card">
      <div className="task-section-header">
        <div>
          <h2>
            <Users size={22} style={{ verticalAlign: "middle" }} />{" "}
            Project Members
          </h2>
          <p>Developers collaborating on this project.</p>
        </div>

        <button
          type="button"
          className="primary-btn"
          onClick={loadMembers}
          disabled={loading}
        >
          <RefreshCw size={17} />
          Refresh
        </button>
      </div>

      {loading ? (
        <p>Loading project members...</p>
      ) : error ? (
        <p className="error">{error}</p>
      ) : (
        <div className="task-list">
          <div className="task-card">
            <h3>
              <UserRound size={18} style={{ verticalAlign: "middle" }} />{" "}
              {ownerName || "Project Owner"}
            </h3>
            <p>
              <strong>Role:</strong> OWNER
            </p>
          </div>

          {members.map((member) => (
            <div className="task-card" key={member.id}>
              <h3>
                <UserRound size={18} style={{ verticalAlign: "middle" }} />{" "}
                {member.developerName}
              </h3>
              <p>
                <strong>Role:</strong> {member.role}
              </p>
              <p>
                <strong>Developer ID:</strong> {member.developerId}
              </p>
            </div>
          ))}

          {members.length === 0 && (
            <p>No contributors have joined this project yet.</p>
          )}
        </div>
      )}

      {!loading && !error && (
        <p className="task-count">
          Total Members: <strong>{members.length + 1}</strong>
        </p>
      )}
    </div>
  );
}

export default ProjectMembers;