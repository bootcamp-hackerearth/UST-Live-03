import AddEditForm from "@/components/AddEditForm";

export default function RacksUpdate() {

  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
  const fields = [
    {
      name: "identifier", type: "text", placeholder: "Rack Name",
      required: true, readOnly: true
    },
    {
      name: "shelves", type: "select", placeholder: "Shelves", dataKey: "shelves",
      hardCoded: false, hardCodedArray: [], required: false, readOnly: false
    }
  ];

  const dropdownApis = {
    shelves: `${baseUrl}/shelf/getactive`
  };

  return (
    
    <AddEditForm
      title="Rack"
      fields={fields}
      apiRoute="racks"
      dropdownApis={dropdownApis}
      method="update"/>

  );
};
