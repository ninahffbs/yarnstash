import { PROJECT_STATUS_LABELS } from './types'
import type { ProjectStatus } from './types'

const STATUS_STYLES: Record<ProjectStatus, string> = {
    PLANNED: 'bg-stone-100 text-stone-600',
    IN_PROGRESS: 'bg-[#8f4733]/12 text-[#8f4733]',
    FINISHED: 'bg-stone-200 text-stone-700',
    FROGGED: 'bg-stone-200 text-stone-500',
}

type Props = {
  status: ProjectStatus
}

export default function StatusBadge({ status }: Props) {
    return (
        <span
          className={`inline-block rounded-full px-2 py-0.5 text-[11px] font-semibold ${STATUS_STYLES[status]}`}
        >
            {PROJECT_STATUS_LABELS[status]}
        </span>
    )
}