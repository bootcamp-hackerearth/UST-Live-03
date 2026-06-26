export const getListContent = (response) => {
    if (Array.isArray(response)) {
        return response;
    }
    return response?.content || [];
};

export const getProductIdentifier = (item) =>
    item?.identifier || item?.id || "";

export const getItemLabel = (item) => {
    if (!item) return "-";

    return (
        item.name ||
        item.title ||
        item.identifier ||
        item.username ||
        item.phoneNo ||
        item.id ||
        "-"
    );
};

export const getDisplayValue = (value) => {
    if (value === null || value === undefined || value === "") {
        return "-";
    }

    if (Array.isArray(value)) {
        return value.length ? value.join(", ") : "-";
    }

    if (typeof value === "object") {
        return (
            value.name ||
            value.title ||
            value.identifier ||
            value.id ||
            JSON.stringify(value)
        );
    }

    return value;
};

export const filterCustomerOption = (
    option,
    inputValue
) => {
    const search = inputValue.toLowerCase();

    return (
        option.label.toLowerCase().includes(search) ||
        option.value.toLowerCase().includes(search)
    );
};