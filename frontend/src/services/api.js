import axios from "axios";

const api = axios.create({
  // Uses the deployed backend URL in production.
  // Falls back to localhost during development.
  baseURL:
    import.meta.env.VITE_API_BASE_URL ||
    "http://localhost:8080",

  timeout: 10000,
});

// ==========================================
// JWT AUTHENTICATION INTERCEPTOR
// ==========================================

api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem("token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

export default api;