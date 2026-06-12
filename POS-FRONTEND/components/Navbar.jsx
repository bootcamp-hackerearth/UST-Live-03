"use client";

import PropTypes from "../lib/propTypes";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

export default function Navbar({
  sidebarOpen,
  setSidebarOpen,
}) {
  const router = useRouter();

  const [profileOpen, setProfileOpen] =
    useState(false);

  const [user, setUser] =
    useState({
      name: "",
      username: "",
      phoneNo: "",
      role: "",
    });

  useEffect(() => {
    setUser({
      name:
        localStorage.getItem(
          "name"
        ) || "",
      username:
        localStorage.getItem(
          "username"
        ) || "",
      phoneNo:
        localStorage.getItem(
          "phoneNo"
        ) || "",
      role: JSON.parse(
        localStorage.getItem(
          "roles"
        ) || "[]"
      )[0] || "",
    });
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    router.replace("/login");
  };

  return (
    <nav className="h-[68px] bg-black flex items-center justify-between px-5 shadow-lg shrink-0">
      <div className="flex items-center gap-4">
        <button
          onClick={() =>
            setSidebarOpen(
              !sidebarOpen
            )
          }
          className="text-white text-2xl"
        >
          ☰
        </button>

        <h1 className="text-white text-xl font-semibold">
          POS Dashboard
        </h1>
      </div>

      <div className="relative">
        <button
          onClick={() =>
            setProfileOpen(
              !profileOpen
            )
          }
          className="
            w-12
            h-12
            rounded-full
            bg-white
            text-black
            font-bold
            flex
            items-center
            justify-center
            border
            border-gray-300
          "
        >
          {user?.name
            ?.charAt(0)
            ?.toUpperCase() ||
            "U"}
        </button>

        {profileOpen && (
          <div
            className="
              absolute
              right-0
              top-14
              w-72
              bg-white
              rounded-2xl
              shadow-2xl
              border
              border-gray-200
              p-5
              z-50
            "
          >
            <div className="flex items-center gap-3 mb-4">
              <div
                className="
                  w-12
                  h-12
                  rounded-xl
                  bg-black
                  text-white
                  font-bold
                  text-lg
                  flex
                  items-center
                  justify-center
                "
              >
                {user?.name
                  ?.charAt(0)
                  ?.toUpperCase() ||
                  "U"}
              </div>

              <div>
                <h3 className="font-semibold text-black">
                  {user?.name ||
                    "User"}
                </h3>

                <p className="text-xs text-gray-500">
                  {user?.role ||
                    "Admin"}
                </p>
              </div>
            </div>

            <div className="space-y-2 text-sm mb-4">
              <div className="flex justify-between">
                <span className="text-gray-500">
                  Username
                </span>

                <span className="text-black font-medium">
                  {user?.username ||
                    "-"}
                </span>
              </div>

              <div className="flex justify-between">
                <span className="text-gray-500">
                  Phone
                </span>

                <span className="text-black font-medium">
                  {user?.phoneNo ||
                    "-"}
                </span>
              </div>
            </div>

            <div
              className="
              w-full
              h-10
              rounded-xl
              bg-black
              text-white
              text-sm
              font-medium
              flex
              items-center
              justify-center
              cursor-default
            "
            >
              Edit Profile
            </div>

            <button
              onClick={
                handleLogout
              }
              className="
                w-full
                h-10
                rounded-xl
                border
                border-black
                text-black
                text-sm
                font-medium
                mt-2
                hover:bg-gray-100
              "
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </nav>
  );
}

Navbar.propTypes = {
  sidebarOpen: PropTypes.bool.isRequired,
  setSidebarOpen: PropTypes.func.isRequired,
};