import {Globe, LockKeyhole, Search, LayoutDashboard, Headphones} from "lucide-react";
import {GlowingEffect} from "@/components/ui/glowing-effect.tsx";

interface GridItemProps {
    area: string;
    icon: React.ReactNode;
    title: string;
    description: React.ReactNode;
}

export default function LandingFeatures() {
    return (
        <ul className="grid grid-cols-1 grid-rows-none gap-4 md:grid-cols-12 md:grid-rows-3 lg:gap-4 xl:max-h-[34rem] xl:grid-rows-2">
            <GridItem
                area="md:[grid-area:1/1/2/7] xl:[grid-area:1/1/2/5]"
                icon={<LockKeyhole className="h-4 w-4 text-black dark:text-neutral-400"/>}
                title="Security by Design"
                description="Your data are private. They're protected at every step, thanks to robust security measures built into the platform from the ground up."
            />
            <GridItem
                area="md:[grid-area:1/7/2/13] xl:[grid-area:2/1/3/5]"
                icon={<Search className="h-4 w-4 text-black dark:text-neutral-400"/>}
                title="Intelligent Job Matching"
                description="Yes, it's true. Our advanced algorithms and expert operators ensure every user is matched with job opportunities that perfectly align with their skills and preferences."
            />
            <GridItem
                area="md:[grid-area:2/1/3/7] xl:[grid-area:1/5/3/8]"
                icon={<Globe className="h-4 w-4 text-black dark:text-neutral-400"/>}
                title="Global Networking"
                description="Thousands of companies and users, rely on us every day to find the best job opportunities and connect with top talent."
            />
            <GridItem
                area="md:[grid-area:2/7/3/13] xl:[grid-area:1/8/2/13]"
                icon={<LayoutDashboard className="h-4 w-4 text-black dark:text-neutral-400"/>}
                title="Interactive Dashboard"
                description="Easily manage all your activities in one simple and intuitive interface."
            />
            <GridItem
                area="md:[grid-area:3/1/4/13] xl:[grid-area:2/8/3/13]"
                icon={<Headphones className="h-4 w-4 text-black dark:text-neutral-400"/>}
                title="24/7 Support"
                description="We got you covered. We provide 24/7 dedicated assistance via email and phone to solve any doubts at any time."
            />
        </ul>
    )
}

const GridItem = ({area, icon, title, description}: GridItemProps) => {
    return (
        <li className={`min-h-[14rem] list-none ${area}`}>
            <div className="relative h-full rounded-2xl border border-gray-400 dark:border-gray-700 p-2 md:rounded-3xl md:p-3">
                <GlowingEffect
                    spread={40}
                    glow={true}
                    disabled={false}
                    proximity={80}
                    inactiveZone={0.01}
                    borderWidth={3}
                />
                <div className="border-0.75 relative flex h-full flex-col justify-between gap-6 overflow-hidden rounded-xl p-6 md:p-6 dark:shadow-[0px_0px_27px_0px_#2D2D2D]">
                    <div className="relative flex flex-1 flex-col justify-between gap-3">
                        <div className="w-fit rounded-lg border border-gray-600 p-2">
                            {icon}
                        </div>
                        <div className="space-y-3">
                            <h3 className="-tracking-4 pt-0.5 font-sans text-xl/[1.375rem] font-semibold text-balance text-black md:text-2xl/[1.875rem] dark:text-white">
                                {title}
                            </h3>
                            <h2 className="font-sans text-sm/[1.125rem] text-black md:text-base/[1.375rem] dark:text-neutral-400 [&_b]:md:font-semibold [&_strong]:md:font-semibold">
                                {description}
                            </h2>
                        </div>
                    </div>
                </div>
            </div>
        </li>
    );
};
