"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

type Chef = {
    chefId: string;
    user: string;
    price: number;
    listingName: string;
};

type Booking = {
    bookingId: string;
    user: string;
    chef: Chef;
    start: string;
    end: string;
    address: string;
    status: string;
};

export default function UserBookingListPage() {
    const { userId } = useParams<{ userId: string }>();

    const [bookings, setBookings] = useState<Booking[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [deletingId, setDeletingId] = useState<string | null>(null);

    useEffect(() => {
        if (!userId) return;

        const fetchBookings = async () => {
        try {
            setLoading(true);
            setError(null);

            const res = await fetch(
            `${API_BASE}/booking/list/user/${encodeURIComponent(userId)}`,
            { method: "GET", mode: "cors" }
            );

            if (!res.ok) throw new Error(`HTTP ${res.status}`);

            const data: Booking[] = await res.json();
            setBookings(data);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Unknown error");
        } finally {
            setLoading(false);
        }
        };

        fetchBookings();
    }, [userId]);

    const deleteBooking = async (bookingId: string) => {
        // optional confirmation
        if (!confirm("Delete this booking? This cannot be undone.")) return;

        setDeletingId(bookingId);

        // optimistic update
        const prev = bookings;
        setBookings((b) => b.filter((x) => x.bookingId !== bookingId));

        try {
        const res = await fetch(
            `${API_BASE}/booking/delete/${encodeURIComponent(bookingId)}`,
            { method: "DELETE", mode: "cors" }
        );

        if (!res.ok) {
            // revert on failure
            setBookings(prev);
            const text = await res.text().catch(() => "");
            throw new Error(text || `HTTP ${res.status}`);
        }
        // success: nothing else to do (already removed)
        } catch (err) {
        alert(
            `Failed to delete booking: ${
            err instanceof Error ? err.message : "Unknown error"
            }`
        );
        } finally {
        setDeletingId(null);
        }
    };

    if (!userId) return <p>Loading route…</p>;
    if (loading) return <p>Loading bookings…</p>;
    if (error) return <p style={{ color: "red" }}>Error: {error}</p>;

    return (
        <div style={{ maxWidth: 900, padding: 16 }}>
        <h2>User Bookings</h2>
        <p>
            <b>User ID:</b> {userId}
        </p>

        {bookings.length === 0 ? (
            <p>No bookings found.</p>
        ) : (
            <table
            border={1}
            cellPadding={6}
            style={{ borderCollapse: "collapse", width: "100%" }}
            >
            <thead>
                <tr>
                <th>Booking ID</th>
                <th>Chef ID</th>
                <th>Start</th>
                <th>End</th>
                <th>Status</th>
                <th>Address</th>
                <th>View</th>
                <th>Delete</th>
                </tr>
            </thead>
            <tbody>
                {bookings.map((b) => (
                <tr key={b.bookingId}>
                    <td>{b.bookingId}</td>
                    <td>{b.chef.chefId}</td>
                    <td>{b.start}</td>
                    <td>{b.end}</td>
                    <td>{b.status}</td>
                    <td>{b.address}</td>
                    <td>
                    <Link href={`/booking/view/${b.bookingId}`}>View</Link>
                    </td>
                    <td>
                    <button
                        onClick={() => deleteBooking(b.bookingId)}
                        disabled={deletingId === b.bookingId}
                    >
                        {deletingId === b.bookingId ? "Deleting..." : "Delete"}
                    </button>
                    </td>
                </tr>
                ))}
            </tbody>
            </table>
        )}
        </div>
    );
}