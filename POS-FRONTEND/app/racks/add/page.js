"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import AddFormSkeleton from "../../components/AddFormSkeleton";
import MultiDropDown from "../../components/MultiDropDown";

function ShelfsMultiDropdownWrapper({ shelfs, setShelfs }) {
    return (
        <MultiDropDown
            label="Shelfs"
            apiUrl="/shelfs/findByStatus"
            valueField="identifier"
            labelField="identifier"
            selectedValues={shelfs}
            onChange={setShelfs}
        />
    );
}

ShelfsMultiDropdownWrapper.propTypes = {
    shelfs: PropTypes.arrayOf(PropTypes.string).isRequired,
    setShelfs: PropTypes.func.isRequired,
};

export default function AddRacks() {
    const [shelfs, setShelfs] = useState([]);

    const rackFields = [
        {
            key: "shelfs",
            type: "custom",
            label: "Shelfs",
            component: (
                <ShelfsMultiDropdownWrapper shelfs={shelfs} setShelfs={setShelfs} />
            ),
        },
    ];

    return (
        <AddFormSkeleton
            title="Rack"
            apiPath="racks"
            extraFields={rackFields}
            extraData={{ shelfs }}
        />
    );
}