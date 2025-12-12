"use client";

import { useState, FormEvent, ChangeEvent } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { fetchWithAuth } from "@/app/refresh";

const API_BASE =
    process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost/api";

type MessageAccount = {
    message_account_id: string,
};

type User = {
    userId: string;
    username: string;
    name: string;
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
    const params = useParams<{ messageAccountId: string, messageAccountFrom: string }>();
    const messageAccountIdSlug = params.messageAccountId;
    const messageAccountFromSlug = params.messageAccountFrom;

    const [sendMessage, setSendMessage] = useState<string>("");

    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState<string>("");

    const handleDateChange =
        (setter: (v: string) => void) =>
            (e: ChangeEvent<HTMLInputElement>) => {
                setter(e.target.value);
            };

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setLoading(true);
        setMessage("");

        // Check auth first
        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
            setMessage("Error: user is not logged in.");
            setLoading(false);
            return;
        }

        let user: User;
        let messageFromId: MessageAccount;

        try {

            const res = await fetchWithAuth(`${API_BASE}/message/send`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    // Authorization header will be added by fetchWithAuth
                },
                body: JSON.stringify({
                    to: messageAccountIdSlug,
                    from: messageAccountFromSlug,
                    message: sendMessage,
                }),
            });

            const text = await res.text();
            const data = text ? (JSON.parse(text) as null | ApiError) : null;

            if (!res.ok) {
                const errMsg =
                    (data as ApiError)?.error ||
                    (data as ApiError)?.message ||
                    `HTTP ${res.status}`;
                throw new Error(errMsg);
            }

            setMessage("Message sent successfully!");

            // Clear the form
            setSendMessage("");
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
            <h2>Send a Message</h2>
            <p>
                <b>Sending To:</b> {messageAccountIdSlug}
            </p>

            <form onSubmit={handleSubmit}>

                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="sendMessage">Message:&nbsp;</label>
                    <textarea
                        id="sendMessage"
                        value={sendMessage}
                        onChange={(e) => setSendMessage(e.target.value)}
                        placeholder="Your message"
                        required
                    ></textarea>
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? "Submitting..." : "Send Message"}
                </button>
            </form>

            {message && <p style={{ marginTop: 16 }}>{message}</p>}


        </div>
    );
}