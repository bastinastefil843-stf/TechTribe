import { useState } from "react";
import {
  Bot,
  Search,
  Loader2,
  Lightbulb,
  ShieldCheck,
  AlertTriangle,
  Sparkles,
} from "lucide-react";
import api from "../services/api";

function AICodeAssistant({ snippetId }) {
  const [explanation, setExplanation] = useState("");
  const [review, setReview] = useState(null);
  const [loading, setLoading] = useState("");
  const [error, setError] = useState("");

  const explainCode = async () => {
    setLoading("explain");
    setError("");

    try {
      const response = await api.post(
        `/api/ai/code-snippets/${snippetId}/explain`
      );

      setExplanation(response.data.explanation || "");
    } catch (err) {
      console.error("AI explanation failed:", err);
      setError(
        typeof err.response?.data?.error === "string"
          ? err.response.data.error
          : "Unable to explain this code. Please try again."
      );
    } finally {
      setLoading("");
    }
  };

  const reviewCode = async () => {
    setLoading("review");
    setError("");

    try {
      const response = await api.get(
        `/api/ai/code-snippets/${snippetId}/review`
      );

      setReview(response.data);
    } catch (err) {
      console.error("AI code review failed:", err);
      setError(
        typeof err.response?.data?.error === "string"
          ? err.response.data.error
          : "Unable to review this code. Please try again."
      );
    } finally {
      setLoading("");
    }
  };

  const panelStyle = {
    marginTop: 16,
    padding: 20,
    background: "#17243a",
    color: "#e2e8f0",
    border: "1px solid #334155",
    borderRadius: 14,
  };

  const buttonStyle = {
    display: "inline-flex",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
    padding: "11px 16px",
    border: "none",
    borderRadius: 10,
    fontWeight: 600,
    cursor: "pointer",
    transition: "opacity 0.2s ease",
  };

  const headingStyle = {
    display: "flex",
    alignItems: "center",
    gap: 9,
    margin: "0 0 14px",
  };

  const score = Number(review?.overallScore);
  const validScore = Number.isFinite(score)
    ? Math.max(0, Math.min(100, score))
    : null;

  const scoreColor =
    validScore === null
      ? "#94a3b8"
      : validScore >= 80
      ? "#4ade80"
      : validScore >= 50
      ? "#fbbf24"
      : "#f87171";

  return (
    <div style={{ marginTop: 18 }}>
      <div
        style={{
          display: "flex",
          flexWrap: "wrap",
          gap: 10,
        }}
      >
        <button
          type="button"
          onClick={explainCode}
          disabled={Boolean(loading)}
          style={{
            ...buttonStyle,
            background: "#0891b2",
            color: "#ffffff",
            opacity: loading ? 0.7 : 1,
          }}
        >
          {loading === "explain" ? (
            <Loader2 size={17} />
          ) : (
            <Bot size={17} />
          )}

          {loading === "explain"
            ? "Explaining..."
            : "Explain Code"}
        </button>

        <button
          type="button"
          onClick={reviewCode}
          disabled={Boolean(loading)}
          style={{
            ...buttonStyle,
            background: "#6366f1",
            color: "#ffffff",
            opacity: loading ? 0.7 : 1,
          }}
        >
          {loading === "review" ? (
            <Loader2 size={17} />
          ) : (
            <Search size={17} />
          )}

          {loading === "review"
            ? "Reviewing..."
            : "Review Code"}
        </button>
      </div>

      {loading && (
        <p
          role="status"
          style={{
            color: "#94a3b8",
            marginTop: 12,
          }}
        >
          AI is processing your request. Please wait...
        </p>
      )}

      {error && (
        <div
          role="alert"
          style={{
            ...panelStyle,
            background: "#3b1823",
            borderColor: "#7f1d1d",
            color: "#fca5a5",
          }}
        >
          <AlertTriangle
            size={17}
            style={{ verticalAlign: "middle", marginRight: 8 }}
          />
          {error}
        </div>
      )}

      {explanation && (
        <section style={panelStyle}>
          <h3
            style={{
              ...headingStyle,
              color: "#67e8f9",
            }}
          >
            <Bot size={20} />
            AI Code Explanation
          </h3>

          <div
            style={{
              whiteSpace: "pre-wrap",
              lineHeight: 1.8,
              overflowWrap: "anywhere",
              color: "#cbd5e1",
            }}
          >
            {explanation}
          </div>
        </section>
      )}

      {review && (
        <section style={panelStyle}>
          <h3
            style={{
              ...headingStyle,
              color: "#a5b4fc",
            }}
          >
            <ShieldCheck size={20} />
            AI Code Review
          </h3>

          <p
            style={{
              lineHeight: 1.7,
              color: "#cbd5e1",
            }}
          >
            {review.summary || "No summary available."}
          </p>

          <div
            style={{
              marginTop: 16,
              padding: 18,
              background: "#0f172a",
              border: "1px solid #334155",
              borderRadius: 12,
            }}
          >
            <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                gap: 12,
                flexWrap: "wrap",
              }}
            >
              <strong>Code Quality Score</strong>

              <strong
                style={{
                  fontSize: 22,
                  color: scoreColor,
                }}
              >
                {validScore === null
                  ? "Not available"
                  : `${validScore}/100`}
              </strong>
            </div>

            {validScore !== null && (
              <div
                role="progressbar"
                aria-valuenow={validScore}
                aria-valuemin={0}
                aria-valuemax={100}
                aria-label="Code quality score"
                style={{
                  height: 9,
                  background: "#334155",
                  borderRadius: 20,
                  overflow: "hidden",
                  marginTop: 14,
                }}
              >
                <div
                  style={{
                    width: `${validScore}%`,
                    height: "100%",
                    background: scoreColor,
                    borderRadius: 20,
                  }}
                />
              </div>
            )}
          </div>

          <h4
            style={{
              marginTop: 24,
              color: "#f8fafc",
            }}
          >
            Issues Found ({review.issues?.length || 0})
          </h4>

          {review.issues?.length ? (
            review.issues.map((issue, index) => (
              <div
                key={index}
                style={{
                  marginTop: 12,
                  padding: 16,
                  background: "#0f172a",
                  border: "1px solid #334155",
                  borderRadius: 10,
                }}
              >
                <strong style={{ color: "#fbbf24" }}>
                  {issue.category || "Issue"}
                  {" — "}
                  {issue.severity || "Unspecified"}
                </strong>

                <p
                  style={{
                    marginTop: 10,
                    color: "#cbd5e1",
                    lineHeight: 1.7,
                  }}
                >
                  {issue.description}
                </p>

                <p
                  style={{
                    marginTop: 10,
                    color: "#94a3b8",
                    lineHeight: 1.7,
                  }}
                >
                  <Lightbulb
                    size={16}
                    style={{
                      verticalAlign: "middle",
                      color: "#fbbf24",
                    }}
                  />{" "}
                  <strong>Suggestion:</strong>{" "}
                  {issue.suggestion}
                </p>
              </div>
            ))
          ) : (
            <p style={{ color: "#94a3b8" }}>
              No significant issues identified.
            </p>
          )}

          <h4
            style={{
              ...headingStyle,
              marginTop: 24,
              color: "#f8fafc",
            }}
          >
            <Sparkles size={18} color="#a78bfa" />
            Suggested Improvements
          </h4>

          {review.improvements?.length ? (
            <ul
              style={{
                paddingLeft: 24,
                color: "#cbd5e1",
                lineHeight: 1.8,
              }}
            >
              {review.improvements.map((improvement, index) => (
                <li key={index} style={{ marginBottom: 8 }}>
                  {improvement}
                </li>
              ))}
            </ul>
          ) : (
            <p style={{ color: "#94a3b8" }}>
              No additional improvements suggested.
            </p>
          )}
        </section>
      )}
    </div>
  );
}

export default AICodeAssistant;