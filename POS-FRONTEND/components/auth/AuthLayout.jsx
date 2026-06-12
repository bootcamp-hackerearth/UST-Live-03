import PropTypes from "prop-types";

export default function AuthLayout({
  leftContent,
  children,
}) {
  return (
    <div className="min-h-screen flex bg-[#111111]">
      <div className="hidden lg:flex w-1/2 text-white px-20 py-16 flex-col justify-between">

        {leftContent}

      </div>

      <div className="w-full lg:w-1/2 bg-[#f5f5f5] flex items-center justify-center px-6 py-10">

        {children}
      </div>
    </div>
  );
}

AuthLayout.propTypes = {
  leftContent: PropTypes.node,
  children: PropTypes.node,
};