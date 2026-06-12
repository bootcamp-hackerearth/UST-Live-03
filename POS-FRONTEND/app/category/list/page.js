"use client";
import CommonList from "@/app/components/CommonList";
const CategoryList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Category", field: "identifier" },
    {
      label: "Super Category",
      render: (item) =>
        item.superCategory
          ? item.superCategory.identifier || item.superCategory
          : "-",
    },

    { label: "Status", field: "status" },
  ];

  return (
    <CommonList
      title="Category Management"
      columns={columns}
      urlName="category"   
      showStatus={true}
    />
  );
};

export default CategoryList;