import AddEditForm from "@/components/AddEditForm";
import { getWarehouseFields } from "../warehouseFields";

export default function WarehouseAdd() {
    return (
        <AddEditForm
            title="Warehouse"
            fields={getWarehouseFields(false)}
            apiRoute="warehouse"
            dropdownApis={{}}
            method="add"
        />
    );
}