import AddEditForm from "@/components/AddEditForm";

export default function RoleUpdate() {

    const fields = [

        { name: "identifier", type: "text", placeholder: "Role Identifier", required: true, readOnly: true },
        { name: "description", type: "text", placeholder: "Role Description", required: true, readOnly: false },

    ];
    const dropdownApis = {};

    return (

        <AddEditForm title="Role" dropdownApis={dropdownApis} apiRoute="role" fields={fields} method="update" />

    )
}