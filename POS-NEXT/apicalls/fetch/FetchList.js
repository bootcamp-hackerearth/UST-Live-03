export async function FetchList(url, page = 0, sizePerPage = 3) {

    try {
        const res = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify({ page, sizePerPage })
        })
        return res.json();   
    } catch (err) {
        console.log(err);
        return null;
    }
}