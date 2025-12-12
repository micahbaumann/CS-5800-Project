"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import {fetchWithAuth} from "@/app/refresh";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost/api";

type User = {
    userId: string;
    username: string;
    name: string;
};

type Props = {
    params: { userId: string };
};

export default function UserProfilePage({ params }: Props) {
    const { userId } = params;
    const [user, setUser] = useState<User | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const token = localStorage.getItem("accessToken");

                if (!token) {
                    setError("Not logged in");
                    setLoading(false);
                    return;
                }

                const res = await fetchWithAuth(`${API_BASE}/user/view`, {
                    cache: "no-store",
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (res.status !== 200) {

                    setError("User not found");
                    setLoading(false);
                    return;
                }

                if (!res.ok) {
                    throw new Error(`Failed to load user: HTTP ${res.status}`);
                }

                const data: User = await res.json();
                setUser(data);
            } catch (err) {
                setError(
                    err instanceof Error ? err.message : "Unknown error fetching user"
                );
            } finally {
                setLoading(false);
            }
        };

        fetchUser();
    }, [userId]);

    if (loading) return <div style={{ padding: 16 }}>Loading...</div>;
    if (error) return <div style={{ padding: 16 }}>Error: {error}</div>;
    if (!user) return <div style={{ padding: 16 }}>No user data</div>;

    return (
        <div style={{ maxWidth: 600, padding: 16 }}>
            <h2>User Profile</h2>
            <ul>
                <li>
                    <b>ID:</b> {user.userId}
                </li>
                <li>
                    <b>Username:</b> {user.username}
                </li>
                <li>
                    <b>Name:</b> {user.name}
                </li>
            </ul>
            <p>
                <Link href={`/user/bookings`}>View Bookings</Link>
            </p>
            <p>
            <Link href={`/message/list`}>View Messages</Link>
            </p>
        </div>
    );
}