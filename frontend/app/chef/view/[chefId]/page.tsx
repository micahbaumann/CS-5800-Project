import Link from "next/link";
import { notFound } from "next/navigation";

const API_BASE = process.env.API_BASE_URL ?? "http://backend:8080";

type Chef = {
    chefId: string,
    user: string;
    price: number;
    listingName: string;
};

type MessageAccount = {
    message_account_id: string,
};

async function getChef(chefId: string): Promise<Chef> {
  const res = await fetch(
    `${API_BASE}/chef/profile/${encodeURIComponent(chefId)}`,
    { cache: "no-store" }
  );

  if (res.status === 404) notFound();
  if (!res.ok) throw new Error(`Failed to load chef: HTTP ${res.status}`);

  return res.json();
}

async function getMessageAccount(chefId: string): Promise<Promise<MessageAccount> | null> {
    const res = await fetch(
        `${API_BASE}/message/chef/${encodeURIComponent(chefId)}/message-account`,
        { cache: "no-store" }
    );

    if (res.status !== 200) return null;
    if (!res.ok) return null;

    return res.json();
}

export default async function ChefProfilePage({
  params,
}: {
  params: Promise<{ chefId: string }>;
}) {
  const { chefId } = await params;
  const chef = await getChef(chefId);
  const messageAccount = await getMessageAccount(chefId);

  return (
    <div style={{ maxWidth: 600, padding: 16 }}>
      <h2>Chef Profile</h2>
      <ul>
        {"chefId" in chef && <li><b>ID:</b> {chef.chefId}</li>}
        <li><b>User:</b> {chef.user}</li>
        <li><b>Name:</b> {chef.listingName}</li>
        <li><b>Price:</b> {chef.price}</li>
      </ul>
      <p><Link href={`/book/chef/${chef.chefId}`}>Book</Link></p>
      <p><Link href={`/chef/bookings/${chef.chefId}`}>View Bookings (chef only)</Link></p>
      {messageAccount && <p><Link href={`/message/list/chef/${chefId}`}>View Messages (chef only)</Link></p>}
      {messageAccount && <p><Link href={`/message/send/${messageAccount.message_account_id}`}>Send Me a Message</Link></p>}
    </div>
  );
}