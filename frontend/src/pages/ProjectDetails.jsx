import { useEffect, useState, useCallback } from "react";
import { useParams, Link } from "react-router-dom";

import {
  ArrowLeft,
  FolderKanban,
  RefreshCw,
  User,
  Code2,
  LayoutDashboard,
  ListTodo,
  Users,
  Sparkles,
} from "lucide-react";

import api from "../services/api";

import ProjectTasks from "../components/ProjectTasks";
import ProjectMembers from "../components/ProjectMembers";
import ProjectJoinRequests from "../components/ProjectJoinRequests";
import ProjectProgress from "../components/ProjectProgress";
import ProjectSnippets from "../components/ProjectSnippets";
import AIDeveloperMatches from "../components/AIDeveloperMatches";

const tabs = [
  { id: "overview", label: "Overview", icon: LayoutDashboard },
  { id: "tasks", label: "Tasks", icon: ListTodo },
  { id: "team", label: "Team", icon: Users },
  { id: "ai", label: "AI Matching", icon: Sparkles },
  { id: "code", label: "Code", icon: Code2 },
];

// Navigation styles scoped to this page.
// No App.css import is required for these styles.
const navigationStyles = `
  .tt-project-workspace {
    display: grid;
    grid-template-columns: 220px minmax(0, 1fr);
    gap: 28px;
    align-items: start;
    width: 100%;
    margin-top: 28px;
  }

  .tt-project-navigation {
    display: flex;
    flex-direction: column;
    gap: 14px;
    width: 100%;
  }

  .tt-project-nav-button {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    gap: 14px;

    width: 100%;
    min-height: 54px;
    padding: 15px 18px;

    background: #1e293b;
    color: #cbd5e1;

    border: 1px solid #334155;
    border-radius: 12px;

    font-size: 15px;
    font-weight: 600;
    font-family: inherit;

    text-align: left;
    cursor: pointer;

    transition:
      background 0.2s ease,
      border-color 0.2s ease,
      transform 0.2s ease;
  }

  .tt-project-nav-button:hover {
    background: #334155;
    color: #ffffff;
    border-color: #38bdf8;
    transform: translateX(3px);
  }

  .tt-project-nav-button.active {
    background: linear-gradient(135deg, #0284c7, #2563eb);
    color: #ffffff;
    border-color: #38bdf8;
    box-shadow: 0 4px 15px rgba(56, 189, 248, 0.2);
  }

  .tt-project-nav-button svg {
    flex-shrink: 0;
  }

  .tt-project-nav-button:focus-visible {
    outline: 2px solid #38bdf8;
    outline-offset: 3px;
  }

  .tt-project-content {
    min-width: 0;
    width: 100%;
  }

  @media (max-width: 768px) {
    .tt-project-workspace {
      grid-template-columns: 1fr;
      gap: 20px;
    }

    .tt-project-navigation {
      max-width: 100%;
    }
  }

  @media (prefers-reduced-motion: reduce) {
    .tt-project-nav-button {
      transition: none;
    }

    .tt-project-nav-button:hover {
      transform: none;
    }
  }
`;

