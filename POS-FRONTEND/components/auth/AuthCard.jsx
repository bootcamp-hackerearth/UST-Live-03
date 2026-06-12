import PropTypes from "prop-types";

export default function AuthCard({
  title,
  subtitle,
  children,
}) {
  return (
    <div className="w-full max-w-xl bg-white rounded-2xl p-14 shadow-sm">
      <div className="text-center mb-10">
        <h1 className="text-5xl font-bold text-black">
          {title}
        </h1>

        <p className="text-gray-600 mt-4 text-lg">
          {subtitle}
        </p>

      </div>

      {children}
    </div>
  );
}

AuthCard.propTypes = {
  title: PropTypes.string,
  subtitle: PropTypes.string,
  children: PropTypes.node,
};