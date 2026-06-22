"use client";

import { useState } from "react";
import EditFormSkeleton from "@/components/EditSkeleton";
import { INITIAL_STATE, buildRacksFields } from "@/components/dropdowns/racksFieldsConfig";

export default function EditRacks() {
    const [fields, setFields] = useState(INITIAL_STATE);

    const handleChange = (key) => (val) => setFields((prev) => ({ ...prev, [key]: val }));

    return (
        <EditFormSkeleton
            title="Racks"
            apiPath="racks"
            extraFields={buildRacksFields(fields, handleChange)}
            extraData={fields}
            setters={{
                shelfs: handleChange("shelfs"),
            }}
        />
    );
}