// app/pos/products/page.jsx

"use client";

import BaseListForm from "../../../components/lists/BaseListForm";

const ProductList = () => {
  const columns = [
    { key: "id", label: "ID" },
    { key: "identifier", label: "SKU Code" },
    { key: "name", label: "Product Name" },
    {
      key: "categories",
      label: "Category",
      render: (val) => {
        if (!val || (Array.isArray(val) && val.length === 0)) {
          return <span className="text-[#231F20]/40">No categories</span>;
        }

        const categoryArray = Array.isArray(val) ? val : [val];
        const categoryNames = categoryArray.map(c =>
          typeof c === 'object' ? (c.name || c.identifier) : c
        );

        return (
          <div className="flex flex-wrap gap-1">
            {categoryNames.map((name, idx) => (
              <span
                key={`cat-${name}-${idx}`}
                className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-semibold bg-[#0097AC]/10 text-[#0097AC] border border-[#0097AC]/20"
              >
                {name}
              </span>
            ))}
          </div>
        );
      }
    },
    { key: "brand", label: "Brand" },
    { 
      key: "model", 
      label: "Model",
      render: (val) => val ? (
          <span className="font-mono text-md text-[#231F20]">{val}</span>
      ) : (
          <span className="text-[10px] font-semibold text-gray-400 bg-gray-100 px-2 py-0.5 rounded-full">
              N/A
          </span>
      )
    },
    { key: "status", label: "Status" }
  ];

  return (
    <BaseListForm
      title="Products"
      entity="product"
      columns={columns}
      addPath="/pos/products/add"
      editPath="/pos/products/edit"
      identifierKey="identifier"
    />
  );
};

export default ProductList;