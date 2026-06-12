"use client";

import Link from "next/link";
import PropTypes from "prop-types";

export default function Sidebar({
  nodes = [],
  pathname,
  collapsed,
}) {
  return (
    <aside
      style={{
        width: collapsed ? 72 : 260,
        height: "100dvh",
        background: "#111827",
        color: "#fff",
        display: "flex",
        flexDirection: "column",
        borderRight: "1px solid #1f2937",
        transition: "width 0.25s ease",
        overflow: "hidden",
      }}
    >

      <div
        style={{
          position: "sticky",
          top: 0,
          zIndex: 10,
          background: "#111827",
          padding: "16px",
          borderBottom: "1px solid #1f2937",
        }}
      >
        {collapsed ? (
          <div
            style={{
              textAlign: "center",
              fontSize: 18,
              fontWeight: "bold",
            }}
          >
            ERP
          </div>
        ) : (
          <>
            <h2
              style={{
                margin: 0,
                fontSize: 18,
                fontWeight: 700,
              }}
            >
              ERP System
            </h2>

            <div
              style={{
                fontSize: 12,
                color: "#9ca3af",
                marginTop: 4,
              }}
            >
              Navigation Menu
            </div>
          </>
        )}
      </div>

      <div
        style={{
          flex: 1,
          overflowY: "auto",
          padding: 12,
        }}
      >
        {collapsed ? null : (
          <div
            style={{
              fontSize: 11,
              color: "#9ca3af",
              letterSpacing: 1,
              marginBottom: 10,
            }}
          >
            MODULES
          </div>
        )}

        {nodes.map((node) => {
          const active = pathname === node.path;

          return (
            <Link
              key={node.path}
              href={node.path}
              style={{ textDecoration: "none" }}
            >
              <div
                style={{
                  padding: "12px 14px",
                  marginBottom: 6,
                  borderRadius: 10,
                  background: active
                    ? "#14b8a6"
                    : "transparent",
                  color: "#fff",
                  transition: "all .2s ease",
                  cursor: "pointer",
                  textAlign: collapsed ? "center" : "left",
                  fontSize: 14,
                }}
              >
                {collapsed
                  ? node.identifier?.charAt(0)
                  : node.identifier}
              </div>
            </Link>
          );
        })}
      </div>

      {collapsed ? null : (
        <div
          style={{
            padding: 16,
            borderTop: "1px solid #1f2937",
            color: "#9ca3af",
            fontSize: 12,
          }}
        >
          Version 1.0
        </div>
      )}
    </aside>
  );
}

Sidebar.propTypes = {
  nodes: PropTypes.arrayOf(
    PropTypes.shape({
      path: PropTypes.string,
      identifier: PropTypes.string,
    })
  ),
  pathname: PropTypes.string,
  collapsed: PropTypes.bool,
};