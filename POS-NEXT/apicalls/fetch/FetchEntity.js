export async function FetchEntity(url, body, content_type = "application/json") {
    try {
        const res = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": content_type
            },
            credentials: "include",
            body: content_type === "application/json" ? JSON.stringify(body) : body.toString()
        })
        const text = await res.text();
        return text ? JSON.parse(text) : null;

    } catch (err) {
        console.log(err);
        return null;
    }
}