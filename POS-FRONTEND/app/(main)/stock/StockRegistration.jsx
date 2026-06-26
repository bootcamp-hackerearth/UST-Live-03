"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { FiArchive } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const StockRegistration = ({ onClose, refreshData }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    warehouseName: "",
    quantity: "",
    status: true,
  });

  const fields = [
    {
      key: "identifier",
      label: "Stock Name",
      type: "text",
    },
    {
      key: "warehouseName",
      label: "Warehouse",
      type: "search",
      api: "/api/warehouse/list",
    },
    {
      key: "quantity",
      label: "Quantity",
      type: "number",
    },
    {
      key: "status",
      label: "Status",
      type: "checkbox",
    },
  ];

  return (
    <CommonAdd
      title="Add Stock"
      icon={FiArchive}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="stock"
      submitLabel="Add Stock"
      onSubmit={() => {
        refreshData?.();
        onClose?.();
      }}
    />
  );
};
StockRegistration.propTypes = {
  onClose: PropTypes.func,
  refreshData: PropTypes.func,
};
export default StockRegistration;