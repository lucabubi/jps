import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";

function Landing() {
    const { handleLogin, handleLogout, isAuthenticated } = useAuth();

    return (
        isAuthenticated ? (
            <Button onClick={handleLogout}>Logout</Button>
        ) : (
            <Button onClick={handleLogin}>Login</Button>
        )
    );
}

export default Landing;