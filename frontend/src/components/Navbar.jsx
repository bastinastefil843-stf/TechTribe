import { useNavigate } from "react-router-dom";
import { LogOut, Bell, UserCircle } from "lucide-react";

function Navbar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    sessionStorage.removeItem("token");
    navigate("/login", { replace: true });
  };

  return (
    <header className="top-navbar">
      <div>
        <h2>Developer Workspace</h2>
        <p>Build. Collaborate. Grow.</p>
      </div>

      <div className="navbar-actions">
        <Bell size={20} />
        <UserCircle size={25} />
        <button className="logout-btn" onClick={handleLogout}>
          <LogOut size={17} />
          Logout
        </button>
      </div>
    </header>
  );
}

export default Navbar;