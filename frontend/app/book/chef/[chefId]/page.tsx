"use client";

import { useState, FormEvent, ChangeEvent } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { fetchWithAuth } from "@/app/refresh";

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

function toJavaLocalDateTime(value: string): string {
    // HTML datetime-local gives "YYYY-MM-DDTHH:MM" (no seconds).
    // Java LocalDateTime is happiest with seconds included.
    if (!value) return value;
    if (value.length === 16) return `${value}:00`;
    return value;
}

export default function BookingPage() {
    const params = useParams<{ chefId: string }>();
    const chefIdFromSlug = params.chefId;

    const [start, setStart] = useState<string>("");
    const [end, setEnd] = useState<string>("");
    const [address, setAddress] = useState<string>("");

    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState<string>("");
    const [booking, setBooking] = useState<Booking | null>(null);

    const handleDateChange =
        (setter: (v: string) => void) =>
            (e: ChangeEvent<HTMLInputElement>) => {
                setter(e.target.value);
            };

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setLoading(true);
        setMessage("");
        setBooking(null);

        // Basic client-side validation
        const startIso = toJavaLocalDateTime(start);
        const endIso = toJavaLocalDateTime(end);

        if (startIso && endIso && startIso >= endIso) {
            setMessage("Error: Start date/time must be before end date/time.");
            setLoading(false);
            return;
        }

        // Check auth first
        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
            setMessage("Error: user is not logged in.");
            setLoading(false);
            return;
        }

        try {
            const res = await fetchWithAuth(`${API_BASE}/booking/create`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    // Authorization header will be added by fetchWithAuth
                },
                body: JSON.stringify({
                    chef_id: chefIdFromSlug,
                    start: startIso,
                    end: endIso,
                    address,
                }),
            });

            const text = await res.text();
            const data = text ? (JSON.parse(text) as Booking | ApiError) : null;

            if (!res.ok) {
                const errMsg =
                    (data as ApiError)?.error ||
                    (data as ApiError)?.message ||
                    `HTTP ${res.status}`;
                throw new Error(errMsg);
            }

            setMessage("Booking created successfully!");
            setBooking(data as Booking);

            // Clear the form
            setStart("");
            setEnd("");
            setAddress("");
        } catch (err) {
            const msg =
                err instanceof Error ? err.message : "Unknown error during booking";

            // Normalize auth-related errors to "not logged in"
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

    return (
        <div style={{ maxWidth: 560, padding: 16 }}>
            <h2>Create Booking</h2>
            <p>
                <b>Chef ID:</b> {chefIdFromSlug}
            </p>

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="start">Start (local):&nbsp;</label>
                    <input
                        id="start"
                        type="datetime-local"
                        value={start}
                        onChange={handleDateChange(setStart)}
                        required
                    />
                </div>

                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="end">End (local):&nbsp;</label>
                    <input
                        id="end"
                        type="datetime-local"
                        value={end}
                        onChange={handleDateChange(setEnd)}
                        required
                    />
                </div>

                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="address">Address:&nbsp;</label>
                    <input
                        id="address"
                        type="text"
                        value={address}
                        onChange={(e) => setAddress(e.target.value)}
                        placeholder="123 Main St, City, State"
                        required
                    />
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? "Submitting..." : "Create Booking"}
                </button>
            </form>

            {message && <p style={{ marginTop: 16 }}>{message}</p>}

            {booking && (
                <div style={{ marginTop: 8 }}>
                    <strong>Returned booking:</strong>
                    <p>Booking Id: {booking.bookingId}</p>
                    <p>User Id: {booking.user}</p>
                    <p>Chef Id: {booking.chef.chefId}</p>
                    <p>Start: {booking.start}</p>
                    <p>End: {booking.end}</p>
                    <p>Status: {booking.status}</p>
                    <p>Address: {booking.address}</p>
                    <p>
                        <Link href={`/booking/view/${booking.bookingId}`}>
                            View Booking
                        </Link>
                    </p>
                </div>
            )}
        </div>
    );
}