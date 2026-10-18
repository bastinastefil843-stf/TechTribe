import { useCallback, useEffect, useState } from "react";
import {
  BarChart3,
  RefreshCw,
  CheckCircle2,
  Clock3,
  ListTodo,
  ClipboardList,
} from "lucide-react";

import api from "../services/api";

function ProjectProgress({ projectId }) {
  const [progress, setProgress] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // ==========================================
  // FETCH PROJECT PROGRESS
  // ==========================================

  const loadProgress = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get(
        `/api/projects/${projectId}/progress`
      );

      setProgress(response.data);
    } catch (err) {
      console.error("Progress loading error:", err);

      if (err.response?.status === 403) {
        setError("You do not have permission to view this progress.");
      } else if (err.response?.status === 404) {
        setError("Project not found.");
      } else {
        setError("Unable to load project progress.");
      }
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  useEffect(() => {
    loadProgress();
  }, [loadProgress]);

  // ==========================================
  // UI
  // ==========================================

  return (
    <div className="project-details-card">

      <div className="task-section-header">
        <div>
          <h2>
            <BarChart3 size={22} />
            Project Progress
          </h2>

          <p>
            Track project completion and task statistics.
          </p>
        </div>

        <button
          className="primary-btn"
          onClick={loadProgress}
          disabled={loading}
        >
          <RefreshCw size={17} />
          Refresh
        </button>
      </div>

      {loading ? (
        <p>Loading project progress...</p>
      ) : error ? (
        <p className="error">{error}</p>
      ) : progress ? (
        <>

          {/* PROJECT NAME */}

          <h3 style={{ marginTop: "20px" }}>
            {progress.projectName}
          </h3>

          {/* TASK STATISTICS */}

          <div className="project-info-grid">

            <div className="project-details-card">
              <ClipboardList size={24} />
              <h3>Total Tasks</h3>
              <h2>{progress.totalTasks}</h2>
            </div>

            <div className="project-details-card">
              <ListTodo size={24} />
              <h3>Pending Tasks</h3>
              <h2>{progress.pendingTasks}</h2>
            </div>

            <div className="project-details-card">
              <Clock3 size={24} />
              <h3>In Progress</h3>
              <h2>{progress.inProgressTasks}</h2>
            </div>

            <div className="project-details-card">
              <CheckCircle2 size={24} />
              <h3>Completed</h3>
              <h2>{progress.completedTasks}</h2>
            </div>

          </div>

          {/* COMPLETION PERCENTAGE */}

          <div style={{ marginTop: "30px" }}>

            <h3>
              Overall Completion:{" "}
              {progress.completionPercentage}%
            </h3>

            <div
              role="progressbar"
              aria-valuenow={progress.completionPercentage}
              aria-valuemin={0}
              aria-valuemax={100}
              aria-label="Project completion"
              style={{
                width: "100%",
                height: "20px",
                background: "#e5e7eb",
                borderRadius: "12px",
                overflow: "hidden",
                marginTop: "15px",
              }}
            >
              <div
                style={{
                  width: `${Math.min(
                    100,
                    Math.max(0, progress.completionPercentage)
                  )}%`,
                  height: "100%",
                  background:
                    "linear-gradient(90deg, #06b6d4, #3b82f6)",
                  borderRadius: "12px",
                  transition: "width 0.4s ease",
                }}
              />
            </div>

          </div>

        </>
      ) : (
        <p>No progress information available.</p>
      )}
    </div>
  );
}

export default ProjectProgress;