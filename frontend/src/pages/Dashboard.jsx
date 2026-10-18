import { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import {
  FolderKanban,
  ListTodo,
  CheckCircle2,
  Clock3,
  ArrowRight,
  Plus,
  RefreshCw,
  CalendarDays,
  Code2,
  Compass,
  Users,
} from "lucide-react";

import api from "../services/api";

function Dashboard() {
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);

  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const [projectError, setProjectError] = useState("");
  const [taskError, setTaskError] = useState("");

  // ==========================================
  // LOAD DASHBOARD DATA
  // ==========================================

  const loadDashboard = useCallback(async (isRefresh = false) => {
    if (isRefresh) {
      setRefreshing(true);
    } else {
      setLoading(true);
    }

    setProjectError("");
    setTaskError("");

    try {
      const results = await Promise.allSettled([
        api.get("/api/projects"),
        api.get("/api/my-tasks"),
      ]);

      // PROJECTS

      if (results[0].status === "fulfilled") {
        const data = results[0].value.data;

        const projectList = Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : [];

        setProjects(projectList);
      } else {
        console.error(
          "Failed to load projects:",
          results[0].reason
        );

        setProjectError("Unable to load projects.");
        setProjects([]);
      }

      // TASKS

      if (results[1].status === "fulfilled") {
        const data = results[1].value.data;

        const taskList = Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : [];

        setTasks(taskList);
      } else {
        console.error(
          "Failed to load tasks:",
          results[1].reason
        );

        setTaskError("Unable to load assigned tasks.");
        setTasks([]);
      }
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    loadDashboard();
  }, [loadDashboard]);

  // ==========================================
  // DASHBOARD STATISTICS
  // ==========================================

  const totalProjects = projects.length;

  const totalTasks = tasks.length;

  const completedTasks = tasks.filter(
    (task) => task.status === "COMPLETED"
  ).length;

  const inProgressTasks = tasks.filter(
    (task) => task.status === "IN_PROGRESS"
  ).length;

  const stats = [
    {
      title: "My Projects",
      value: projectError ? "—" : totalProjects,
      icon: FolderKanban,
      color: "blue",
    },
    {
      title: "Assigned Tasks",
      value: taskError ? "—" : totalTasks,
      icon: ListTodo,
      color: "purple",
    },
    {
      title: "Completed Tasks",
      value: taskError ? "—" : completedTasks,
      icon: CheckCircle2,
      color: "green",
    },
    {
      title: "In Progress",
      value: taskError ? "—" : inProgressTasks,
      icon: Clock3,
      color: "orange",
    },
  ];

  // ==========================================
  // FORMAT HELPERS
  // ==========================================

  const formatStatus = (status) => {
    if (!status) return "Not specified";

    return status.replaceAll("_", " ");
  };

  const formatDate = (date) => {
    if (!date) return "No deadline";

    const parsed = new Date(date);

    if (Number.isNaN(parsed.getTime())) {
      return "No deadline";
    }

    return parsed.toLocaleDateString("en-IN", {
      day: "numeric",
      month: "short",
      year: "numeric",
    });
  };

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="dashboard">

      {/* HEADER */}

      <div className="dashboard-heading">
        <div>
          <h1>Welcome to Tech Tribe 👋</h1>

          <p>
            Manage your projects, collaborate with developers,
            and track your progress.
          </p>
        </div>

        <button
          className="primary-btn"
          onClick={() => loadDashboard(true)}
          disabled={loading || refreshing}
        >
          <RefreshCw size={18} />

          {refreshing ? "Refreshing..." : "Refresh"}
        </button>
      </div>

      {/* STATISTICS */}

      <div className="stats-grid">
        {stats.map(({ title, value, icon: Icon, color }) => (
          <div className="stat-card" key={title}>
            <div className={`stat-icon ${color}`}>
              <Icon size={23} />
            </div>

            <p>{title}</p>

            <h2>{loading ? "..." : value}</h2>
          </div>
        ))}
      </div>

      {/* MAIN CONTENT */}

      <div className="dashboard-grid">

        {/* RECENT PROJECTS */}

        <section className="dashboard-panel">
          <div className="panel-heading">
            <h2>Recent Projects</h2>

            <Link
              to="/projects"
              style={{
                color: "#38bdf8",
                display: "flex",
                alignItems: "center",
                gap: "6px",
                textDecoration: "none",
              }}
            >
              View All
              <ArrowRight size={18} />
            </Link>
          </div>

          {loading ? (
            <p>Loading projects...</p>
          ) : projectError ? (
            <p className="error">{projectError}</p>
          ) : projects.length === 0 ? (
            <div className="empty-state">
              <FolderKanban size={35} />

              <h3>No projects yet</h3>

              <p>
                Create your first project or explore projects
                created by other developers.
              </p>

              <Link
                to="/projects"
                className="primary-btn"
                style={{
                  display: "inline-flex",
                  textDecoration: "none",
                  marginTop: "15px",
                }}
              >
                <Plus size={17} />
                View Projects
              </Link>
            </div>
          ) : (
            <div className="project-list">
              {projects.slice(0, 5).map((project) => (
                <Link
                  to={`/projects/${project.id}`}
                  className="project-item clickable-project"
                  key={project.id}
                  style={{
                    display: "block",
                    textDecoration: "none",
                  }}
                >
                  <h3>{project.title}</h3>

                  <p>
                    {project.description ||
                      "No description available."}
                  </p>

                  <small>
                    Status: {formatStatus(project.status)}
                  </small>
                </Link>
              ))}
            </div>
          )}
        </section>

        {/* MY TASKS */}

        <section className="dashboard-panel">
          <div className="panel-heading">
            <h2>My Tasks</h2>

            <Link
              to="/my-tasks"
              style={{
                color: "#38bdf8",
                display: "flex",
                alignItems: "center",
                gap: "6px",
                textDecoration: "none",
              }}
            >
              View All
              <ArrowRight size={18} />
            </Link>
          </div>

          {loading ? (
            <p>Loading tasks...</p>
          ) : taskError ? (
            <p className="error">{taskError}</p>
          ) : tasks.length === 0 ? (
            <div className="empty-state">
              <ListTodo size={35} />

              <h3>No assigned tasks</h3>

              <p>
                Tasks assigned to you by project owners
                will appear here.
              </p>
            </div>
          ) : (
            <div className="task-list">
              {tasks.slice(0, 5).map((task) => (
                <div className="task-card" key={task.id}>
                  <h3>{task.title}</h3>

                  <p>
                    {task.description ||
                      "No description available."}
                  </p>

                  <div
                    style={{
                      display: "flex",
                      flexWrap: "wrap",
                      gap: "12px",
                      alignItems: "center",
                      marginTop: "12px",
                    }}
                  >
                    <span
                      style={{
                        color:
                          task.status === "COMPLETED"
                            ? "#4ade80"
                            : task.status === "IN_PROGRESS"
                            ? "#fbbf24"
                            : "#38bdf8",
                        fontSize: "13px",
                        fontWeight: "600",
                      }}
                    >
                      {formatStatus(task.status)}
                    </span>

                    <span
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "5px",
                        color: "#94a3b8",
                        fontSize: "13px",
                      }}
                    >
                      <CalendarDays size={14} />

                      {formatDate(task.deadline)}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </div>

      {/* QUICK ACTIONS */}

      <section
        className="dashboard-panel"
        style={{ marginTop: "25px" }}
      >
        <div className="panel-heading">
          <h2>Quick Actions</h2>
        </div>

        <div
          style={{
            display: "flex",
            flexWrap: "wrap",
            gap: "12px",
            marginTop: "20px",
          }}
        >
          <Link
            to="/projects"
            className="primary-btn"
            style={{ textDecoration: "none" }}
          >
            <Plus size={18} />
            My Projects
          </Link>

          <Link
            to="/explore"
            className="primary-btn"
            style={{ textDecoration: "none" }}
          >
            <Compass size={18} />
            Explore Projects
          </Link>

          <Link
            to="/my-tasks"
            className="primary-btn"
            style={{ textDecoration: "none" }}
          >
            <ListTodo size={18} />
            My Tasks
          </Link>

          <Link
            to="/developers"
            className="primary-btn"
            style={{ textDecoration: "none" }}
          >
            <Users size={18} />
            Find Developers
          </Link>
        </div>
      </section>
    </div>
  );
}

export default Dashboard;