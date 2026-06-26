'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";

const RackDropdown = ({ value, onChange }) => {
  const [racks, setRacks] = useState([]);

  useEffect(() => {
    fetchRacks();
  }, []);

  const fetchRacks = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await api.get("/rack/active", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setRacks(res.data || []);

    } catch (err) {
      console.error("Rack fetch error:", err);
    }
  };

  return (
    <select
      value={value || ""}
      onChange={(e) => onChange(e.target.value)}
      className="border rounded-lg px-3 py-2 w-full"
    >
      <option value="">Select Rack</option>

      {racks.map((rack) => (
        <option key={rack.identifier} value={rack.identifier}>
          {rack.name}
        </option>
      ))}
    </select>
  );
};

RackDropdown.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

export default RackDropdown;