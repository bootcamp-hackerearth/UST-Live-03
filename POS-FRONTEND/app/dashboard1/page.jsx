import { cookies } from "next/headers";
import api from "../api";

async function getNodes() {
  try {
    const token = (await cookies()).get("token")?.value;

    if (!token) return [];

    const res = await api.get("/node/getNodesForRoles", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      cache: "no-store",
    });

    return res.data || [];
  } catch (err) {
    console.log(err);
    return [];
  }
}

export default async function Dashboard1() {
  const nodes = await getNodes();

  return (
    <div
      style={{
        padding: 24,
        background: "#f3f4f6",
        minHeight: "100%",
      }}
    >
      <div
        style={{
          background: "linear-gradient(to right, #0f766e, #134e4a)",
          color: "white",
          padding: 24,
          borderRadius: 12,
          marginBottom: 20,
        }}
      >
        <h3>Welcome to POS Dashboard 👋</h3>
        <p style={{ opacity: 0.9 }}>
          Manage your system from one place
        </p>
      </div>

      {nodes.length > 0 && (
        <div
          style={{
            display: "grid",
            gridTemplateColumns:
              "repeat(auto-fit,minmax(200px,1fr))",
            gap: 16,
            marginBottom: 20,
          }}
        >
          <div
            style={{
              background: "white",
              padding: 16,
              borderRadius: 10,
              boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
            }}
          >
            <h4>Total Modules</h4>
            <h1>{nodes.length}</h1>
          </div>

          <div
            style={{
              background: "white",
              padding: 16,
              borderRadius: 10,
              boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
            }}
          >
            <h4>Status</h4>
            <h1 style={{ color: "green" }}>Active</h1>
          </div>
        </div>
      )}

      {nodes.length > 0 && (
        <div
          style={{
            background: "white",
            padding: 20,
            borderRadius: 10,
            boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
          }}
        >
          <h3 style={{ marginBottom: 15 }}>
            Available Modules
          </h3>

          <div
            style={{
              display: "grid",
              gridTemplateColumns:
                "repeat(auto-fit,minmax(220px,1fr))",
              gap: 15,
            }}
          >
            {nodes.map((node) => (
              <button
                key={node.path}
                type="button"
                style={{
                  padding: 15,
                  borderRadius: 10,
                  border: "1px solid #e5e7eb",
                  background: "#fff",
                  transition: "0.2s",
                  cursor: "pointer",
                  textAlign: "left",
                }}
                onMouseEnter={(e) =>
                  (e.currentTarget.style.transform =
                    "scale(1.03)")
                }
                onMouseLeave={(e) =>
                  (e.currentTarget.style.transform =
                    "scale(1)")
                }
              >
                <h4>{node.identifier}</h4>
                <p
                  style={{
                    color: "#888",
                    fontSize: 13,
                  }}
                >
                  {node.path}
                </p>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}