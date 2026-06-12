import { FetchList } from "@/apicalls/fetch/FetchList";

export default async function DropDownService(dropdownApisParam) {

  const entries = Object.entries(dropdownApisParam);
  const results = await Promise.all(
    entries.map(async ([key, api]) => {
      const res = await FetchList(api, 0, 200);
      return [key, res?.content || []];
    })
  );
  return Object.fromEntries(results);
};

