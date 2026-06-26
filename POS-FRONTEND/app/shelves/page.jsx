"use client";

import PropTypes from "prop-types";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const shelfFields = [
  {
    name: "identifier",
    placeholder: "Identifier",
    disableOnEdit: true,
  },
];

const shelfValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Identifier is required";
  }

  return errors;
};

const ShelfForm = (props) => {
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
      title="Shelf"
      fields={shelfFields}
      validate={shelfValidate}
      onSubmit={handleSubmit}
    />
  );
};

ShelfForm.propTypes = {
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

ShelfForm.defaultProps = {
  data: {},
  fields: [],
  mode: "add",
  onClose: () => { },
  validate: null,
  title: "Shelf",
};

export default function ShelfPage() {
  const keys = ["identifier", "status"];

  return (
    <CommonList
      routeName="shelves"
      keys={keys}
      editField="identifier"
      FormComponent={ShelfForm}
    />
  );
}