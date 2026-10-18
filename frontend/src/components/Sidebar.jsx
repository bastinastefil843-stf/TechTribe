import { NavLink } from "react-router-dom";

import {
  LayoutDashboard,
  FolderKanban,
  ListTodo,
  UsersRound,
  UserRound,
  Compass,
  Code2,
} from "lucide-react";

function Sidebar() {
  const links = [
    {
      name: "Dashboard",
      path: "/dashboard",
      icon: LayoutDashboard,
      color: "#38bdf8",
    },
    {
      name: "Projects",
      path: "/projects",
      icon: FolderKanban,
      color: "#a78bfa",
    },
    {
      name: "Tasks",
      path: "/tasks",
      icon: ListTodo,
      color: "#fbbf24",
    },
    {
      name: "Developers",
      path: "/developers",
      icon: UsersRound,
      color: "#34d399",
    },
    {
      name: "My Profile",
      path: "/profile",
      icon: UserRound,
      color: "#f472b6",
    },
    {
      name: "Explore Projects",
      path: "/explore",
      icon: Compass,
      color: "#22d3ee",
    },
  ];

  return (
    <aside className="sidebar tt-sidebar">

      {/* TECH TRIBE LOGO */}
      <div className="sidebar-logo">
        <Code2 size={28} />
        <span>Tech Tribe</span>
      </div>

      {/* WORKSPACE LABEL */}
      <p className="sidebar-label">WORKSPACE</p>

      {/* SIDEBAR NAVIGATION */}
      <nav className="sidebar-nav">
        {links.map(({ name, path, icon: Icon, color }) => (
          <NavLink
            key={path}
            to={path}
            style={{ "--item-color": color }}
            className={({ isActive }) =>
              `sidebar-link tt-sidebar-link ${
                isActive ? "active" : ""
              }`
            }
          >
            <span className="tt-sidebar-icon">
              <Icon size={20} strokeWidth={2.2} />
            </span>

            <span>{name}</span>
          </NavLink>
        ))}
      </nav>

    </aside>
  );
}

export default Sidebar;