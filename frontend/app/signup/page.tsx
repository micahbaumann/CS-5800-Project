"use client";

import Link from "next/link";
import { useState, FormEvent } from "react";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

type User = {
    userId: string;
    username: string;
    name: string;
};

type ApiError = { error?: string; message?: string };

export default function SignupPage() {
    const [username, setUsername] = useState("");
    const [name, setName] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState<string>("");
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setLoading(true);
        setMessage("");
        setUser(null);

        try {
        const res = await fetch(`${API_BASE}/user/create`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, name, password }),
            mode: "cors",
        });

        const text = await res.text();
        const data = text ? (JSON.parse(text) as User | ApiError) : null;

        if (!res.ok) {
            const errMsg =
            (data as ApiError)?.error ||
            (data as ApiError)?.message ||
            `HTTP ${res.status}`;
            throw new Error(errMsg);
        }

        setMessage("Signup successful!");
        setUser(data as User);
        setUsername("");
        setName("");
        setPassword("");
        } catch (err) {
        setMessage(`Error: ${err instanceof Error ? err.message : "Unknown error"}`);
        } finally {
        setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: 480, padding: 16 }}>
        <h2>Signup</h2>

        <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: 12 }}>
            <label htmlFor="username">Username:&nbsp;</label>
            <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
            />
            </div>

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
                <label htmlFor="password">Password:&nbsp;</label>
                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
            </div>

            <button type="submit" disabled={loading}>
            {loading ? "Submitting..." : "Sign Up"}
            </button>
        </form>

        {message && <p style={{ marginTop: 16 }}>{message}</p>}

        {user && (
            <p><Link href={`/login`}>Login</Link></p>
        )}
        </div>
    );
}