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
      } catch (err) {
        console.error(`Failed to load options from ${apiUrl}:`, err.response?.data || err.message);
        alert(`Could not load options. Please refresh and try again.`);
      } finally {
        setLoading(false);
      }
    };
    loadOptions();
  }, [apiUrl, valueField, labelField]);

  return { options, loading };
}

export function useSidebarOpen() {
  const [isOpen, setIsOpen] = useState(true);
  useEffect(() => {
    const handleToggle = (event) => setIsOpen(event.detail?.isOpen ?? true);
    globalThis.addEventListener("sidebar-toggle", handleToggle);
    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);
  return isOpen;
}

export function extractList(payload) {
  if (Array.isArray(payload)) return payload;
  if (!payload || typeof payload !== "object") return [];
  if (Array.isArray(payload.dtoList)) return payload.dtoList;
  if (Array.isArray(payload.data)) return payload.data;
  if (Array.isArray(payload.content)) return payload.content;
  if (Array.isArray(payload.list)) return payload.list;
  if (Array.isArray(payload.records)) return payload.records;
  return [];
}

export function getEntityLabel(item) {
  if (typeof item === "string") return item;
  if (!item || typeof item !== "object") return "";
  return item.name || item.subCategoryName || item.categoryName || item.brandName || item.title || item.identifier || item.id || "";
}

export function getEntityValue(item) {
  if (typeof item === "string") return item;
  if (!item || typeof item !== "object") return "";
  return item.identifier ?? item.id ?? getEntityLabel(item);
}
