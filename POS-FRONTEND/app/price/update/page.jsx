'use client';

import PropTypes from 'prop-types';
import UpdatePage from "../../../components/common/UpdatePage";

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

export default function UpdatePrice() {
  const fields = [
    {
      name: "product",
      label: "Product",
      component: ReadOnlyField,
    },

    {
      name: "priceType",
      label: "Price Type",
      component: ReadOnlyField,
    },

    {
      name: "amount",
      label: "Amount",
      type: "number",
    },
  ];

  return (
    <UpdatePage
      fields={fields}
      modelName="price"
    />
  );
}