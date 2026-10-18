import { useCallback, useEffect, useState } from "react";

import {
  ClipboardList,
  RefreshCw,
  Plus,
  X,
  CalendarDays,
  Save,
  UserPlus,
  UserRound,
  Sparkles,
  Lightbulb,
  CheckCircle2,
} from "lucide-react";

import api from "../services/api";

const initialForm = {
  title: "",
  description: "",
  deadline: "",
};

const STATUS_OPTIONS = [
  { value: "TODO", label: "To Do" },
  { value: "IN_PROGRESS", label: "In Progress" },
  { value: "COMPLETED", label: "Completed" },
];

function getErrorMessage(err, fallback) {
  const data = err.response?.data;

  if (err.response?.status === 401) {
    return "Your session has expired. Please log in again.";
  }

  if (err.response?.status === 403) {
    return "You do not have permission to perform this action.";
  }

  if (typeof data === "string" && data.trim()) {
    return data;
  }

  if (typeof data?.message === "string") {
    return data.message;
  }

  if (typeof data?.error === "string") {
    return data.error;
  }

  return fallback;
}

function ProjectTasks({ projectId }) {
  // =====================================
  // EXISTING STATE
  // =====================================

  const [tasks, setTasks] = useState([]);
  const [members, setMembers] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [membersError, setMembersError] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ ...initialForm });
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState("");

  const [selectedStatuses, setSelectedStatuses] = useState({});
  const [selectedDevelopers, setSelectedDevelopers] = useState({});

  const [updatingTaskId, setUpdatingTaskId] = useState(null);
  const [assigningTaskId, setAssigningTaskId] = useState(null);

  const [taskErrors, setTaskErrors] = useState({});
  const [success, setSuccess] = useState("");

  // =====================================
  // MODULE 12: AI STATE
  // =====================================

  const [aiSuggestions, setAiSuggestions] = useState([]);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiError, setAiError] = useState("");
  const [showAiSuggestions, setShowAiSuggestions] = useState(false);
  const [selectedAiTask, setSelectedAiTask] = useState(null);

  // =====================================
  // LOAD TASKS
  // =====================================

  const loadTasks = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get(
        `/api/projects/${projectId}/tasks`
      );

      const taskList = Array.isArray(response.data)
        ? response.data
        : [];

      setTasks(taskList);

      const statuses = {};
      const developers = {};

      taskList.forEach((task) => {
        statuses[task.id] = task.status || "TODO";

        developers[task.id] =
          task.assignedToId != null
            ? String(task.assignedToId)
            : "";
      });

      setSelectedStatuses(statuses);
      setSelectedDevelopers(developers);
    } catch (err) {
      console.error("Failed to load tasks:", err);

      setTasks([]);
      setError(
        getErrorMessage(err, "Unable to load project tasks.")
      );
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  // =====================================
  // LOAD PROJECT MEMBERS
  // =====================================

  const loadMembers = useCallback(async () => {
    setMembersError("");

    try {
      const response = await api.get(
        `/api/projects/${projectId}/members`
      );

      setMembers(
        Array.isArray(response.data) ? response.data : []
      );
    } catch (err) {
      console.error("Failed to load members:", err);

      setMembers([]);
      setMembersError(
        getErrorMessage(err, "Unable to load developers.")
      );
    }
  }, [projectId]);

  useEffect(() => {
    loadTasks();
    loadMembers();
  }, [loadTasks, loadMembers]);

  // =====================================
  // MODULE 12: GENERATE AI TASKS
  // =====================================

  const handleGenerateAiTasks = async () => {
    setAiLoading(true);
    setAiError("");
    setSuccess("");
    setShowAiSuggestions(true);

    try {
      const response = await api.get(
        `/api/ai/projects/${projectId}/task-suggestions`
      );

      const suggestions = Array.isArray(response.data)
        ? response.data
        : [];

      setAiSuggestions(suggestions);

      if (suggestions.length === 0) {
        setAiError("Gemini did not return any task suggestions.");
      }
    } catch (err) {
      console.error("Failed to generate AI tasks:", err);

      setAiSuggestions([]);

      setAiError(
        getErrorMessage(
          err,
          "Unable to generate AI task suggestions. Please try again."
        )
      );
    } finally {
      setAiLoading(false);
    }
  };

  // =====================================
  // MODULE 12: USE AI SUGGESTION
  // =====================================

  const handleUseAiTask = (suggestion) => {
    setForm({
      title: suggestion.title || "",
      description: suggestion.description || "",
      deadline: "",
    });

    setSelectedAiTask(suggestion);
    setShowForm(true);
    setFormError("");
    setSuccess("");

    // Scroll to the task creation form
    setTimeout(() => {
      document
        .getElementById("task-title")
        ?.scrollIntoView({
          behavior: "smooth",
          block: "center",
        });
    }, 100);
  };

  // =====================================
  // CREATE TASK
  // =====================================

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleCreateTask = async (event) => {
    event.preventDefault();

    if (!form.title.trim()) {
      setFormError("Task title is required.");
      return;
    }

    setSaving(true);
    setFormError("");
    setSuccess("");

    try {
      await api.post(
        `/api/projects/${projectId}/tasks`,
        {
          title: form.title.trim(),
          description: form.description.trim(),
          status: "TODO",
          deadline: form.deadline || null,
        }
      );

      setForm({ ...initialForm });
      setShowForm(false);
      setSelectedAiTask(null);

      await loadTasks();

      setSuccess("Task created successfully!");
    } catch (err) {
      setFormError(
        getErrorMessage(err, "Unable to create task.")
      );
    } finally {
      setSaving(false);
    }
  };

  // =====================================
  // UPDATE STATUS
  // =====================================

  const handleStatusChange = (taskId, value) => {
    setSelectedStatuses((previous) => ({
      ...previous,
      [taskId]: value,
    }));

    setTaskErrors((previous) => ({
      ...previous,
      [taskId]: "",
    }));

    setSuccess("");
  };

  const handleUpdateStatus = async (task) => {
    const newStatus = selectedStatuses[task.id];

    if (!newStatus || newStatus === task.status) {
      return;
    }

    setUpdatingTaskId(task.id);
    setSuccess("");

    setTaskErrors((previous) => ({
      ...previous,
      [task.id]: "",
    }));

    try {
      await api.put(
        `/api/projects/${projectId}/tasks/${task.id}`,
        {
          status: newStatus,
        }
      );

      setTasks((previous) =>
        previous.map((item) =>
          item.id === task.id
            ? { ...item, status: newStatus }
            : item
        )
      );

      setSuccess(`Status updated for "${task.title}".`);
    } catch (err) {
      setTaskErrors((previous) => ({
        ...previous,
        [task.id]: getErrorMessage(
          err,
          "Unable to update task status."
        ),
      }));
    } finally {
      setUpdatingTaskId(null);
    }
  };

  // =====================================
  // ASSIGN TASK
  // =====================================

  const handleDeveloperChange = (taskId, developerId) => {
    setSelectedDevelopers((previous) => ({
      ...previous,
      [taskId]: developerId,
    }));

    setTaskErrors((previous) => ({
      ...previous,
      [taskId]: "",
    }));

    setSuccess("");
  };

  const handleAssignTask = async (task) => {
    const developerId = selectedDevelopers[task.id];

    if (!developerId) {
      setTaskErrors((previous) => ({
        ...previous,
        [task.id]: "Please select a developer.",
      }));

      return;
    }

    setAssigningTaskId(task.id);
    setSuccess("");

    setTaskErrors((previous) => ({
      ...previous,
      [task.id]: "",
    }));

    try {
      await api.put(
        `/api/projects/${projectId}/tasks/${task.id}/assign/${developerId}`
      );

      await loadTasks();

      setSuccess(
        `Developer assigned successfully to "${task.title}".`
      );
    } catch (err) {
      console.error("Failed to assign task:", err);

      setTaskErrors((previous) => ({
        ...previous,
        [task.id]: getErrorMessage(
          err,
          "Unable to assign developer."
        ),
      }));
    } finally {
      setAssigningTaskId(null);
    }
  };

  // =====================================
  // UI
  // =====================================

  return (
    <div className="project-details-card">

      {/* HEADER */}

      <div className="task-section-header">
        <div>
          <h2>Project Tasks</h2>

          <p>
            Create, update, assign, and generate AI task suggestions.
          </p>
        </div>

        <div className="project-actions">

          <button
            type="button"
            className="primary-btn"
            onClick={() => {
              loadTasks();
              loadMembers();
            }}
            disabled={loading}
          >
            <RefreshCw size={17} />
            Refresh
          </button>

          {/* MODULE 12: AI BUTTON */}

          <button
            type="button"
            className="primary-btn"
            onClick={handleGenerateAiTasks}
            disabled={aiLoading}
            style={{
              backgroundColor: "#7c3aed",
              color: "#ffffff",
            }}
          >
            <Sparkles size={17} />

            {aiLoading
              ? "Generating..."
              : "Generate AI Tasks"}
          </button>

          <button
            type="button"
            className="primary-btn"
            onClick={() => {
              setShowForm((previous) => !previous);
              setFormError("");
            }}
          >
            {showForm ? <X size={17} /> : <Plus size={17} />}
            {showForm ? "Close" : "Create Task"}
          </button>

        </div>
      </div>

      {success && <p className="task-success">{success}</p>}

      {/* =====================================
          MODULE 12: AI TASK SUGGESTIONS
          ===================================== */}

      {showAiSuggestions && (
        <div
          style={{
            marginTop: "20px",
            marginBottom: "25px",
            padding: "20px",
            backgroundColor: "#f8fafc",
            border: "1px solid #ddd6fe",
            borderRadius: "12px",
            color: "#1e293b",
          }}
        >
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              gap: "12px",
              flexWrap: "wrap",
            }}
          >
            <h3
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                color: "#6d28d9",
              }}
            >
              <Sparkles size={20} />
              Gemini AI Task Recommendations
            </h3>

            <button
              type="button"
              className="cancel-btn"
              onClick={() => setShowAiSuggestions(false)}
            >
              <X size={16} />
              Close
            </button>
          </div>

          <p style={{ marginTop: "10px" }}>
            Review the suggested tasks and choose which ones
            you want to add to your project.
          </p>

          {aiLoading && (
            <p style={{ marginTop: "15px" }}>
              Gemini is analyzing your project and generating tasks...
            </p>
          )}

          {aiError && (
            <p className="error" style={{ marginTop: "15px" }}>
              {aiError}
            </p>
          )}

          {!aiLoading && aiSuggestions.length > 0 && (
            <div
              style={{
                display: "grid",
                gap: "14px",
                marginTop: "18px",
              }}
            >
              {aiSuggestions.map((suggestion, index) => (
                <div
                  key={`${suggestion.title}-${index}`}
                  style={{
                    padding: "16px",
                    backgroundColor: "#ffffff",
                    border: "1px solid #e2e8f0",
                    borderRadius: "10px",
                    color: "#1e293b",
                  }}
                >
                  <h4
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: "8px",
                      marginBottom: "10px",
                    }}
                  >
                    <Lightbulb size={18} color="#7c3aed" />
                    {suggestion.title}
                  </h4>

                  <p
                    style={{
                      lineHeight: "1.7",
                      marginBottom: "12px",
                      whiteSpace: "pre-wrap",
                    }}
                  >
                    {suggestion.description}
                  </p>

                  <p
                    style={{
                      marginBottom: "14px",
                    }}
                  >
                    <strong>Suggested Priority:</strong>{" "}
                    <span
                      style={{
                        fontWeight: "600",
                        color:
                          suggestion.priority === "HIGH"
                            ? "#dc2626"
                            : suggestion.priority === "MEDIUM"
                              ? "#d97706"
                              : "#16a34a",
                      }}
                    >
                      {suggestion.priority}
                    </span>
                  </p>

                  <button
                    type="button"
                    className="primary-btn"
                    onClick={() => handleUseAiTask(suggestion)}
                  >
                    <CheckCircle2 size={17} />
                    Use This Task
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* CREATE TASK FORM */}

      {showForm && (
        <form
          className="create-project-form"
          onSubmit={handleCreateTask}
        >
          <h3>
            {selectedAiTask
              ? "Create Task from AI Suggestion"
              : "Create New Task"}
          </h3>

          {selectedAiTask && (
            <p
              style={{
                color: "#6d28d9",
                fontWeight: "600",
              }}
            >
              <Sparkles
                size={16}
                style={{ verticalAlign: "middle" }}
              />{" "}
              AI-generated suggestion selected. Review and
              edit the details before saving.
            </p>
          )}

          {formError && <p className="error">{formError}</p>}

          <label htmlFor="task-title">Task Title *</label>

          <input
            id="task-title"
            type="text"
            name="title"
            value={form.title}
            onChange={handleChange}
            maxLength={150}
            required
          />

          <label htmlFor="task-description">
            Description
          </label>

          <textarea
            id="task-description"
            name="description"
            value={form.description}
            onChange={handleChange}
            maxLength={2000}
          />

          <label htmlFor="task-deadline">
            <CalendarDays size={16} /> Deadline
          </label>

          <input
            id="task-deadline"
            type="date"
            name="deadline"
            value={form.deadline}
            onChange={handleChange}
          />

          <p className="task-form-note">
            New tasks start with To Do status.
          </p>

          <div className="project-actions">
            <button
              type="submit"
              className="primary-btn"
              disabled={saving}
            >
              <Plus size={17} />

              {saving ? "Creating..." : "Save Task"}
            </button>

            <button
              type="button"
              className="cancel-btn"
              onClick={() => {
                setShowForm(false);
                setSelectedAiTask(null);
              }}
              disabled={saving}
            >
              Cancel
            </button>
          </div>
        </form>
      )}

      {/* TASK LIST */}

      {loading ? (
        <p>Loading tasks...</p>
      ) : error ? (
        <p className="error">{error}</p>
      ) : tasks.length === 0 ? (
        <div className="task-empty-state">
          <ClipboardList size={38} />

          <h3>No tasks yet</h3>

          <p>Click Create Task to add your first task.</p>
        </div>
      ) : (
        <div className="task-list">

          {tasks.map((task) => {
            const selectedStatus =
              selectedStatuses[task.id] || "TODO";

            const selectedDeveloper =
              selectedDevelopers[task.id] || "";

            const statusChanged =
              selectedStatus !== task.status;

            const assignmentChanged =
              selectedDeveloper !==
              String(task.assignedToId ?? "");

            return (
              <div className="task-card" key={task.id}>

                <h3>{task.title}</h3>

                <p>
                  {task.description || "No description provided."}
                </p>

                <p>
                  <strong>Current Status:</strong>{" "}
                  {task.status?.replaceAll("_", " ")}
                </p>

                {task.deadline && (
                  <p>
                    <strong>Deadline:</strong> {task.deadline}
                  </p>
                )}

                <p>
                  <UserRound
                    size={16}
                    style={{ verticalAlign: "middle" }}
                  />{" "}
                  <strong>Assigned Developer:</strong>{" "}
                  {task.assignedToName || "Not Assigned"}
                </p>

                {/* UPDATE STATUS */}

                <div className="task-status-editor">

                  <label htmlFor={`status-${task.id}`}>
                    Update Status
                  </label>

                  <select
                    id={`status-${task.id}`}
                    value={selectedStatus}
                    onChange={(event) =>
                      handleStatusChange(
                        task.id,
                        event.target.value
                      )
                    }
                    disabled={updatingTaskId === task.id}
                  >
                    {STATUS_OPTIONS.map((option) => (
                      <option
                        key={option.value}
                        value={option.value}
                      >
                        {option.label}
                      </option>
                    ))}
                  </select>

                  <button
                    type="button"
                    className="primary-btn"
                    onClick={() => handleUpdateStatus(task)}
                    disabled={
                      !statusChanged ||
                      updatingTaskId === task.id
                    }
                  >
                    <Save size={16} />

                    {updatingTaskId === task.id
                      ? "Saving..."
                      : "Save Status"}
                  </button>
                </div>

                {/* ASSIGN DEVELOPER */}

                <div className="task-status-editor">

                  <label htmlFor={`developer-${task.id}`}>
                    Assign Developer
                  </label>

                  <select
                    id={`developer-${task.id}`}
                    value={selectedDeveloper}
                    onChange={(event) =>
                      handleDeveloperChange(
                        task.id,
                        event.target.value
                      )
                    }
                    disabled={assigningTaskId === task.id}
                  >
                    <option value="">
                      Select Developer
                    </option>

                    {members.map((member) => (
                      <option
                        key={member.developerId}
                        value={String(member.developerId)}
                      >
                        {member.developerName}
                      </option>
                    ))}
                  </select>

                  <button
                    type="button"
                    className="primary-btn"
                    onClick={() => handleAssignTask(task)}
                    disabled={
                      !selectedDeveloper ||
                      !assignmentChanged ||
                      assigningTaskId === task.id
                    }
                  >
                    <UserPlus size={16} />

                    {assigningTaskId === task.id
                      ? "Assigning..."
                      : "Assign Task"}
                  </button>
                </div>

                {taskErrors[task.id] && (
                  <p className="error">{taskErrors[task.id]}</p>
                )}
              </div>
            );
          })}
        </div>
      )}

      {membersError && (
        <p className="error">{membersError}</p>
      )}

      {!loading && !error && (
        <p className="task-count">
          Total Tasks: <strong>{tasks.length}</strong>
        </p>
      )}
    </div>
  );
}

export default ProjectTasks;