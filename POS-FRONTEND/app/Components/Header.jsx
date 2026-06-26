"use client";

import PropTypes from "prop-types";
import { useRouter } from "next/navigation";

function Header({ user, logout }) {

  const router = useRouter();

  const handleLogout = () => {
    if (typeof logout === "function") {
      logout();
      return;
    }

    localStorage.removeItem("token");
    localStorage.removeItem("user");
    router.push("/login");
  };

  return (
    <div
      style={{
        background: "#0f172a",
        padding: "15px 25px",
        borderBottom: "1px solid #334155",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        color: "#f8fafc"
      }}
    >

      <div>
        <h2 style={{ margin: 0, color: "#fff" }}>
          POS Dashboard
        </h2>

        <small style={{ color: "#ccc" }}>
          Welcome back, {user?.name}
        </small>
      </div>

      <div style={{ display: "flex", gap: "10px" }}>

        <button
          onClick={() => router.push("/Profile")}
          style={{
            background: "#4b6cb7",
            color: "#fff",
            border: "none",
            padding: "10px",
            borderRadius: "6px",
            cursor: "pointer"
          }}
        >
          View Profile
        </button>

        <button
          onClick={handleLogout}
          style={{
            background: "#dc3545",
            color: "#fff",
            border: "none",
            padding: "10px",
            borderRadius: "6px",
            cursor: "pointer"
          }}
        >
          Logout
        </button>

      </div>
    </div>
  );
}

Header.propTypes = {
  user: PropTypes.shape({
    name: PropTypes.string,
  }),
  logout: PropTypes.func,
};

export default Header;