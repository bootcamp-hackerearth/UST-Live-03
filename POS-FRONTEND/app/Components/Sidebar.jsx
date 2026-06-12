"use client";

import PropTypes from "prop-types";
import { useRouter } from "next/navigation";

function Sidebar({ nodes = [], user }) {

  const router = useRouter();

  const userRoles = Array.isArray(user?.roles) ? user.roles : [];

  const allowedNodes = nodes.filter((node) => {
    const nodeRoles = Array.isArray(node?.roles) ? node.roles : [];

    if (userRoles.length === 0 || node?.status === false) {
      return false;
    }

    return nodeRoles.some((role) => userRoles.includes(role));
  });

  const getNodePath = (path = "") => {

    if (path.startsWith("/")) {
      return path;
    }

    const parts = path.split("/").filter(Boolean);

    if (parts[0]?.toLowerCase() === "user") {
      return `/User/${parts.slice(1).join("/")}`;
    }

    return `/${path}`;
  };

  return (

    <div
      style={{
        width: "260px",
        background: "#1f2937",
        color: "#fff",
        padding: "20px",
        height: "100vh",
        position: "sticky",
        top: 0,
        overflowY: "auto",
        flexShrink: 0
      }}
    >

      {/* USER INFO */}
      <div
        style={{
          textAlign: "center",
          marginBottom: "30px",
          paddingBottom: "20px",
          borderBottom: "1px solid #374151"
        }}
      >

        <div
          style={{
            width: "75px",
            height: "75px",
            borderRadius: "50%",
            background: "linear-gradient(135deg, #4b6cb7, #182848)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            margin: "0 auto 15px auto",
            fontSize: "30px",
            fontWeight: "bold"
          }}
        >
          {user?.name ? user.name.charAt(0).toUpperCase() : "U"}
        </div>

        <h3>{user?.name || "User"}</h3>
        <small>{user?.username}</small>

      </div>

      <h4>Navigation</h4>

      {allowedNodes.length === 0 ? (

        <p>No menu available</p>

      ) : (

        allowedNodes.map((node) => (

          <div key={node.id} style={{ marginBottom: "10px" }}>

            <button
              type="button"
              onClick={() => router.push(getNodePath(node.path))}
              style={{
                cursor: "pointer",
                padding: "12px",
                width: "100%",
                textAlign: "left",
                background: "#374151",
                borderRadius: "8px",
                color: "#fff",
                border: "none"
              }}
            >
              {node.identifier}
            </button>

          </div>

        ))
      )}

    </div>
  );
}

Sidebar.propTypes = {
  nodes: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      path: PropTypes.string,
      identifier: PropTypes.string,
      roles: PropTypes.arrayOf(PropTypes.string),
    })
  ),
  user: PropTypes.shape({
    name: PropTypes.string,
    username: PropTypes.string,
    roles: PropTypes.arrayOf(PropTypes.string),
  }),
};

export default Sidebar;
