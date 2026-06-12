 
export async function CommonAddFetch(url, body, content_type = "application/json") {
    const token = localStorage.getItem("token");
    try {
        console.debug("CommonAddFetch request:", { url, content_type, body });
        console.debug("CommonAddFetch token present:", !!token);
        const res = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": content_type,
                "Authorization": `Bearer ${token}`,
            },
            credentials: "include",
            body: content_type === "application/json" ? JSON.stringify(body) : body
        })
       
        console.debug("CommonAddFetch response status:", res.status);
        const text = await res.text();  
        console.debug("CommonAddFetch response text:", text);

        if (!text) return null;
        try {
            return JSON.parse(text);
        } catch (err) {
            console.error("Failed to parse JSON response:", err);
            return { message: text };
        }
 
    } catch (err) {
        console.log(err);
        return null;
    }
}
 