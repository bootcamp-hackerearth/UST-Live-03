import { useEffect, useState } from "react";
import { FetchEntity } from "@/apicalls/fetch/FetchEntity";

export const useFetchEntity = ({ baseUrl, apiRoute, identifier, method }) => {
  
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchProduct = async () => {
      setLoading(true);
      try {
        const response = await FetchEntity(
          `${baseUrl}/${apiRoute}/${identifier}`,
          "GET",
        );
        setData(response);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    if (method === "update" && identifier) {
      fetchProduct();
    }
  }, [baseUrl, apiRoute, identifier, method]);

  return { data, loading };
};