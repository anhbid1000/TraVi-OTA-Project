export function formatDateInputValue(date: Date): string {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}-${month}-${day}`
}

export function getDefaultHotelStay() {
  const checkIn = new Date()
  checkIn.setHours(0, 0, 0, 0)

  const checkOut = new Date(checkIn)
  checkOut.setDate(checkOut.getDate() + 1)

  return {
    checkIn: formatDateInputValue(checkIn),
    checkOut: formatDateInputValue(checkOut),
    guests: 2,
  }
}
