import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Profile.css";

const Profile = () => {

  const navigate = useNavigate(); //create a navigate function to programmatically navigate between routes

  const [user, setUser] = useState({
    id: "",
    name: "",
    username: "",
    phoneNo: "",
    roles: [],
  });

  const [allRoles, setAllRoles] = useState([]);
  const [originalUsername, setOriginalUsername] = useState("");
  const [editMode, setEditMode] = useState(false);
  const [error, setError] = useState("");

  const username = localStorage.getItem("username");
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!username) {
      navigate("/");
      return;
    }

    fetchProfile();
    fetchRoles();
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/user/get?username=${username}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const data = await response.json();

      if (response.ok) {
        setUser({
          ...data,
          roles:
            data.roles?.map((r) =>
              typeof r === "string" ? r : r.identifier
            ) || [],
        });

        setOriginalUsername(data.username);
      } else {
        setError("Failed to load profile");
      }
    } catch {
      setError("Server error");
    }
  };

  const fetchRoles = async () => {
    try {
      const response = await fetch(
        "http://localhost:8080/api/role/list",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            page: 0,
            sizePerPage: 10,
            sortDirection: "ASC",
            sortField: "identifier",
          }),
        }
      );

      const data = await response.json();
      setAllRoles(data || []);
    } catch (error) {
      console.error("Error fetching roles:", error);
    }
  };

  const handleChange = (e) => {
    setUser({
      ...user,
      [e.target.name]: e.target.value,
    });
  };

  const handleRoleChange = (e) => {
    const { value, checked } = e.target;

    setUser((prev) => ({
      ...prev,
      roles: checked
        ? [...prev.roles, value]
        : prev.roles.filter((r) => r !== value),
    }));
  };

  const handleUpdate = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/user/update?oldUsername=${originalUsername}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(user),
        }
      );

      const data = await response.json();

      if (data.success) {
        alert("Profile updated successfully ✅");
        localStorage.setItem("username", user.username);
        setEditMode(false);
      } else {
        alert(data.message || "Email already exists ❌");
      }
    } catch (error) {
      console.error(error);
      alert("Server error ❌");
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate("/");
  };

  return (
    <div className="profile-container">

      <div className="profile-card">

        {/* Header */}
        <div className="profile-header">

          <div className="profile-avatar">
            {user.name?.charAt(0).toUpperCase()}
          </div>

          <div>
            <h2>{user.name}</h2>
            <p>{user.roles.join(", ")}</p>
          </div>

        </div>

        {error && <div className="error-box">{error}</div>}

        {/* Body */}
        <div className="profile-body">

          <div className="profile-item">
            <label>Full Name</label>
            <input
              name="name"
              value={user.name}
              onChange={handleChange}
              disabled={!editMode}
            />
          </div>

          <div className="profile-item">
            <label>Email Address</label>
            <input
              name="username"
              value={user.username}
              onChange={handleChange}
              disabled={!editMode}
            />
          </div>

          <div className="profile-item">
            <label>Phone Number</label>
            <input
              name="phoneNo"
              value={user.phoneNo}
              onChange={handleChange}
              disabled={!editMode}
            />
          </div>

          <div className="profile-item">
            <label>Roles</label>

            {!editMode ? (
              <div className="profile-value">
                {user.roles.join(", ")}
              </div>
            ) : (
              <div className="roles-container">
                {allRoles.map((role, i) => (
                  <label className="role-item" key={i}>
                    <input
                      type="checkbox"
                      value={role.identifier}
                      checked={user.roles.includes(role.identifier)}
                      onChange={handleRoleChange}
                    />
                    {role.identifier}
                  </label>
                ))}
              </div>
            )}
          </div>

        </div>

        {/* Buttons */}
        <div className="profile-actions">

          {!editMode ? (
            <button
              className="edit-btn"
              onClick={() => setEditMode(true)}
            >
              Edit Profile
            </button>
          ) : (
            <button
              className="edit-btn"
              onClick={handleUpdate}
            >
              Save Changes
            </button>
          )}

          <button
            className="logout-btn"
            onClick={handleLogout}
          >
            Logout
          </button>

        </div>

      </div>

    </div>
  );
};

export default Profile;