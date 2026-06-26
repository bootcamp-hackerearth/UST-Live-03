'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";

const WareHouseDropdown = ({ value, onChange }) => {
  const [warehouses, setWarehouses] = useState([]);

  useEffect(() => {
    fetchWarehouses();
  }, []);

  const fetchWarehouses = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await api.get("/warehouse/active", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setWarehouses(res.data || []);

    } catch (err) {
      console.error("Warehouse fetch error:", err);
    }
  };

  return (
    <select
      value={value || ""}
      onChange={(e) => onChange(e.target.value)}
      className="border rounded-lg px-3 py-2 w-full"
    >
      <option value="">Select Warehouse</option>

      {warehouses.map((w) => (
        <option key={w.identifier} value={w.identifier}>
          {w.name || w.identifier}
        </option>
      ))}
    </select>
  );
};

WareHouseDropdown.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

export default WareHouseDropdown;