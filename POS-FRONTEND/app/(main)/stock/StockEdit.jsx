"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { FiArchive } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";

const StockEdit = ({ stock, onClose, refreshData }) => {
  const [formData, setFormData] = useState(stock || {});

  useEffect(() => {
    setFormData(stock || {});
  }, [stock]);

  const fields = [
    {
      key: "identifier",
      label: "Stock Name",
      type: "text",
      disabled: true,
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
    <CommonEdit
      title="Edit Stock"
      icon={FiArchive}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="stock"
      submitLabel="Update Stock"
      onSubmit={() => {
        refreshData?.();
        onClose?.();
      }}
    />
  );
};
StockEdit.propTypes = {
  stock: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    identifier: PropTypes.string,
    warehouseName: PropTypes.string,
    quantity: PropTypes.number,
    status: PropTypes.bool,
  }),
  onClose: PropTypes.func,
  refreshData: PropTypes.func,
};
export default StockEdit;
