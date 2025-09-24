import { useNavigate } from "react-router-dom";
import { HoverBorderGradient } from "@/components/ui/hover-border-gradient.tsx";
import { TextHoverEffect } from "@/components/ui/text-hover-effect";
import ThemeSwitch from "@/components/ThemeSwitch";
import { Cover } from "@/components/ui/cover";
import { FlipWords } from "@/components/ui/flip-words";
import { useAuth } from "@/hooks/useAuth";


function Landing() {
    const { handleLogin, isAuthenticated } = useAuth();
    const navigate = useNavigate();

    const words = ["Unlock", "Search among", "Apply to", "Share", "Create"];

    return (
        <div>
            <ThemeSwitch />
            <div className="flex flex-col items-center justify-center w-full">
                <div className="h-[30rem] flex items-center justify-center w-full">
                    <TextHoverEffect text="JPS" />
                </div>
                <div className="flex items-center justify-center w-full">
                    <h1 className="text-4xl md:text-4xl lg:text-6xl font-semibold max-w-7xl mx-auto text-center mt-6 relative z-20 py-6 bg-clip-text text-transparent bg-gradient-to-b from-neutral-800 via-neutral-700 to-neutral-700 dark:from-neutral-800 dark:via-white dark:to-white">
                        <FlipWords words={words} /> endless job opportunities<br />at <Cover>warp speed</Cover>
                    </h1>
                </div>
                <HoverBorderGradient
                    onClick={isAuthenticated ? () => navigate("/dashboard") : handleLogin}
                    containerClassName="rounded-full"
                    as="button"
                    className="dark:bg-black bg-white text-black dark:text-white flex items-center space-x-2"
                >
                    <span className="flex items-center space-x-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" aria-hidden="true">
                            <path d="m12.707 7.293-1.414 1.414L15.586 13H7V4H5v11h10.586l-4.293 4.293 1.414 1.414L19.414 14l-6.707-6.707z"/>
                        </svg>
                        Dashboard
                    </span>
                </HoverBorderGradient>
            </div>
        </div>
    );
}

export default Landing;