import axios from "../components/axiosConfig"

export const validateRole = async (url) => {

    const isBrowser = globalThis.window !== undefined;
    const username = isBrowser ? localStorage.getItem("username") : null;

    if (username === null) return null;

    const res = await axios.get(`/user/get?username=${username}`);
    const roles = res.data.roles || []

    const response = await fetch("/api/roleValidation", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            roles: roles,
            url: url,
        })
    });

    
    const data = await response.json();
    return data
}
