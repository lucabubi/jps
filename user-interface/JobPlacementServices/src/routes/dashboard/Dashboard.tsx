import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";

export function Dashboard() {
    const { handleLogout, isAuthenticated } = useAuth();

    return (
        <div className="h-screen w-full">
            { isAuthenticated && <Button onClick={handleLogout}>Logout</Button> }
        </div>
    );
}