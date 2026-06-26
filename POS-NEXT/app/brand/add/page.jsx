import AddEditForm from "@/components/AddEditForm";

export default function BrandAdd(){

    const fields = [
    { 
      name: "identifier", type: "text", placeholder: "Brand Name", required: true, 
      readOnly: false 
    },
    { 
      name: "description", type: "text", placeholder: "Brand Description", required: false, 
      readOnly: false 
    }
  ];

  const dropdownApis = {
  };

  return (
    
    <AddEditForm
      title="Brand"
      fields={fields}
      apiRoute="brand"
      dropdownApis={dropdownApis}
      method="add"
    />
  );
};
