"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { fetchWithAuth } from "@/app/refresh";

const API_BASE =
    process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost/api";

type Chef = {
    chefId: string;
    user: string;
    price: number;
    listingName: string;
};

type Status = "Approved" | "Pending" | "Denied";

type Booking = {
    bookingId: string;
    user: string;
    chef: Chef;
    start: string;
    end: string;
    address: string;
    status: Status | string; // tolerate backend variants
};

type ApiError = { error?: string; message?: string };

const STATUS_OPTIONS: Status[] = ["Approved", "Pending", "Denied"];

function normalizeStatus(s: string): Status {
    const t = s.toLowerCase();
    if (t === "approved") return "Approved";
    if (t === "pending") return "Pending";
    if (t === "denied") return "Denied";
    // fallback to Pending if something unexpected comes back
    return "Pending";
}

export default function ChefBookingListPage() {
    const { chefId } = useParams<{ chefId: string }>();

    const [bookings, setBookings] = useState<Booking[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [busy, setBusy] = useState<Set<string>>(new Set()); // bookingIds being updated/deleted
    const [deletingId, setDeletingId] = useState<string | null>(null);

    useEffect(() => {
        if (!chefId) return;

        let cancelled = false;

        const fetchBookings = async () => {
            try {
                setLoading(true);
                setError(null);

                const accessToken = localStorage.getItem("accessToken");
                if (!accessToken) {
                    if (!cancelled) {
                        setError("Error: user is not logged in.");
                    }
                    return;
                }

                const res = await fetchWithAuth(
                    `${API_BASE}/booking/list/chef/${encodeURIComponent(chefId)}`,
                    { method: "GET" }
                );

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
                if (!cancelled) {
                    // normalize statuses
                    setBookings(
                        data.map((b) => ({
                            ...b,
                            status: normalizeStatus(String(b.status)),
                        }))
                    );
                }
            } catch (err) {
                const msg =
                    err instanceof Error ? err.message : "Unknown error while loading";

                if (!cancelled) {
                    if (
                        msg.toLowerCase().includes("no access token") ||
                        msg.toLowerCase().includes("not authenticated") ||
                        msg.toLowerCase().includes("session expired")
                    ) {
                        setError("Error: user is not logged in.");
                    } else {
                        setError(msg);
                    }
                }
            } finally {
                if (!cancelled) {
                    setLoading(false);
                }
            }
        };

        fetchBookings();

        return () => {
            cancelled = true;
        };
    }, [chefId]);

    const deleteBooking = async (bookingId: string) => {
        if (!confirm("Delete this booking? This cannot be undone.")) return;

        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
            alert("Error: user is not logged in.");
            return;
        }

        setDeletingId(bookingId);

        const prev = bookings;
        setBookings((b) => b.filter((x) => x.bookingId !== bookingId));

        try {
            const res = await fetchWithAuth(
                `${API_BASE}/booking/delete/${encodeURIComponent(bookingId)}`,
                { method: "DELETE" }
            );

            if (!res.ok) {
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
            // success: already removed
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

            setBookings(prev);
        } finally {
            setDeletingId(null);
        }
    };

    const changeStatus = async (bookingId: string, newStatus: Status) => {
        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
            alert("Error: user is not logged in.");
            return;
        }

        const prev = bookings;
        setBookings((b) =>
            b.map((x) =>
                x.bookingId === bookingId ? { ...x, status: newStatus } : x
            )
        );
        setBusy((s) => new Set(s).add(bookingId));

        try {
            const res = await fetchWithAuth(`${API_BASE}/booking/update-status`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ booking_id: bookingId, status: newStatus }),
            });

            if (!res.ok) {
                setBookings(prev);

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
                    `Failed to change status: HTTP ${res.status}`;
                throw new Error(msg);
            }
        } catch (err) {
            const msg =
                err instanceof Error ? err.message : "Unknown error while updating";

            if (
                msg.toLowerCase().includes("no access token") ||
                msg.toLowerCase().includes("not authenticated") ||
                msg.toLowerCase().includes("session expired")
            ) {
                alert("Error: user is not logged in.");
            } else {
                alert(`Failed to change status: ${msg}`);
            }

            setBookings(prev);
        } finally {
            setBusy((s) => {
                const copy = new Set(s);
                copy.delete(bookingId);
                return copy;
            });
        }
    };

    if (!chefId) return <p>Loading route…</p>;
    if (loading) return <p>Loading bookings…</p>;
    if (error) return <p style={{ color: "red" }}>Error: {error}</p>;

    return (
        <div style={{ maxWidth: 1000, padding: 16 }}>
            <h2>Chef Bookings</h2>
            <p>
                <b>Chef ID:</b> {chefId}
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
                        <th>Change Status</th>
                        <th>Delete</th>
                    </tr>
                    </thead>
                    <tbody>
                    {bookings.map((b) => {
                        const isBusy = busy.has(b.bookingId) || deletingId === b.bookingId;
                        return (
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
                                    <select
                                        value={normalizeStatus(String(b.status))}
                                        onChange={(e) =>
                                            changeStatus(b.bookingId, e.target.value as Status)
                                        }
                                        disabled={isBusy}
                                    >
                                        {STATUS_OPTIONS.map((opt) => (
                                            <option key={opt} value={opt}>
                                                {opt}
                                            </option>
                                        ))}
                                    </select>
                                </td>
                                <td>
                                    <button
                                        onClick={() => deleteBooking(b.bookingId)}
                                        disabled={isBusy}
                                    >
                                        {deletingId === b.bookingId ? "Deleting..." : "Delete"}
                                    </button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            )}
        </div>
    );
}
