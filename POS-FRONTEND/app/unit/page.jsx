"use client";

import PropTypes from "prop-types";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const unitFields = [
  {
    name: "identifier",
    placeholder: "Identifier",
    disableOnEdit: true,
  },
];

const unitValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Identifier is required";
  }

  return errors;
};

const UnitForm = (props) => {
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
      title="Unit"
      fields={unitFields}
      validate={unitValidate}
      onSubmit={handleSubmit}
    />
  );
};

UnitForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  data: PropTypes.shape({
    status: PropTypes.bool,
  }),
  fields: PropTypes.arrayOf(PropTypes.object),
  mode: PropTypes.string,
  onClose: PropTypes.func,
  validate: PropTypes.func,
  title: PropTypes.string,
};

UnitForm.defaultProps = {
  data: {},
  fields: [],
  mode: "add",
  onClose: () => { },
  validate: null,
  title: "Unit",
};

export default function UnitPage() {
  const keys = ["identifier", "status"];

  return (
    <CommonList
      routeName="unit"
      keys={keys}
      editField="identifier"
      FormComponent={UnitForm}
    />
  );
}