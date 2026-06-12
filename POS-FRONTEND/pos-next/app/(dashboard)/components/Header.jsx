import PropTypes from "prop-types";
import LogoutButton from "./LogoutButton";

export default function Header({ username }) {

  return (
    <header
      className="fixed top-0 right-0 left-16 h-16 bg-white border-b border-gray-200 flex items-center justify-between px-8 z-40 shadow-sm"
    >
      <div className="flex items-center gap-3">
        <h1 className="text-lg font-semibold text-slate-700">
          Dashboard
        </h1>
      </div>


      <div className="flex items-center gap-4">
        <div className="text-right">
          <div className="text-xs text-gray-400">
            Welcome
          </div>
          <div className="text-sm font-semibold text-gray-700">
            {username}
          </div>
        </div>

        <div
          className="w-8 h-8 rounded-full bg-blue-500 flex items-center justify-center text-white font-semibold"
        >
          {username
            ? username.charAt(0).toUpperCase()
            : "U"}
        </div>
        <LogoutButton />
      </div>
    </header>
  );
}

Header.propTypes = {username: PropTypes.string};