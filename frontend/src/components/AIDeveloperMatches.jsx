import { useState } from "react";
import { Link } from "react-router-dom";
import {
  Sparkles,
  Users,
  UserRound,
  Code2,
  ExternalLink,
  RefreshCw,
  X,
  AlertCircle,
} from "lucide-react";

import api from "../services/api";

function AIDeveloperMatches({ projectId }) {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showMatches, setShowMatches] = useState(false);

  const findDevelopers = async () => {
    setLoading(true);
    setError("");
    setShowMatches(true);

    try {
      const response = await api.get(
        `/api/ai/projects/${projectId}/developer-matches`
      );

      setMatches(
        Array.isArray(response.data) ? response.data : []
      );
    } catch (err) {
      console.error("AI Developer Matching Error:", err);

      setMatches([]);

      if (err.response?.status === 401) {
        setError("Your session has expired. Please log in again.");
      } else if (err.response?.status === 403) {
        setError("You do not have access to this project.");
      } else {
        setError(
          typeof err.response?.data?.error === "string"
            ? err.response.data.error
            : "Unable to find matching developers."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  const getMatchColor = (score) => {
    if (score >= 80) return "#4ade80";
    if (score >= 50) return "#fbbf24";
    return "#94a3b8";
  };

  const cardStyle = {
    background: "#0f172a",
    border: "1px solid #334155",
    borderRadius: 12,
    padding: 20,
  };

  return (
    <section className="project-details-card">
      <div className="task-section-header">
        <div>
          <h2
            style={{
              display: "flex",
              alignItems: "center",
              gap: 9,
            }}
          >
            <Users size={22} />
            AI Developer Matching
          </h2>

          <p>
            Discover developers whose skills match your
            project's technologies.
          </p>
        </div>

        <button
          type="button"
          className="primary-btn"
          onClick={findDevelopers}
          disabled={loading}
          style={{
            background: "#7c3aed",
            color: "#ffffff",
          }}
        >
          {loading ? (
            <RefreshCw size={17} />
          ) : (
            <Sparkles size={17} />
          )}

          {loading
            ? "Finding Developers..."
            : "Find Developers with AI"}
        </button>
      </div>

      {showMatches && (
        <div style={{ marginTop: 22 }}>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              gap: 12,
              flexWrap: "wrap",
              marginBottom: 18,
            }}
          >
            <h3 style={{ margin: 0 }}>
              Recommended Developers
            </h3>

            <button
              type="button"
              onClick={() => setShowMatches(false)}
              style={{
                display: "inline-flex",
                alignItems: "center",
                gap: 6,
                background: "transparent",
                color: "#cbd5e1",
                border: "1px solid #475569",
                borderRadius: 8,
                padding: "8px 12px",
                cursor: "pointer",
              }}
            >
              <X size={16} />
              Close
            </button>
          </div>

          {loading && (
            <div
              role="status"
              style={{
                ...cardStyle,
                color: "#94a3b8",
              }}
            >
              <RefreshCw
                size={18}
                style={{
                  verticalAlign: "middle",
                  marginRight: 8,
                }}
              />
              Gemini is analyzing developer skills.
              Please wait...
            </div>
          )}

          {error && (
            <div
              role="alert"
              style={{
                ...cardStyle,
                color: "#fca5a5",
                borderColor: "#7f1d1d",
              }}
            >
              <AlertCircle
                size={18}
                style={{
                  verticalAlign: "middle",
                  marginRight: 8,
                }}
              />
              {error}
            </div>
          )}

          {!loading && !error && matches.length === 0 && (
            <div
              style={{
                ...cardStyle,
                textAlign: "center",
                color: "#94a3b8",
              }}
            >
              <Users size={30} />

              <p style={{ marginTop: 12 }}>
                No matching developers found.
                Try adding more developer profiles
                or updating project technologies.
              </p>
            </div>
          )}

          {!loading && !error && matches.length > 0 && (
            <div
              style={{
                display: "grid",
                gap: 16,
              }}
            >
              {matches.map((developer) => {
                const matchScore = Number(
                  developer.matchScore
                ) || 0;

                const matchColor = getMatchColor(
                  matchScore
                );

                return (
                  <article
                    key={developer.developerId}
                    style={cardStyle}
                  >
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                        flexWrap: "wrap",
                        gap: 12,
                      }}
                    >
                      <h3
                        style={{
                          display: "flex",
                          alignItems: "center",
                          gap: 9,
                          margin: 0,
                          color: "#f8fafc",
                        }}
                      >
                        <UserRound size={20} />
                        {developer.developerName}
                      </h3>

                      <span
                        style={{
                          background: "#17243a",
                          border: `1px solid ${matchColor}`,
                          color: matchColor,
                          padding: "7px 12px",
                          borderRadius: 20,
                          fontWeight: 700,
                          fontSize: 13,
                        }}
                      >
                        {matchScore}% Match
                      </span>
                    </div>

                    <p
                      style={{
                        marginTop: 15,
                        color: "#cbd5e1",
                        lineHeight: 1.7,
                      }}
                    >
                      <Code2
                        size={16}
                        style={{
                          verticalAlign: "middle",
                          marginRight: 6,
                          color: "#38bdf8",
                        }}
                      />
                      <strong>Skills:</strong>{" "}
                      {developer.skills || "Not specified"}
                    </p>

                    <div
                      style={{
                        marginTop: 15,
                        padding: 16,
                        background: "#17243a",
                        borderLeft: "4px solid #a78bfa",
                        borderRadius: 9,
                      }}
                    >
                      <p
                        style={{
                          margin: 0,
                          color: "#cbd5e1",
                          lineHeight: 1.7,
                        }}
                      >
                        <Sparkles
                          size={16}
                          style={{
                            verticalAlign: "middle",
                            color: "#a78bfa",
                            marginRight: 7,
                          }}
                        />
                        {developer.reason ||
                          "No explanation available."}
                      </p>
                    </div>

                    <Link
                      to={`/developers/${developer.developerId}`}
                      className="primary-btn"
                      style={{
                        display: "inline-flex",
                        alignItems: "center",
                        gap: 8,
                        marginTop: 18,
                        textDecoration: "none",
                      }}
                    >
                      <ExternalLink size={16} />
                      View Profile
                    </Link>
                  </article>
                );
              })}
            </div>
          )}
        </div>
      )}
    </section>
  );
}

export default AIDeveloperMatches;