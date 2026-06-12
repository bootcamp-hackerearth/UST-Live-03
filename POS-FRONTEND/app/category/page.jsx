'use client';

import PropTypes from 'prop-types';

import Sidebar from "../../components/layout/Sidebar";
import ListPage from "../../components/Common/ListPage";
import Categories from "../../components/dropdown/Categories";

function IdentifierField({ value }) {
  return (
    <input
      value={value || ""}
      readOnly
      className="w-full border rounded-lg px-3 py-2 bg-gray-100"
    />
  );
}

IdentifierField.propTypes = {
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
};

function SuperCategoryField({ value, onChange }) {
  return (
    <Categories
      value={value || ""}
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

export default function CategoryList() {
  const keys = [
    "identifier",
    "name",
    "superCategory",
  ];

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      component: IdentifierField,
    },
    {
      name: "name",
      label: "Category Name",
      type: "text",
    },
    {
      name: "superCategory",
      label: "Super Category",
      component: SuperCategoryField,
    },
  ];

  return (
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        modelName="category"
        showToggle={false}
      />
    </Sidebar>
  );
}