import { useEffect, useState } from "react";
import api from "@/api/axios";

export function useDropdownOptions(apiUrl, valueField, labelField) {
  const [options, setOptions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!apiUrl) {
      setLoading(false);
      return;
    }
    const loadOptions = async () => {
      try {
        const res = await api.get(apiUrl);
        setOptions(res.data.map(item => ({ value: item[valueField], label: item[labelField] })));
      } catch {
        alert("Could not load list. Please refresh.");
      } finally {
        setLoading(false);
      }
    };
    loadOptions();
  }, [apiUrl, valueField, labelField]);

  return { options, loading };
}
