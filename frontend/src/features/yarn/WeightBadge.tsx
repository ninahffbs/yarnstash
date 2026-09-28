import { WEIGHT_LABELS } from './types'
import type { YarnWeight } from './types'

const RAMP: Record<YarnWeight, string> = {
  LACE: 'bg-[#f5f1ea] text-stone-600',
  FINGERING: 'bg-[#e8dfd0] text-stone-600',
  SPORT: 'bg-[#d9cbb3] text-stone-700',
  DK: 'bg-[#c4b094] text-stone-900',
  WORSTED: 'bg-[#a89275] text-stone-50',
  ARAN: 'bg-[#8a7358] text-stone-50',
  CHUNKY: 'bg-[#6b573f] text-stone-50',
  SUPER_CHUNKY: 'bg-[#4a3b29] text-stone-50',
}

type Props = {
  weight: YarnWeight
}

export default function WeightBadge({ weight }: Props) {
  return (
    <span
      className={`inline-block rounded-full px-2 py-0.5 text-[11px] font-semibold ${RAMP[weight]}`}
    >
      {WEIGHT_LABELS[weight]}
    </span>
  )
}
