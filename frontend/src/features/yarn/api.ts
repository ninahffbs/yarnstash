import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { request, toQueryString } from '../../api/client'
import type { PageResponse } from '../../api/types'
import type { YarnInput, StashStats, Yarn, YarnWeight } from './types'
import { ApiError } from '../../api/client'

export type YarnQuery = {
  weight?: YarnWeight
  fiber?: string
  page?: number
  size?: number
  sort?: string
}

export const yarnKeys = {
  all: ['yarns'] as const,
  list: (query: YarnQuery) => ['yarns', 'list', query] as const,
  detail: (id: number) => ['yarns', 'detail', id] as const,
  stats: () => ['yarns', 'stats'] as const,
}

export function useYarns(query: YarnQuery = {}) {
  return useQuery({
    queryKey: yarnKeys.list(query),
    queryFn: () => request<PageResponse<Yarn>>(`/yarns${toQueryString(query)}`),
  })
}

export function useYarn(id: number) {
  return useQuery({
    queryKey: yarnKeys.detail(id),
    queryFn: () => request<Yarn>(`/yarns/${id}`),
  })
}

export function useStashStats() {
  return useQuery({
    queryKey: yarnKeys.stats(),
    queryFn: () => request<StashStats>('/yarns/stats'),
  })
}

export function useCreateYarn() {
  const queryClient = useQueryClient()
  return useMutation<Yarn, ApiError, YarnInput>({
    mutationFn: (input) => request<Yarn>('/yarns', {method: 'POST', body: input}),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: yarnKeys.all })
    },
  })
}

export function useUpdateYarn() {
  const queryClient = useQueryClient()
  return useMutation<Yarn, ApiError, { id: number; input: YarnInput }>({
    mutationFn: ({ id, input }) =>
    request<Yarn>(`/yarns/${id}`, { method: 'PUT', body: input }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: yarnKeys.all })
    },
  })
}

export function useDeleteYarn() {
  const queryClient = useQueryClient()

  return useMutation<void, ApiError, number>({
    mutationFn: (id) => request<void>(`/yarns/${id}`, { method: 'DELETE' }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: yarnKeys.all })
    },
  })
}