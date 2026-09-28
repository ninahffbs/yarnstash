/**
 * Response shapes that are not specific to any one feature.
 * These mirror the backend's com.yarnstash.common package.
 */

/**
 * The envelope every list endpoint returns.
 *
 * Note this is NOT a bare array — `GET /api/yarns` and `GET /api/projects`
 * both wrap their results, so always read `.content`.
 */
export type PageResponse<T> = {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export type ApiFieldError = {
  field: string
  message: string
}

/**
 * The single error shape produced by the backend's @RestControllerAdvice.
 * `fieldErrors` is present only on validation failures (400).
 */
export type ApiErrorBody = {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  fieldErrors?: ApiFieldError[]
}
