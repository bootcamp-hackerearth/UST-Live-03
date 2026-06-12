"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

function Profile() {
  const router = useRouter();
  const [user, setUser] = useState(null);
  const [rolesList, setRolesList] = useState([]);
  const [editing, setEditing] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      router.push("/login");
      return;
    }
    fetchProfile();
    fetchRoles();
  }, []);

  const fetchProfile = async () => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch("http://localhost:8080/api/user/profile", {
        method: "GET",
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await response.json();
      setUser(data);
    } catch (err) {
      console.error(err);
      setError("Error fetching profile");
    }
  };

  const fetchRoles = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/role/list", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ page: 0, sizePerPage: 100 }),
      });
      const data = await response.json();
      setRolesList(data.dtoList || []);
    } catch (err) {
      console.error(err);
    }
  };

  const handleChange = (e) => {
    setError("");
    setSuccess("");
    const { name, value } = e.target;
    if (name === "phoneNo") {
      const numbersOnly = value.replaceAll(/\D/g, "").slice(0, 10);
      setUser({ ...user, phoneNo: numbersOnly });
      return;
    }
    setUser({ ...user, [name]: value });
  };

  const handleRoleChange = (e) => {
    setError("");
    const values = Array.from(
      e.target.selectedOptions,
      (option) => option.value,
    );
    setUser({ ...user, roles: values });
  };

  const validate = () => {
    if (!user.name?.trim()) return "Name is required.";
    if (user.name.trim().length < 3)
      return "Name must be at least 3 characters.";
    if (!/^[A-Za-z ]+$/.test(user.name.trim()))
      return "Name should contain only letters.";
    if (!user.phoneNo || !/^\d{10}$/.test(user.phoneNo))
      return "Phone number must be exactly 10 digits.";
    if (!user.roles?.length) return "Please select at least one role.";
    return "";
  };

  const handleUpdate = async () => {
    setError("");
    setSuccess("");

    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
      router.push("/login");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/user/update", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(user),
      });
      const responseText = await response.text();
      if (response.ok) {
        setSuccess("Profile updated successfully.");
        setEditing(false);
        fetchProfile();
      } else {
        setError(
          `Failed to update profile: ${responseText || response.status}`,
        );
      }
    } catch (err) {
      console.error("Update Error:", err);
      setError("Server error. Please try again.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    router.push("/");
  };

  const handleCancelEdit = () => {
    setEditing(false);
    setError("");
    setSuccess("");
    fetchProfile();
  };

  const inputStyle = {
    width: "100%",
    padding: "8px 12px",
    border: "1px solid #d1d5db",
    borderRadius: "6px",
    fontSize: "14px",
    outline: "none",
    boxSizing: "border-box",
  };

  const btnStyle = (bg, color, border) => ({
    padding: "9px 20px",
    backgroundColor: bg,
    color: color,
    border: border || "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "600",
    fontSize: "14px",
  });

  if (!user) {
    return <div style={{ padding: "20px" }}>Loading...</div>;
  }

  return (
    <div
      style={{
        padding: "20px",
        backgroundColor: "#f5f5f5",
        minHeight: "100vh",
      }}
    >
      <h1 style={{ marginBottom: "4px", color: "#111827" }}>My Profile</h1>
      <p style={{ color: "#6b7280", fontSize: "14px", marginBottom: "20px" }}>
        Manage your personal information
      </p>

      <div
        style={{
          backgroundColor: "white",
          borderRadius: "8px",
          overflow: "hidden",
          boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
          maxWidth: "700px",
        }}
      >
        <div
          style={{
            backgroundColor: "#111827",
            color: "white",
            padding: "16px 20px",
            fontSize: "15px",
            fontWeight: "600",
          }}
        >
          User Information
        </div>

        <div style={{ padding: "20px" }}>
          {error && (
            <div
              style={{
                backgroundColor: "#fee2e2",
                color: "#dc2626",
                padding: "10px 14px",
                borderRadius: "6px",
                marginBottom: "16px",
                fontSize: "14px",
              }}
            >
              <strong>Error:</strong> {error}
            </div>
          )}
          {success && (
            <div
              style={{
                backgroundColor: "#dcfce7",
                color: "#16a34a",
                padding: "10px 14px",
                borderRadius: "6px",
                marginBottom: "16px",
                fontSize: "14px",
              }}
            >
              {success}
            </div>
          )}

          <table width="100%" style={{ borderCollapse: "collapse" }}>
            <thead>
              <tr
                style={{
                  backgroundColor: "#f9fafb",
                  borderBottom: "1px solid #e5e7eb",
                }}
              >
                <th
                  style={{
                    textAlign: "left",
                    padding: "12px 16px",
                    fontWeight: "600",
                    fontSize: "13px",
                    color: "#374151",
                    width: "35%",
                  }}
                >
                  Field
                </th>
                <th
                  style={{
                    textAlign: "left",
                    padding: "12px 16px",
                    fontWeight: "600",
                    fontSize: "13px",
                    color: "#374151",
                  }}
                >
                  Value
                </th>
              </tr>
            </thead>
            <tbody>
              <tr style={{ borderBottom: "1px solid #f3f4f6" }}>
                <td
                  style={{
                    padding: "14px 16px",
                    color: "#6b7280",
                    fontSize: "14px",
                    fontWeight: "500",
                  }}
                >
                  Username
                </td>
                <td
                  style={{
                    padding: "14px 16px",
                    fontSize: "14px",
                    color: "#111827",
                  }}
                >
                  {user.username}
                </td>
              </tr>

              <tr style={{ borderBottom: "1px solid #f3f4f6" }}>
                <td
                  style={{
                    padding: "14px 16px",
                    color: "#6b7280",
                    fontSize: "14px",
                    fontWeight: "500",
                  }}
                >
                  Name
                </td>
                <td style={{ padding: "14px 16px" }}>
                  {editing ? (
                    <input
                      type="text"
                      name="name"
                      value={user.name || ""}
                      onChange={handleChange}
                      placeholder="Enter name"
                      style={inputStyle}
                    />
                  ) : (
                    <span style={{ fontSize: "14px", color: "#111827" }}>
                      {user.name}
                    </span>
                  )}
                </td>
              </tr>

              <tr style={{ borderBottom: "1px solid #f3f4f6" }}>
                <td
                  style={{
                    padding: "14px 16px",
                    color: "#6b7280",
                    fontSize: "14px",
                    fontWeight: "500",
                  }}
                >
                  Phone Number
                </td>
                <td style={{ padding: "14px 16px" }}>
                  {editing ? (
                    <input
                      type="text"
                      name="phoneNo"
                      value={user.phoneNo || ""}
                      onChange={handleChange}
                      placeholder="Enter 10-digit phone number"
                      maxLength={10}
                      style={inputStyle}
                    />
                  ) : (
                    <span style={{ fontSize: "14px", color: "#111827" }}>
                      {user.phoneNo}
                    </span>
                  )}
                </td>
              </tr>

              <tr>
                <td
                  style={{
                    padding: "14px 16px",
                    color: "#6b7280",
                    fontSize: "14px",
                    fontWeight: "500",
                  }}
                >
                  Roles
                </td>
                <td style={{ padding: "14px 16px" }}>
                  {editing ? (
                    <>
                      <select
                        multiple
                        value={user.roles ?? []}
                        onChange={handleRoleChange}
                        style={{ ...inputStyle, minHeight: "110px" }}
                      >
                        {rolesList.map((role) => (
                          <option key={role.identifier} value={role.identifier}>
                            {role.identifier}
                          </option>
                        ))}
                      </select>
                      <p
                        style={{
                          fontSize: "12px",
                          color: "#6b7280",
                          marginTop: "4px",
                        }}
                      >
                        Hold Ctrl / Cmd to select multiple roles
                      </p>
                    </>
                  ) : (
                    <div
                      style={{ display: "flex", flexWrap: "wrap", gap: "6px" }}
                    >
                      {user.roles?.map((role) => (
                        <span
                          key={role}
                          style={{
                            display: "inline-block",
                            backgroundColor: "#111827",
                            color: "white",
                            padding: "4px 10px",
                            borderRadius: "5px",
                            fontSize: "13px",
                          }}
                        >
                          {role}
                        </span>
                      ))}
                    </div>
                  )}
                </td>
              </tr>
            </tbody>
          </table>

          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginTop: "20px",
              paddingTop: "16px",
              borderTop: "1px solid #e5e7eb",
            }}
          >
            <button
              onClick={() => router.back()}
              style={btnStyle("#e5e7eb", "#111827")}
            >
              Back
            </button>

            <div style={{ display: "flex", gap: "10px" }}>
              {editing ? (
                <>
                  <button
                    onClick={handleCancelEdit}
                    style={btnStyle("#e5e7eb", "#111827")}
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handleUpdate}
                    style={btnStyle("#111827", "white")}
                  >
                    Save Changes
                  </button>
                </>
              ) : (
                <button
                  onClick={() => setEditing(true)}
                  style={btnStyle("#111827", "white")}
                >
                  Edit Profile
                </button>
              )}
              <button
                onClick={handleLogout}
                style={btnStyle("#dc2626", "white")}
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
export default Profile;
