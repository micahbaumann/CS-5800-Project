import Link from "next/link";
import { notFound } from "next/navigation";

const API_BASE = process.env.API_BASE_URL ?? "http://localhost:8080";

type Chef = {
    chefId: string,
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

async function getBooking(bookingId: string): Promise<Booking> {
    const res = await fetch(
        `${API_BASE}/booking/view/${encodeURIComponent(bookingId)}`,
        { cache: "no-store" }
    );

    if (res.status !== 200) notFound();
    if (!res.ok) throw new Error(`Failed to load booking: HTTP ${res.status}`);

    return res.json();
}

export default async function BookingProfilePage({
  params,
}: {
  params: Promise<{ bookingId: string }>;
}) {
    const { bookingId } = await params;
    const booking = await getBooking(bookingId);

    return (
        <div style={{ maxWidth: 600, padding: 16 }}>
        <h2>Booking Profile</h2>
        <ul>
            {"bookingId" in booking && <li><b>ID:</b> {booking.bookingId}</li>}
            <p>Booking Id: {booking.bookingId}</p>
            <p>User Id: {booking.user}</p>
            <p>Start: {booking.start}</p>
            <p>End: {booking.end}</p>
            <p>Status: {booking.status}</p>
            <p>Address: {booking.address}</p>
        </ul>
        <h2>Chef Profile</h2>
        <ul>
            {"chefId" in booking.chef && <li><b>ID:</b> {booking.chef.chefId}</li>}
            <p>Chef Id: {booking.chef.chefId}</p>
            <p>User Id: {booking.chef.user}</p>
            <p>Price: {booking.chef.price}</p>
            <p>Name: {booking.chef.listingName}</p>
        </ul>
        </div>
    );
}