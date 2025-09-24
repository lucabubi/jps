import { Sun, Moon } from "lucide-react";
import { Button } from "@/components/ui/button"
import { useTheme } from "@/hooks/useTheme"

export default function ThemeSwitch() {
    const { theme, setTheme, resolvedTheme } = useTheme();

    const toggleTheme = () => {
        // toggle between dark and light
        setTheme(resolvedTheme === "light" ? "dark" : "light");
    };

    // determine which icon should be visible
    const getIconState = () => {
        if (theme === "system") {
            return resolvedTheme === "light" ? "moon" : "sun";
        } else {
            const storedTheme = localStorage.getItem("vite-ui-theme");
            return storedTheme === "light" ? "moon" : "sun";
        }
    };

    const iconState : "moon"|"sun" = getIconState();

    return (
        <Button
            variant="ghost"
            size="icon"
            onClick={toggleTheme}
            className="p-0 w-[2.0rem] h-[2.0rem] absolute top-6 right-8 overflow-hidden hover:bg-transparent hover:shadow-none focus:bg-transparent focus:shadow-none"
        >
            <Sun
                className={`absolute inset-0 w-full h-full origin-center transition-transform duration-500 ease-in-out
                ${iconState === "sun" ? "scale-100 rotate-0 opacity-100" : "scale-0 -rotate-90 opacity-0"}`}
                style={{ width: '100%', height: '100%' }}
            />
            <Moon
                className={`absolute inset-0 w-full h-full origin-center transition-transform duration-500 ease-in-out
                ${iconState === "moon" ? "scale-100 rotate-0 opacity-100" : "scale-0 rotate-90 opacity-0"}`}
                style={{ width: '100%', height: '100%' }}
            />
            <span className="sr-only">Toggle theme</span>
        </Button>
    );
}