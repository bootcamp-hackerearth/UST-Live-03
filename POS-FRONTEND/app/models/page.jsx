"use client";

import PropTypes from "prop-types";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const modelFields = [
  {
    name: "identifier",
    placeholder: "Identifier",
    disableOnEdit: true,
  },
];

const modelValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Identifier is required";
  }

  return errors;
};

const ModelForm = (props) => {
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
      title="Model"
      fields={modelFields}
      validate={modelValidate}
      onSubmit={handleSubmit}
    />
  );
};

ModelForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  data: PropTypes.shape({
    status: PropTypes.bool,
  }),
  fields: PropTypes.arrayOf(PropTypes.object),
  mode: PropTypes.string,
  onClose: PropTypes.func,
  validate: PropTypes.func,
};

ModelForm.defaultProps = {
  data: {},
  fields: [],
  mode: "add",
  onClose: () => { },
  validate: null,
};

export default function ModelPage() {
  const keys = ["identifier", "status"];

  return (
    <CommonList
      routeName="models"
      keys={keys}
      editField="identifier"
      FormComponent={ModelForm}
    />
  );
}