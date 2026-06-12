'use client';

import PropTypes from 'prop-types';
import AddPage from "../../../components/Common/AddPage";
import ProductDropdown from "../../../components/dropdown/Product";
import Sidebar from "../../../components/layout/Sidebar";

function ProductField({ value, onChange }) {
  return (
    <ProductDropdown value={value} onChange={onChange} />
  );
}

ProductField.propTypes = {
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
    PropTypes.object,
  ]),
  onChange: PropTypes.func.isRequired,
};

function PriceTypeField({ value, onChange }) {
  return (
    <select
      value={value || ""}
      onChange={(e) => onChange(e.target.value)}
      className="border rounded-lg px-3 py-2 w-full"
    >
      <option value="">Select Price Type</option>
      <option value="MRP">MRP</option>
      <option value="SELLING">SELLING</option>
      <option value="DISCOUNT">DISCOUNT</option>
    </select>
  );
}

PriceTypeField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

export default function AddPrice() {
  const fields = [
    {
      name: "product",
      label: "Product",
      component: ProductField,
    },

    {
      name: "priceType",
      label: "Price Type",
      component: PriceTypeField,
    },

    {
      name: "amount",
      label: "Amount",
      type: "number",
    },
  ];

  return (
    <Sidebar>
    <AddPage
      fields={fields}
      modelName="price"
    />
    </Sidebar>
  );
}