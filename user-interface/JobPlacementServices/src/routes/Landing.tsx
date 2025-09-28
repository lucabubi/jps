import {useNavigate} from "react-router-dom";
import {HoverBorderGradient} from "@/components/ui/hover-border-gradient.tsx";
import {TextHoverEffect} from "@/components/ui/text-hover-effect";
import {Cover} from "@/components/ui/cover";
import {FlipWords} from "@/components/ui/flip-words";
import ThemeSwitch from "@/components/landing/ThemeSwitch";
import LandingBanner from "@/components/landing/LandingBanner";
import LandingFeatures from "@/components/landing/LandingFeatures";
import LandingTestimonials from "@/components/landing/LandingTestimonials";
import {InteractiveGridPattern} from "@/components/ui/shadcn-io/interactive-grid-pattern";
import TextPressure from "@/components/ui/shadcn-io/text-pressure";
import { useAuth } from "@/hooks/useAuth";
import {cn} from "@/lib/utils.ts";

function Landing() {
    const {handleLogin, isAuthenticated} = useAuth();
    const navigate = useNavigate();

    const words = ["Unlock", "Search among", "Apply to", "Share", "Create"];

    return (
        <div>
            <LandingBanner />
            <div className="relative min-w-[375px] h-full bg-gradient-to-b from-white via-purple-100 to-purple-300 dark:from-black dark:via-black dark:to-purple-950">
                <div className="relative h-full w-full items-center justify-center">
                    <div className="relative z-10 flex flex-col items-center justify-center w-full min-h-screen pointer-events-auto">
                        <ThemeSwitch/>
                        <InteractiveGridPattern
                            className={cn(
                                "absolute left-0 top-0 [mask-image:radial-gradient(175px_circle_at_left,white,transparent)] lg:[mask-image:radial-gradient(250px_circle_at_left,white,transparent)] xl:[mask-image:radial-gradient(315px_circle_at_left,white,transparent)]",
                                "skew-y-[20deg] rotate-180 inset-y-[30%] lg:inset-y-[20%] xl:inset-y-[10%] -z-10"
                            )}
                        />
                        <div className="h-[30rem] flex items-center justify-center w-full">
                            <TextHoverEffect text="JPS"/>
                        </div>
                        <div className="flex items-center justify-center -mt-20 mb-20">
                            <h1 className="text-3xl sm:text-3xl md:text-4xl lg:text-5xl xl:text-6xl font-semibold max-w-6xl mx-auto text-center relative bg-clip-text text-transparent bg-gradient-to-b from-neutral-800 via-neutral-700 to-neutral-700 dark:from-neutral-800 dark:via-white dark:to-white">
                                <div className="flex flex-col md:flex-row items-center justify-center w-full">
                                    <FlipWords words={words} className="whitespace-nowrap"/>
                                    <span className="ml-0 mt-1 md:ml-1  md:mt-0 whitespace-nowrap">endless job opportunities,</span>
                                </div>
                                <div
                                    className="mt-4 bg-clip-text text-transparent bg-gradient-to-b from-neutral-800 via-neutral-700 to-neutral-700 dark:from-neutral-800 dark:via-white dark:to-white">
                                    <Cover>at warp speed</Cover>
                                </div>
                            </h1>
                        </div>
                        <HoverBorderGradient
                            onClick={isAuthenticated ? () => navigate("/dashboard") : handleLogin}
                            containerClassName="rounded-full"
                            as="button"
                            className=" bg-white text-black dark:bg-black dark:text-white flex items-center space-x-2"
                        >
                    <span className="flex items-center space-x-1">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" aria-hidden="true">
                            <path
                                d="m12.707 7.293-1.414 1.414L15.586 13H7V4H5v11h10.586l-4.293 4.293 1.414 1.414L19.414 14l-6.707-6.707z"/>
                        </svg>
                        <span className="font-semibold">Access the Dashboard</span>
                    </span>
                        </HoverBorderGradient>
                    </div>
                </div>
                <div className="h-full w-full">
                    <div className="mx-6 sm:mx-8 md:mx-14 lg:mx-24 xl:mx-32 2xl:mx-52">
                        <h2 className="text-5xl font-semibold text-center tracking-[-0.02em]">
                            Why <span className="text-purple-500 dark:text-purple-600 font-bold">us</span>?
                        </h2>
                        <p className="mt-8 text-center text-muted-foreground text-xl mb-14">
                            Empowering peoples' career journey: we align ambitious talent with forward-thinking companies, making job searching seamless, personal, and truly innovative.<br />
                            <span className="text-purple-600 dark:text-purple-600 font-bold">
                                Because your future deserves more than just a job.
                            </span>
                        </p>
                        <LandingFeatures />
                    </div>
                </div>
                <div className="mx-6 sm:mx-8 md:mx-14 lg:mx-24 xl:mx-32 2xl:mx-52">
                    <LandingTestimonials />
                </div>
            </div>
            <div className="w-full h-full">
                < div className="mx-8 my-8 md:mx-12">
                <TextPressure
                    text="Built with ❤ by Luca & Daniele"
                    flex={true}
                    alpha={false}
                    stroke={false}
                    width={false}
                    weight={true}
                    italic={false}
                    textColor="currentColor"
                    minFontSize={56}
                    className="text-foreground"
                />
                </div>
            </div>
        </div>
    );
}

export default Landing;