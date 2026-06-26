"use client";

import PropTypes from "prop-types";
import { useParams, useRouter } from "next/navigation";
import { useState } from "react";
import Update from "../../../../components/edit";
import SingleDropdown from "../../../../components/SingleDropdown";
import { useAuditField } from "../../../../utils/useAuditField";

function SuperCategoryField({ value, onChange }) {
  return (
    <SingleDropdown
      value={value}
      label="Super Category"
      apiPath="category/list"
      onChange={onChange}
      urlMethod={"post"}
    />
  );
}

SuperCategoryField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

export default function EditCategory() {
  const params = useParams();
  const router = useRouter();
  const [superCategory, setSuperCategory] = useState("");
  const auditField = useAuditField();

  const extraFields = [
    {
      key: "superCategory",
      type: "custom",
      onLoad: (val) => setSuperCategory(val || ""),
      component: (
        <SuperCategoryField
          value={superCategory}
          onChange={(val) => setSuperCategory(val)}
        />
      ),
    },
    auditField,
  ];

  return (
    <Update
      identifier={params.identifier}
      apiPath="category"
      title="Category"
      extraFields={extraFields}
      extraData={{ superCategory }}
      showDescription={false}
      onClose={() => router.push("/category/list")}
      onSuccess={() => router.push("/category/list")}
    />
  );
}
