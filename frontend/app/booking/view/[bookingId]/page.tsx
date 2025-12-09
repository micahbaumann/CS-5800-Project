"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { fetchWithAuth } from "@/app/refresh"

// For browser code, use NEXT_PUBLIC_ env vars.
// If you're fronting everything through nginx at /api you can also just use "/api"
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

type Props = {
    params: { bookingId: string };
};

export default function BookingProfilePage({ params }: Props) {
    const { bookingId } = params;

    const [booking, setBooking] = useState<Booking | null>(null);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState<string>("");

    useEffect(() => {
        const loadBooking = async () => {
            setLoading(true);
            setMessage("");
            setBooking(null);

            const accessToken = localStorage.getItem("accessToken");
            if (!accessToken) {
                setMessage("Error: user is not logged in.");
                setLoading(false);
                return;
            }

            try {
                const res = await fetchWithAuth(
                    `${API_BASE}/booking/view/${encodeURIComponent(bookingId)}`,
                    {
                        cache: "no-store",
                    }
                );

                if (res.status === 404) {
                    setMessage("Booking not found.");
                    setLoading(false);
                    return;
                }

                if (!res.ok) {
                    const text = await res.text();
                    let apiError: ApiError | null = null;
                    try {
                        apiError = text ? (JSON.parse(text) as ApiError) : null;
                    } catch {
                        // ignore JSON parse errors
                    }
                    const errMsg =
                        apiError?.error ||
                        apiError?.message ||
                        `Failed to load booking: HTTP ${res.status}`;
                    throw new Error(errMsg);
                }

                const data = (await res.json()) as Booking;
                setBooking(data);
            } catch (err) {
                const msg =
                    err instanceof Error ? err.message : "Unknown error fetching booking";

                if (
                    msg.toLowerCase().includes("no access token") ||
                    msg.toLowerCase().includes("not authenticated") ||
                    msg.toLowerCase().includes("session expired")
                ) {
                    setMessage("Error: user is not logged in.");
                } else {
                    setMessage(`Error: ${msg}`);
                }
            } finally {
                setLoading(false);
            }
        };

        loadBooking();
    }, [bookingId]);

    if (loading) {
        return <div style={{ maxWidth: 600, padding: 16 }}>Loading...</div>;
    }

    if (message && !booking) {
        return (
            <div style={{ maxWidth: 600, padding: 16 }}>
                <p>{message}</p>
            </div>
        );
    }

    if (!booking) {
        return (
            <div style={{ maxWidth: 600, padding: 16 }}>
                <p>No booking data.</p>
            </div>
        );
    }

    return (
        <div style={{ maxWidth: 600, padding: 16 }}>
            <h2>Booking Profile</h2>
            <ul>
                {"bookingId" in booking && (
                    <li>
                        <b>ID:</b> {booking.bookingId}
                    </li>
                )}
                <li>Booking Id: {booking.bookingId}</li>
                <li>User Id: {booking.user}</li>
                <li>Start: {booking.start}</li>
                <li>End: {booking.end}</li>
                <li>Status: {booking.status}</li>
                <li>Address: {booking.address}</li>
            </ul>

            <h2>Chef Profile</h2>
            <ul>
                {"chefId" in booking.chef && (
                    <li>
                        <b>ID:</b> {booking.chef.chefId}
                    </li>
                )}
                <li>Chef Id: {booking.chef.chefId}</li>
                <li>User Id: {booking.chef.user}</li>
                <li>Price: {booking.chef.price}</li>
                <li>Name: {booking.chef.listingName}</li>
            </ul>

            {/* Optional actions/links */}
            <p>
                <Link href={`/chef/view/${booking.chef.chefId}`}>
                    View Chef Profile
                </Link>
            </p>
        </div>
    );
}
