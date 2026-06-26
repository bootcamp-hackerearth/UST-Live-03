export async function FetchListUsingGet(url) {

    try {
        const res = await fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
        })
        return res.json();   
    } catch (err) {
        console.log(err);
        return null;
    }
}