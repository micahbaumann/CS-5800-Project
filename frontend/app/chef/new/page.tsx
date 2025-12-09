"use client";

import Link from "next/link";
import { useState, FormEvent } from "react";
import { fetchWithAuth} from "@/app/refresh";

const API_BASE =
    process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost/api";

type Chef = {
    chefId: string;
    user: string;
    price: number;
    listingName: string;
};

type ApiError = { error?: string; message?: string };

export default function ChefSignupPage() {
    const [name, setName] = useState("");
    const [price, setPrice] = useState<number | "">("");
    const [message, setMessage] = useState("");
    const [chef, setChef] = useState<Chef | null>(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setLoading(true);
        setMessage("");
        setChef(null);

        // If there is no access token, bail out immediately
        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
            setMessage("Error: user is not logged in.");
            setLoading(false);
            return;
        }

        try {
            const res = await fetchWithAuth(`${API_BASE}/chef/create`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    // fetchWithAuth will inject the Authorization header,
                    // so we don't set it here.
                },
                body: JSON.stringify({
                    listing_name: name,
                    price: Number(price),
                }),
            });

            const text = await res.text();
            const data = text ? (JSON.parse(text) as Chef | ApiError) : null;

            if (!res.ok) {
                const errMsg =
                    (data as ApiError)?.error ||
                    (data as ApiError)?.message ||
                    `Signup failed (HTTP ${res.status})`;
                throw new Error(errMsg);
            }

            setMessage("Chef signup successful!");
            setChef(data as Chef);
            setName("");
            setPrice("");
        } catch (err) {
            const msg =
                err instanceof Error ? err.message : "Unknown error during signup";

            // If our auth helper threw a "not authenticated" style error,
            // normalize the message.
            if (msg.toLowerCase().includes("no access token") ||
                msg.toLowerCase().includes("not authenticated") ||
                msg.toLowerCase().includes("session expired")) {
                setMessage("Error: user is not logged in.");
            } else {
                setMessage(`Error: ${msg}`);
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: 480, padding: 16 }}>
            <h2>Chef Signup</h2>

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="name">Name:&nbsp;</label>
                    <input
                        id="name"
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>

                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="price">Price:&nbsp;</label>
                    <input
                        id="price"
                        type="number"
                        step="0.01"
                        value={price}
                        onChange={(e) =>
                            setPrice(e.target.value ? Number(e.target.value) : "")
                        }
                        required
                    />
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? "Submitting..." : "Sign Up as Chef"}
                </button>
            </form>

            {message && <p style={{ marginTop: 16 }}>{message}</p>}

            {chef && (
                <div style={{ marginTop: 8 }}>
                    <strong>User Information:</strong>
                    <p>Chef Id: {chef.chefId}</p>
                    <p>User Id: {chef.user}</p>
                    <p>Price: {chef.price}</p>
                    <p>Name: {chef.listingName}</p>
                    <p>
                        <Link href={`/chef/view/${chef.chefId}`}>View Profile</Link>
                    </p>
                </div>
            )}
        </div>
    );
}