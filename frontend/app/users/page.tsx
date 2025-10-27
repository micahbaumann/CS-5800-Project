"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

type User = {
    userId: string;
    username: string;
    name: string;
};

export default function UserListPage() {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchUsers = async () => {
        try {
            const res = await fetch(`${API_BASE}/user/list`, {
            method: "GET",
            mode: "cors",
            });

            if (!res.ok) {
            throw new Error(`HTTP ${res.status}`);
            }

            const data: User[] = await res.json();
            setUsers(data);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Unknown error");
        } finally {
            setLoading(false);
        }
        };

        fetchUsers();
    }, []);

    if (loading) return <p>Loading users...</p>;
    if (error) return <p style={{ color: "red" }}>Error: {error}</p>;

    return (
        <div style={{ maxWidth: 600, padding: 16 }}>
        <h2>User List</h2>
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
                <tr key={u.userId ?? u.username}>
                    <td>{u.userId}</td>
                    <td>{u.username}</td>
                    <td>{u.name}</td>
                    <td><Link href={`/user/${u.userId}`}>View</Link></td>
                </tr>
                ))}
            </tbody>
            </table>
        )}
        </div>
    );
}
