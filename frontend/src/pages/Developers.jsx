import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  Search,
  Users,
  Code2,
  ArrowRight,
  RefreshCw,
} from "lucide-react";

import api from "../services/api";

function Developers() {
  const [developers, setDevelopers] = useState([]);
  const [statuses, setStatuses] = useState({});
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadDevelopers = useCallback(async (keyword = "") => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get("/api/developers", {
        params: keyword.trim()
          ? { search: keyword.trim() }
          : {},
      });

      const developerList = Array.isArray(response.data)
        ? response.data
        : [];

      setDevelopers(developerList);

      const results = await Promise.all(
        developerList.map(async (developer) => {
          try {
            const statusResponse = await api.get(
              `/api/connections/status/${developer.userId}`
            );

            return [
              developer.userId,
              statusResponse.status === 204
                ? null
                : statusResponse.data,
            ];
          } catch (err) {
            console.error(err);
            return [developer.userId, null];
          }
        })
      );

      setStatuses(Object.fromEntries(results));
    } catch (err) {
      console.error(err);
      setError("Unable to load developers.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadDevelopers();
  }, [loadDevelopers]);

  const handleSearch = (event) => {
    event.preventDefault();
    loadDevelopers(search);
  };

  const handleConnect = async (developerId) => {
    setBusyId(developerId);
    setError("");
    setMessage("");

    try {
      await api.post(
        `/api/connections/request/${developerId}`
      );

      setMessage("Connection request sent!");
      await loadDevelopers(search);
    } catch (err) {
      setError(
        err.response?.data?.error ||
          "Unable to send connection request."
      );
    } finally {
      setBusyId(null);
    }
  };

  const getSkills = (skills) =>
    skills
      ? skills.split(",").map((s) => s.trim()).filter(Boolean)
      : [];

  return (
    <div className="dashboard">
      <div className="dashboard-heading">
        <div>
          <h1>Developer Directory</h1>
          <p>Find developers and build your Tech Tribe network.</p>
        </div>

        <Link to="/connections" className="primary-btn">
          My Connections
        </Link>
      </div>

      <div
        className="project-details-card"
        style={{ marginBottom: "24px" }}
      >
        <h2>Find Developers</h2>

        <form
          onSubmit={handleSearch}
          style={{
            display: "flex",
            gap: "12px",
            flexWrap: "wrap",
            marginTop: "16px",
          }}
        >
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Search by name or skill..."
            style={{
              flex: "1 1 250px",
              padding: "12px",
              border: "1px solid #d1d5db",
              borderRadius: "8px",
            }}
          />

          <button className="primary-btn" disabled={loading}>
            <Search size={17} />
            Search
          </button>

          <button
            type="button"
            onClick={() => {
              setSearch("");
              loadDevelopers("");
            }}
          >
            Clear
          </button>
        </form>
      </div>

      {error && <p style={{ color: "red" }}>{error}</p>}
      {message && <p style={{ color: "green" }}>{message}</p>}

      {loading ? (
        <p>Loading developers...</p>
      ) : developers.length === 0 ? (
        <div className="project-details-card">
          <Users size={30} />
          <p>No developers found.</p>
        </div>
      ) : (
        <div
          style={{
            display: "grid",
            gridTemplateColumns:
              "repeat(auto-fit, minmax(280px, 1fr))",
            gap: "20px",
          }}
        >
          {developers.map((developer) => {
            const connection = statuses[developer.userId];
            const skills = getSkills(developer.skills);

            let statusText = "Connect";

            if (connection?.status === "ACCEPTED") {
              statusText = "Connected";
            } else if (connection?.status === "PENDING") {
              statusText = "Pending";
            } else if (connection?.status === "REJECTED") {
              statusText = "Request Rejected";
            }

            return (
              <div
                key={developer.userId}
                className="project-details-card"
              >
                <h3>{developer.name}</h3>

                <p>
                  {developer.bio || "No bio available."}
                </p>

                <h4>
                  <Code2 size={17} /> Skills
                </h4>

                <div
                  style={{
                    display: "flex",
                    gap: "8px",
                    flexWrap: "wrap",
                    marginBottom: "16px",
                  }}
                >
                  {skills.map((skill, index) => (
                    <span
                      key={index}
                      style={{
                        background: "#e0f2fe",
                        color: "#0369a1",
                        padding: "6px 10px",
                        borderRadius: "16px",
                      }}
                    >
                      {skill}
                    </span>
                  ))}
                </div>

                <div
                  style={{
                    display: "flex",
                    gap: "10px",
                    flexWrap: "wrap",
                  }}
                >
                  <Link
                    to={`/developers/${developer.userId}`}
                    className="primary-btn"
                  >
                    View Profile
                    <ArrowRight size={16} />
                  </Link>

                  <button
                    className="primary-btn"
                    disabled={
                      Boolean(connection) ||
                      busyId === developer.userId
                    }
                    onClick={() =>
                      handleConnect(developer.userId)
                    }
                  >
                    {statusText}
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

export default Developers;