"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import { dropdownField } from "@/app/components/DropdownFieldHelper";
import { entitySubmit } from "@/app/components/EntitySubmitHelper";

export default function CategoryAddPage() {
  const handleSubmit = async (data) =>
    entitySubmit(
      "/api/category/add",
      {
        ...data,
        status: data.status === true || data.status === "true",
      },
      "Category added successfully"
    );

  return (
    <CommonAddPage
      title="Add Category"
      submitApi={handleSubmit}
      redirectRoute="/category/list"
      initialValues={{
        identifier: "",
        superCategory: "",
      }}
      fields={[
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
        },
        dropdownField(
          "Super Category",
          "superCategory",
          "/api/category/list",
          {
            includeNoneOption: true,
          }
        ),
      ]}
    />
  );
}