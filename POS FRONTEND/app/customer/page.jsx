"use client";
import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

export default function CustomerList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Phone Number", field: "identifier" },
    { label: "Customer Name", field: "customerName" },
    { label: "Email", field: "username" },
    { label: "Status", field: "status" },
    ...auditColumns(),
  ];

  const listTemplateProps = {
    title: "Customer Management",
    columns,
    urlName: "customer",
    showStatus: true,
    editKey: "identifier",
    deleteKey: "identifier",
    deleteParam: "identifier",
    statusKey: "identifier",
    addButtonLabel: "Customer",
    pageSize: 10,
  };

  return <ListTemplate {...listTemplateProps} />;
}