'use client';

import PropTypes from 'prop-types';
import AddPage from "../../../components/Common/AddPage";
import Categories from "../../../components/dropdown/Categories";

function SuperCategoryField({ value, onChange }) {
  return (
    <Categories
      value={value}
      onChange={onChange}
    />
  );
}

SuperCategoryField.propTypes = {
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.object,
  ]),
  onChange: PropTypes.func.isRequired,
};

export default function AddCategory() {
  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      name: "name",
      label: "Category Name",
      type: "text",
    },
    {
      name: "superCategory",
      label: "Super Category",
      required: false,
      component: SuperCategoryField,
    },
  ];

  return (
    <AddPage
      fields={fields}
      modelName="category"
    />
  );
}