import Link from "next/link";
import { notFound } from "next/navigation";

const API_BASE = process.env.API_BASE_URL ?? "http://localhost:8080";

type User = {
  userId: string;
  username: string;
  name: string;
};

async function getUser(userId: string): Promise<User> {
  const res = await fetch(
    `${API_BASE}/user/view/${encodeURIComponent(userId)}`,
    { cache: "no-store" }
  );

  if (res.status === 404) notFound();
  if (!res.ok) throw new Error(`Failed to load user: HTTP ${res.status}`);

  return res.json();
}

export default async function UserProfilePage({
  params,
}: {
  params: Promise<{ userId: string }>;
}) {
  const { userId } = await params;
  const user = await getUser(userId);

  return (
    <div style={{ maxWidth: 600, padding: 16 }}>
      <h2>User Profile</h2>
      <ul>
        {"userId" in user && <li><b>ID:</b> {user.userId}</li>}
        <li><b>Username:</b> {user.username}</li>
        <li><b>Name:</b> {user.name}</li>
      </ul>
      <p><Link href={`/user/${user.userId}/bookings`}>View Bookings</Link></p>
    </div>
  );
}