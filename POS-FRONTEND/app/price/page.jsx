'use client';

import PropTypes from "prop-types";
import Sidebar from "../../components/layout/Sidebar";
import ListPage from "../../components/common/ListPage";

function ReadOnlyField({ value }) {
  return (
    <input
      value={value || ""}
      readOnly
      className="border rounded-lg px-3 py-2 bg-gray-100 w-full"
    />
  );
}

ReadOnlyField.propTypes = {
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
};

export default function PriceList() {
  const keys = [
    "identifier",
    "product",
    "priceType",
    "amount",
  ];

  const fields = [
    {
      name: "product",
      label: "Product",
      component: ReadOnlyField,
      type: "text",
    },
    {
      name: "priceType",
      label: "Price Type",
      component: ReadOnlyField,
      type: "text",
    },
    {
      name: "amount",
      label: "Amount",
      type: "number",
    },
  ];

  return (
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        modelName="price"
        showToggle={false}
      />
    </Sidebar>
  );
}