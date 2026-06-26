export async function FetchEntity(
    url,
    method,
    body = null,
    content_type = "application/json"
) {
    const options = {
        method,
        credentials: "include",
        headers: {
            "Content-Type": content_type
        }
    };

    if (method !== "GET" && body != null) {
        options.body =
            content_type === "application/json"
                ? JSON.stringify(body)
                : body.toString();
    }

    const res = await fetch(url, options);

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