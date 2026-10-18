import { useCallback, useEffect, useState } from "react";
import {
  Code2,
  Plus,
  Copy,
  Pencil,
  Trash2,
  RefreshCw,
  X,
} from "lucide-react";

import api from "../services/api";

// Module 11: AI Code Explanation & Review
import AICodeAssistant from "./AICodeAssistant";

const emptyForm = {
  title: "",
  language: "Java",
  description: "",
  code: "",
};

function ProjectSnippets({ projectId }) {
  const [snippets, setSnippets] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(emptyForm);

  // ==========================================
  // LOAD SNIPPETS AND USER
  // ==========================================

  const loadData = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const [snippetsResponse, userResponse] =
        await Promise.all([
          api.get(`/api/projects/${projectId}/snippets`),
          api.get("/api/users/me"),
        ]);

      setSnippets(
        Array.isArray(snippetsResponse.data)
          ? snippetsResponse.data
          : []
      );

      setCurrentUser(userResponse.data);
    } catch (err) {
      console.error("Failed to load snippets:", err);

      setError(
        err.response?.status === 403
          ? "You do not have access to these project snippets."
          : "Unable to load project snippets."
      );
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  // ==========================================
  // FORM HANDLING
  // ==========================================

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
    setShowForm(false);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      if (editingId) {
        await api.put(
          `/api/projects/${projectId}/snippets/${editingId}`,
          form
        );

        setSuccess("Code snippet updated successfully!");
      } else {
        await api.post(
          `/api/projects/${projectId}/snippets`,
          form
        );

        setSuccess("Code snippet created successfully!");
      }

      resetForm();
      await loadData();
    } catch (err) {
      console.error("Failed to save snippet:", err);

      setError(
        err.response?.data?.message ||
          (typeof err.response?.data === "string"
            ? err.response.data
            : "Unable to save code snippet.")
      );
    } finally {
      setSaving(false);
    }
  };

  // ==========================================
  // EDIT SNIPPET
  // ==========================================

  const handleEdit = (snippet) => {
    setEditingId(snippet.id);

    setForm({
      title: snippet.title,
      language: snippet.language,
      description: snippet.description || "",
      code: snippet.code,
    });

    setShowForm(true);
    setError("");
    setSuccess("");
  };

  // ==========================================
  // DELETE SNIPPET
  // ==========================================

  const handleDelete = async (snippetId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this code snippet?"
    );

    if (!confirmed) return;

    setError("");
    setSuccess("");

    try {
      await api.delete(
        `/api/projects/${projectId}/snippets/${snippetId}`
      );

      setSuccess("Code snippet deleted successfully!");
      await loadData();
    } catch (err) {
      console.error("Failed to delete snippet:", err);

      setError("Unable to delete code snippet.");
    }
  };

  // ==========================================
  // COPY CODE
  // ==========================================

  const handleCopy = async (code) => {
    try {
      await navigator.clipboard.writeText(code);

      setSuccess("Code copied to clipboard!");
      setError("");
    } catch {
      setError("Unable to copy code.");
    }
  };

  // ==========================================
  // UI
  // ==========================================

  return (
    <div className="project-details-card">

      {/* SECTION HEADER */}

      <div className="task-section-header">
        <div>
          <h2>
            <Code2 size={22} />
            Project Code Snippets
          </h2>

          <p>
            Share reusable code with your project team
            and analyze it using Gemini AI.
          </p>
        </div>

        <div
          style={{
            display: "flex",
            gap: "10px",
            flexWrap: "wrap",
          }}
        >
          <button
            className="primary-btn"
            onClick={loadData}
            disabled={loading}
          >
            <RefreshCw size={16} />
            Refresh
          </button>

          <button
            className="primary-btn"
            onClick={() => {
              resetForm();
              setShowForm(true);
            }}
          >
            <Plus size={16} />
            Add Snippet
          </button>
        </div>
      </div>

      {/* SUCCESS / ERROR */}

      {error && <p className="error">{error}</p>}

      {success && (
        <p className="task-success">{success}</p>
      )}

      {/* CREATE / EDIT FORM */}

      {showForm && (
        <form
          className="create-project-form"
          onSubmit={handleSubmit}
        >
          <h3>
            {editingId ? "Edit Code Snippet" : "New Code Snippet"}
          </h3>

          <label>Snippet Title</label>

          <input
            name="title"
            value={form.title}
            onChange={handleChange}
            maxLength={150}
            required
            placeholder="Example: Java Binary Search"
          />

          <label>Programming Language</label>

          <select
            name="language"
            value={form.language}
            onChange={handleChange}
          >
            <option value="Java">Java</option>
            <option value="Python">Python</option>
            <option value="JavaScript">JavaScript</option>
            <option value="C++">C++</option>
            <option value="C">C</option>
            <option value="HTML">HTML</option>
            <option value="CSS">CSS</option>
            <option value="SQL">SQL</option>
            <option value="Other">Other</option>
          </select>

          <label>Description</label>

          <textarea
            name="description"
            value={form.description}
            onChange={handleChange}
            maxLength={2000}
            placeholder="Explain what this code does..."
          />

          <label>Source Code</label>

          <textarea
            name="code"
            value={form.code}
            onChange={handleChange}
            maxLength={100000}
            required
            rows={12}
            spellCheck={false}
            style={{
              fontFamily: "monospace",
              fontSize: "14px",
            }}
            placeholder="Paste your code here..."
          />

          <div
            style={{
              display: "flex",
              gap: "10px",
              marginTop: "15px",
            }}
          >
            <button
              type="submit"
              className="primary-btn"
              disabled={saving}
            >
              {saving
                ? "Saving..."
                : editingId
                  ? "Update Snippet"
                  : "Publish Snippet"}
            </button>

            <button
              type="button"
              className="cancel-btn"
              onClick={resetForm}
            >
              <X size={16} />
              Cancel
            </button>
          </div>
        </form>
      )}

      {/* SNIPPET LIST */}

      {loading ? (
        <p>Loading code snippets...</p>
      ) : snippets.length === 0 ? (
        <p>No code snippets yet. Create the first one!</p>
      ) : (
        <div className="task-list">

          {snippets.map((snippet) => {
            const isAuthor =
              Number(snippet.authorId) ===
              Number(currentUser?.id);

            return (
              <div
                className="task-card"
                key={snippet.id}
              >

                {/* SNIPPET TITLE */}

                <h3>{snippet.title}</h3>

                {/* SNIPPET INFORMATION */}

                <p>
                  <strong>Language:</strong>{" "}
                  {snippet.language}
                </p>

                <p>
                  <strong>Author:</strong>{" "}
                  {snippet.authorName}
                </p>

                {snippet.description && (
                  <p>{snippet.description}</p>
                )}

                {/* SOURCE CODE */}

                <pre
                  style={{
                    background: "#0f172a",
                    color: "#e2e8f0",
                    padding: "18px",
                    borderRadius: "10px",
                    overflowX: "auto",
                    fontSize: "13px",
                  }}
                >
                  <code>{snippet.code}</code>
                </pre>

                {/* EXISTING SNIPPET ACTIONS */}

                <div
                  style={{
                    display: "flex",
                    gap: "10px",
                    flexWrap: "wrap",
                    marginTop: "15px",
                  }}
                >

                  <button
                    className="primary-btn"
                    onClick={() =>
                      handleCopy(snippet.code)
                    }
                  >
                    <Copy size={16} />
                    Copy Code
                  </button>

                  {isAuthor && (
                    <button
                      className="primary-btn"
                      onClick={() =>
                        handleEdit(snippet)
                      }
                    >
                      <Pencil size={16} />
                      Edit
                    </button>
                  )}

                  {isAuthor && (
                    <button
                      className="cancel-btn"
                      onClick={() =>
                        handleDelete(snippet.id)
                      }
                    >
                      <Trash2 size={16} />
                      Delete
                    </button>
                  )}

                </div>

                {/* =====================================
                    MODULE 11: AI CODE ASSISTANT
                    ===================================== */}

                <div
                  style={{
                    marginTop: "20px",
                    paddingTop: "18px",
                    borderTop: "1px solid #e2e8f0",
                  }}
                >
                  <AICodeAssistant
                    snippetId={snippet.id}
                  />
                </div>

              </div>
            );
          })}

        </div>
      )}
    </div>
  );
}

export default ProjectSnippets;