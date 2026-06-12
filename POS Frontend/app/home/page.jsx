"use client";

export default function HomePage() {
  return (
    <div
      style={{
        fontFamily: "'Barlow', sans-serif",
        minHeight: "calc(100vh - 52px)",
        background: "#f0f2f5",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: "40px 20px",
      }}
    >
      <div
        style={{
          background: "#fff",
          borderRadius: 12,
          border: "1px solid #e0e0e0",
          padding: "48px 52px",
          textAlign: "center",
          maxWidth: 440,
          width: "100%",
          boxShadow: "0 2px 12px rgba(0,0,0,.06)",
        }}
      >
        <div
          style={{
            width: 52,
            height: 52,
            borderRadius: 8,
            background: "#005dab",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: 22,
            color: "#fff",
            fontWeight: 900,
            margin: "0 auto 22px",
          }}
        >
          P
        </div>
        <h1
          style={{
            fontSize: 24,
            fontWeight: 900,
            color: "#111",
            marginBottom: 10,
            letterSpacing: "-0.03em",
          }}
        >
          Welcome to POS Enterprise
        </h1>
        <p
          style={{
            fontSize: 14,
            color: "#777",
            lineHeight: 1.75,
            fontWeight: 500,
            margin: 0,
          }}
        >
          Use the menu in the top-left to navigate to any section of your
          operations dashboard.
        </p>
      </div>
    </div>
  );
}
