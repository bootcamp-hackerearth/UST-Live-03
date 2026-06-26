"use client";

import PropTypes from "prop-types";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const brandFields = [
  {
    name: "identifier",
    placeholder: "Identifier",
    disableOnEdit: true,
  },
  {
    name: "description",
    placeholder: "Description",
    type: "text",
  },
];

const brandValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Identifier is required";
  }

  if (!formData.description?.trim()) {
    errors.description = "Description is required";
  }

  return errors;
};

const BrandForm = (props) => {
  const rest = { ...props };
  delete rest.fields;

  const handleSubmit = (formData) => {
    props.onSubmit({
      ...formData,
      status: props.data?.status ?? true,
    });
  };

  return (
    <CommonForm
      {...rest}
      title="Brand"
      fields={brandFields}
      validate={brandValidate}
      onSubmit={handleSubmit}
    />
  );
};

BrandForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  data: PropTypes.shape({
    status: PropTypes.bool,
  }),
  fields: PropTypes.arrayOf(PropTypes.object),
  mode: PropTypes.string,
  onClose: PropTypes.func,
  validate: PropTypes.func,
};

BrandForm.defaultProps = {
  data: {},
  fields: [],
  mode: "add",
  onClose: () => {},
  validate: null,
};

export default function BrandPage() {
  const keys = ["identifier", "description", "status"];

  return (
    <CommonList
      routeName="brand"
      keys={keys}
      editField="identifier"
      FormComponent={BrandForm}
    />
  );
}