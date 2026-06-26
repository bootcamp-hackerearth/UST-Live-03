export async function FetchList(url, page = 0, sizePerPage = 3, sortDirection = "ASC", sortField = "id") {
    try {
        console.log(`[FetchList] Calling ${url}`, { page, sizePerPage, sortDirection, sortField });

        const res = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify({ page, sizePerPage, sortDirection, sortField })
        });

        console.log(`[FetchList] Response status from ${url}:`, res.status);

        const text = await res.text();

        console.log(`[FetchList] Success from ${url}:`, text.substring(0, 200));
        return text ? JSON.parse(text) : null;
    } catch (err) {
        console.log(`[FetchList] Exception from ${url}:`, err);
        return null;
    }
}