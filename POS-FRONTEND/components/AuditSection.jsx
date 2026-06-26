"use client";

import PropTypes from "prop-types";

function AuditSection({ data }) {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        background: "#f3f4f6",
        borderRadius: 8,
        padding: 16,
        marginTop: 20,
      }}
    >
      <div>
        <div style={{ fontWeight: "bold" }}>CREATED DETAILS</div>
        <div>Created By : {data.createdBy || "-"}</div>
        <div>Created On : {data.createdOn || "-"}</div>
      </div>

      <div style={{ textAlign: "right" }}>
        <div style={{ fontWeight: "bold" }}>MODIFIED DETAILS</div>
        <div>Modified By : {data.modifiedBy || "-"}</div>
        <div>Modified On : {data.modifiedOn || "-"}</div>
      </div>
    </div>
  );
}

AuditSection.propTypes = {
  data: PropTypes.object,
};

export default AuditSection;
