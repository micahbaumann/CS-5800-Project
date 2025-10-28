"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

type Chef = {
    chefId: string,
    user: string;
    price: number;
    listingName: string;
};

export default function UserListPage() {
    const [users, setUsers] = useState<Chef[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchUsers = async () => {
        try {
            const res = await fetch(`${API_BASE}/chef/list`, {
            method: "GET",
            mode: "cors",
            });

            if (!res.ok) {
            throw new Error(`HTTP ${res.status}`);
            }

            const data: Chef[] = await res.json();
            setUsers(data);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Unknown error");
        } finally {
            setLoading(false);
        }
        };

        fetchUsers();
    }, []);

    if (loading) return <p>Loading chefs...</p>;
    if (error) return <p style={{ color: "red" }}>Error: {error}</p>;

    return (
        <div style={{ maxWidth: 600, padding: 16 }}>
        <h2>Chef List</h2>
        {users.length === 0 ? (
            <p>No users found.</p>
        ) : (
            <table border={1} cellPadding={6} style={{ borderCollapse: "collapse" }}>
            <thead>
                <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Name</th>
                </tr>
            </thead>
            <tbody>
                {users.map((u) => (
                <tr key={u.chefId}>
                    <td>{u.chefId}</td>
                    <td>{u.user}</td>
                    <td>{u.listingName}</td>
                    <td>{u.price}</td>
                    <td><Link href={`/chef/view/${u.chefId}`}>View</Link></td>
                </tr>
                ))}
            </tbody>
            </table>
        )}
        </div>
    );
}
