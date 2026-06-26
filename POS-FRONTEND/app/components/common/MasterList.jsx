"use client";

import React from "react";
import PropTypes from "prop-types";
import DynamicList from "./DynamicList";
import POSLayout from "../PosLayout";

const statusOptions = [
  {
    label: "Active",
    value: true,
  },
  {
    label: "Inactive",
    value: false,
  },
];

function MasterListPage({
  title,
  routeName,
  entityLabel,
  placeholder,
  editUrl,
}) {
  const columns = [
    {
      key: "identifier",
      label: entityLabel,
      type: "text",
    },
    {
      key: "status",
      label: "Status",
      type: "toggle",
    },
  ];

  const formFields = [
    {
      key: "identifier",
      label: entityLabel,
      type: "text",
      placeholder,
      required: true,
    },
    {
      key: "status",
      label: "Status",
      type: "select",
      options: statusOptions,
      optionLabel: "label",
      optionValue: "value",
      required: true,
    },
  ];

  return (
    <POSLayout>
      <DynamicList
        title={title}
        routeName={routeName}
        columns={columns}
        editUrl={editUrl}
        formFields={formFields}
        formTitle={entityLabel}
        uniqueFields={["identifier"]}
      />
    </POSLayout>
  );
}

MasterListPage.propTypes = {
  title: PropTypes.string.isRequired,
  routeName: PropTypes.string.isRequired,
  entityLabel: PropTypes.string.isRequired,
  placeholder: PropTypes.string.isRequired,
  editUrl: PropTypes.string,
};

export default MasterListPage;