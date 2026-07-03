'use client';

import { useState } from "react";
import Modal from "../../../components/common/Modal";

const RoleUpdate = () => {
  const [showModal, setShowModal] = useState(false);

  const [formData, setFormData] = useState({
    identifier: "",
    description: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      name: "description",
      label: "Description",
      type: "textarea",
    },
  ];

  const handleSubmit = async () => {
    console.log("Updated Role:", formData);
    setShowModal(false);
  };

  return (
    <div>
      <button
        onClick={() => setShowModal(true)}
        className="bg-blue-500 text-white px-4 py-2 rounded-lg"
      >
        Update Role
      </button>
      {showModal && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div className="bg-white rounded-xl p-6 w-full max-w-md shadow-lg">
            
            <h2 className="text-xl font-semibold mb-4">
              Update Role
            </h2>
            <Modal
              formData={formData}
              handleChange={handleChange}
              fields={fields}
            />
            <div className="flex justify-end gap-3 mt-6">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 border rounded-lg"
              >
                Cancel
              </button>

              <button
                onClick={handleSubmit}
                className="px-4 py-2 bg-green-500 text-white rounded-lg"
              >
                Save
              </button>
            </div>

          </div>
        </div>
      )}
    </div>
  );
};

export default RoleUpdate;