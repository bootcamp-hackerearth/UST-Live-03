'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";

const CategoryDropdown = ({ value, onChange }) => {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    api.post("/category/list", {
      page: 0,
      sizePerPage: 1000
    })
      .then(res => setCategories(res.data.dtoList || []))
      .catch(err => console.error(err));
  }, []);

  return (
    <select
      value={value || ""}
      size={5}
      onChange={(e) => onChange(e.target.value)}
      className="border rounded-lg px-3 py-2 w-full"
    >
      <option value="">Select Super Category</option>

      {categories.map(cat => (
        <option key={cat.identifier} value={cat.name}>
          {cat.name}
        </option>
      ))}
    </select>
  );
};
CategoryDropdown.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};
export default CategoryDropdown;