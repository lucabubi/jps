import { Sun, Moon } from "lucide-react";
import { Button } from "@/components/ui/button.tsx"
import { useTheme } from "@/hooks/useTheme.tsx"
import { useCallback } from "react";

export default function ThemeSwitch() {
    const { setTheme, resolvedTheme } = useTheme();

    const toggleTheme = useCallback(() => {
        // Inject circle-blur animation styles
        const styleId = `theme-transition-${Date.now()}`;
        const style = document.createElement('style');
        style.id = styleId;

        // Circle-blur animation CSS centered on the button
        const css = `
            @supports (view-transition-name: root) {
                ::view-transition-old(root) { 
                    animation: none;
                }
                ::view-transition-new(root) {
                    animation: circle-blur-expand 0.5s ease-out;
                    transform-origin: top right;
                    filter: blur(0);
                }
                @keyframes circle-blur-expand {
                    from {
                        clip-path: circle(0% at 95% 5%);
                        filter: blur(4px);
                    }
                    to {
                        clip-path: circle(150% at 95% 5%);
                        filter: blur(0);
                    }
                }
            }
        `;

        style.textContent = css;
        document.head.appendChild(style);

        // Clean up animation styles after transition
        setTimeout(() => {
            const styleEl = document.getElementById(styleId);
            if (styleEl) {
                styleEl.remove();
            }
        }, 600);

        // Perform the theme change
        const updateTheme = () => {
            setTheme(resolvedTheme === "light" ? "dark" : "light");
        };

        if ('startViewTransition' in document) {
            (document as Document).startViewTransition(updateTheme);
        } else {
            updateTheme();
        }
    }, [resolvedTheme, setTheme]);

    // determine which icon should be visible
    const getIconState = () => {
        return resolvedTheme === "light" ? "moon" : "sun";
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