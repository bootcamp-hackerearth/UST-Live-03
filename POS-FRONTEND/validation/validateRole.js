import axios from "../components/axiosConfig"

export const validateRole = async(url) =>{
    
    const isBrowser = globalThis.window !== undefined;
    const username = isBrowser ? localStorage.getItem("username") : null;

    if(username===null) return null;

    const res = await axios.get(`/user/get?username=${username}`);
    const roles = res.data.roles || []

    const response = await axios.post(`/roleValidation`,{
        roles : roles,
        url : url
    });

    return response.data
}
