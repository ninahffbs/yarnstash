import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ApiError, request, toQueryString } from '../../api/client'
import type { PageResponse } from '../../api/types'
import type { Project, ProjectDetail, ProjectStatus, ProjectInput } from './types'

export type ProjectQuery = {
    status?: ProjectStatus
    page?: number
    size?: number
    sort?: string
}

export const projectKeys = {
    all: ['projects'] as const,
    list: (query: ProjectQuery) => ['projects', 'list', query] as const,
    detail: (id: number) => ['projects', 'detail', id] as const,
}

export function useProjects(query: ProjectQuery = {}) {
    return useQuery({
        queryKey: projectKeys.list(query),
        queryFn: () => request<PageResponse<Project>>(`/projects${toQueryString(query)}`),
    })
}

export function useProject(id: number) {
    return useQuery({
        queryKey: projectKeys.detail(id),
        queryFn: () => request<ProjectDetail>(`/projects/${id}`),
    })
}

export function useCreateProject() {
    const queryClient = useQueryClient()
    return useMutation<Project, ApiError, ProjectInput>({
    mutationFn: (input) => request<Project>('/projects', { method: 'POST', body: input }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: projectKeys.all })
    },
  })
}

export function useUpdateProject() {
    const queryClient = useQueryClient()
    return useMutation<Project, ApiError, { id: number; input: ProjectInput }>({
        mutationFn: ({ id, input }) => 
            request<Project>(`/projects/${id}`, { method: 'PUT', body: input }),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: projectKeys.all })
            }
    })
}

export function useDeleteProject() {
    const queryClient = useQueryClient()
    return useMutation<void, ApiError, number>({
        mutationFn: (id) => request<void>(`/projects/${id}`, { method: 'DELETE'}),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: projectKeys.all })
        }
    })
}