"use client";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import Layout from "../Components/Layout";

function Profile() {

    const [user, setUser] = useState(null);
    const navigate = useRouter();

   useEffect(() => {

    const fetchProfile = async () => {

        try {

            const token = localStorage.getItem("token");

            const response = await axios.get(
                "http://localhost:8080/api/user/profile",
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            console.log("PROFILE RESPONSE:", response.data);

            setUser(response.data);

        } catch (error) {

            console.error("Profile fetch error:", error);

        }
    };

    fetchProfile();

}, []);

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        navigate.push("/login");
    };

    if (!user) {
        return (
            <div style={{
                display: "flex",
                flexDirection: "column",
                justifyContent: "center",
                alignItems: "center",
                minHeight: "100vh",
                fontFamily: "Arial"
            }}>
                <h2 style={{ color: "#888" }}>No User Logged In</h2>
                <button
                    onClick={() => navigate.push("/login")}
                    style={{
                        marginTop: "16px",
                        padding: "10px 24px",
                        backgroundColor: "#667eea",
                        color: "white",
                        border: "none",
                        borderRadius: "8px",
                        cursor: "pointer",
                        fontSize: "14px"
                    }}
                >
                    Go to Login
                </button>
            </div>
        );
    }

    return (
        <Layout user={user} logout={handleLogout} navigate={navigate}>
            <div style={{
                minHeight: "100vh",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                background: "#f4f6fb",
                fontFamily: "Arial"
            }}>
                <div style={{
                    background: "#ffffff",
                    padding: "35px 40px",
                    borderRadius: "16px",
                    boxShadow: "0 10px 30px rgba(0,0,0,0.12)",
                    width: "380px"
                }}>

                {/* Avatar */}
                <div style={{
                    width: "70px",
                    height: "70px",
                    borderRadius: "50%",
                    background: "linear-gradient(135deg, #667eea, #182848)",
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    margin: "0 auto 20px auto"
                }}>
                    <span style={{
                        color: "white",
                        fontSize: "28px",
                        fontWeight: "bold"
                    }}>
                        {user.name
                            ? user.name.charAt(0).toUpperCase()
                            : "?"}
                    </span>
                </div>

                <h2 style={{
                    textAlign: "center",
                    color: "#4b6cb7",
                    marginBottom: "24px"
                }}>
                    User Profile
                </h2>

                {/* Name */}
                <div style={rowStyle}>
                    <span style={labelStyle}>Name</span>
                    <span style={valueStyle}>{user.name || "—"}</span>
                </div>

                {/* Email */}
                <div style={rowStyle}>
                    <span style={labelStyle}>Email</span>
                    <span style={valueStyle}>{user.username || "—"}</span>
                </div>

                {/* Phone */}
                <div style={rowStyle}>
                    <span style={labelStyle}>Phone</span>
                    <span style={valueStyle}>{user.phoneNo || "—"}</span>
                </div>

                {/* Roles */}
                <div style={{ ...rowStyle, alignItems: "flex-start" }}>
                    <span style={labelStyle}>Roles</span>
                    <div style={{
                        display: "flex",
                        flexWrap: "wrap",
                        gap: "8px"
                    }}>
                        {user.roles && user.roles.length > 0 ? (
                            user.roles.map((role) => {
                                const trimmedRole = String(role).trim();
                                const key = trimmedRole || `role-${Math.random().toString(36).slice(2, 8)}`;
                                return (
                                    <span key={key} style={badgeStyle}>
                                        {trimmedRole}
                                    </span>
                                );
                            })
                        ) : (
                            <span style={{ color: "#aaa" }}>
                                No roles assigned
                            </span>
                        )}
                    </div>
                </div>

                {/* Back Button */}
                <button
                    onClick={() => navigate.push("/Dashboard")}
                    style={{
                        ...btnStyle,
                        marginTop: "24px",
                        background: "linear-gradient(135deg, #4b6cb7, #182848)"
                    }}
                >
                    ← Back
                </button>

                {/* Logout Button */}
                <button
                    onClick={handleLogout}
                    style={{
                        ...btnStyle,
                        marginTop: "10px",
                        background: "linear-gradient(135deg, #e53e3e, #742a2a)"
                    }}
                >
                    Logout
                </button>
            </div>
        </div>
        </Layout>
    );
}

const rowStyle = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "12px 0",
    borderBottom: "1px solid #f0f0f0"
};

const labelStyle = {
    fontSize: "13px",
    color: "#0c0c0c",
    fontWeight: "600",
    textTransform: "uppercase",
    letterSpacing: "0.5px",
    minWidth: "80px"
};

const valueStyle = {
    fontSize: "14px",
    color: "#333",
    textAlign: "right"
};

const badgeStyle = {
    padding: "4px 12px",
    backgroundColor: "#eef0fb",
    color: "#4b6cb7",
    borderRadius: "20px",
    fontSize: "13px",
    fontWeight: "600"
};

const btnStyle = {
    width: "100%",
    padding: "11px",
    color: "white",
    border: "none",
    borderRadius: "10px",
    fontSize: "15px",
    cursor: "pointer"
};

export default Profile;