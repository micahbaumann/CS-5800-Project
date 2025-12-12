"use client";

import Link from "next/link";
import { useState, FormEvent } from "react";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost/api";

type Auth = {
    access: string;
    refresh: string;
};

type ApiError = { error?: string; message?: string };

export default function LogoutPage() {

    const handleLogout = async () => {

        try {
            console.log(localStorage.getItem("refreshToken"));
            const res = await fetch(`${API_BASE}/auth/logout`, {
                method: "POST",
                mode: "cors",
            });

            const text = await res.text();
            const data = text ? (JSON.parse(text) as Auth | ApiError) : null;

            if (!res.ok) {
                const errMsg =
                    (data as ApiError)?.error ||
                    (data as ApiError)?.message ||
                    `HTTP ${res.status}`;
                throw new Error(errMsg);
            }
        } catch (err) {
            console.log(`Error: ${err instanceof Error ? err.message : "Unknown error"}`);
        } finally {
            localStorage.removeItem("accessToken");
        }
    };

    const handleLogoutAll = async () => {

        try {
            const res = await fetch(`${API_BASE}/auth/logout/all`, {
                method: "POST",
                headers: { 'Authorization': `Bearer ${localStorage.getItem("accessToken")}` },
                mode: "cors",
            });

            const text = await res.text();
            const data = text ? (JSON.parse(text) as Auth | ApiError) : null;

            if (!res.ok) {
                const errMsg =
                    (data as ApiError)?.error ||
                    (data as ApiError)?.message ||
                    `HTTP ${res.status}`;
                throw new Error(errMsg);
            }
        } catch (err) {
            console.log(`Error: ${err instanceof Error ? err.message : "Unknown error"}`);
        } finally {
            localStorage.removeItem("accessToken");
        }
    };

    return (
        <div style={{ maxWidth: 480, padding: 16 }}>
            <h2>Logout</h2>

            <button type="button" onClick={handleLogout}>Logout Here</button><br/><br/>
            <button type="button" onClick={handleLogoutAll}>Logout Everywhere</button>

        </div>
    );
}