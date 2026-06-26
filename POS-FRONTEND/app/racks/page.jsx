"use client";

import PropTypes from "prop-types";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const rackFields = [
  {
    name: "identifier",
    placeholder: "Identifier",
    disableOnEdit: true,
  },
  {
    name: "shelves",
    placeholder: "Shelves",
    type: "multiselect",
    apiUrl: "http://localhost:8080/api/shelves/list",
  },
];

const rackValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Identifier is required";
  }

  if (!formData.shelves || formData.shelves.length === 0) {
    errors.shelves = "At least one shelf is required";
  }

  return errors;
};

const RackForm = (props) => {
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
      title="Rack"
      fields={rackFields}
      validate={rackValidate}
      onSubmit={handleSubmit}
    />
  );
};

RackForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  data: PropTypes.shape({
    status: PropTypes.bool,
  }),
  fields: PropTypes.arrayOf(PropTypes.object),
  mode: PropTypes.string,
  onClose: PropTypes.func,
  validate: PropTypes.func,
};

RackForm.defaultProps = {
  data: {},
  fields: [],
  mode: "add",
  onClose: () => { },
  validate: null,
};

export default function RackPage() {
  const keys = ["identifier", "shelves", "status"];

  return (
    <CommonList
      routeName="racks"
      keys={keys}
      editField="identifier"
      FormComponent={RackForm}
    />
  );
}