"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation } from "@/validation/validation";

export default function ShelfPage() {
  return (
    <CommonList
      routeName="shelf"
      editField="identifier"
      keys={["identifier", "status"]}
      headers={["Shelf Name", "Status"]}
      FormComponent={ShelfForm}
    />
  );
}

ShelfForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function ShelfForm({ mode, data, onClose, onSubmit }) {
  const customValidations = {
    identifier: requiredValidation,
  };

  return (
    <CommonForm
      title="Shelf"
      mode={mode}
      data={data}
      onClose={onClose}
      validate={customValidations}
      onSubmit={onSubmit}
      fields={[
        {
          name: "identifier",
          label: "Shelf Name",
          placeholder: "Shelf Name",
          required: true,
        },
      ]}
    />
  );
}