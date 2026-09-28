import React, { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useCreateYarn, useUpdateYarn, useYarn } from './api'
import { WEIGHT_LABELS, YARN_WEIGHTS } from './types'
import type { Yarn, YarnInput, YarnWeight } from './types'

type FormState = {
    brand: string,
    colorway: string,
    fiber: string,
    weight: YarnWeight,
    skeins: string,
    yardsPerSkein: string,
    purchasedOn: string
}

const EMPTY: FormState = {
    brand: '',
    colorway: '',
    fiber: '',
    weight: 'WORSTED',
    skeins: '',
    yardsPerSkein: '',
    purchasedOn: ''
}

function fromYarn(yarn: Yarn): FormState {
    return {
        brand: yarn.brand,
        colorway: yarn.colorway,
        fiber: yarn.fiber ?? '',
        weight: yarn.weight,
        skeins: String(yarn.skeins),
        yardsPerSkein: String(yarn.yardsPerSkein),
        purchasedOn: yarn.purchasedOn ?? ''
    }
}

function toInput(form: FormState): YarnInput {
    const fiber = form.fiber.trim()
    return {
        brand: form.brand.trim(),
        colorway: form.colorway.trim(),
        fiber: fiber === '' ? null : fiber,
        weight: form.weight,
        skeins: Number(form.skeins),
        yardsPerSkein: Number(form.yardsPerSkein),
        purchasedOn: form.purchasedOn === '' ? null : form.purchasedOn
    }
}

export default function YarnFormPage() {
    const { id } = useParams()
    if (id === undefined) {
        return <YarnForm heading="Add yarn" initial={EMPTY} />
    }
    return <EditYarn id={Number(id)} />
}

function EditYarn({ id }: { id: number }) {
    const { data, isPending, isError,error } = useYarn(id)
    if(isPending) {
        return <p className="text-sm text-stone-500">Loading</p>
    }
    if(isError) {
        return (
            <div className="rounded-md border border-red-200 bg-red-50 p-4">
                <p className="text-sm font-medium text-red-800">Could not load this yarn</p>
                <p className="mt-1 text-xs text-red-700">{error.message}</p>
            </div>
        )
    }
    return <YarnForm heading={`Edit ${data.brand}`} initial={fromYarn(data)} yarnId={id} />
}

type Props = {
    heading: string
    initial: FormState
    yarnId?: number
}

function YarnForm({ heading, initial, yarnId }: Props) {
    const navigate = useNavigate()
    const [form, setForm] = useState(initial)

    const create = useCreateYarn()
    const update = useUpdateYarn()

    const pending = create.isPending || update.isPending
    const error = create.error ?? update.error

    function set<K extends keyof FormState>(key: K, value: FormState[K]) {
        setForm((prev) => ({ ...prev, [key]: value }))
    }

    function handleSubmit(event: React.FormEvent) {
        event.preventDefault()
        const input = toInput(form)
        const onSuccess = () => navigate('/yarns')

        if(yarnId === undefined) {
            create.mutate(input, { onSuccess })
        }
        else {
            update.mutate({ id: yarnId, input }, { onSuccess })
        }
    }

    const skeins = Number(form.skeins)
    const perSkein = Number(form.yardsPerSkein)
    const total = skeins > 0 && perSkein > 0 ? skeins * perSkein : null

    return (
        <div className="mx-auto max-w-md">
        <Link to="/yarns" className="text-sm text-stone-500 hover:text-stone-900">
            ‹ Stash
        </Link>

        <h1 className="mt-2 mb-6 text-xl font-semibold">{heading}</h1>
        {error && !error.isValidation && (
            <div className="mb-4 rounded-md border border-red-200 bg-red-50 p-3">
            <p className="text-sm text-red-800">{error.message}</p>
            </div>
        )}

        <form onSubmit={handleSubmit} className="rounded-lg border border-stone-200 bg-white p-4">
            <Field label="Brand" required error={error?.fieldError('brand')}>
            <input
                className={inputClass(error?.fieldError('brand'))}
                value={form.brand}
                onChange={(e) => set('brand', e.target.value)}
            />
            </Field>

            <Field label="Colorway" required error={error?.fieldError('colorway')}>
            <input
                className={inputClass(error?.fieldError('colorway'))}
                value={form.colorway}
                onChange={(e) => set('colorway', e.target.value)}
            />
            </Field>

            <Field label="Fiber" error={error?.fieldError('fiber')}>
            <input
                className={inputClass(error?.fieldError('fiber'))}
                value={form.fiber}
                onChange={(e) => set('fiber', e.target.value)}
                placeholder="65% wool, 35% alpaca"
            />
            </Field>

            <Field label="Weight" required error={error?.fieldError('weight')}>
            <select
                className={inputClass(error?.fieldError('weight'))}
                value={form.weight}
                onChange={(e) => set('weight', e.target.value as YarnWeight)}
            >
                {YARN_WEIGHTS.map((weight) => (
                <option key={weight} value={weight}>
                    {WEIGHT_LABELS[weight]}
                </option>
                ))}
            </select>
            </Field>

            <div className="flex gap-3">
            <div className="flex-1">
                <Field label="Skeins" required error={error?.fieldError('skeins')}>
                <input
                    type="number"
                    className={inputClass(error?.fieldError('skeins'))}
                    value={form.skeins}
                    onChange={(e) => set('skeins', e.target.value)}
                />
                </Field>
            </div>
            <div className="flex-1">
                <Field label="Yards per skein" required error={error?.fieldError('yardsPerSkein')}>
                <input
                    type="number"
                    className={inputClass(error?.fieldError('yardsPerSkein'))}
                    value={form.yardsPerSkein}
                    onChange={(e) => set('yardsPerSkein', e.target.value)}
                />
                </Field>
            </div>
            </div>

            {total !== null && (
            <p className="mb-3 text-xs text-stone-500">
                → <span className="font-medium text-stone-700">{total.toLocaleString()} yards total</span>
            </p>
            )}

            <Field label="Purchased" error={error?.fieldError('purchasedOn')}>
            <input
                type="date"
                className={inputClass(error?.fieldError('purchasedOn'))}
                value={form.purchasedOn}
                onChange={(e) => set('purchasedOn', e.target.value)}
            />
            </Field>

            <div className="mt-4 flex justify-end gap-2">
            <Link
                to="/yarns"
                className="rounded-md border border-stone-300 bg-white px-3 py-1.5 text-sm font-medium"
            >
                Cancel
            </Link>
            <button
                type="submit"
                disabled={pending}
                className="rounded-md bg-[#8f4733] px-3 py-1.5 text-sm font-medium text-white hover:bg-[#7a3c2b] disabled:opacity-50"
            >
                {pending ? 'Saving…' : 'Save yarn'}
            </button>
            </div>
        </form>
        </div>
  )
}

function inputClass(invalid?: string) {
  return `w-full rounded-md border px-2 py-2 text-sm ${
    invalid ? 'border-red-400' : 'border-stone-300'
  }`
}

function Field({
  label,
  required,
  error,
  children,
}: {
  label: string
  required?: boolean
  error?: string
  children: React.ReactNode
}) {
  return (
    <label className="mb-3 block">
      <span className="mb-1 block text-xs text-stone-500">
        {label}
        {required && <span className="text-stone-400"> *</span>}
      </span>
      {children}
      {error && <span className="mt-1 block text-xs text-red-700">⚠ {error}</span>}
    </label>
  )
}