import { API_URL } from "../config/api";

export async function authenticatedFetch(url, options = {}) {

    const token = localStorage.getItem("token");

    const response = await fetch(API_URL + url, {
        ...options,
        headers: {
            ...(options.headers || {}),
            Authorization: `Bearer ${token}`
        }
    });

    if (response.status === 401) {

        alert("This account has been logged in on another device.");

        localStorage.clear();

        window.location.href = "/";

        throw new Error("Unauthorized");
    }

    return response;
}