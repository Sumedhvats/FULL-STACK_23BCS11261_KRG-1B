import { BrowserRouter, Routes, Route, Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import Login from "./pages/Login.jsx";
import CreateSurvey from "./pages/CreateSurvey.jsx";
import TakeSurvey from "./pages/TakeSurvey.jsx";
import Analysis from "./pages/Analysis.jsx";
import MySurveys from "./pages/MySurveys.jsx";

// 🌙 Navbar Component
function Navbar() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser) setUser(JSON.parse(storedUser));

    // Sync across tabs
    const syncUser = () => {
      const updatedUser = localStorage.getItem("user");
      setUser(updatedUser ? JSON.parse(updatedUser) : null);
    };
    window.addEventListener("storage", syncUser);
    return () => window.removeEventListener("storage", syncUser);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("user");
    setUser(null);
    navigate("/login");
  };

  return (
    <header className="sticky top-0 z-50 bg-[#0f172a]/80 backdrop-blur-lg border-b border-white/10 shadow-lg">
      <div className="max-w-6xl mx-auto flex items-center justify-between px-6 py-4">
        <Link
          to="/"
          className="font-bold text-2xl text-blue-400 tracking-tight hover:text-blue-300 transition-all"
        >
          Surveyly
        </Link>

        <nav className="flex gap-4 text-sm items-center text-gray-300">
          {user ? (
            <>
              <Link
                to="/dashboard"
                className="hover:text-blue-400 font-medium transition"
              >
                Dashboard
              </Link>
              <Link
                to="/create"
                className="hover:text-blue-400 font-medium transition"
              >
                Create Survey
              </Link>
              <button
                onClick={handleLogout}
                className="bg-gradient-to-r from-blue-600 to-indigo-500 text-white px-3 py-1.5 rounded-lg hover:opacity-90 transition"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="hover:text-blue-400 font-medium transition"
              >
                Login
              </Link>
              <Link
                to="/create"
                className="bg-gradient-to-r from-blue-600 to-indigo-500 text-white px-3 py-1.5 rounded-lg hover:opacity-90 transition font-medium shadow-md"
              >
                Create Survey
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}

// ⚙️ App Component
export default function App() {
  return (
    <BrowserRouter>
      <div className="flex flex-col min-h-screen bg-gradient-to-br from-[#0b1120] via-[#0f172a] to-[#1e293b] text-gray-100">
        <Navbar />

        <main className="flex-1 max-w-6xl mx-auto w-full p-6 animate-fadeIn">
          <Routes>
            {/* 🏠 Home Page */}
            <Route
              path="/"
              element={
                <div className="flex flex-col items-center justify-center text-center min-h-[70vh]">
                  <h1 className="text-5xl font-bold mb-4 text-blue-400 tracking-tight">
                    Welcome to Surveyly 🚀
                  </h1>
                  <p className="text-gray-400 max-w-md text-lg leading-relaxed mb-8">
                    Create surveys, share them effortlessly, and explore real-time analysis — 
                    all powered by React + Spring Boot.
                  </p>
                  <Link
                    to="/create"
                    className="bg-gradient-to-r from-blue-600 to-indigo-500 text-white px-6 py-3 rounded-lg hover:shadow-[0_0_20px_rgba(59,130,246,0.6)] transition font-medium"
                  >
                    Start Creating →
                  </Link>
                </div>
              }
            />

            {/* 🔐 Auth + Pages */}
            <Route path="/login" element={<Login />} />
            <Route path="/create" element={<CreateSurvey />} />
            <Route path="/take-survey/:id" element={<TakeSurvey />} />
            <Route path="/analysis/:id" element={<Analysis />} />
            <Route path="/dashboard" element={<MySurveys />} />
          </Routes>
        </main>

        <footer className="border-t border-white/10 text-center py-6 text-sm text-gray-500 mt-10">
          © {new Date().getFullYear()}{" "}
          <span className="font-semibold text-blue-400">Surveyly</span> — Designed with 💙 using React + Spring Boot
        </footer>
      </div>
    </BrowserRouter>
  );
}
