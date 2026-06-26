"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import Layout from "../Components/Layout";

function Dashboard() {
  const [nodes, setNodes] = useState([]);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      router.push(
        "/login?error=" +
          encodeURIComponent(
            "Session expired or missing credentials. Please login again."
          )
      );
      return;
    }

    // Run parallel fetches for profile and nodes
    Promise.all([fetchProfile(), fetchNodes()]).finally(() => setLoading(false));
  }, []);

  const fetchProfile = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        router.push("/login");
        return;
      }

      const response = await axios.get(
        "http://localhost:8080/api/user/profile",
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );
      setUser(response.data);
    } catch (error) {
      console.error("Profile fetch error:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        handleAuthExpiry();
      }
    }
  };

  const fetchNodes = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        handleAuthExpiry();
        return;
      }

      const response = await axios.post(
        "http://localhost:8080/api/node/list",
        {
          page: 0,
          sizePerPage: 100,
          sortDirection: "ASC",
          sortField: "id"
        },
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );
      
      const nodeData = response.data.dtoList || (Array.isArray(response.data) ? response.data : []);
      setNodes(nodeData);
    } catch (error) {
      console.error("Node fetch error:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        handleAuthExpiry();
      }
    }
  };

  const handleAuthExpiry = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    router.push(
      "/Login?error=" +
        encodeURIComponent(
          "Session expired or invalid login. Please login again."
        )
    );
  };

  const logout = () => {
    localStorage.clear();
    router.push("/Login");
  };

  return (
    <Layout user={user} nodes={nodes} logout={logout} navigate={router}>
      <div className="min-h-screen bg-slate-50 p-6 lg:p-10 text-slate-800">
        
        {/* Welcome Banner */}
        <div className="mb-8 p-6 lg:p-8 rounded-3xl bg-gradient-to-r from-indigo-600 via-indigo-700 to-purple-800 text-white shadow-xl flex flex-col md:flex-row justify-between items-start md:items-center gap-4 transition duration-300">
          <div>
            <h1 className="text-3xl lg:text-4xl font-extrabold tracking-tight">
              Welcome back, {user?.name || user?.username || "Developer"}!
            </h1>
            <p className="text-indigo-100 text-sm mt-1.5 max-w-xl">
              Your central POS architecture, active interface configuration nodes, and terminal status parameters are fully operational.
            </p>
          </div>
          <div className="bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl px-4 py-2.5 text-xs font-semibold tracking-wider uppercase">
            🟢 Base Station Active
          </div>
        </div>

        {loading ? (
          <div className="w-full h-64 flex items-center justify-center rounded-3xl border border-slate-200 bg-white shadow-sm">
            <div className="text-center">
              <div className="w-10 h-10 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
              <p className="text-slate-500 font-medium text-sm">Loading workspace metrics...</p>
            </div>
          </div>
        ) : (
          <>
            {/* Quick Metrics Grid */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
              {/* Card 1 */}
              <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-md hover:shadow-lg transition duration-300">
                <div className="flex items-center gap-4 mb-4">
                  <div className="p-3 bg-indigo-50 border border-indigo-100 rounded-2xl text-indigo-600">
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                    </svg>
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">Active System Nodes</p>
                    <h3 className="text-2xl font-black text-slate-900">{nodes.length}</h3>
                  </div>
                </div>
                <div className="w-full bg-slate-100 rounded-full h-1.5">
                  <div className="bg-indigo-600 h-1.5 rounded-full" style={{ width: '75%' }}></div>
                </div>
              </div>

              {/* Card 2 */}
              <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-md hover:shadow-lg transition duration-300">
                <div className="flex items-center gap-4 mb-4">
                  <div className="p-3 bg-emerald-50 border border-emerald-100 rounded-2xl text-emerald-600">
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">Operator Profile</p>
                    <h3 className="text-lg font-bold text-slate-900 truncate max-w-[180px]">{user?.username || "Authenticated User"}</h3>
                  </div>
                </div>
                <p className="text-xs text-slate-500 font-medium">Role Level: <span className="text-emerald-600 font-bold">Administrator</span></p>
              </div>

              {/* Card 3 */}
              <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-md hover:shadow-lg transition duration-300">
                <div className="flex items-center gap-4 mb-4">
                  <div className="p-3 bg-amber-50 border border-amber-100 rounded-2xl text-amber-600">
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                    </svg>
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">Security State</p>
                    <h3 className="text-2xl font-black text-slate-900">CSRF / JWT</h3>
                  </div>
                </div>
                <div className="inline-flex items-center gap-1.5 text-xs text-emerald-600 font-bold bg-emerald-50 px-2.5 py-1 rounded-lg">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span> Context Signed
                </div>
              </div>
            </div>

            {/* Bottom Utility Footer Area */}
            <div className="mt-8 flex flex-col sm:flex-row justify-between items-center p-6 bg-white border border-slate-200 rounded-3xl shadow-sm gap-4">
              <div className="flex items-center gap-3">
                <span className="relative flex h-2 w-2">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                  <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
                </span>
                <p className="text-xs font-semibold text-slate-500">Live API Endpoint connected locally via active Session State</p>
              </div>
              <button
                onClick={logout}
                className="bg-rose-50 hover:bg-rose-100 text-rose-600 font-bold text-xs px-5 py-2.5 rounded-xl border border-rose-100 shadow-sm transition duration-150"
              >
                Disconnect & Logout Session
              </button>
            </div>
          </>
        )}
      </div>
    </Layout>
  );
}

export default Dashboard;