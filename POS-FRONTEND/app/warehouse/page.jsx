"use client";

import PropTypes from "prop-types";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const warehouseFields = [
  {
    name: "identifier",
    placeholder: "Identifier",
    disableOnEdit: true,
  },
  {
    name: "region",
    placeholder: "Region",
    type: "text",
  },
  {
    name: "country",
    placeholder: "Country",
    type: "text",
  },
  {
    name: "location",
    placeholder: "Location",
    type: "text",
  },
  {
    name: "contactName",
    placeholder: "Contact Name",
    type: "text",
  },
  {
    name: "contactNumber",
    placeholder: "Contact Number",
    type: "text",
  },
];

const warehouseValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Identifier is required";
  }

  if (!formData.region?.trim()) {
    errors.region = "Region is required";
  }

  if (!formData.country?.trim()) {
    errors.country = "Country is required";
  }

  if (!formData.location?.trim()) {
    errors.location = "Location is required";
  }

  if (!formData.contactName?.trim()) {
    errors.contactName = "Contact name is required";
  }

  if (!formData.contactNumber?.trim()) {
    errors.contactNumber = "Contact number is required";
  } else if (!/^\d{10}$/.test(formData.contactNumber)) {
    errors.contactNumber = "Enter a valid 10-digit contact number";
  }

  return errors;
};

const WarehouseForm = (props) => {
  const rest = { ...props };
  delete rest.fields;

  return (
    <CommonForm
      {...rest}
      title="Warehouse"
      fields={warehouseFields}
      validate={warehouseValidate}
    />
  );
};

WarehouseForm.propTypes = {
  onSubmit: PropTypes.func,
  data: PropTypes.shape({
    status: PropTypes.bool,
  }),
  fields: PropTypes.arrayOf(PropTypes.object),
  mode: PropTypes.string,
  onClose: PropTypes.func,
  validate: PropTypes.func,
};

WarehouseForm.defaultProps = {
  data: {},
  fields: [],
  mode: "add",
  onSubmit: null,
  onClose: () => { },
  validate: null,
};

export default function WarehousePage() {
  const keys = [
    "identifier",
    "region",
    "country",
    "location",
    "contactName",
    "contactNumber",
  ];

  return (
    <CommonList
      routeName="warehouse"
      keys={keys}
      editField="identifier"
      FormComponent={WarehouseForm}
    />
  );
}