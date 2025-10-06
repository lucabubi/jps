import { toast } from 'sonner'
import { UserOuterInterface } from '@/hooks/useAuth';

// URL of the backend server
const backendUrl = "http://localhost:8083";

// Function to fetch user data from the backend
async function fetchUser(): Promise<UserOuterInterface | null> {
    let result: UserOuterInterface | null = null;

    try {
        const res = await fetch(`${backendUrl}/me`, { credentials: 'include' });

        if (res.ok) {
            const parsed = await res.json();
            const x: UserOuterInterface = {
                loginUrl: parsed.loginUrl,
                logoutUrl: parsed.logoutUrl,
                userData: null
            };
            if (parsed.principal) {
                x.userData = {
                    name: parsed.name,
                    fullName: parsed.fullName,
                    principal: parsed.principal,
                    xsrfToken: parsed.xsrfToken,
                    roles: parsed.roles
                };
            }
            result = x;
        } else {
            toast.error("Error contacting authentication server.");
            result = null;
        }
    } catch {
        toast.error("Error contacting authentication server.");
        result = null;
    }

    return result;
}

export { fetchUser };