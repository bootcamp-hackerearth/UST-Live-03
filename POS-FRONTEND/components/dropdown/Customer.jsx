'use client';

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../../services/api";

const CustomerDropdown = ({ value, onChange }) => {
  const [customers, setCustomers] = useState([]);

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await api.post(
        "/customer/list",
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
      setCustomers(res.data.dtoList || []);
    } catch (err) {
      console.error("Customer fetch error:", err);
    }
  };

  return (
    <select
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="border rounded-lg px-3 py-2 w-full"
    >
      <option value="">Select Customer</option>

      {customers.map((c) => (
        <option key={c.identifier} value={c.identifier}>
          {/* Change field based on your DTO */}
          {c.name || c.identifier}
        </option>
      ))}
    </select>
  );
};

CustomerDropdown.propTypes = {
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
};

export default CustomerDropdown;