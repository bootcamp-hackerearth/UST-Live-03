import { useEffect, useState } from "react";
import { FetchEntity } from "@/apicalls/fetch/FetchEntity";

export const useFetchEntity = ({ baseUrl, apiRoute, identifier, method }) => {

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProduct = async () => {
      setLoading(true);

      try {
        const response = await FetchEntity(
          `${baseUrl}/${apiRoute}/${identifier}`,
          "GET"
        );

        setData(response);

      } catch (err) {
        setError({
          status: err.status,
          message: err.message
        });
      } finally {
        setLoading(false);
      }
    };

    if (method === "update" && identifier) {
      fetchProduct();
    }
  }, [baseUrl, apiRoute, identifier, method]);

  return { data, loading, error };
};