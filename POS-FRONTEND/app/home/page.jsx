"use client";

import React from "react";

const Home = () => {
  return (
    <>
      <style>{`
        .home-container {
          min-height: calc(100vh - 88px);
          background-color: #f4f5fa;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
          padding: 24px;
          box-sizing: border-box;
        }

        .content-box {
          width: 100%;
          max-width: 600px;
          text-align: center;
        }

        .status-tag {
          display: inline-flex;
          align-items: center;
          gap: 8px;
          padding: 6px 16px;
          border-radius: 20px;
          background: #eef0f6;
          margin-bottom: 24px;
        }

        .status-dot {
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background: #6c63ff;
        }

        .status-text {
          font-size: 11px;
          font-weight: 600;
          letter-spacing: 0.05em;
          text-transform: uppercase;
          color: #4b4b75;
        }

        .main-heading {
          font-size: 48px;
          font-weight: 800;
          color: #2d2d6e;
          line-height: 1.1;
          margin-bottom: 16px;
          letter-spacing: -0.03em;
        }

        .main-heading span {
          color: #6c63ff;
        }

        .description {
          font-size: 15px;
          color: #8888a0;
          line-height: 1.6;
          margin-bottom: 36px;
        }

        .btn-group {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 16px;
        }

        @media (max-width: 480px) {
          .btn-group {
            flex-direction: column;
            width: 100%;
          }
        }

        .btn-primary {
          height: 44px;
          padding: 0 24px;
          background: #6c63ff;
          border: none;
          border-radius: 8px;
          color: #ffffff;
          font-size: 14px;
          font-weight: 500;
          cursor: pointer;
          transition: background-color 0.15s ease;
        }

        .btn-primary:hover {
          background-color: #5850ec;
        }

        @media (max-width: 480px) {
          .btn-primary {
            width: 100%;
          }
        }

        .btn-secondary {
          height: 44px;
          padding: 0 24px;
          background: #ffffff;
          border: 1px solid #ebebf5;
          border-radius: 8px;
          color: #4b4b75;
          font-size: 14px;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.15s ease;
        }

        .btn-secondary:hover {
          border-color: #b0b0c8;
          color: #2d2d6e;
        }

        @media (max-width: 480px) {
          .btn-secondary {
            width: 100%;
          }
        }
      `}</style>

      <div className="home-container">
        <div className="content-box">
          <div className="status-tag">
            <div className="status-dot" />
            <span className="status-text">Dashboard Status</span>
          </div>

          <h1 className="main-heading">
            Empty <br />
            <span>Dashboard</span>
          </h1>

          <p className="description">
            No modules are currently opened. Use the sidebar menu to access products, users, categories, pricing and other management sections.
          </p>

          <div className="btn-group">
            <button type="button" className="btn-primary">
              View Menu
            </button>
            <button type="button" className="btn-secondary">
              Manage Your Modules
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default Home;