import AddEditForm from "@/components/AddEditForm";
import { getWarehouseFields } from "../../warehouseFields";

export default function WarehouseUpdate() {
    return (
        <AddEditForm
            title="Warehouse"
            fields={getWarehouseFields(true)}
            apiRoute="warehouse"
            dropdownApis={{}}
            method="update"/>
    );
}