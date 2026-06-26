'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";

const Shelves = ({ value = [], onChange }) => {
  const [shelves, setShelves] = useState([]);

  useEffect(() => {
    fetchShelves();
  }, []);

  const fetchShelves = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await api.get(
        "/shelf/active",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setShelves(res.data || []);
    } catch (err) {
      console.error("Shelf fetch error:", err);
    }
  };

  let selectedValues = [];

  if (Array.isArray(value)) {
    selectedValues = value.map((item) => {
      if (typeof item === "object") {
        return item.shelfName;
      }
      return item;
    });
  } else if (value) {
    selectedValues = value.split(",");
  }

  return (
    <select
      multiple
      value={selectedValues}
      onChange={(e) =>
        onChange(
          Array.from(
            e.target.selectedOptions,
            (option) => option.value
          )
        )
      }
      className="w-full border rounded-lg px-3 py-2 min-h-[120px]"
    >
      {shelves.map((shelf) => (
        <option
          key={shelf.identifier}
          value={shelf.shelfName}
        >
          {shelf.shelfName}
        </option>
      ))}
    </select>
  );
};

Shelves.propTypes = {
  value: PropTypes.oneOfType([
    PropTypes.array,
    PropTypes.string,
  ]),
  onChange: PropTypes.func.isRequired,
};

export default Shelves;