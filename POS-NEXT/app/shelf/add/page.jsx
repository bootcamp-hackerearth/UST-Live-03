import AddEditForm from "@/components/AddEditForm";

export default function ShelfAdd(){

    const fields = [
    { 
      name: "identifier", type: "text", placeholder: "Shelf Name", required: true, 
      readOnly: false 
    },
    { 
      name: "description", type: "text", placeholder: "Description", required: false, 
      readOnly: false 
    }
  ];

  const dropdownApis = {
  };

  return (
    
    <AddEditForm
      title="Shelf"
      fields={fields}
      apiRoute="shelf"
      dropdownApis={dropdownApis}
      method="add"
    />
  );
};
