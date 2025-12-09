const API_BASE =
    process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost/api";

type Tokens = {
    access: string;
};

async function refreshAccessToken(): Promise<string | null> {
    try {
        const res = await fetch(`${API_BASE}/auth/refresh`, {
            method: "POST",
            credentials: "include", // send refresh cookie
        });

        if (!res.ok) {
            console.warn("Refresh token invalid or expired");
            return null;
        }

        const data = (await res.json()) as Tokens;

        // Adjust field name if backend uses different ones
        if (data.access) {
            localStorage.setItem("accessToken", data.access);
            return data.access;
        }

        return null;
    } catch (err) {
        console.error("Failed to refresh access token", err);
        return null;
    }
}

/**
 * Wrapper around fetch that:
 *  - sends Authorization: Bearer <accessToken>
 *  - on 401 tries to refresh and retries once
 */
export async function fetchWithAuth(
    input: string,
    init: RequestInit = {}
): Promise<Response> {
    let accessToken = localStorage.getItem("accessToken");

    if (!accessToken) {
        // not logged in
        throw new Error("No access token; user is not authenticated");
    }

    const doFetch = async (token: string): Promise<Response> => {
        const headers = new Headers(init.headers || {});
        headers.set("Authorization", `Bearer ${token}`);

        return fetch(input, {
            ...init,
            headers,
            credentials: "include", // if your API needs cookies too
        });
    };

    // First attempt
    let res = await doFetch(accessToken);

    if (res.status !== 401) {
        return res;
    }

    // Try refreshing once
    const newToken = await refreshAccessToken();
    if (!newToken) {
        // refresh failed -> treat as logged out
        throw new Error("Session expired; please log in again");
    }

    // Retry with new token
    res = await doFetch(newToken);
    return res;
}