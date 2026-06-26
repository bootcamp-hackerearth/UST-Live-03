"use client";

import CustomerForm from "../customer/CustomerForm";
import PropTypes from "prop-types";

function CustomerAddModal({ onClose, onSuccess }) {
  return (
    <div
      style={{
        position: "fixed",
        inset: 0,
        backgroundColor: "rgba(0,0,0,0.5)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 1000,
        padding: 16,
      }}
    >
      <div
        style={{
          backgroundColor: "#fff",
          borderRadius: 12,
          padding: 24,
          width: "100%",
          maxWidth: 480,
          maxHeight: "90vh",
          overflowY: "auto",
          boxShadow: "0 20px 60px rgba(0,0,0,0.3)",
        }}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginBottom: 16,
          }}
        >
          <h2 style={{ margin: 0, fontSize: 18, color: "#111827" }}>
            Add Customer
          </h2>
          <button
            type="button"
            onClick={onClose}
            style={{
              background: "none",
              border: "none",
              fontSize: 22,
              cursor: "pointer",
              color: "#6b7280",
              lineHeight: 1,
            }}
          >
            ✕
          </button>
        </div>

        <CustomerForm
          idPrefix="modal-"
          simple
          onSuccess={onSuccess}
          onCancel={onClose}
        />
      </div>
    </div>
  );
}

export default CustomerAddModal;

CustomerAddModal.propTypes = {
  onClose: PropTypes.func.isRequired,
  onSuccess: PropTypes.func.isRequired,
};
