import type { ChangeEvent, ReactNode } from 'react'
import {
  Eye,
  EyeOff,
  KeyRound,
  LockKeyhole,
  Mail,
  Phone,
  User,
  type LucideIcon,
} from 'lucide-react'
import { useState } from 'react'
import {
  authErrorClass,
  authInputBaseClass,
  authInputIconClass,
  authInputWrapperClass,
  authLabelClass,
} from '../utils'

type AuthTone = 'blue' | 'deepEmerald' | 'emerald' | 'slate'
type AuthFieldIcon = 'key' | 'lock' | 'mail' | 'phone' | 'user'

type AuthTextFieldProps = {
  id: string
  name: string
  label: string
  labelAction?: ReactNode
  type?: string
  value: string
  placeholder?: string
  autoComplete?: string
  error?: string
  icon?: AuthFieldIcon
  tone?: AuthTone
  showPasswordToggle?: boolean
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
  deepEmerald:
    'border-slate-200 hover:border-emerald-600 focus:border-emerald-900 focus:ring-2 focus:ring-emerald-100',
  emerald:
    'border-slate-200 hover:border-emerald-400 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-100',
  slate:
    'border-slate-200 hover:border-slate-400 focus:border-slate-800 focus:ring-2 focus:ring-slate-100',
};

const iconToneClasses: Record<AuthTone, string> = {
  blue: 'text-blue-400',
  deepEmerald: 'text-emerald-900',
  emerald: 'text-emerald-500',
  slate: 'text-slate-400',
}

export function AuthTextField({
  id,
  name,
  label,
  labelAction,
  type = 'text',
  value,
  placeholder,
  autoComplete,
  error,
  icon,
  tone = 'blue',
  showPasswordToggle = false,
  onChange,
}: AuthTextFieldProps) {
  const [isPasswordVisible, setIsPasswordVisible] = useState(false)

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    onChange(event.target.value)
  }

  const Icon = icon ? iconComponents[icon] : null
  const inputType = type === 'password' && showPasswordToggle ? (isPasswordVisible ? 'text' : 'password') : type
  const shouldShowToggle = type === 'password' && showPasswordToggle

  return (
    <div>
      <div className="mb-2 flex items-center justify-between gap-3">
        <label htmlFor={id} className={`${authLabelClass} mb-0`}>
          {label}
        </label>
        {labelAction}
      </div>

      <div className={authInputWrapperClass}>
        {Icon && (
          <span className={`${authInputIconClass} ${iconToneClasses[tone]}`}>
            <Icon size={18} strokeWidth={2.2} aria-hidden="true" />
          </span>
        )}

        <input
          id={id}
          name={name}
          type={inputType}
          value={value}
          placeholder={placeholder}
          autoComplete={autoComplete}
          onChange={handleChange}
          className={`${authInputBaseClass} ${toneClasses[tone]} ${shouldShowToggle ? 'pr-12' : ''}`}
        />

        {shouldShowToggle && (
          <button
            type="button"
            onClick={() => setIsPasswordVisible((current) => !current)}
            className="absolute right-4 top-1/2 -translate-y-1/2 text-[#6b7280] transition-colors hover:text-[#191c1e]"
            aria-label={isPasswordVisible ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
          >
            {isPasswordVisible ? <EyeOff size={18} aria-hidden="true" /> : <Eye size={18} aria-hidden="true" />}
          </button>
        )}
      </div>

      {error && <p className={authErrorClass}>{error}</p>}
    </div>
  )
}


