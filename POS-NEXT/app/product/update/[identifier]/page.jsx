import AddEditForm from "@/components/AddEditForm";

export default function Productupdate() {

    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";

    const createTextField = (name, placeholder, readOnly = false, extra = {}) => ({
        name,
        type: "text",
        placeholder,
        required: true,
        readOnly,
        ...extra
    });

    const createSelectField = (name) => ({
        name,
        type: "select",
        placeholder: name,
        dataKey: name,
        hardCoded: false,
        hardCodedArray: [],
        required: true,
        readOnly: false
    });

    const fields = [
        createTextField("identifier", "Identifier", true, {
            pattern: String.raw`^P\d{3,}$`,
            patternMessage: "Identifier must start with P followed by at least 3 digits"
        }),

        createTextField("name", "Name"),
        createTextField("description", "Description"),

        ["category", "brand", "unit", "model"].map(createSelectField)
    ].flat();

    const dropdownApis = ["unit", "brand", "model", "category"]
        .reduce((acc, key) => {
            acc[key] = key === "model"
                ? `${baseUrl}/models/list`
                : `${baseUrl}/${key}/list`;
            return acc;
        }, {});

    return (
        <AddEditForm
            title="Product"
            dropdownApis={dropdownApis}
            apiRoute="product"
            fields={fields}
            method="update"
        />
    );
}
