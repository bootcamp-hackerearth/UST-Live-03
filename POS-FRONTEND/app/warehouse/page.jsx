"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation, nameValidation, phoneValidation } from "@/validation/validation";

export default function WarehousePage() {
  return (
    <CommonList
      routeName="warehouse"
      editField="identifier"
      keys={["identifier", "region", "country", "contactName", "contactNumber", "location"]}
      headers={["Warehouse ID", "Region", "Country", "Contact Name", "Contact Number", "Location"]}
      FormComponent={WarehouseForm}
    />
  );
}

WarehouseForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function WarehouseForm({ mode, data, onClose, onSubmit }) {
  const customValidations = {
    identifier: requiredValidation,
    region: requiredValidation,
    country: requiredValidation,
    contactName: nameValidation,
    contactNumber: phoneValidation,
    location: requiredValidation,
  };

  return (
    <CommonForm
      title="Warehouse"
      mode={mode}
      data={data}
      onClose={onClose}
      validate={customValidations}
      onSubmit={onSubmit}
      fields={[
        {
          name: "identifier",
          label: "Warehouse ID",
          placeholder: "Warehouse ID",
          required: true,
        },
        {
          name: "region",
          label: "Region",
          placeholder: "Region",
          required: true,
        },
        {
          name: "country",
          label: "Country",
          placeholder: "Country",
          required: true,
        },
        {
          name: "contactName",
          label: "Contact Name",
          placeholder: "Contact Name",
          required: true,
        },
        {
          name: "contactNumber",
          label: "Contact Number",
          placeholder: "Contact Number",
          required: true,
        },
        {
          name: "location",
          label: "Location",
          placeholder: "Location",
          required: true,
        },
      ]}
    />
  );
}