"use client";

import { useParams } from "next/navigation";
import { useState } from "react";
import PropTypes from "prop-types";
import Update from "../../../../components/edit";
import SingleDropdown from "../../../../components/SingleDropdown";
import { useAuditField } from "../../../../utils/useAuditField";

function ShelfField({ value, onChange }) {
  return (
    <SingleDropdown
      value={value}
      label="Shelf"
      apiPath="shelfs/getAllActive"
      required
      onChange={onChange}
      urlMethod={"get"}
    />
  );
}

ShelfField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

export default function EditRack() {
  const params = useParams();

  const [shelfs, setShelfs] = useState([]);
  const auditField = useAuditField();

  const extraFields = [
    {
      key: "shelfs",
      type: "custom",
      onLoad: (val) => {
        if (Array.isArray(val)) {
          setShelfs(val);
        } else if (val) {
          setShelfs([val]);
        }
      },
      component: (
        <ShelfField
          value={shelfs[0] || ""}
          onChange={(value) => setShelfs([value])}
        />
      ),
    },
    auditField,
  ];

  return (
    <Update
      identifier={decodeURIComponent(params.identifier)}
      apiPath="rack"
      title="Rack"
      showDescription={true}
      extraFields={extraFields}
      extraData={{
        shelfs,
      }}
    />
  );
}
