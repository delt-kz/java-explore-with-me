export function describeEventState(state: string) {
  switch (state) {
    case 'PENDING':
      return 'на модерации'
    case 'PUBLISHED':
      return 'опубликовано'
    case 'CANCELED':
      return 'отменено'
    case 'REVISION_REQUIRED':
      return 'нужна доработка'
    default:
      return state
  }
}

export function describeRequestStatus(status: string) {
  switch (status) {
    case 'PENDING':
      return 'ожидает решения'
    case 'CONFIRMED':
      return 'подтверждена'
    case 'REJECTED':
      return 'отклонена'
    case 'CANCELED':
      return 'отменена'
    default:
      return status
  }
}

export function describeReviewStatus(status: string) {
  switch (status) {
    case 'RETURNED':
      return 'вернули на доработку'
    case 'APPROVED':
      return 'одобрено'
    default:
      return status
  }
}

export function describeAccess(paid: boolean) {
  return paid ? 'Платное участие' : 'Бесплатное участие'
}

export function describeModerationMode(requestModeration: boolean) {
  return requestModeration ? 'Подтверждение вручную' : 'Автоподтверждение'
}

export function formatViews(views: number) {
  return `${views ?? 0} просмотров`
}

export function formatRequests(count: number) {
  return `${count ?? 0} подтверждено`
}
