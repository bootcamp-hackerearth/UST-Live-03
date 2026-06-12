"use client";

import React, { useEffect, useState } from "react";

import POSLayout from "../components/PosLayout";
import DynamicList from "../components/common/DynamicList";
import commonApi from "../services/commonApi";
function NodeList() {
  const [roles, setRoles] = useState([]);

  useEffect(() => {
    commonApi
      .list("role", {
        page: 0,
        sizePerPage: 1000,
        sortDirection: "ASC",
        sortField: "id",
        search: "",
      })
      .then((res) => {
        setRoles(res.data.dtoList || []);
      })
      .catch((err) => console.log(err));
  }, []);

  const columns = [
    {
      key: "identifier",
      label: "Node",
      type: "text",
    },
    {
      key: "path",
      label: "Path",
      type: "text",
    },
    {
      key: "roles",
      label: "Roles",
      type: "list",
      displayKey: "identifier",
    },
  ];

  const formFields = [
    {
      key: "identifier",
      label: "Node",
      type: "text",
      placeholder: "Enter Node",
      required: true,
      readOnlyOnEdit: true,
    },
    {
      key: "path",
      label: "Path",
      type: "text",
      placeholder: "Enter Path",
      required: true,
    },
    {
      key: "roles",
      label: "Roles",
      type: "multiselect",
      options: roles,
      optionLabel: "identifier",
      optionValue: "identifier",
      required: true,
    },
  ];

  return (
    <POSLayout>
      <DynamicList
        title="Node List"
        routeName="node"
        columns={columns}
        formFields={formFields}
        formTitle="Node"
        uniqueFields={["identifier", "path"]}
      />
    </POSLayout>
  );
}

export default NodeList;