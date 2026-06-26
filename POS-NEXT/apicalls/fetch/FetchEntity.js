export async function FetchEntity(
    url,
    method,
    body = null,
    content_type = "application/json"
) {
    try {

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
        return text ? JSON.parse(text) : null;

    } catch (err) {
        console.log(err);
        return null;
    }
}