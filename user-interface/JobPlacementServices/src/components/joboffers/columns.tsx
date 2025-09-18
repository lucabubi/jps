"use client"

import { ColumnDef } from "@tanstack/react-table"

// This type is used to define the shape of our data.
// You can use a Zod schema here if you want.
export type JobOffer = {
    id: string
    duration: number
    status: "created" | "selection_phase" | "candidate_proposal" | "consolidated" | "done" | "aborted"
    description: string
    notes: string[]
    requiredSkills: string[]
    customer: string
    professional: string
    value: number
}

export const columns: ColumnDef<JobOffer>[] = [
    {
        accessorKey: "status",
        header: "Status",
    },
    {
        accessorKey: "description",
        header: "Description",
    },
    {
        accessorKey: "professional",
        header: "Professional",
    },
    {
        accessorKey: "duration",
        header: "Duration",
    },
    {
        accessorKey: "value",
        header: "Value",
    },
    {
        accessorKey: "requiredSkills",
        header: "Required Skills",
    },
    {
        accessorKey: "notes",
        header: "Notes",
    },
]
