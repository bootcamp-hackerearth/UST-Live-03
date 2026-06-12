// app/lib/useApiWithLoader.jsx

import { useCallback } from "react";
import { useLoading } from "./loadingContext";
import api from "../../app/api/axios";

export function useApiWithLoader() {
  const { showLoader, hideLoader } = useLoading();

  const get = useCallback(async (url, config = {}) => {
    showLoader();
    try {
      const response = await api.get(url, config);
      return response.data;
    } finally {
      hideLoader();
    }
  }, [showLoader, hideLoader]);

  const post = useCallback(async (url, data, config = {}) => {
    showLoader();
    try {
      const response = await api.post(url, data, config);
      return response.data;
    } finally {
      hideLoader();
    }
  }, [showLoader, hideLoader]);

  return { get, post };
}