"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";

function Navbar() {
  const [showProfile, setShowProfile] = useState(false);
  const navigate = useRouter();

  return (
    <div
      style={{
        height: "70px",
        backgroundColor: "#111827",
        color: "white",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        padding: "0 20px",
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        zIndex: 1000,
      }}
    >
      <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
        <button
          type="button"
          data-sidebar-toggle="true"
          onClick={() =>
            globalThis.dispatchEvent(new CustomEvent("toggle-sidebar"))
          }
          style={{
            background: "none",
            border: "none",
            cursor: "pointer",
            display: "flex",
            flexDirection: "column",
            gap: "5px",
            padding: "4px",
          }}
        >
          <span
            style={{
              display: "block",
              width: "22px",
              height: "2px",
              backgroundColor: "white",
            }}
          />
          <span
            style={{
              display: "block",
              width: "22px",
              height: "2px",
              backgroundColor: "white",
            }}
          />
          <span
            style={{
              display: "block",
              width: "22px",
              height: "2px",
              backgroundColor: "white",
            }}
          />
        </button>
        <h2> 🛒 Point of Sale</h2>
      </div>
      <div style={{ position: "relative" }}>
        <button
          type="button"
          onClick={() => setShowProfile(!showProfile)}
          aria-label="Toggle profile menu"
          style={{
            width: "40px",
            height: "40px",
            borderRadius: "50%",
            backgroundColor: "black",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            cursor: "pointer",
            border: "none",
            color: "white",
          }}
        >
          H
        </button>

        {showProfile && (
          <div
            style={{
              position: "absolute",
              right: 0,
              top: "50px",
              backgroundColor: "white",
              color: "black",
              padding: "15px",
              borderRadius: "8px",
              width: "180px",
              zIndex: 1001,
            }}
          >
            <button
              onClick={() => {
                setShowProfile(false);
                navigate.push("/profile");
              }}
              style={{
                width: "100%",
                marginBottom: "10px",
                padding: "8px",
                backgroundColor: "black",
                color: "white",
                border: "none",
                cursor: "pointer",
              }}
            >
              Profile
            </button>
            <button
              onClick={() => {
                setShowProfile(false);
                localStorage.removeItem("token");
                navigate.push("/login");
              }}
              style={{
                width: "100%",
                padding: "8px",
                backgroundColor: "red",
                color: "white",
                border: "none",
                cursor: "pointer",
              }}
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
export default Navbar;
