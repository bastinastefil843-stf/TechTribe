import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ArrowLeft, FolderKanban, User, Code2 } from "lucide-react";

import api from "../services/api";
import ProjectJoinRequests from "../components/ProjectJoinRequests";

function ExploreProjectDetails() {
  const { projectId } = useParams();

  const [project, setProject] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadDetails = async () => {
      setLoading(true);
      setError("");

      try {
        const [projectResponse, userResponse] = await Promise.all([
          api.get(`/api/projects/explore/${projectId}`),
          api.get("/api/users/me"),
        ]);

        setProject(projectResponse.data);
        setCurrentUser(userResponse.data);
      } catch (err) {
        console.error("Failed to load project details:", err);

        setError(
          err.response?.status === 401
            ? "Your session has expired. Please log in again."
            : err.response?.status === 404
              ? "Project not found."
              : "Unable to load project details."
        );
      } finally {
        setLoading(false);
      }
    };

    loadDetails();
  }, [projectId]);

  if (loading) {
    return (
      <div className="dashboard">
        <p>Loading project details...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard">
        <p className="error">{error}</p>
        <Link to="/explore">Back to Explore Projects</Link>
      </div>
    );
  }

  if (!project || !currentUser) {
    return (
      <div className="dashboard">
        <p>Project information is unavailable.</p>
      </div>
    );
  }

  const isOwner =
    Number(project.ownerId) === Number(currentUser.id);

  return (
    <div className="dashboard">
      <Link to="/explore">
        <ArrowLeft size={16} /> Back to Explore Projects
      </Link>

      <div className="dashboard-heading">
        <div>
          <h1>{project.title}</h1>
          <p>Explore this project and collaborate with developers.</p>
        </div>
      </div>

      <div className="project-details-card">
        <h2>
          <FolderKanban size={22} /> Project Information
        </h2>

        <div className="project-info-grid">
          <p>
            <strong>Project ID:</strong> {project.id}
          </p>

          <p>
            <strong>Status:</strong>{" "}
            {project.status?.replaceAll("_", " ")}
          </p>

          <p>
            <User size={16} /> <strong>Owner:</strong>{" "}
            {project.ownerName}
          </p>

          <p>
            <Code2 size={16} /> <strong>Technologies:</strong>{" "}
            {project.technologies || "Not specified"}
          </p>
        </div>

        <div className="project-description">
          <h3>Description</h3>
          <p>{project.description || "No description available."}</p>
        </div>
      </div>

      {isOwner ? (
        <div className="project-details-card">
          <h2>You own this project</h2>
          <p>
            Manage tasks, members, and join requests from your project
            dashboard.
          </p>

          <Link
            to={`/projects/${project.id}`}
            className="primary-btn"
            style={{
              display: "inline-flex",
              marginTop: "12px",
              textDecoration: "none",
            }}
          >
            Manage Project
          </Link>
        </div>
      ) : (
        <ProjectJoinRequests
          projectId={projectId}
          isOwner={false}
        />
      )}
    </div>
  );
}

export default ExploreProjectDetails;