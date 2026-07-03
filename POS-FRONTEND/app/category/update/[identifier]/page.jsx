'use client';

import PropTypes from 'prop-types';
import UpdatePage from "../../../../components/common/UpdatePage";
import Categories from "../../../../components/dropdown/Categories";

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

const CategoryUpdate = () => {
  const fields = [
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
    <UpdatePage
      fields={fields}
      modelName="category"
    />
  );
};

export default CategoryUpdate;