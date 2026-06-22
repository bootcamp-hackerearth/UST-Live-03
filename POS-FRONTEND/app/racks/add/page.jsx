"use client";

import { useState } from "react";
import AddFormSkeleton from "@/components/AddSkeleton";
import { INITIAL_STATE, buildRacksFields } from "@/components/dropdowns/racksFieldsConfig";

export default function AddRacks() {
    const [fields, setFields] = useState(INITIAL_STATE);

    const handleChange = (key) => (val) => setFields((prev) => ({ ...prev, [key]: val }));

    return (
        <AddFormSkeleton
            title="Racks"
            apiPath="racks"
            extraFields={buildRacksFields(fields, handleChange)}
            extraData={fields}
        />
    );
}