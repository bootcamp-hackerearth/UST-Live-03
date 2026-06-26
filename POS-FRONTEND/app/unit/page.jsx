"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation } from "@/validation/validation"


export default function UnitPage() {
  return (
    <CommonList
      routeName="unit"
      editField="identifier"
      keys={["identifier", "status"]}
      headers={["Unit Name", "Status"]}
      FormComponent={UnitForm}
    />
  );
}

UnitForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function UnitForm({ mode, data, onClose, onSubmit }) {
  return (
    <CommonForm
      title="Unit"
      mode={mode}
      data={data}
      onClose={onClose}
      onSubmit={onSubmit}
      fields={[
        {
          name: "identifier",
          label: "Unit Name",
          placeholder: "Unit Name",
          required: true,
          validation: requiredValidation
        },
      ]}
    />
  );
}