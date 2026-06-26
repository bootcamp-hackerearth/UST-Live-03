"use client";

import React, { useEffect, useState } from "react";
import { useParams } from "next/navigation";

import POSLayout from "../../../components/PosLayout";
import SectionForm from "../../../components/common/SectionForm";
import commonApi from "../../../services/commonApi";

function UserEdit() {
const params = useParams();

const identifier = decodeURIComponent(
  params.identifier
);

  const [initialValues, setInitialValues] = useState(null);
  const [roles, setRoles] = useState([]);

  useEffect(() => {
    fetchUser();
    fetchRoles();
  }, []);

  const fetchUser = async () => {
    try {
      const res = await commonApi.get(
        "user",
        "identifier",
        identifier
      );

      setInitialValues(res.data);
    } catch (err) {
      console.log(err);
    }
  };

  const fetchRoles = async () => {
    try {
      const res = await commonApi.list("role", {
        page: 0,
        sizePerPage: 1000,
        sortDirection: "ASC",
        sortField: "id",
        search: "",
      });

      setRoles(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

  if (!initialValues) {
    return (
      <POSLayout>
        <div className="bg-white rounded-xl p-6 shadow-sm">
          Loading...
        </div>
      </POSLayout>
    );
  }

  const sections = [
    {
      title: "User Information",
      columns: 2,
      fields: [
        {
          key: "name",
          label: "Name",
          type: "text",
          required: true,
        },
        {
          key: "username",
          label: "Email",
          type: "email",
          disabled: true,
          required: true,
        },
        {
          key: "phoneNo",
          label: "Phone Number",
          type: "text",
          required: true,
          isPhone: true,
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
         {
  key: "createdBy",
  label: "Created By",
  type: "text",
  required: false,
},
{
  key: "createdOn",
  label: "Created On",
  type: "text",
  required: false,
},
{
  key: "modifiedBy",
  label: "Modified By",
  type: "text",
  required: false,
},
{
  key: "modifiedOn",
  label: "Modified On",
  type: "text",
  required: false,
}
      ],
    },
  ];

  const buildUserPayload = (formData) => {
    return {
      id: initialValues.id,
      identifier: initialValues.identifier,
      username: initialValues.username,
      name: formData.name,
      phoneNo: formData.phoneNo,
      roles: Array.isArray(formData.roles)
        ? formData.roles.map((role) =>
            typeof role === "object"
              ? role.identifier
              : role
          )
        : [],
    };
  };

  return (
    <POSLayout>
      <SectionForm
        title="Edit User"
        submitUrl="/user/update"
        backUrl="/user"
        sections={sections}
        initialValues={initialValues}
        buildCustomPayload={buildUserPayload}
      />
    </POSLayout>
  );
}

export default UserEdit;