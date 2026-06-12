"use client";
import PropTypes from "prop-types";

export default function DashboardLayout({
  children
}) {
  return (
    <div>
      <div
        style={{
          display: "flex"
        }}
      >
        <div
          style={{
            flex: 1,
            padding: "20px"
          }}
        >
        </div>
      </div>
    </div>
  );
}
DashboardLayout.propTypes = {
  children: PropTypes.node.isRequired,
};