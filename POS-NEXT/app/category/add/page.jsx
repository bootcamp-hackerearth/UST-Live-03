import AddEditForm from "@/components/AddEditForm";

export default function CategoryAdd(){

    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";

    const fields = [
    { 
      name: "identifier", type: "text", placeholder: "Identifier", required: true, 
      readOnly: false 
    },
    { 
      name: "superCategory", type: "select", placeholder: "Super Category", dataKey: "superCategory", multiple: true, 
      hardCoded: false, hardCodedArray: [], required: true, readOnly: false
    }
  ];

  const dropdownApis = {
    superCategory: `${baseUrl}/category/getactive`
  };

  return (
    
    <AddEditForm
      title="Category"
      fields={fields}
      apiRoute="category"
      dropdownApis={dropdownApis}
      method="add"
    />
  );
};
