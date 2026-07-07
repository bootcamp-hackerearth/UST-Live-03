import { useState } from "react";

export const useToast = () => {
  const [toast, setToast] = useState({ visible: false, message: "" });

  const triggerToast = (message) => {
    setToast({ visible: true, message });
    setTimeout(() => setToast({ visible: false, message: "" }), 3000);
  };

  return { toast, triggerToast };
};