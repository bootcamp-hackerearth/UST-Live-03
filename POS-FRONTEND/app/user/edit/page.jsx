"use client";

import { useMemo } from "react";
import { useSearchParams } from "next/navigation";
import CommonUpdateTemplate from "../../components/CommonUpdateTemplate";

export default function UserEdit() {
  const searchParams = useSearchParams();
  const username = searchParams.get("username") || "";

  const extraFields = useMemo(
    () => [
      {
        key: "name",
        label: "Name",
        type: "text",
        placeholder: "Enter full name",
        required: true,
      },
      {
        key: "phoneNo",
        label: "Phone Number",
        type: "text",
        placeholder: "Enter phone number",
        required: true,
        maxLength: 10,
        pattern: "^[0-9]{10}$",
        title: "Phone Number must contain exactly 10 digits",
      }, 
      {
        key: "roles",
        label: "Roles",
        type: "multiselect",
        apiEndpoint: "/role/findByStatus",
        required: true,
      },
    ],
    []
  );

  if (!username) {
    return (
      <div className="w-full max-w-3xl mx-auto py-16 text-center text-slate-600">
        No user selected for editing.
      </div>
    );
  }

  return (
    <CommonUpdateTemplate
      title="User"
      apiPath="user"
      recordId={username}
      recordParam="username"
      recordGetEndpoint="identifier"
      identifierKey="username"
      identifierLabel="Username"
      extraFields={extraFields}
      showDescription={false}
      identifierEditable={false}
      onSuccessPath="/user"
    />
  );
}