function ProjectDetails() {
  const { projectId } = useParams();

  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [activeTab, setActiveTab] = useState("overview");
  const [refreshKey, setRefreshKey] = useState(0);

  // =====================================
  // LOAD PROJECT
  // =====================================

  const loadProject = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get(`/api/projects/${projectId}`);
      setProject(response.data);
    } catch (err) {
      console.error("Failed to load project:", err);
      setProject(null);

      if (err.response?.status === 401) {
        setError("Your session has expired. Please log in again.");
      } else if (err.response?.status === 403) {
        setError("You do not have permission to view this project.");
      } else if (err.response?.status === 404) {
        setError("Project not found.");
      } else {
        setError(
          "Unable to load project details. Check your backend server."
        );
      }
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  useEffect(() => {
    loadProject();
  }, [loadProject]);

  useEffect(() => {
    setActiveTab("overview");
  }, [projectId]);

  const refreshComponents = () => {
    setRefreshKey((previous) => previous + 1);
  };

  const handleDeveloperApproved = () => {
    refreshComponents();
  };

  const formatStatus = (status) =>
    status ? status.replaceAll("_", " ") : "Not specified";

  // =====================================
  // RENDER SELECTED SECTION
  // =====================================

  const renderActiveTab = () => {
    switch (activeTab) {
      case "overview":
        return (
          <>
            <div className="project-details-card">
              <h2>
                <FolderKanban size={22} />
                Project Information
              </h2>

              <div className="project-info-grid">
                <p>
                  <strong>Project ID:</strong> {project.id}
                </p>

                <p>
                  <strong>Status:</strong>{" "}
                  {formatStatus(project.status)}
                </p>

                <p>
                  <User size={16} />
                  <strong> Owner:</strong> {project.ownerName}
                </p>

                <p>
                  <Code2 size={16} />
                  <strong> Technologies:</strong>{" "}
                  {project.technologies || "Not specified"}
                </p>
              </div>

              <div className="project-description">
                <h3>Description</h3>
                <p>
                  {project.description || "No description available."}
                </p>
              </div>
            </div>

            <ProjectProgress
              key={`progress-${projectId}-${refreshKey}`}
              projectId={projectId}
            />
          </>
        );

      case "tasks":
        return (
          <ProjectTasks
            key={`tasks-${projectId}-${refreshKey}`}
            projectId={projectId}
          />
        );

      case "team":
        return (
          <>
            <ProjectMembers
              key={`members-${projectId}-${refreshKey}`}
              projectId={projectId}
              ownerName={project.ownerName}
            />

            <ProjectJoinRequests
              projectId={projectId}
              isOwner={true}
              onApproved={handleDeveloperApproved}
            />
          </>
        );

      case "ai":
        return (
          <AIDeveloperMatches
            key={`ai-matches-${projectId}-${refreshKey}`}
            projectId={projectId}
          />
        );

      case "code":
        return (
          <ProjectSnippets
            key={`snippets-${projectId}-${refreshKey}`}
            projectId={projectId}
          />
        );

      default:
        return null;
    }
  };

  // =====================================
  // MAIN PAGE
  // =====================================

  return (
    <div className="dashboard">
      {/* Page-specific CSS */}
      <style>{navigationStyles}</style>

      <Link
        to="/projects"
        style={{
          display: "inline-flex",
          alignItems: "center",
          gap: 8,
          marginBottom: 20,
          textDecoration: "none",
          color: "#38bdf8",
        }}
      >
        <ArrowLeft size={18} />
        Back to My Projects
      </Link>

      {loading ? (
        <div className="project-details-card">
          <p>Loading project details...</p>
        </div>
      ) : error ? (
        <div className="project-details-card">
          <p className="error">{error}</p>

          <button
            type="button"
            className="primary-btn"
            onClick={loadProject}
          >
            <RefreshCw size={17} />
            Retry
          </button>
        </div>
      ) : project ? (
        <>
          {/* PROJECT HEADING */}

          <div className="dashboard-heading">
            <div>
              <h1>{project.title}</h1>
              <p>
                Developer Collaboration and Project Management
              </p>
            </div>

            <button
              type="button"
              className="primary-btn"
              onClick={() => {
                loadProject();
                refreshComponents();
              }}
            >
              <RefreshCw size={17} />
              Refresh All
            </button>
          </div>

          {/* VERTICAL NAVIGATION + CONTENT */}

          <div className="tt-project-workspace">
            {/* LEFT SIDE: INDIVIDUAL BUTTONS */}

            <div
              className="tt-project-navigation"
              role="tablist"
              aria-label="Project workspace sections"
              aria-orientation="vertical"
            >
              {tabs.map(({ id, label, icon: Icon }) => (
                <button
                  key={id}
                  type="button"
                  role="tab"
                  aria-selected={activeTab === id}
                  className={`tt-project-nav-button ${
                    activeTab === id ? "active" : ""
                  }`}
                  onClick={() => setActiveTab(id)}
                >
                  <Icon size={20} />
                  <span>{label}</span>
                </button>
              ))}
            </div>

            {/* RIGHT SIDE: SELECTED SECTION */}

            <div
              className="tt-project-content"
              role="tabpanel"
            >
              {renderActiveTab()}
            </div>
          </div>
        </>
      ) : (
        <div className="project-details-card">
          <p>No project information available.</p>
        </div>
      )}
    </div>
  );
}

export default ProjectDetails;