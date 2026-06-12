import React from "react";

function Header({
  sidebarOpen,
  setSidebarOpen,
  profileOpen,
  setProfileOpen,
  handleLogout,
}) {

  // GET LOGGED IN USERNAME
  const username =
    localStorage.getItem("username");

  return (

    <header
      className="
        fixed top-0 right-0 left-0
        h-[60px]
        bg-white
        border-b border-gray-200
        flex items-center justify-between
        px-4
        z-40
        shadow-sm
        transition-all duration-300
      "

      style={{
        left: sidebarOpen
          ? "220px"
          : "64px",
      }}
    >

      {/* LEFT SIDE */}
      <div className="flex items-center gap-3">

        {/* SIDEBAR TOGGLE BUTTON */}
        <button
          onClick={() =>
            setSidebarOpen(
              (prev) => !prev
            )
          }

          className="
            p-1.5
            rounded-lg
            text-gray-500
            hover:bg-gray-100
          "
        >
          ☰
        </button>

      </div>

      {/* RIGHT SIDE */}
      <div className="relative">

        {/* PROFILE BUTTON */}
        <button

          onClick={() =>
            setProfileOpen(
              (prev) => !prev
            )
          }

          className="
            flex items-center gap-2
          "
        >

          {/* USER TEXT */}
          <div className="text-right hidden sm:block">

            <div className="text-xs text-gray-400">
              Hello 👋
            </div>

            <div className="text-sm font-semibold text-gray-700">
              {username || "User"}
            </div>
          </div>
          {/* PROFILE CIRCLE */}
          <div
            className="
              w-8 h-8
              rounded-full
              bg-blue-500
              flex items-center justify-center
              text-white
              font-semibold
            "
          >
            {/* FIRST LETTER */}
            {username
              ? username.charAt(0).toUpperCase()
              : "U"}
          </div>
        </button>
        {/* DROPDOWN */}
        {profileOpen && (
          <div
            className="
              absolute
              right-0
              top-11
              w-52
              bg-white
              rounded-xl
              shadow-xl
              border border-gray-100
              overflow-hidden
              z-50
            "
          >
            {/* LOGOUT BUTTON */}
            <button
              onClick={handleLogout}
              className="
                w-full
                px-4 py-2
                text-left
                text-red-500
                hover:bg-red-50
              "
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </header>
  );
}
export default Header;