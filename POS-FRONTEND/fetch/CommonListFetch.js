export async function CommonListFetch(url, page = 0, sizePerPage = 3) {
 
 const token = localStorage.getItem("token");
    try {
        const res = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`,
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