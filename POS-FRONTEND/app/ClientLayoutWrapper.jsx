"use client";

import PropTypes from "prop-types";

export default function ClientLayoutWrapper({ children }) {
  return children;
}

ClientLayoutWrapper.propTypes = {
  children: PropTypes.node,
};