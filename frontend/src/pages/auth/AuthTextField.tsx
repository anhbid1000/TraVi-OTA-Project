import type { ChangeEvent } from 'react'
import {
  KeyRound,
  LockKeyhole,
  Mail,
  Phone,
  User,
  type LucideIcon,
} from 'lucide-react'
import {
  authErrorClass,
  authInputBaseClass,
  authInputIconClass,
  authInputWrapperClass,
  authLabelClass,
} from './authUi'

type AuthTone = 'blue' | 'emerald' | 'slate'
type AuthFieldIcon = 'key' | 'lock' | 'mail' | 'phone' | 'user'

type AuthTextFieldProps = {
  id: string
  name: string
  label: string
  type?: string
  value: string
  placeholder?: string
  autoComplete?: string
  error?: string
  icon?: AuthFieldIcon
  tone?: AuthTone
  onChange: (value: string) => void
}

const iconComponents: Record<AuthFieldIcon, LucideIcon> = {
  key: KeyRound,
  lock: LockKeyhole,
  mail: Mail,
  phone: Phone,
  user: User,
}

const toneClasses: Record<AuthTone, string> = {
  blue: 'border-blue-100 hover:border-blue-400 focus:border-blue-600 focus:ring-2 focus:ring-blue-100',
  emerald:
    'border-emerald-100 hover:border-emerald-400 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100',
  slate:
    'border-slate-200 hover:border-slate-400 focus:border-slate-800 focus:ring-2 focus:ring-slate-100',
}

const iconToneClasses: Record<AuthTone, string> = {
  blue: 'text-blue-400',
  emerald: 'text-emerald-500',
  slate: 'text-slate-400',
}

export function AuthTextField({
  id,
  name,
  label,
  type = 'text',
  value,
  placeholder,
  autoComplete,
  error,
  icon,
  tone = 'blue',
  onChange,
}: AuthTextFieldProps) {
  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    onChange(event.target.value)
  }

  const Icon = icon ? iconComponents[icon] : null

  return (
    <div>
      <label htmlFor={id} className={authLabelClass}>
        {label}
      </label>

      <div className={authInputWrapperClass}>
        {Icon && (
          <span className={`${authInputIconClass} ${iconToneClasses[tone]}`}>
            <Icon size={18} strokeWidth={2.2} aria-hidden="true" />
          </span>
        )}

        <input
          id={id}
          name={name}
          type={type}
          value={value}
          placeholder={placeholder}
          autoComplete={autoComplete}
          onChange={handleChange}
          className={`${authInputBaseClass} ${toneClasses[tone]}`}
        />
      </div>

      {error && <p className={authErrorClass}>{error}</p>}
    </div>
  )
}
