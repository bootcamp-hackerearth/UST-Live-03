"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation } from "@/validation/validation"

export default function BrandPage() {
  return (
    <CommonList
      routeName="brand"
      editField="identifier"
      keys={["identifier", "description", "status"]}
      headers={["Brand Name", "Description", "Status"]}
      FormComponent={BrandForm}
    />
  );
}

BrandForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function BrandForm({ mode, data, onClose, onSubmit }) {
  return (
    <CommonForm
      title="Brand"
      mode={mode}
      data={data}
      onClose={onClose}
      onSubmit={onSubmit}
      fields={[
        { name: "identifier", label: "Brand Name", placeholder: "Enter Brand Name", required: true, validation: requiredValidation },
        { name: "description", label: "Description", placeholder: "Enter Description", required: true, validation: requiredValidation },
      ]}
    />
  );
}