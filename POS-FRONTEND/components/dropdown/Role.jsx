'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";

const Roles = ({ value = [], onChange }) => {
  const [roles, setRoles] = useState([]);

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      const res = await api.get("/role");
      console.log("Roles API:", res.data);
      setRoles(res.data || []);
    } catch (err) {
      console.error("Error fetching roles:", err);
    }
  };

  return (
    <select
      multiple
      value={value}
      onChange={(e) => {
        const selectedValues = Array.from(
          e.target.selectedOptions,
          (option) => option.value
        );
        console.log("Selected:", selectedValues);
        onChange(selectedValues);
      }}
      className="border rounded-lg px-3 py-2 w-full h-40"
    >
      {roles.map((role) => (
        <option key={role.id} value={role.identifier}>
          {role.identifier}
        </option>
      ))}
    </select>
  );
};
Roles.propTypes = {
  value: PropTypes.arrayOf(PropTypes.string),
  onChange: PropTypes.func.isRequired,
};
export default Roles;