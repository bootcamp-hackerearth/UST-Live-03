'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";


const ProductDropdown = ({ value, onChange }) => {
  const [Products, setProducts] = useState([]);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await api.post(
        "/product/list",
        {
          page: 0,
          sizePerPage: 1000,
          sortDirection: "ASC",
          sortField: "identifier"
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setProducts(res.data.dtoList || []);
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
      {Products.map((p) => (
        <option key={p.identifier} value={p.productName}>
          {p.productName}
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