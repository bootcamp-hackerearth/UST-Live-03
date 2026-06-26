"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation } from "@/validation/validation"

export default function ModelsPage() {
  return (
    <CommonList
      routeName="models"
      editField="identifier"
      keys={["identifier", "status"]}
      headers={["Model Name", "Status"]}
      FormComponent={ModelsForm}
    />
  );
}

ModelsForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function ModelsForm({ mode, data, onClose, onSubmit }) {
  return (
    <CommonForm
      title="Model"
      mode={mode}
      data={data}
      onClose={onClose}
      onSubmit={onSubmit}
      fields={[
        {
          name: "identifier",
          label: "Model Name",
          placeholder: "Model Name",
          required: true,
          validation: requiredValidation
        },
      ]}
    />
  );
}