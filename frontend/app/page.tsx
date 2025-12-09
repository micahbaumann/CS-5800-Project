// import Image from "next/image";
// import styles from "./page.module.css";

import Link from "next/link";

export default function Home() {
    return (
        <div>
            <ul>
                <li><Link href="/login">Login</Link></li>
                <li><Link href="/logout">Logout</Link></li>
                <li><Link href="/signup">Signup</Link></li>
                <li><Link href="/users">View All Users</Link></li>
                <li><Link href="/user">View User Profile</Link></li>
                <li><Link href="/chef/new">Create New Chef</Link></li>
                <li><Link href="/chef/list">View All Chefs</Link></li>
            </ul>
        </div>
    );
}
