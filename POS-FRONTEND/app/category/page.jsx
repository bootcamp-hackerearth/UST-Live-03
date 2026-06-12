"use client";

import CommonList from "@/components/CommonList";

export default function CategoryPage() {

  return (

    <CommonList
      title="Categories"
      subtitle="Manage categories"
      apiUrl="http://localhost:8080/api/category/list"
      deleteUrl="http://localhost:8080/api/category/delete"
      apiRoute="category"
      searchKeys={[
        "identifier",
      ]}
      columns={[
        {
          label:
            "Category",
          key:
            "identifier",
        },
        {
          label:
            "Super Category",
          key:
            "superCategory",
          render: (item) => (

            <div className="flex flex-wrap gap-2">

              {Array.isArray(
                item.superCategory
              ) ? (

                item.superCategory.map(
                  (
                    category,
                    index
                  ) => (

                    <span
                      key={`${category}-${index}`}
                      className="px-3 py-1 rounded-full bg-[#fff7f2] border border-[#f4dfd2] text-[#c17a47] text-xs"
                    >

                      {category}

                    </span>
                  )
                )

              ) : (

                <span>

                  {
                    item.superCategory
                  }

                </span>
              )}

            </div>
          ),
        },
      ]}
      fields={[
        {
          name:
            "identifier",
          type: "text",
          placeholder:
            "Enter Category Name",
          required: true,
        },
        {
          name:
            "superCategory",
          type: "select",
          multiple: true,
          dataKey:
            "categories",
          placeholder:
            "Select Super Category",
        },
      ]}
      dropdownApis={{
        categories:
          "http://localhost:8080/api/category/list",
      }}
    />

  );
}