import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  Outlet,
} from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Projects from "./pages/Projects";
import ProjectDetails from "./pages/ProjectDetails";
import ExploreProjects from "./pages/ExploreProjects";
import ExploreProjectDetails from "./pages/ExploreProjectDetails";
import MyTasks from "./pages/MyTasks";

// Module 9
import Profile from "./pages/Profile";

// Module 10
import Developers from "./pages/Developers";
import DeveloperDetails from "./pages/DeveloperDetails";
import MyConnections from "./pages/MyConnections";

import ProtectedRoute from "./components/ProtectedRoute";
import Sidebar from "./components/Sidebar";
import Navbar from "./components/Navbar";

function AppLayout() {
  return (
    <div className="app-layout">
      <Sidebar />

      <div className="main-layout">
        <Navbar />

        <main className="page-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

function ComingSoon({ title }) {
  return (
    <div className="dashboard">
      <h1>{title}</h1>
      <p>We're building this feature next.</p>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* PUBLIC ROUTES */}

        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* PROTECTED ROUTES */}

        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>

            <Route
              path="/dashboard"
              element={<Dashboard />}
            />

            <Route
              path="/projects"
              element={<Projects />}
            />

            <Route
              path="/projects/:projectId"
              element={<ProjectDetails />}
            />

            <Route
              path="/explore"
              element={<ExploreProjects />}
            />

            <Route
              path="/explore/:projectId"
              element={<ExploreProjectDetails />}
            />

            <Route
              path="/tasks"
              element={<MyTasks />}
            />

            <Route
              path="/snippets"
              element={<ComingSoon title="Code Snippets" />}
            />

            {/* DEVELOPER DIRECTORY */}

            <Route
              path="/developers"
              element={<Developers />}
            />

            <Route
              path="/developers/:userId"
              element={<DeveloperDetails />}
            />

            {/* DEVELOPER CONNECTIONS */}

            <Route
              path="/connections"
              element={<MyConnections />}
            />

            {/* MY PROFILE */}

            <Route
              path="/profile"
              element={<Profile />}
            />

          </Route>
        </Route>

        {/* DEFAULT ROUTES */}

        <Route
          path="/"
          element={<Navigate to="/dashboard" replace />}
        />

        <Route
          path="*"
          element={<Navigate to="/dashboard" replace />}
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;