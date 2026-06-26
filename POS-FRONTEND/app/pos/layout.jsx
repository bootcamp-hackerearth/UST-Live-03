// src/app/pos/layout.jsx

"use client";

import "../globals.css";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "../api/axios";
import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import PropTypes from "prop-types";

export default function AuthenticatedLayout({ children }) {
    const router = useRouter();
    const [nodes,            setNodes]            = useState([]);
    const [loading,          setLoading]          = useState(true);
    const [name,             setName]             = useState("User");
    const [sidebarCollapsed, setSidebarCollapsed] = useState(true);

    useEffect(() => {
        const fetchNodes = async () => {
            try {
                setLoading(true);
                const token    = localStorage.getItem("token");
                const userName = localStorage.getItem("name") || "User";
                setName(userName);

                if (!token) { router.push("/login"); return; }

                const res = await api.get("/node/getNodesForRoles");

                if (Array.isArray(res.data))       setNodes(res.data);
                else if (res.data?.dtoList)         setNodes(res.data.dtoList);
                else                                setNodes([]);
            } catch (err) {
                console.error("Error fetching nodes:", err);
                if (err.response?.status === 401) {
                    localStorage.clear();
                    router.push("/login");
                } else {
                    setNodes([]);
                }
            } finally {
                setLoading(false);
            }
        };
        fetchNodes();
    }, [router]);

    const handleLogout = () => {
        localStorage.clear();
        router.push("/login");
    };

    return (
        <div className="h-screen w-screen flex overflow-hidden bg-slate-50">

            <div className={`shrink-0 transition-all duration-300 ${sidebarCollapsed ? "w-20" : "w-64"}`}>
                <Sidebar
                    nodes={nodes}
                    loading={loading}
                    onLogout={handleLogout}
                    onCollapseChange={(isCollapsed) => setSidebarCollapsed(isCollapsed)}
                />
            </div>

            <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
                <Header name={name} />

                <main className="flex-1 overflow-y-auto min-h-0">
                    {children}
                </main>

                <Footer />
            </div>
        </div>
    );
}

AuthenticatedLayout.propTypes = { children: PropTypes.node.isRequired };