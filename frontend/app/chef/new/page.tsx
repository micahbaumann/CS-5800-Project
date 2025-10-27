"use client";

import Link from "next/link";
import { useState, FormEvent } from "react";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

type Chef = {
    chefId: string,
    user: string;
    price: number;
    listingName: string;
};

type ApiError = { error?: string; message?: string };

export default function ChefSignupPage() {
  const [userId, setUserId] = useState("");
  const [name, setName] = useState("");
  const [price, setPrice] = useState<number | "">("");
  const [message, setMessage] = useState("");
  const [chef, setChef] = useState<Chef | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");
    setChef(null);

    try {
      const res = await fetch(`${API_BASE}/chef/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          "user_id": userId,
          "listing_name": name,
          "price": Number(price),
        }),
        mode: "cors",
      });

      const text = await res.text();
      const data = text ? (JSON.parse(text) as Chef) : null;

      if (!res.ok) {
        throw new Error(
          (data as any)?.error || `Signup failed (HTTP ${res.status})`
        );
      }

      setMessage("Chef signup successful!");
      setChef(data);
      setUserId("");
      setName("");
      setPrice("");
    } catch (err) {
      setMessage(
        `Error: ${err instanceof Error ? err.message : "Unknown error"}`
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 480, padding: 16 }}>
      <h2>Chef Signup</h2>

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 12 }}>
          <label htmlFor="userId">User ID:&nbsp;</label>
          <input
            id="userId"
            type="text"
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
            required
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label htmlFor="name">Name:&nbsp;</label>
          <input
            id="name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label htmlFor="price">Price:&nbsp;</label>
          <input
            id="price"
            type="number"
            step="0.01"
            value={price}
            onChange={(e) => setPrice(e.target.value ? Number(e.target.value) : "")}
            required
          />
        </div>

        <button type="submit" disabled={loading}>
          {loading ? "Submitting..." : "Sign Up as Chef"}
        </button>
      </form>

      {message && <p style={{ marginTop: 16 }}>{message}</p>}

      {chef && (
        <div style={{ marginTop: 8 }}>
            <strong>User Information:</strong>
            <p>Chef Id: {chef.chefId}</p>
            <p>User Id: {chef.user}</p>
            <p>Price: {chef.price}</p>
            <p>Name: {chef.listingName}</p>
            <p><Link href={`/chef/${chef.chefId}`}>View Profile</Link></p>
        </div>
      )}
    </div>
  );
}