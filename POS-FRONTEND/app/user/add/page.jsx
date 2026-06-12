"use client";

import React, { useEffect, useState } from "react";

import POSLayout from "../../components/PosLayout";
import SectionForm from "../../components/common/SectionForm";
import commonApi from "../../services/commonApi";

function UserAdd() {
  const [roles, setRoles] = useState([]);
  const [existingUsers, setExistingUsers] = useState([]);

  useEffect(() => {
    fetchRoles();
    fetchUsers();
  }, []);

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

  const fetchUsers = async () => {
    try {
      const res = await commonApi.list("user", {
        page: 0,
        sizePerPage: 1000,
        sortDirection: "ASC",
        sortField: "id",
        search: "",
      });

      setExistingUsers(res.data.dtoList || []);
    } catch (err) {
      console.log(err);
    }
  };

  const sections = [
    {
      title: "User Information",
      columns: 2,
      fields: [
        {
          key: "name",
          label: "Name",
          type: "text",
          placeholder: "Enter Name",
          required: true,
        },
        {
          key: "username",
          label: "Email",
          type: "email",
          placeholder: "Enter Email",
          required: true,
        },
        {
          key: "phoneNo",
          label: "Phone Number",
          type: "text",
          placeholder: "Enter Phone Number",
          required: true,
          isPhone: true,
        },
        {
          key: "password",
          label: "Password",
          type: "password",
          placeholder: "Enter Password",
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
          fullWidth: true,
        },
      ],
    },
  ];

  return (
    <POSLayout>
      <SectionForm
        title="Add New User"
        submitUrl="/user/register"
        backUrl="/user"
        sections={sections}
        existingData={existingUsers}
        uniqueFields={["username"]}
        initialValues={{
          name: "",
          username: "",
          phoneNo: "",
          password: "",
          roles: [],
        }}
      />
    </POSLayout>
  );
}

export default UserAdd;
