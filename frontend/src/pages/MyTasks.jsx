import { useCallback, useEffect, useState } from "react";
import {
  ClipboardList,
  RefreshCw,
  CalendarDays,
  CheckCircle2,
  Clock3,
} from "lucide-react";

import api from "../services/api";

function MyTasks() {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [updatingId, setUpdatingId] = useState(null);

  // ==========================================
  // LOAD MY ASSIGNED TASKS
  // ==========================================

  const loadTasks = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get("/api/my-tasks");

      setTasks(
        Array.isArray(response.data)
          ? response.data
          : []
      );
    } catch (err) {
      console.error("Failed to load tasks:", err);

      setError(
        err.response?.status === 401
          ? "Your session has expired. Please log in again."
          : "Unable to load your assigned tasks."
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTasks();
  }, [loadTasks]);

  // ==========================================
  // UPDATE TASK STATUS
  // ==========================================

  const updateStatus = async (task, newStatus) => {
    setUpdatingId(task.id);
    setError("");

    try {
      await api.put(
        `/api/projects/${task.projectId}/tasks/${task.id}`,
        {
          status: newStatus,
        }
      );

      await loadTasks();
    } catch (err) {
      console.error("Failed to update task:", err);

      setError(
        err.response?.data?.message ||
          "Unable to update task status."
      );
    } finally {
      setUpdatingId(null);
    }
  };

  // ==========================================
  // TASK COUNTS
  // ==========================================

  const totalTasks = tasks.length;

  const todoTasks = tasks.filter(
    (task) => task.status === "TODO"
  ).length;

  const inProgressTasks = tasks.filter(
    (task) => task.status === "IN_PROGRESS"
  ).length;

  const completedTasks = tasks.filter(
    (task) => task.status === "COMPLETED"
  ).length;

  // ==========================================
  // FORMAT DATE
  // ==========================================

  const formatDate = (date) => {
    if (!date) return "No deadline";

    return new Date(`${date}T00:00:00`)
      .toLocaleDateString("en-IN", {
        day: "numeric",
        month: "short",
        year: "numeric",
      });
  };

  // ==========================================
  // UI
  // ==========================================

  return (
    <div className="dashboard">

      <div className="dashboard-heading">
        <div>
          <h1>My Assigned Tasks</h1>
          <p>Track your tasks and update your progress.</p>
        </div>

        <button
          className="primary-btn"
          onClick={loadTasks}
          disabled={loading}
        >
          <RefreshCw size={17} />
          Refresh
        </button>
      </div>

      {/* TASK SUMMARY */}

      <div className="project-info-grid">

        <div className="project-details-card">
          <h3>Total Tasks</h3>
          <h2>{totalTasks}</h2>
        </div>

        <div className="project-details-card">
          <h3>To Do</h3>
          <h2>{todoTasks}</h2>
        </div>

        <div className="project-details-card">
          <h3>In Progress</h3>
          <h2>{inProgressTasks}</h2>
        </div>

        <div className="project-details-card">
          <h3>Completed</h3>
          <h2>{completedTasks}</h2>
        </div>

      </div>

      {/* ERROR */}

      {error && <p className="error">{error}</p>}

      {/* LOADING */}

      {loading ? (
        <div className="project-details-card">
          <p>Loading assigned tasks...</p>
        </div>
      ) : tasks.length === 0 ? (
        <div className="project-details-card">
          <ClipboardList size={30} />
          <h3>No tasks assigned yet</h3>
          <p>
            Tasks assigned to you by project owners
            will appear here.
          </p>
        </div>
      ) : (
        <div className="task-list">

          {tasks.map((task) => (
            <div className="task-card" key={task.id}>

              <h3>{task.title}</h3>

              <p>
                {task.description ||
                  "No description available."}
              </p>

              <p>
                <strong>Project ID:</strong>{" "}
                {task.projectId}
              </p>

              <p>
                <CalendarDays size={16} />{" "}
                <strong>Deadline:</strong>{" "}
                {formatDate(task.deadline)}
              </p>

              <p>
                <strong>Status:</strong>{" "}
                {task.status?.replaceAll("_", " ")}
              </p>

              {/* TASK STATUS ACTIONS */}

              <div
                style={{
                  display: "flex",
                  gap: "10px",
                  flexWrap: "wrap",
                  marginTop: "15px",
                }}
              >

                {task.status === "TODO" && (
                  <button
                    className="primary-btn"
                    disabled={updatingId === task.id}
                    onClick={() =>
                      updateStatus(task, "IN_PROGRESS")
                    }
                  >
                    <Clock3 size={16} />
                    Start Task
                  </button>
                )}

                {task.status === "IN_PROGRESS" && (
                  <button
                    className="primary-btn"
                    disabled={updatingId === task.id}
                    onClick={() =>
                      updateStatus(task, "COMPLETED")
                    }
                  >
                    <CheckCircle2 size={16} />
                    Mark Completed
                  </button>
                )}

                {task.status === "COMPLETED" && (
                  <p style={{ color: "#22c55e" }}>
                    <CheckCircle2 size={16} />
                    Task Completed
                  </p>
                )}

              </div>

            </div>
          ))}

        </div>
      )}
    </div>
  );
}

export default MyTasks;