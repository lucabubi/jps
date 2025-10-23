import { Button } from "@/components/ui/button"
import {
    Sheet,
    SheetClose,
    SheetContent,
    SheetDescription,
    SheetFooter,
    SheetHeader,
    SheetTitle,
    SheetTrigger,
} from "@/components/ui/sheet"
import {
    ArrowLeftFromLine,
    ArrowRightFromLine,
    Check,
    Contact,
    Earth,
    ListTodo,
    SendHorizontal,
    User,
    UserPlus, X
} from "lucide-react"
import {
    Field,
    FieldContent,
    FieldDescription,
    FieldGroup,
    FieldLabel,
    FieldSet,
    FieldTitle,
} from "@/components/ui/field.tsx"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group.tsx"
import { useState } from "react"
import {
    Stepper,
    StepperContent,
    StepperIndicator,
    StepperItem,
    StepperNav,
    StepperPanel,
    StepperSeparator,
    StepperTitle,
    StepperTrigger,
} from "@/components/ui/stepper.tsx"
import { Spinner } from "@/components/ui/spinner.tsx"
import { Badge } from "@/components/ui/badge.tsx"

type CreateProfessionalButtonProps = {
    isLoading: boolean
}

export default function CreateProfessionalButton({ isLoading }: CreateProfessionalButtonProps) {
    const [currentStep, setCurrentStep] = useState(1)
    const steps = [
        { title: "Region selection", icon: Earth },
        { title: "Professional information", icon: User },
        { title: "Contact information", icon: Contact },
        { title: "Review & Submit", icon: ListTodo },
    ]

    return (
        <>
            <Sheet>
                <SheetTrigger asChild>
                    <Button disabled={isLoading} size="sm">
                        <UserPlus />
                        Add Professional
                    </Button>
                </SheetTrigger>
                <SheetContent side="right" className="!w-full sm:!max-w-5xl h-full p-0 flex flex-col">
                    <SheetHeader className="flex-none text-left py-4 px-4 sm:px-6">
                        <SheetTitle>Create a Professional</SheetTitle>
                        <SheetDescription>
                            To create a new professional, please fill out the required information in every step. Don&apos;t worry, at
                            the end you&apos;ll be able to review and make changes before submitting.
                        </SheetDescription>
                    </SheetHeader>

                    <Stepper
                        value={currentStep}
                        onValueChange={setCurrentStep}
                        indicators={{
                            completed: <Check className="size-4" />,
                            loading: <Spinner />,
                        }}
                        className="flex-1 min-h-0 flex flex-col"
                    >
                        <StepperNav className="gap-3 mb-15 flex-none border-b pb-5 px-4 sm:px-6">
                            {steps.map((step, index) => (
                                <StepperItem key={index} step={index + 1} className="relative flex-1 items-start">
                                    <StepperTrigger asChild className="flex flex-col items-start justify-center gap-2.5 grow">
                                        <div className="flex flex-col items-start justify-center gap-2.5 grow">
                                            <StepperIndicator className="size-8 border-2 data-[state=completed]:text-white data-[state=completed]:bg-green-500 data-[state=inactive]:bg-transparent data-[state=inactive]:border-border data-[state=inactive]:text-muted-foreground">
                                                <step.icon className="size-4" />
                                            </StepperIndicator>
                                            <div className="flex flex-col items-start gap-1">
                                                <div className="text-[10px] font-semibold uppercase text-muted-foreground">Step {index + 1}</div>
                                                <StepperTitle className="text-start text-base font-semibold group-data-[state=inactive]/step:text-muted-foreground">
                                                    {step.title}
                                                </StepperTitle>
                                                <div>
                                                    <Badge variant="default" className="hidden group-data-[state=active]/step:inline-flex">
                                                        In Progress
                                                    </Badge>
                                                    <Badge variant="default" className="hidden group-data-[state=completed]/step:inline-flex bg-green-600">Completed</Badge>
                                                    <Badge
                                                        variant="secondary"
                                                        className="hidden group-data-[state=inactive]/step:inline-flex text-muted-foreground"
                                                    >
                                                        Pending
                                                    </Badge>
                                                </div>
                                            </div>
                                        </div>
                                    </StepperTrigger>
                                    {steps.length > index + 1 && (
                                        <StepperSeparator className="absolute top-4 inset-x-0 start-9 m-0 group-data-[orientation=horizontal]/stepper-nav:w-[calc(100%-2rem)] group-data-[orientation=horizontal]/stepper-nav:flex-none  group-data-[state=completed]/step:bg-green-500" />
                                    )}
                                </StepperItem>
                            ))}
                        </StepperNav>

                        <StepperPanel className="flex-1 min-h-0 overflow-y-auto py-4 px-4 sm:px-6">
                            <StepperContent value={1}>
                                <FieldGroup>
                                    <FieldSet>
                                        <FieldTitle id="region-label" className="text-lg sm:text-xl">Region</FieldTitle>
                                        <FieldDescription className="pb-2">
                                            Select the professional&apos;s region. You have the ability to add professionals from outside your working region. Be careful.
                                        </FieldDescription>

                                        <RadioGroup defaultValue="EMEA" aria-labelledby="region-label" className="grid grid-cols-2 gap-2">
                                            <FieldLabel htmlFor="region-emea">
                                                <Field orientation="horizontal">
                                                    <FieldContent>
                                                        <FieldTitle>EMEA</FieldTitle>
                                                        <FieldDescription>Europe, the Middle East and Africa</FieldDescription>
                                                    </FieldContent>
                                                    <RadioGroupItem value="EMEA" id="region-emea" />
                                                </Field>
                                            </FieldLabel>

                                            <FieldLabel htmlFor="region-na">
                                                <Field orientation="horizontal">
                                                    <FieldContent>
                                                        <FieldTitle>NA</FieldTitle>
                                                        <FieldDescription>North America</FieldDescription>
                                                    </FieldContent>
                                                    <RadioGroupItem value="NA" id="region-na" />
                                                </Field>
                                            </FieldLabel>

                                            <FieldLabel htmlFor="region-latam">
                                                <Field orientation="horizontal">
                                                    <FieldContent>
                                                        <FieldTitle>LATAM</FieldTitle>
                                                        <FieldDescription>Latin America</FieldDescription>
                                                    </FieldContent>
                                                    <RadioGroupItem value="LATAM" id="region-latam" />
                                                </Field>
                                            </FieldLabel>

                                            <FieldLabel htmlFor="region-apac">
                                                <Field orientation="horizontal">
                                                    <FieldContent>
                                                        <FieldTitle>APAC</FieldTitle>
                                                        <FieldDescription>Asia-Pacific</FieldDescription>
                                                    </FieldContent>
                                                    <RadioGroupItem value="APAC" id="region-apac" />
                                                </Field>
                                            </FieldLabel>
                                        </RadioGroup>
                                    </FieldSet>
                                </FieldGroup>
                            </StepperContent>

                            <StepperContent value={2}>
                                {/* TODO: Professional information form fields */}
                            </StepperContent>

                            <StepperContent value={3}>
                                {/* TODO: Contact information form fields */}
                            </StepperContent>

                            <StepperContent value={4}>
                                {/* TODO: Review & Submit content */}
                            </StepperContent>
                        </StepperPanel>
                    </Stepper>

                    <SheetFooter className="flex-none border-t p-4 grid grid-cols-2 gap-2">
                        <SheetClose asChild>
                            <Button size="sm" variant="destructive" className="w-full">
                                <X className="mr-2 size-4" />
                                Cancel
                            </Button>
                        </SheetClose>

                        <div className="w-full">
                            {currentStep === 1 ? (
                                <Button
                                    size="sm"
                                    variant="default"
                                    className="w-full"
                                    onClick={() => setCurrentStep((prev) => Math.min(steps.length, prev + 1))}
                                >
                                    Next
                                    <ArrowRightFromLine />
                                </Button>
                            ) : currentStep === steps.length ? (
                                <div className="grid grid-cols-2 gap-1">
                                    <Button
                                        size="sm"
                                        variant="outline"
                                        className="w-full"
                                        onClick={() => setCurrentStep((prev) => Math.max(1, prev - 1))}
                                    >
                                        <ArrowLeftFromLine />
                                        Previous
                                    </Button>
                                    <Button
                                        size="sm"
                                        variant="default"
                                        className="w-full"
                                        onClick={() => {
                                            // TODO: handle final submit
                                        }}
                                    >
                                        Submit
                                        <SendHorizontal />
                                    </Button>
                                </div>
                            ) : (
                                <div className="grid grid-cols-2 gap-2">
                                    <Button
                                        size="sm"
                                        variant="outline"
                                        className="w-full"
                                        onClick={() => setCurrentStep((prev) => Math.max(1, prev - 1))}
                                    >
                                        <ArrowLeftFromLine />
                                        Previous
                                    </Button>
                                    <Button
                                        size="sm"
                                        variant="default"
                                        className="w-full"
                                        onClick={() => setCurrentStep((prev) => Math.min(steps.length, prev + 1))}
                                    >
                                        Next
                                        <ArrowRightFromLine />
                                    </Button>
                                </div>
                            )}
                        </div>
                    </SheetFooter>

                </SheetContent>
            </Sheet>
        </>
    )
}
