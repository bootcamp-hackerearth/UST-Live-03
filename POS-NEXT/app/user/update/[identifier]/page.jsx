import AddEditForm from "@/components/AddEditForm";

export default function User() {

    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";

    const fields = [

        {
            name: "username", type: "email", placeholder: "Email",
            pattern: String.raw`^[^\s@]+@(ust\.com|gmail\.com)$`, patternMessage: "Please enter a valid UST email address", required: true, readOnly: true
        },
        {
            name: "name", type: "text", placeholder: "name",
            minlength: 3, patternMessage: "Name must be at least 3 characters",
            required: true, readOnly: false
        },
        {
            name: "roles", type: "select", placeholder: "roles", dataKey: "roles",
            hardCoded: false, hardCodedArray: [], required: true, readOnly: false, multiple: true
        },
        {
            name: "phoneNo", type: "number", placeholder: "Phone Number", patternMessage: "Phone number must be 10 digits", pattern: "^[0-9]{10}$", required: true,
            readOnly: false
        },
    ]

    const dropdownApis = {

        roles: `${baseUrl}/role/list`
    }

    return (
        
            <AddEditForm
                title="User"
                fields={fields}
                apiRoute="user"
                dropdownApis={dropdownApis}
                method="update"
            />
        
    )
}
