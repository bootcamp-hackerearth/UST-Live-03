import AddEditForm from "@/components/AddEditForm";

export default function ModelsAdd(){

    const fields = [
    { 
      name: "identifier", type: "text", placeholder: "Model Name", required: true, 
      readOnly: false 
    },
    { 
      name: "description", type: "text", placeholder: "Model Description", required: false, 
      readOnly: false 
    }
  ];

  const dropdownApis = {
  };

  return (
    
    <AddEditForm
      title="Model"
      fields={fields}
      apiRoute="models"
      dropdownApis={dropdownApis}
      method="add"
    />
  );
};
