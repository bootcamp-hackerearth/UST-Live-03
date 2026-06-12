import SingleDropdown from "@/components/dropdowns/SingleDropdown";
import MultiDropDown from "@/components/dropdowns/MultiDropDown";

const SINGLE_DROPDOWN_CONFIGS = [
    { key: "brand", label: "Brand", apiUrl: "/brand/findByStatus" },
    { key: "unit", label: "Unit", apiUrl: "/unit/findByStatus" },
    { key: "model", label: "Model", apiUrl: "/models/findByStatus" },
];

export const INITIAL_STATE = { brand: "", unit: "", model: "", category: [] };

export function buildProductFields(fields, handleChange) {
    const singleDropdownFields = SINGLE_DROPDOWN_CONFIGS.map(({ key, label, apiUrl }) => ({
        key,
        label,
        type: "custom",
        component: (
            <SingleDropdown
                label={label}
                apiUrl={apiUrl}
                selectedValue={fields[key]}
                onChange={handleChange(key)}
            />
        ),
    }));

    return [
        { key: "name", label: "Product Name", type: "text", required: true },
        ...singleDropdownFields,
        {
            key: "category",
            type: "custom",
            label: "Category",
            component: (
                <MultiDropDown
                    label="Category"
                    apiUrl="/category/getBySuperCategoryNotNull"
                    valueField="identifier"
                    labelField="identifier"
                    selectedValues={fields.category}
                    onChange={handleChange("category")}
                />
            ),
        },
    ];
}