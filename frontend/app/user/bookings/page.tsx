"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { fetchWithAuth } from "@/app/refresh"

const API_BASE =
    process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost/api";

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

type ApiError = { error?: string; message?: string };

export default function UserBookingListPage() {

    const [bookings, setBookings] = useState<Booking[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [deletingId, setDeletingId] = useState<string | null>(null);

    useEffect(() => {

        const fetchBookings = async () => {
            try {
                setLoading(true);
                setError(null);

                const accessToken = localStorage.getItem("accessToken");
                if (!accessToken) {
                    setError("Error: user is not logged in.");
                    setLoading(false);
                    return;
                }

                const res = await fetchWithAuth(`${API_BASE}/booking/list/user`, {
                    method: "GET",
                });

                if (!res.ok) {
                    const text = await res.text().catch(() => "");
                    let data: ApiError | null = null;
                    try {
                        data = text ? (JSON.parse(text) as ApiError) : null;
                    } catch {
                        // ignore JSON parse errors
                    }

                    const msg =
                        data?.error ||
                        data?.message ||
                        `Failed to load bookings: HTTP ${res.status}`;
                    throw new Error(msg);
                }

                const data: Booking[] = await res.json();
                setBookings(data);
            } catch (err) {
                const msg =
                    err instanceof Error ? err.message : "Unknown error while loading";

                if (
                    msg.toLowerCase().includes("no access token") ||
                    msg.toLowerCase().includes("not authenticated") ||
                    msg.toLowerCase().includes("session expired")
                ) {
                    setError("Error: user is not logged in.");
                } else {
                    setError(msg);
                }
            } finally {
                setLoading(false);
            }
        };

        fetchBookings();
    }, []);

    const deleteBooking = async (bookingId: string) => {
        if (!confirm("Delete this booking? This cannot be undone.")) return;

        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
            alert("Error: user is not logged in.");
            return;
        }

        setDeletingId(bookingId);

        // optimistic update
        const prev = bookings;
        setBookings((b) => b.filter((x) => x.bookingId !== bookingId));

        try {
            const res = await fetchWithAuth(
                `${API_BASE}/booking/delete/${encodeURIComponent(bookingId)}`,
                { method: "DELETE" }
            );

            if (!res.ok) {
                // revert on failure
                setBookings(prev);

                const text = await res.text().catch(() => "");
                let data: ApiError | null = null;
                try {
                    data = text ? (JSON.parse(text) as ApiError) : null;
                } catch {
                    // ignore JSON parse errors
                }
                const msg =
                    data?.error || data?.message || `Failed to delete: HTTP ${res.status}`;
                throw new Error(msg);
            }
            // success: nothing else to do (already removed)
        } catch (err) {
            const msg =
                err instanceof Error ? err.message : "Unknown error while deleting";

            if (
                msg.toLowerCase().includes("no access token") ||
                msg.toLowerCase().includes("not authenticated") ||
                msg.toLowerCase().includes("session expired")
            ) {
                alert("Error: user is not logged in.");
            } else {
                alert(`Failed to delete booking: ${msg}`);
            }

            // make sure we restore list on error if not already restored
            setBookings(prev);
        } finally {
            setDeletingId(null);
        }
    };

    if (loading) return <p>Loading bookings…</p>;
    if (error) return <p style={{ color: "red" }}>{error}</p>;

    return (
        <div style={{ maxWidth: 900, padding: 16 }}>
            <h2>User Bookings</h2>

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
