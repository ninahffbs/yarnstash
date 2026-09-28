import type { ApiErrorBody, ApiFieldError } from './types'

/**
 * A failed API call, carrying the backend's status and field errors.
 *
 * The `fieldError` helper is what makes server-side validation cheap to render:
 * the backend already returns which field failed and why, so forms never need
 * to duplicate the rules in TypeScript.
 */
export class ApiError extends Error {
  readonly status: number
  readonly fieldErrors: ApiFieldError[]

  constructor(status: number, message: string, fieldErrors: ApiFieldError[] = []) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }

  /** The message for one field, or undefined if that field is fine. */
  fieldError(field: string): string | undefined {
    return this.fieldErrors.find((e) => e.field === field)?.message
  }

  get isNotFound(): boolean {
    return this.status === 404
  }

  /** 409 — a business rule was violated (over-allocation, duplicate, in use). */
  get isConflict(): boolean {
    return this.status === 409
  }

  get isValidation(): boolean {
    return this.status === 400
  }
}

type RequestOptions = {
  method?: string
  body?: unknown
  signal?: AbortSignal
}

/**
 * The only place in the app that calls fetch.
 *
 * Prefixes /api (proxied to the backend by Vite in dev, by nginx in prod),
 * serialises the body, converts any non-2xx response into a thrown ApiError,
 * and tolerates empty 204 bodies from the DELETE endpoints.
 */
export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', body, signal } = options

  const response = await fetch(`/api${path}`, {
    method,
    headers: body === undefined ? undefined : { 'Content-Type': 'application/json' },
    body: body === undefined ? undefined : JSON.stringify(body),
    signal,
  })

  if (!response.ok) {
    let detail: ApiErrorBody | undefined

    try {
      detail = (await response.json()) as ApiErrorBody
    } catch {
      // Not every failure has a JSON body — a dead proxy or a 500 from the
      // container can return HTML or nothing at all.
    }

    throw new ApiError(
      response.status,
      detail?.message ?? `Request failed with status ${response.status}`,
      detail?.fieldErrors ?? [],
    )
  }

  // 204 No Content: DELETE succeeds with an empty body, and response.json()
  // would throw on it.
  if (response.status === 204) {
    return undefined as T
  }

  return (await response.json()) as T
}

/** Builds a query string, omitting empty values. Blank strings are dropped. */
export function toQueryString(params: Record<string, string | number | undefined>): string {
  const search = new URLSearchParams()

  for (const [key, value] of Object.entries(params)) {
    if (value === undefined) continue
    if (typeof value === 'string' && value.trim() === '') continue
    search.set(key, String(value))
  }

  const qs = search.toString()
  return qs ? `?${qs}` : ''
}
