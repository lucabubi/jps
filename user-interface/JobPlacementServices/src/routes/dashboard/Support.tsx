import {
    Accordion,
    AccordionContent,
    AccordionItem,
    AccordionTrigger,
} from "@/components/ui/accordion";

const items = [
    {
        title: "Question?",
        content: "Answer!",
    },
    {
        title: "Question?",
        content: "Answer!",
    },
    {
        title: "Question?",
        content: "Answer!",
    },
];

export default function Support() {
    return (
        <Accordion
            type="single"
            collapsible
            defaultValue="item-0"
            className="max-w-3xl my-4 w-full space-y-2"
        >
            {items.map(({ title, content }, index) => (
                <AccordionItem
                    key={index}
                    value={`item-${index}`}
                    className="border-none rounded-md px-4 data-[state=open]:bg-secondary"
                >
                    <AccordionTrigger className="data-[state=closed]:py-2">
                        {title}
                    </AccordionTrigger>
                    <AccordionContent>{content}</AccordionContent>
                </AccordionItem>
            ))}
        </Accordion>
    );
}
