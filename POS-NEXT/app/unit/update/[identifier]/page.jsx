import AddEditForm from "@/components/AddEditForm";

export default function UnitAdd(){

    const fields = [
    { 
      name: "identifier", type: "text", placeholder: "Unit Name", required: true, 
      readOnly: true 
    },
    { 
      name: "description", type: "text", placeholder: "Unit Description", required: false, 
      readOnly: false 
    }
  ];

  const dropdownApis = {
  };

  return (
    
    <AddEditForm
      title="Unit"
      fields={fields}
      apiRoute="unit"
      dropdownApis={dropdownApis}
      method="update"
    />
  );
};
