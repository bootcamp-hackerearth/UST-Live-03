import { FetchListUsingGet } from "@/apicalls/fetch/FetchListUsingGet";

export default async function DropDownService(dropdownApisParam) {

  const entries = Object.entries(dropdownApisParam);
  const results = await Promise.all(
    entries.map(async ([key, api]) => {
      const res = await FetchListUsingGet(api);
      console.log("RESPONSE",res);
      return [key, res || []];
    })
  );
  return Object.fromEntries(results);
};

