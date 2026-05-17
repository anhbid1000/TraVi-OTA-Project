export const isValidEmail = (email: string) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

export const isValidPhoneNumber = (phone: string) => {
  return /^(0|\+84)\d{9}$/.test(phone)
}

export const validatePassword = (password: string) => {
  if (!password) {
    return 'Vui lòng nhập mật khẩu.'
  }

  if (password.length < 9) {
    return 'Mật khẩu phải có ít nhất 9 ký tự.'
  }

  if (password.length > 20) {
    return 'Mật khẩu không được vượt quá 20 ký tự.'
  }

  if (!/[A-Z]/.test(password)) {
    return 'Mật khẩu phải có ít nhất 1 chữ hoa.'
  }

  if (!/[a-z]/.test(password)) {
    return 'Mật khẩu phải có ít nhất 1 chữ thường.'
  }

  if (!/[0-9]/.test(password)) {
    return 'Mật khẩu phải có ít nhất 1 chữ số.'
  }

  if (!/[@$!%*?&]/.test(password)) {
    return 'Mật khẩu phải có ít nhất 1 ký tự đặc biệt (@$!%*?&).'
  }

  if (!/^[A-Za-z\d@$!%*?&]+$/.test(password)) {
    return 'Mật khẩu chỉ được chứa chữ, số và ký tự đặc biệt @$!%*?&.'
  }

  return ''
}
