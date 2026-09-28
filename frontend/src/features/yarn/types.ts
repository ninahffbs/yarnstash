export const YARN_WEIGHTS = [
  'LACE',
  'FINGERING',
  'SPORT',
  'DK',
  'WORSTED',
  'ARAN',
  'CHUNKY',
  'SUPER_CHUNKY',
] as const

export type YarnWeight = (typeof YARN_WEIGHTS)[number]

export type Yarn = {
  id: number
  brand: string
  colorway: string
  fiber: string | null
  weight: YarnWeight
  skeins: number
  yardsPerSkein: number
  purchasedOn: string | null
  totalYards: number
  allocatedYards: number
  availableYards: number
}

export type YarnInput = {
  brand: string
  colorway: string
  fiber: string | null
  weight: YarnWeight
  skeins: number
  yardsPerSkein: number
  purchasedOn: string | null
}

export type StashStats = {
  distinctYarns: number
  totalYards: number
}

export const WEIGHT_LABELS: Record<YarnWeight, string> = {
  LACE: 'Lace',
  FINGERING: 'Fingering',
  SPORT: 'Sport',
  DK: 'DK',
  WORSTED: 'Worsted',
  ARAN: 'Aran',
  CHUNKY: 'Chunky',
  SUPER_CHUNKY: 'Super chunky',
}
