"use client";

import { useEffect, useState } from "react";
import { fetchWithAuth } from "@/app/refresh";
import Link from "next/link";

const API_BASE =
    process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost/api";

type Message = {
    messageId: string;
    from: string;
    to: string;
    message: string;
    timestamp: string;
};

type InboxSent = (Message | null)[];

type MessageReturn = {
    inbox: InboxSent;
    sent: InboxSent;
};

type ApiError = { error?: string; message?: string };

type Props = {
    params: { chefId: string };
};

export default function MessageListPage({ params }: Props) {
    const { chefId } = params;

    const [data, setData] = useState<MessageReturn | null>(null);
    const [loading, setLoading] = useState(true);
    const [statusMessage, setStatusMessage] = useState<string>("");

    useEffect(() => {
        if (!chefId) {
            setStatusMessage("Error: Missing chef ID.");
            setLoading(false);
            return;
        }

        const loadMessages = async () => {
            setLoading(true);
            setStatusMessage("");
            setData(null);

            const accessToken = localStorage.getItem("accessToken");
            if (!accessToken) {
                setStatusMessage("Error: user is not logged in.");
                setLoading(false);
                return;
            }

            try {
                const res = await fetchWithAuth(
                    `${API_BASE}/message/list/chef/${encodeURIComponent(chefId)}`,
                    { cache: "no-store" }
                );

                if (res.status === 404) {
                    setStatusMessage("No messages found.");
                    setLoading(false);
                    return;
                }

                if (!res.ok) {
                    const text = await res.text();
                    let apiError: ApiError | null = null;
                    try {
                        apiError = text ? (JSON.parse(text) as ApiError) : null;
                    } catch {}
                    const errMsg =
                        apiError?.error ||
                        apiError?.message ||
                        `Failed to load messages: HTTP ${res.status}`;
                    throw new Error(errMsg);
                }

                const json = (await res.json()) as MessageReturn;
                setData(json);
            } catch (err) {
                const msg =
                    err instanceof Error ? err.message : "Unknown error fetching messages";

                if (
                    msg.toLowerCase().includes("no access token") ||
                    msg.toLowerCase().includes("not authenticated") ||
                    msg.toLowerCase().includes("session expired")
                ) {
                    setStatusMessage("Error: user is not logged in.");
                } else {
                    setStatusMessage(`Error: ${msg}`);
                }
            } finally {
                setLoading(false);
            }
        };

        loadMessages();
    }, [chefId]);

    if (loading) {
        return <div style={{ maxWidth: 600, padding: 16 }}>Loading...</div>;
    }

    if (statusMessage && !data) {
        return (
            <div style={{ maxWidth: 600, padding: 16 }}>
                <p>{statusMessage}</p>
            </div>
        );
    }

    if (!data) {
        return (
            <div style={{ maxWidth: 600, padding: 16 }}>
                <p>No message data.</p>
            </div>
        );
    }

    const { inbox, sent } = data;

    return (
        <div style={{ maxWidth: 600, padding: 16 }}>
            <h2>Messages for Chef: {chefId}</h2>

            <h3>Inbox</h3>
            <ul>
                {(!inbox || inbox.length === 0) && <li>No inbox messages.</li>}
                {inbox?.map((msg) =>
                    msg ? (
                        <li key={msg.messageId} style={{ marginBottom: 12 }}>
                            <div><b>From:</b> {msg.from}</div>
                            <div><b>To:</b> {msg.to}</div>
                            <div>
                                <b>Reply</b> <Link href={`/message/send/${msg.from}/${msg.to}`}>Reply</Link>
                            </div>
                            <div><b>Message:</b> {msg.message}</div>
                            <div><b>Timestamp:</b> {new Date(msg.timestamp).toLocaleString()}</div>
                        </li>
                    ) : null
                )}
            </ul>

            <h3>Sent</h3>
            <ul>
                {(!sent || sent.length === 0) && <li>No sent messages.</li>}
                {sent?.map((msg) =>
                    msg ? (
                        <li key={msg.messageId} style={{ marginBottom: 12 }}>
                            <div><b>From:</b> {msg.from}</div>
                            <div><b>To:</b> {msg.to}</div>
                            <div><b>Message:</b> {msg.message}</div>
                            <div><b>Timestamp:</b> {new Date(msg.timestamp).toLocaleString()}</div>
                        </li>
                    ) : null
                )}
            </ul>
        </div>
    );
}
