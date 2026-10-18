import { useCallback, useEffect, useState } from "react";

import {
  UserRound,
  ExternalLink,
  Code2,
  Pencil,
  Save,
  X,
  RefreshCw,
} from "lucide-react";

import api from "../services/api";

// ==========================================
// DEFAULT FORM VALUES
// ==========================================

const emptyForm = {
  bio: "",
  skills: "",
  githubUrl: "",
  linkedinUrl: "",
};

function Profile() {
  // ==========================================
  // STATE MANAGEMENT
  // ==========================================

  const [profile, setProfile] = useState(null);
  const [user, setUser] = useState(null);

  const [form, setForm] = useState(emptyForm);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [editing, setEditing] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // ==========================================
  // 1. LOAD DEVELOPER PROFILE
  // ==========================================

  const loadProfile = useCallback(async () => {
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      // Get logged-in user information
      const userResponse = await api.get("/api/users/me");

      setUser(userResponse.data);

      try {
        // Get developer profile
        const profileResponse = await api.get(
          "/api/profiles/me"
        );

        const profileData = profileResponse.data;

        setProfile(profileData);

        setForm({
          bio: profileData.bio || "",
          skills: profileData.skills || "",
          githubUrl: profileData.githubUrl || "",
          linkedinUrl: profileData.linkedinUrl || "",
        });

        setEditing(false);

      } catch (profileError) {
        if (profileError.response?.status === 404) {
          // No profile exists yet
          setProfile(null);

          setForm({ ...emptyForm });

          setEditing(true);
        } else {
          throw profileError;
        }
      }

    } catch (err) {
      console.error("Failed to load profile:", err);

      if (err.response?.status === 401) {
        setError(
          "Your session has expired. Please log in again."
        );
      } else {
        setError(
          "Unable to load developer profile. Check your backend server."
        );
      }

    } finally {
      setLoading(false);
    }
  }, []);

  // ==========================================
  // 2. LOAD PROFILE ON PAGE OPEN
  // ==========================================

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  // ==========================================
  // 3. HANDLE INPUT CHANGES
  // ==========================================

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  // ==========================================
  // 4. SAVE PROFILE
  // ==========================================

  const handleSave = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      let response;

      if (profile) {
        // Update existing profile
        response = await api.put(
          "/api/profiles/me",
          form
        );
      } else {
        // Create new profile
        response = await api.post(
          "/api/profiles",
          form
        );
      }

      setProfile(response.data);

      setEditing(false);

      setSuccess(
        "Developer profile saved successfully!"
      );

    } catch (err) {
      console.error("Failed to save profile:", err);

      const responseData = err.response?.data;

      if (err.response?.status === 409) {
        setError(
          "A developer profile already exists. Refresh the page."
        );
      } else if (err.response?.status === 400) {
        setError(
          typeof responseData === "string"
            ? responseData
            : responseData?.message ||
                "Please check your profile information."
        );
      } else {
        setError(
          typeof responseData === "string"
            ? responseData
            : responseData?.message ||
                "Unable to save developer profile."
        );
      }

    } finally {
      setSaving(false);
    }
  };

  // ==========================================
  // 5. CANCEL EDITING
  // ==========================================

  const handleCancel = () => {
    if (!profile) {
      setForm({ ...emptyForm });
      return;
    }

    setForm({
      bio: profile.bio || "",
      skills: profile.skills || "",
      githubUrl: profile.githubUrl || "",
      linkedinUrl: profile.linkedinUrl || "",
    });

    setEditing(false);
    setError("");
  };

  // ==========================================
  // 6. FORMAT TECHNICAL SKILLS
  // ==========================================

  const skills = profile?.skills
    ? profile.skills
        .split(",")
        .map((skill) => skill.trim())
        .filter(Boolean)
    : [];

  // ==========================================
  // 7. USER INTERFACE
  // ==========================================

  return (
    <div className="dashboard">

      {/* ==================================
          PAGE HEADER
      ================================== */}

      <div className="dashboard-heading">
        <div>
          <h1>My Developer Profile</h1>

          <p>
            Manage your developer identity on Tech Tribe.
          </p>
        </div>

        <button
          className="primary-btn"
          onClick={loadProfile}
          disabled={loading}
        >
          <RefreshCw size={17} />
          Refresh
        </button>
      </div>

      {/* ==================================
          LOADING STATE
      ================================== */}

      {loading ? (
        <div className="project-details-card">
          <p>Loading developer profile...</p>
        </div>
      ) : (
        <>

          {/* ERROR MESSAGE */}

          {error && (
            <div className="project-details-card">
              <p className="error">{error}</p>
            </div>
          )}

          {/* SUCCESS MESSAGE */}

          {success && (
            <div className="project-details-card">
              <p style={{ color: "#16a34a" }}>
                {success}
              </p>
            </div>
          )}

          {/* ==================================
              DEVELOPER INFORMATION
          ================================== */}

          <div className="project-details-card">

            <h2>
              <UserRound size={24} />
              Developer Information
            </h2>

            <div className="project-info-grid">

              <p>
                <strong>Name:</strong>{" "}
                {user?.name || "Developer"}
              </p>

              <p>
                <strong>Email:</strong>{" "}
                {user?.email || "Not available"}
              </p>

              <p>
                <strong>Role:</strong>{" "}
                {user?.role || "DEVELOPER"}
              </p>

            </div>

          </div>

          {/* ==================================
              CREATE / EDIT PROFILE
          ================================== */}

          {editing ? (
            <div className="project-details-card">

              <h2>
                <Pencil size={22} />

                {profile
                  ? "Edit Developer Profile"
                  : "Create Developer Profile"}
              </h2>

              <form
                className="create-project-form"
                onSubmit={handleSave}
              >

                {/* BIO */}

                <label>About Me</label>

                <textarea
                  name="bio"
                  value={form.bio}
                  onChange={handleChange}
                  maxLength={2000}
                  rows={5}
                  placeholder="Tell other developers about yourself..."
                />

                {/* SKILLS */}

                <label>Technical Skills</label>

                <textarea
                  name="skills"
                  value={form.skills}
                  onChange={handleChange}
                  maxLength={1000}
                  rows={3}
                  placeholder="Java, Spring Boot, React, MySQL"
                />

                {/* GITHUB */}

                <label>GitHub Profile URL</label>

                <input
                  type="url"
                  name="githubUrl"
                  value={form.githubUrl}
                  onChange={handleChange}
                  maxLength={255}
                  placeholder="https://github.com/username"
                />

                {/* LINKEDIN */}

                <label>LinkedIn Profile URL</label>

                <input
                  type="url"
                  name="linkedinUrl"
                  value={form.linkedinUrl}
                  onChange={handleChange}
                  maxLength={255}
                  placeholder="https://www.linkedin.com/in/username"
                />

                {/* BUTTONS */}

                <div
                  style={{
                    display: "flex",
                    gap: "12px",
                    marginTop: "20px",
                    flexWrap: "wrap",
                  }}
                >

                  <button
                    type="submit"
                    className="primary-btn"
                    disabled={saving}
                  >
                    <Save size={17} />

                    {saving
                      ? "Saving..."
                      : "Save Profile"}
                  </button>

                  {profile && (
                    <button
                      type="button"
                      className="cancel-btn"
                      onClick={handleCancel}
                    >
                      <X size={17} />
                      Cancel
                    </button>
                  )}

                </div>

              </form>

            </div>

          ) : profile ? (
            <>

              {/* ==================================
                  ABOUT ME
              ================================== */}

              <div className="project-details-card">

                <h2>
                  <UserRound size={22} />
                  About Me
                </h2>

                <p>
                  {profile.bio ||
                    "No bio added yet."}
                </p>

              </div>

              {/* ==================================
                  TECHNICAL SKILLS
              ================================== */}

              <div className="project-details-card">

                <h2>
                  <Code2 size={22} />
                  Technical Skills
                </h2>

                <div
                  style={{
                    display: "flex",
                    gap: "10px",
                    flexWrap: "wrap",
                    marginTop: "15px",
                  }}
                >

                  {skills.length > 0 ? (
                    skills.map((skill, index) => (
                      <span
                        key={index}
                        style={{
                          padding: "8px 14px",
                          background: "#e0f2fe",
                          color: "#0369a1",
                          borderRadius: "20px",
                          fontWeight: "500",
                        }}
                      >
                        {skill}
                      </span>
                    ))
                  ) : (
                    <p>No skills added yet.</p>
                  )}

                </div>

              </div>

              {/* ==================================
                  DEVELOPER SOCIAL LINKS
              ================================== */}

              <div className="project-details-card">

                <h2>
                  <ExternalLink size={22} />
                  Developer Links
                </h2>

                <div
                  style={{
                    display: "flex",
                    gap: "15px",
                    flexWrap: "wrap",
                    marginTop: "15px",
                  }}
                >

                  {profile.githubUrl && (
                    <a
                      href={profile.githubUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="primary-btn"
                    >
                      <ExternalLink size={18} />
                      GitHub
                    </a>
                  )}

                  {profile.linkedinUrl && (
                    <a
                      href={profile.linkedinUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="primary-btn"
                    >
                      <ExternalLink size={18} />
                      LinkedIn
                    </a>
                  )}

                  {!profile.githubUrl &&
                    !profile.linkedinUrl && (
                      <p>
                        No developer links added yet.
                      </p>
                    )}

                </div>

              </div>

              {/* ==================================
                  EDIT PROFILE BUTTON
              ================================== */}

              <button
                className="primary-btn"
                onClick={() => {
                  setEditing(true);
                  setSuccess("");
                }}
              >
                <Pencil size={17} />
                Edit Profile
              </button>

            </>
          ) : (
            <div className="project-details-card">
              <p>
                Create your developer profile to get started.
              </p>
            </div>
          )}

        </>
      )}

    </div>
  );
}

export default Profile;