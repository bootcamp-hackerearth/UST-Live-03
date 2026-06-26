"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation } from "@/validation/validation";

export default function RacksPage() {
  return (
    <CommonList
      routeName="racks"
      editField="identifier"
      keys={["identifier", "shelves", "status"]}
      headers={["Rack", "Shelves", "Status"]}
      FormComponent={RackForm}
    />
  );
}

RackForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function RackForm({ mode, data, onClose, onSubmit }) {
  const customValidations = {
    identifier: requiredValidation,
    shelves: requiredValidation,
  };

  return (
    <CommonForm
      title="Rack"
      mode={mode}
      data={data}
      onClose={onClose}
      validate={customValidations}
      onSubmit={async (formData) => {
        if (typeof formData.shelves === "string") {
          formData.shelves = formData.shelves
            .split(",")
            .map((s) => s.trim())
            .filter(Boolean);
        }
        await onSubmit(formData);
      }}
      fields={[
        {
          name: "identifier",
          label: "Rack Identifier",
          placeholder: "Rack Identifier",
          required: true,
        },
        {
          name: "shelves",
          label: "Shelves",
          placeholder: "S1,S2,S3,S4",
          required: true,
        },
      ]}
    />
  );
}