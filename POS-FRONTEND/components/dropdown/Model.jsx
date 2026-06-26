'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";

const ModelDropdown = ({ value, onChange }) => {
  const [models, setModels] = useState([]);

  useEffect(() => {
    fetchModels();
  }, []);

  const fetchModels = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await api.get("/models/active", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setModels(res.data || []);
    } catch (err) {
      console.error("Model fetch error:", err);
    }
  };

  return (
    <select
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="border rounded-lg px-3 py-2 w-full"
    >
      <option value="">Select Model</option>

      {models.map((m) => (
        <option key={m.identifier} value={m.modelName}>
          {m.modelName}
        </option>
      ))}
    </select>
  );
};

ModelDropdown.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

export default ModelDropdown;