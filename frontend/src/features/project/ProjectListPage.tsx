import { useState } from 'react'
import { useProjects } from './api'
import StatusBadge from './StatusBadge'

const PAGE_SIZE = 10

export default function ProjectListPage() {
    const[page, setPage] = useState(0)

    const projects = useProjects({ page, size: PAGE_SIZE, sort: 'name,asc'})

    return (
        <>
          <div className="mb-6 flex items-center justify-between">
            <h1 className="text-xl font-semibold">Projects</h1>
            <button className="rounded-md bg-[#8f4733] px-3 py-1.5 text-sm font-medium text-white hover:bg-[#7a3c2b]">
              + New project
            </button>
          </div>

          <ProjectTable page={page} onPageChange={setPage} query={projects} />
        </>
    )
}

type TableProps = {
    page: number
    onPageChange: (page:number) => void
    query: ReturnType<typeof useProjects>
}

function ProjectTable({ page, onPageChange, query }: TableProps) {
    const { data, isPending, isError, error } = query

if (isPending) {
    return (
      <div className="space-y-2">
        {Array.from({ length: 5 }).map((_, i) => (
          <div key={i} className="h-10 animate-pulse rounded bg-stone-100" />
        ))}
      </div>
    )
  }

  if (isError) {
    return (
      <div className="rounded-md border border-red-200 bg-red-50 p-4">
        <p className="text-sm font-medium text-red-800">Could not load projects.</p>
        <p className="mt-1 text-xs text-red-700">{error.message}</p>
        <button
          onClick={() => query.refetch()}
          className="mt-3 rounded-md border border-red-300 bg-white px-3 py-1.5 text-sm font-medium text-red-700 hover:bg-red-100"
        >
          Retry
        </button>
      </div>
    )
  }

if (data.content.length === 0) {
    return (
      <div className="rounded-lg border border-dashed border-stone-300 px-6 py-12 text-center">
        <p className="text-sm text-stone-600">You have no projects.</p>
        <button className="mt-3 rounded-md bg-[#8f4733] px-3 py-1.5 text-sm font-medium text-white">
          + Add your first project
        </button>
      </div>
    )
  }

  return (
    <>
      <div className="overflow-hidden rounded-lg border border-stone-200 bg-white">
        <table className="w-full text-sm">
          <thead className="border-b border-stone-200 bg-stone-50 text-left text-[11px] uppercase tracking-wide text-stone-500">
            <tr>
              <th className="px-4 py-2 font-semibold">Name</th>
              <th className="px-4 py-2 font-semibold">Status</th>
              <th className="px-4 py-2 font-semibold">Hook size</th>
              <th className="px-4 py-2 text-right font-semibold">Notes</th>
              <th className="px-4 py-2 text-right font-semibold">Started date</th>
              <th className="px-4 py-2 text-right font-semibold">Finish date</th>
            </tr>
          </thead>
          <tbody>
            {data.content.map((project) => (
              <tr key={project.id} className="border-t border-stone-100 hover:bg-stone-50">
                <td className="px-4 py-2.5 font-medium">{project.name}</td>
                <td className="px-4 py-2.5">
                  <StatusBadge status={project.status} />
                </td>
                <td className="px-4 py-2.5 text-right tabular-nums">{project.hookSize}</td>
                <td className="px-4 py-2.5 text-right tabular-nums">{project.notes}</td>
                <td className="px-4 py-2.5 text-right tabular-nums">{project.startedOn}</td>
                <td className="px-4 py-2.5 text-right tabular-nums">{project.finishedOn}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="mt-3 flex items-center justify-between text-sm text-stone-500">
        <span>
          {data.totalElements === 0
            ? 'No projects'
            : `Showing ${page * data.size + 1}–${page * data.size + data.content.length} of ${data.totalElements}`}
        </span>
        <div className="flex items-center gap-1">
          <button
            onClick={() => onPageChange(page - 1)}
            disabled={data.first}
            className="rounded px-2 py-1 hover:bg-stone-100 disabled:opacity-40 disabled:hover:bg-transparent"
          >
            ‹ Prev
          </button>
          <span className="px-2 tabular-nums">
            {data.page + 1} / {data.totalPages}
          </span>
          <button
            onClick={() => onPageChange(page + 1)}
            disabled={data.last}
            className="rounded px-2 py-1 hover:bg-stone-100 disabled:opacity-40 disabled:hover:bg-transparent"
          >
            Next ›
          </button>
        </div>
      </div>
    </>
  )
}