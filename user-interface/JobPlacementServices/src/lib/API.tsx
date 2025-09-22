import { toast } from 'sonner'
import { UserInterface } from '@/hooks/useAuth.tsx';

// URL of the backend server
const backendUrl = "http://localhost:8083";

// Function to fetch user data from the backend
async function fetchUser(): Promise<UserInterface | null> {
    try {
        const res = await fetch(`${backendUrl}/me`, { credentials: 'include' });

        if (res.ok)
            return await res.json();
        else
            return null;
    } catch {
        toast.error("Error contacting authentication server.");
        return null;
    }
}

export { fetchUser };
