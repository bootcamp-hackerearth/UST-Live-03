'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";

const CategoryDropdown = ({ value, onChange }) => {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const token = localStorage.getItem("token"); 

      const res = await api.get("/category/childCategories", {
        headers: { Authorization: `Bearer ${token}` },
      });

      setCategories(res.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <select
      multiple
      size={5}
      value={value ? value.split(",") : []}
      onChange={(e) =>
        onChange(
          Array.from(e.target.selectedOptions, (opt) => opt.value).join(",")
        )
      }
      className="border rounded-lg px-3 py-2 w-full"
    >
      {categories.map((c) => (
        <option key={c.identifier} value={c.name}>
          {c.name}
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