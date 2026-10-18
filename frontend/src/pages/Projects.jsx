import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  FolderKanban,
  RefreshCw,
  Plus,
  X,
  ArrowRight,
} from "lucide-react";

import api from "../services/api";

const initialForm = {
  title: "",
  description: "",
  technologies: "",
  status: "PLANNING",
};

function Projects() {
  const navigate = useNavigate();

  // Project data
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Create project form
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(initialForm);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState("");

  // =====================================
  // FETCH PROJECTS
  // =====================================

  const loadProjects = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get("/api/projects");

      const data = response.data;

      const projectList = Array.isArray(data)
        ? data
        : Array.isArray(data?.content)
          ? data.content
          : [];

      setProjects(projectList);
    } catch (err) {
      if (err.response?.status === 401) {
        setError(
          "Your session has expired. Please log in again."
        );
      } else {
        setError(
          "Unable to load projects. Check that Spring Boot is running."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProjects();
  }, []);

  // =====================================
  // HANDLE FORM CHANGES
  // =====================================

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  // =====================================
  // CREATE PROJECT
  // =====================================

  const handleCreateProject = async (e) => {
    e.preventDefault();

    if (!form.title.trim()) {
      setFormError("Project title is required.");
      return;
    }

    setSaving(true);
    setFormError("");

    try {
      await api.post("/api/projects", {
        title: form.title.trim(),
        description: form.description.trim(),
        technologies: form.technologies.trim(),
        status: form.status,
      });

      // Reset form
      setForm(initialForm);
      setShowForm(false);

      // Reload projects
      await loadProjects();

    } catch (err) {
      const responseData = err.response?.data;

      const titleError = responseData?.fieldErrors?.title;

      setFormError(
        (Array.isArray(titleError)
          ? titleError.join(", ")
          : titleError) ||
          responseData?.message ||
          "Failed to create project. Please try again."
      );

    } finally {
      setSaving(false);
    }
  };

  // =====================================
  // CANCEL CREATE PROJECT
  // =====================================

  const handleCancel = () => {
    setShowForm(false);
    setForm(initialForm);
    setFormError("");
  };

  // =====================================
  // OPEN PROJECT DETAILS
  // =====================================

  const openProject = (projectId) => {
    navigate(`/projects/${projectId}`);
  };

  // =====================================
  // UI
  // =====================================

  return (
    <div className="dashboard">

      {/* PAGE HEADING */}
      <div className="dashboard-heading">
        <div>
          <h1>Projects</h1>

          <p>
            Explore, create, and manage your developer projects.
          </p>
        </div>

        <div className="project-actions">

          {/* REFRESH */}
          <button
            type="button"
            className="primary-btn"
            onClick={loadProjects}
            disabled={loading}
          >
            <RefreshCw size={18} />
            Refresh
          </button>

          {/* CREATE PROJECT */}
          <button
            type="button"
            className="primary-btn"
            onClick={() => {
              if (showForm) {
                handleCancel();
              } else {
                setShowForm(true);
              }
            }}
          >
            {showForm ? <X size={18} /> : <Plus size={18} />}

            {showForm ? "Close" : "Create Project"}
          </button>

        </div>
      </div>

      {/* =====================================
          CREATE PROJECT FORM
      ===================================== */}

      {showForm && (
        <form
          className="create-project-form"
          onSubmit={handleCreateProject}
        >
          <h2>Create New Project</h2>

          {formError && (
            <p className="error">{formError}</p>
          )}

          {/* TITLE */}
          <label htmlFor="project-title">
            Project Title *
          </label>

          <input
            id="project-title"
            type="text"
            name="title"
            placeholder="Enter project title"
            value={form.title}
            onChange={handleChange}
            maxLength={150}
            required
          />

          {/* DESCRIPTION */}
          <label htmlFor="project-description">
            Description
          </label>

          <textarea
            id="project-description"
            name="description"
            placeholder="Describe your project"
            value={form.description}
            onChange={handleChange}
            maxLength={2000}
          />

          {/* TECHNOLOGIES */}
          <label htmlFor="project-technologies">
            Technologies
          </label>

          <input
            id="project-technologies"
            type="text"
            name="technologies"
            placeholder="React, Java, Spring Boot, MySQL"
            value={form.technologies}
            onChange={handleChange}
            maxLength={500}
          />

          {/* STATUS */}
          <label htmlFor="project-status">
            Project Status
          </label>

          <select
            id="project-status"
            name="status"
            value={form.status}
            onChange={handleChange}
          >
            <option value="PLANNING">
              Planning
            </option>

            <option value="IN_PROGRESS">
              In Progress
            </option>

            <option value="COMPLETED">
              Completed
            </option>

            <option value="ON_HOLD">
              On Hold
            </option>
          </select>

          {/* FORM BUTTONS */}
          <div className="project-actions">

            <button
              type="submit"
              className="primary-btn"
              disabled={saving}
            >
              <Plus size={18} />

              {saving
                ? "Creating..."
                : "Save Project"}
            </button>

            <button
              type="button"
              className="cancel-btn"
              onClick={handleCancel}
              disabled={saving}
            >
              Cancel
            </button>

          </div>
        </form>
      )}

      {/* =====================================
          PROJECT LIST
      ===================================== */}

      {loading ? (

        <p>Loading projects...</p>

      ) : error ? (

        <p className="error">{error}</p>

      ) : projects.length === 0 ? (

        <div className="empty-state">
          <FolderKanban size={40} />

          <h3>No projects found</h3>

          <p>
            Create your first project to get started.
          </p>
        </div>

      ) : (

        <div className="projects-grid">

          {projects.map((project) => (

            <div
              className="project-item clickable-project"
              key={project.id}
              role="link"
              tabIndex={0}
              onClick={() => openProject(project.id)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  openProject(project.id);
                }
              }}
            >

              <div className="project-card-header">
                <h3>{project.title}</h3>

                <ArrowRight
                  size={20}
                  color="#38bdf8"
                />
              </div>

              <p>
                {project.description ||
                  "No description provided."}
              </p>

              <p>
                <strong>Status:</strong>{" "}
                {project.status || "Not specified"}
              </p>

              <p>
                <strong>Technologies:</strong>{" "}
                {project.technologies || "Not specified"}
              </p>

              <p>
                <strong>Owner:</strong>{" "}
                {project.ownerName || "Unknown"}
              </p>

              <p className="project-view-text">
                View Project Details →
              </p>

            </div>

          ))}

        </div>
      )}
    </div>
  );
}

export default Projects;