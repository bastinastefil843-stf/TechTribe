import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Search, FolderKanban, User, Code2 } from "lucide-react";
import api from "../services/api";

function ExploreProjects() {
  const [projects, setProjects] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const response = await api.get("/api/projects/explore");
        setProjects(response.data);
      } catch (err) {
        console.error(err);
        setError("Unable to load projects. Check your backend.");
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, []);

  const filteredProjects = projects.filter((project) => {
    const query = search.toLowerCase();

    return (
      project.title?.toLowerCase().includes(query) ||
      project.technologies?.toLowerCase().includes(query) ||
      project.description?.toLowerCase().includes(query)
    );
  });

  return (
    <div className="dashboard">
      <div className="dashboard-heading">
        <div>
          <h1>Explore Projects</h1>
          <p>Discover projects and collaborate with developers.</p>
        </div>
      </div>

      <div className="project-details-card">
        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          <Search size={20} />

          <input
            type="text"
            placeholder="Search projects or technologies..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{
              width: "100%",
              padding: "12px",
              borderRadius: "8px",
              border: "1px solid #444",
              background: "#111827",
              color: "white",
            }}
          />
        </div>
      </div>

      {loading && <p>Loading projects...</p>}

      {error && <p className="error">{error}</p>}

      {!loading && !error && filteredProjects.length === 0 && (
        <p>No projects found.</p>
      )}

      <div className="task-list">
        {filteredProjects.map((project) => (
          <div className="project-details-card" key={project.id}>
            <h2>
              <FolderKanban size={20} /> {project.title}
            </h2>

            <p>{project.description || "No description available."}</p>

            <p>
              <User size={16} /> <strong>Owner:</strong>{" "}
              {project.ownerName}
            </p>

            <p>
              <Code2 size={16} /> <strong>Technologies:</strong>{" "}
              {project.technologies || "Not specified"}
            </p>

            <p>
              <strong>Status:</strong> {project.status}
            </p>

            <Link
              to={`/explore/${project.id}`}
              className="primary-btn"
              style={{
                display: "inline-flex",
                marginTop: "15px",
                textDecoration: "none",
              }}
            >
              View Project
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ExploreProjects;