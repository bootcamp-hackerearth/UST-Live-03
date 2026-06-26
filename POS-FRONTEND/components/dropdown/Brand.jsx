'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";

const BrandDropdown = ({ value, onChange }) => {
  const [brands, setBrands] = useState([]);

  useEffect(() => {
    fetchBrands();
  }, []);

  const fetchBrands = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await api.get(
        "/brand/active",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setBrands(res.data || []);
    } catch (err) {
      console.error("Active Brand fetch error:", err);
    }
  };

  return (
    <select
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="border rounded-lg px-3 py-2 w-full"
    >
      <option value="">Select Brand</option>
      {brands.map((b) => (
        <option key={b.identifier} value={b.brandName}>
          {b.brandName}
        </option>
      ))}
    </select>
  );
};

BrandDropdown.propTypes = {
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
};

export default BrandDropdown;