import type { YarnWeight } from '../yarn/types'

export const PROJECT_STATUSES = [
    'PLANNED',
    'IN_PROGRESS',
    'FINISHED',
    'FROGGED'
] as const

export type ProjectStatus = (typeof PROJECT_STATUSES)[number]

export const PROJECT_STATUS_LABELS: Record<ProjectStatus, string> = {
    PLANNED: 'Planned',
    IN_PROGRESS: 'In progress',
    FINISHED: 'Finished',
    FROGGED: 'Frogged'
}

export type Project = {
    id: number
    name: string
    status: ProjectStatus
    hookSize: number | null
    notes: string | null
    startedOn: string | null
    finishedOn: string | null
}

export type Allocation = {
    id: number
    yarnId: number
    brand: string
    colorway: string
    weight: YarnWeight
    yardsUsed: number
}

export type AllocationInput = {
    yarnId: number
    yardsUsed: number
}

export type AllocationUpdateInput = {
    yardsUsed: number
}

export type ProjectDetail = Project & {
    totalYardsUsed: number
    allocations: Allocation[]
}

export type ProjectInput = {
    name: string
    status: ProjectStatus
    hookSize: number | null
    notes: string | null
    startedOn: string | null
    finishedOn: string | null
}