import { Link, useNavigate } from "react-router-dom";

export default function Navbar() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user"));

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <nav className="sticky top-0 z-50 bg-[#0f172a]/80 backdrop-blur-lg border-b border-white/10 shadow-sm">
      <div className="max-w-6xl mx-auto flex items-center justify-between px-6 py-4">
        <Link to="/" className="text-xl font-bold text-blue-400 hover:text-blue-300 transition">
          Surveyly
        </Link>

        {user && (
          <div className="flex items-center gap-4 text-sm">
            <Link to="/dashboard" className="hover:text-blue-300">
              Dashboard
            </Link>
            <Link to="/create" className="hover:text-blue-300">
              Create Survey
            </Link>
            <button
              onClick={handleLogout}
              className="bg-gradient-to-r from-blue-600 to-indigo-500 px-3 py-1.5 rounded-lg text-white hover:opacity-90"
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </nav>
  );
}
