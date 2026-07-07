export async function FetchList(
  url,
  page = 0,
  search = "",
  sizePerPage = 3
) {
  const res = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify({ page, sizePerPage, search }),
  });

  const text = await res.text();
  const data = text ? JSON.parse(text) : null;

  if (!res.ok) {
    const error = new Error(
      data?.message || `Request failed with status ${res.status}`
    );
    error.status = res.status;
    error.data = data;
    throw error;
  }

  return data;
}