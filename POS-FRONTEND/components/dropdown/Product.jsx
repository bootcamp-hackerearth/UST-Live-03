'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";

const ProductDropdown = ({ value, onChange }) => {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await api.get("/product/active", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setProducts(res.data || []);
    } catch (err) {
      console.error("Product fetch error:", err);
    }
  };

  return (
    <select
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="border rounded-lg px-3 py-2 w-full"
    >
      <option value="">Select Product</option>

      {products.map((p) => (
        <option key={p.identifier} value={p.identifier}>
          {p.identifier}
        </option>
      ))}
    </select>
  );
};

ProductDropdown.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

export default ProductDropdown;