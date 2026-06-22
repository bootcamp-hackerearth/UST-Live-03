"use client";

import MultiDropDown from "./MultiDropDown";

export const INITIAL_STATE = {
    shelfs: [],
};

export function buildRacksFields(fields, handleChange) {
    return [
        {
            key: "shelfs",
            label: "Shelfs",
            type: "custom",
            component: (
                <MultiDropDown
                    label="Shelfs"
                    apiUrl="/shelfs/findByStatus"
                    selectedValues={fields.shelfs}
                    onChange={handleChange("shelfs")}
                    valueField="identifier"
                    labelField="identifier"
                />
            ),
        },
    ];
}