import {
    ColumnDef,
    flexRender,
    getCoreRowModel,
    useReactTable, VisibilityState,
} from "@tanstack/react-table"

import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import {
    DropdownMenu, DropdownMenuCheckboxItem,
    DropdownMenuContent,
    DropdownMenuLabel, DropdownMenuSeparator,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";
import {Button} from "@/components/ui/button.tsx";
import {
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    ChevronsLeft,
    ChevronsRight, CornerRightDown,
    Eye,
    EyeOff,
    MoreHorizontal
} from "lucide-react";
import {Dispatch, SetStateAction, useEffect, useMemo, useState} from "react";
import { paginationType } from "@/routes/dashboard/Professionals.tsx";

import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "@/components/ui/select.tsx";

interface DataTableProps<TData, TValue> {
    columns: ColumnDef<TData, TValue>[],
    data: TData[],
    pagination: paginationType,
    setPagination: Dispatch<SetStateAction<paginationType>>,
    variant: "job_offer" | "professional" | "customer" | "message",
}

export function DataTable<TData, TValue>({
                                             columns,
                                             data,
                                             pagination,
                                             setPagination,
                                             variant
                                         }: DataTableProps<TData, TValue>) {
    const COLUMN_VISIBILITY_STORAGE_KEY = `${variant}.columnVisibility`;
    const [columnVisibility, setColumnVisibility] = useState<VisibilityState>(() => {
        try {
            const saved = localStorage.getItem(COLUMN_VISIBILITY_STORAGE_KEY);
            if (saved) {
                return JSON.parse(saved) as VisibilityState;
            }
        } catch {
            // ignore parsing/storage errors
        }
        return { id: false };
    })

    useEffect(() => {
        try {
            localStorage.setItem(COLUMN_VISIBILITY_STORAGE_KEY, JSON.stringify(columnVisibility));
        } catch {
            // ignore storage errors
        }
    }, [COLUMN_VISIBILITY_STORAGE_KEY, columnVisibility])

    const totalPages = useMemo(() => {
        const pages = Math.ceil((pagination.totalResults || 0) / (pagination.pageSize || 1));
        return Math.max(1, pages);
    }, [pagination.totalResults, pagination.pageSize]);

    const table = useReactTable({
        data,
        columns,
        getCoreRowModel: getCoreRowModel(),
        manualPagination: true,
        pageCount: totalPages,
        onPaginationChange: (updater) => {
            setPagination((prev) => {
                const current = {pageIndex: prev.pageIndex, pageSize: prev.pageSize};
                const next = typeof updater === 'function' ? updater(current) : updater;
                const pageSizeChanged = next.pageSize !== prev.pageSize;
                const nextPageIndex = pageSizeChanged ? 0 : next.pageIndex;
                return {...prev, pageIndex: nextPageIndex, pageSize: next.pageSize};
            });
        },
        autoResetPageIndex: true,
        onColumnVisibilityChange: setColumnVisibility,
        state: {
            columnVisibility,
            pagination: {pageIndex: pagination.pageIndex, pageSize: pagination.pageSize},
        },
    })

    return (
        <div className="w-full">
            <VisibilityButton/>
            <div className="overflow-hidden rounded-md border">
                <Table>
                    <TableHeader>
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map((header) => {
                                    return (
                                        <TableHead key={header.id}>
                                            {header.isPlaceholder
                                                ? null
                                                : flexRender(
                                                    header.column.columnDef.header,
                                                    header.getContext()
                                                )}
                                        </TableHead>
                                    )
                                })}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody>
                        {table.getRowModel().rows?.length ? (
                            table.getRowModel().rows.map((row) => (
                                <TableRow
                                    key={row.id}
                                    data-state={row.getIsSelected() && "selected"}
                                >
                                    {row.getVisibleCells().map((cell) => (
                                        <TableCell key={cell.id}>
                                            {flexRender(
                                                cell.column.columnDef.cell,
                                                cell.getContext()
                                            )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length}
                                    className="h-24 text-center"
                                >
                                    No results.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
            <PaginationFooter/>
        </div>
    )

    function VisibilityButton() {
        const allColumns = table.getAllColumns();
        const visibleColumns = allColumns.filter(column => column.getIsVisible());
        const hiddenColumns = allColumns.filter(column => !column.getIsVisible());
        return (
            <div className="w-full">
                <div className="flex items-center pb-4 pt-2">
                    <div className="flex items-center space-x-2">
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="outline" className="ml-auto" size="sm">
                                    <Eye />
                                    Columns visibility <ChevronDown />
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end" className="w-[185px]">
                                <DropdownMenuLabel className="inline-flex items-center gap-1">
                                    Current view <CornerRightDown className="h-3.5 w-3.5 pt-1" />
                                </DropdownMenuLabel>
                                <div className="px-2 text-sm text-muted-foreground -pt-2 pb-1">
                                    <span>{visibleColumns.length} visible / </span>
                                    <span>{hiddenColumns.length} hidden</span>
                                </div>
                                <DropdownMenuSeparator/>
                                {allColumns
                                    .filter((column) => column.getCanHide())
                                    .map((column) => {
                                        const isVisible = column.getIsVisible();
                                        return (
                                            <DropdownMenuCheckboxItem
                                                key={column.id}
                                                className="relative capitalize pl-8 [&>span:first-child]:hidden"
                                                checked={isVisible}
                                                onCheckedChange={(value) => column.toggleVisibility(!!value)}
                                            >
        <span className="absolute left-2 flex h-3.5 w-3.5 items-center justify-center">
          {isVisible ? <Eye className="h-4 w-4" /> : <EyeOff className="h-4 w-4" />}
        </span>
                                                <span>
          {typeof column.columnDef.header === "string"
              ? column.columnDef.header
              : column.id}
        </span>
                                            </DropdownMenuCheckboxItem>
                                        );
                                    })}
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>
                </div>
            </div>
        )
    }

    function PaginationFooter() {
        const canPrev = table.getCanPreviousPage()
        const canNext = table.getCanNextPage()
        const pageIndex = table.getState().pagination.pageIndex
        const currentPage = pageIndex + 1

        // Generate page numbers to display
        const getPageNumbers = () => {
            const pages: (number | string)[] = []
            const maxVisible = 7 // Maximum number of page buttons to show

            if (totalPages <= maxVisible) {
                // Show all pages if total is small
                for (let i = 1; i <= totalPages; i++) {
                    pages.push(i)
                }
            } else {
                // Always show first page
                pages.push(1)

                if (currentPage <= 3) {
                    // Near the start
                    for (let i = 2; i <= 4; i++) {
                        pages.push(i)
                    }
                    pages.push("ellipsis-end")
                    pages.push(totalPages)
                } else if (currentPage >= totalPages - 2) {
                    // Near the end
                    pages.push("ellipsis-start")
                    for (let i = totalPages - 3; i <= totalPages; i++) {
                        pages.push(i)
                    }
                } else {
                    // In the middle
                    pages.push("ellipsis-start")
                    for (let i = currentPage - 1; i <= currentPage + 1; i++) {
                        pages.push(i)
                    }
                    pages.push("ellipsis-end")
                    pages.push(totalPages)
                }
            }

            return pages
        }

        const pageNumbers = getPageNumbers()

        return (
            <div className="w-full py-2 sm:px-2">
                {/* Default (non-sm): Single row with 3 columns */}
                <div className="hidden sm:grid sm:grid-cols-[1fr_auto_1fr] sm:items-center sm:gap-4">
                    {/* Page info - Left */}
                    <div className="text-sm text-muted-foreground">
                        Page <span className="font-medium text-foreground">{currentPage}</span> of{" "}
                        <span className="font-medium text-foreground">{totalPages}</span>
                    </div>

                    {/* Navigation - Center */}
                    <div className="flex items-center gap-1 justify-center">
                        <Button
                            variant="outline"
                            size="icon"
                            className="h-7 w-9 bg-transparent"
                            onClick={() => table.setPageIndex(0)}
                            disabled={!canPrev}
                            aria-label="Go to first page"
                        >
                            <ChevronsLeft className="h-4 w-4"/>
                        </Button>
                        <Button
                            variant="outline"
                            size="icon"
                            className="h-7 w-9 bg-transparent"
                            onClick={() => table.previousPage()}
                            disabled={!canPrev}
                            aria-label="Go to previous page"
                        >
                            <ChevronLeft className="h-4 w-4"/>
                        </Button>
                        <div className="flex items-center gap-1">
                            {pageNumbers.map((page, index) => {
                                if (typeof page === "string") {
                                    return (
                                        <div key={`${page}-${index}`} className="flex h-7 w-9 items-center justify-center" aria-hidden="true">
                                            <MoreHorizontal className="h-4 w-4 text-muted-foreground"/>
                                        </div>
                                    )
                                }
                                const isActive = page === currentPage
                                return (
                                    <Button
                                        key={page}
                                        variant={isActive ? "default" : "outline"}
                                        size="icon"
                                        className="h-7 w-9"
                                        onClick={() => table.setPageIndex(page - 1)}
                                        aria-label={`Go to page ${page}`}
                                        aria-current={isActive ? "page" : undefined}
                                    >
                                        {page}
                                    </Button>
                                )
                            })}
                        </div>
                        <Button
                            variant="outline"
                            size="icon"
                            className="h-7 w-9 bg-transparent"
                            onClick={() => table.nextPage()}
                            disabled={!canNext}
                            aria-label="Go to next page"
                        >
                            <ChevronRight className="h-4 w-4"/>
                        </Button>
                        <Button
                            variant="outline"
                            size="icon"
                            className="h-7 w-9 bg-transparent"
                            onClick={() => table.setPageIndex(totalPages - 1)}
                            disabled={!canNext}
                            aria-label="Go to last page"
                        >
                            <ChevronsRight className="h-4 w-4"/>
                        </Button>
                    </div>

                    {/* Page size selector - Right */}
                    <div className="flex justify-end">
                        <Select
                            value={table.getState().pagination.pageSize.toString()}
                            onValueChange={(value) => table.setPageSize(Number(value))}
                        >
                            <SelectTrigger className="h-7 w-fit gap-2" aria-label="Select page size">
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                                {[10, 20, 50, 100].map((size) => (
                                    <SelectItem key={size} value={size.toString()}>
                                        {`${size} / page`}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>
                </div>

                {/* Small screens: 2 rows */}
                <div className="sm:hidden space-y-3">
                    {/* First row: Page info and Page size selector */}
                    <div className="flex items-center justify-between">
                        <div className="text-sm text-muted-foreground">
                            Page <span className="font-medium text-foreground">{currentPage}</span> of{" "}
                            <span className="font-medium text-foreground">{totalPages}</span>
                        </div>
                        <Select
                            value={table.getState().pagination.pageSize.toString()}
                            onValueChange={(value) => table.setPageSize(Number(value))}
                        >
                            <SelectTrigger className="h-7 w-fit gap-2" aria-label="Select page size">
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                                {[10, 20, 50, 100].map((size) => (
                                    <SelectItem key={size} value={size.toString()}>
                                        {`${size} / page`}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    {/* Second row: Navigation centered */}
                    <div className="flex items-center gap-1 justify-center">
                        <Button
                            variant="outline"
                            size="icon"
                            className="h-7 w-9 bg-transparent"
                            onClick={() => table.setPageIndex(0)}
                            disabled={!canPrev}
                            aria-label="Go to first page"
                        >
                            <ChevronsLeft className="h-4 w-4"/>
                        </Button>
                        <Button
                            variant="outline"
                            size="icon"
                            className="h-7 w-9 bg-transparent"
                            onClick={() => table.previousPage()}
                            disabled={!canPrev}
                            aria-label="Go to previous page"
                        >
                            <ChevronLeft className="h-4 w-4"/>
                        </Button>
                        <Button
                            variant="outline"
                            size="icon"
                            className="h-7 w-9 bg-transparent"
                            onClick={() => table.nextPage()}
                            disabled={!canNext}
                            aria-label="Go to next page"
                        >
                            <ChevronRight className="h-4 w-4"/>
                        </Button>
                        <Button
                            variant="outline"
                            size="icon"
                            className="h-7 w-9 bg-transparent"
                            onClick={() => table.setPageIndex(totalPages - 1)}
                            disabled={!canNext}
                            aria-label="Go to last page"
                        >
                            <ChevronsRight className="h-4 w-4"/>
                        </Button>
                    </div>
                </div>
            </div>
        )
    }
}