import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar.tsx";
import { StarIcon } from "lucide-react";

const testimonials = [
    {
        id: 1,
        name: "Mark Zuckerberg",
        designation: "Meta CEO",
        testimonial: "After years of relentless research, I was finally able to leave Meta and land my dream job.",
        avatar: "https://iili.io/K10eHDg.md.jpg",
    },
    {
        id: 2,
        name: "Sam Altman",
        designation: "OpenAI CEO",
        testimonial: "I finally found a platform that doesn’t rely on AI for everything. I love it!",
        avatar: "https://pyxis.nymag.com/v1/imgs/97e/01f/3fa7a2332eb6ea3bbbb6d48d33263b4adb-Sam-Altman.rsquare.w400.jpg",
    },
    {
        id: 3,
        name: "Steve Carrell",
        designation: "The Office Actor",
        testimonial: "After leaving my beloved Scranton's Office, I found a better job through your platform. Couldn’t be happier!",
        avatar: "https://iili.io/K1lgUoF.md.jpg",
    },
    {
        id: 4,
        name: "Luca Barbato",
        designation: "PoliTO Student",
        testimonial: "I found a job in just three days without any special skills! This platform is simply amazing, surely not because I created it!",
        avatar: "https://avatars.githubusercontent.com/u/59212611?v=4",
    },
    {
        id: 5,
        name: "Daniele De Rossi",
        designation: "PoliTO Student",
        testimonial: "This platform completely changed my life in less than a week. Super recommended, and trust me, it’s not just creator's bias talking!",
        avatar: "https://avatars.githubusercontent.com/u/114685212?v=4",
    },
    {
        id: 6,
        name: "Jensen Huang",
        designation: "Nvidia CEO",
        testimonial: "Thanks to this platform, my son found his first job! We had been searching for months, but here he quickly discovered the perfect opportunity to start his career.",
        avatar: "https://iili.io/K10NL3N.md.jpg",
    },
];

const LandingTestimonials = () => (
    <div className="min-h-screen flex justify-center items-center py-12 px-6">
        <div>
            <h2 className="text-5xl font-semibold text-center tracking-[-0.02em]">
                Loved by People and Companies
            </h2>
            <p className="mt-5 text-center text-muted-foreground text-xl">
                See what individuals and organizations say about us.
            </p>
            <div className="mt-8 sm:mt-14 w-full max-w-(--breakpoint-xl) mx-auto">
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 overflow-hidden">
                    {testimonials.map((testimonial) => (
                        <div
                            key={testimonial.id}
                            className={`flex flex-col outline-solid px-6 py-8
        ${testimonial.id === 1 ? "md:border-r-4 md:border-b-4 border-purple-200 dark:border-purple-950" : ""}
        ${testimonial.id === 2 ? "md:border-b-4 lg:border-r-4 border-purple-200 dark:border-purple-950" : ""}
        ${testimonial.id === 3 ? "md:border-r-4 md:border-b-4 lg:border-r-0 border-purple-200 dark:border-purple-950" : ""}
        ${testimonial.id === 4 ? "md:border-b-4 lg:border-b-0 lg:border-r-4 border-purple-200 dark:border-purple-950" : ""}
        ${testimonial.id === 5 ? "md:border-r-4  border-purple-200 dark:border-purple-950" : ""}
        ${testimonial.id === 6 ? "border-purple-200 dark:border-purple-950" : ""}
        `}
                        >
                            <div className="flex items-center justify-center gap-2">
                                <StarIcon className="w-6 h-6 fill-yellow-500 stroke-yellow-500" />
                                <StarIcon className="w-6 h-6 fill-yellow-500 stroke-yellow-500" />
                                <StarIcon className="w-6 h-6 fill-yellow-500 stroke-yellow-500" />
                                <StarIcon className="w-6 h-6 fill-yellow-500 stroke-yellow-500" />
                                <StarIcon className="w-6 h-6 fill-yellow-500 stroke-yellow-500" />
                            </div>
                            <p className="my-6 text-[17px] text-center font-semibold">
                                &quot;{testimonial.testimonial}&quot;
                            </p>
                            <div className="mt-auto flex items-center justify-center gap-3 font-semibold">
                                <Avatar className="size-9">
                                    <AvatarImage src={testimonial.avatar} alt={testimonial.name} />
                                    <AvatarFallback className="text-xl font-medium bg-primary text-primary-foreground">
                                        {testimonial.name.charAt(0)}
                                    </AvatarFallback>
                                </Avatar>
                                <div>
                                    <p className="text-lg">{testimonial.name}</p>
                                    <p className="text-sm text-purple-500 dark:text-purple-400">
                                        {testimonial.designation}
                                    </p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    </div>
);

export default LandingTestimonials