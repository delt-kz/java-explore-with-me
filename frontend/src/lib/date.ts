const formatter = new Intl.DateTimeFormat('ru-RU', {
  day: '2-digit',
  month: 'long',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
})

export function formatDateTime(value: string | null | undefined) {
  if (!value) {
    return 'Не указано'
  }

  const date = new Date(value.replace(' ', 'T'))
  if (Number.isNaN(date.getTime())) {
    return value
  }

  return formatter.format(date)
}

export function toBackendDateTime(value: string) {
  if (!value) {
    return ''
  }

  return `${value.replace('T', ' ')}:00`
}
