"use client";

import { useParams } from "next/navigation";
import { useState } from "react";
import PropTypes from "prop-types";
import Update from "../../../../components/edit";
import SingleDropdown from "../../../../components/SingleDropdown";
import { useAuditField } from "../../../../utils/useAuditField";

function ProductField({ value, onChange }) {
  return (
    <SingleDropdown
      value={value}
      label="Product"
      apiPath="product/getAllActive"
      valueField="productName"
      displayField="productName"
      required
      onChange={onChange}
      urlMethod="get"
    />
  );
}

function WarehouseField({ value, onChange }) {
  return (
    <SingleDropdown
      value={value}
      label="Warehouse"
      apiPath="warehouse/list"
      required
      onChange={onChange}
      urlMethod="post"
    />
  );
}

function StockStatusField({ value, onChange }) {
  return (
    <div>
      <label
        style={{
          display: "block",
          fontWeight: "bold",
          marginBottom: "6px",
          fontSize: "14px",
        }}
        htmlFor="stockStatus"
      >
        Stock Status
      </label>

      <select
        id="stockStatus"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        required
        style={{
          width: "100%",
          border: "1px solid #d1d5db",
          borderRadius: "6px",
          padding: "10px 14px",
          fontSize: "14px",
          boxSizing: "border-box",
        }}
      >
        <option value="">Select Status</option>
        <option value="IN_STOCK">In Stock</option>
        <option value="OUT_OF_STOCK">Out Of Stock</option>
      </select>
    </div>
  );
}

ProductField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

WarehouseField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

StockStatusField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

export default function EditStock() {
  const params = useParams();

  const [productName, setProductName] = useState("");
  const [warehouseName, setWarehouseName] = useState("");
  const [stockStatus, setStockStatus] = useState("");
  const auditField = useAuditField();

  const extraFields = [
    {
      key: "productName",
      type: "custom",
      onLoad: (val) => setProductName(val),
      component: <ProductField value={productName} onChange={setProductName} />,
    },
    {
      key: "warehouseName",
      type: "custom",
      onLoad: (val) => setWarehouseName(val),
      component: (
        <WarehouseField value={warehouseName} onChange={setWarehouseName} />
      ),
    },
    {
      key: "quantity",
      label: "Quantity",
      type: "number",
      required: true,
    },
    {
      key: "stockStatus",
      type: "custom",
      onLoad: (val) => setStockStatus(val),
      component: (
        <StockStatusField value={stockStatus} onChange={setStockStatus} />
      ),
    },
    auditField,
  ];

  return (
    <Update
      identifier={params.identifier}
      apiPath="stock"
      title="Stock"
      extraFields={extraFields}
      extraData={{
        productName,
        warehouseName,
        stockStatus,
      }}
    />
  );
}
