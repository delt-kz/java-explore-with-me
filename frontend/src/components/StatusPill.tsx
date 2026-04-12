type StatusPillProps = {
  label: string
  tone?: string
}

const toneClassMap: Record<string, string> = {
  PENDING: 'warning',
  PUBLISHED: 'success',
  CONFIRMED: 'success',
  APPROVED: 'success',
  REJECTED: 'danger',
  CANCELED: 'muted',
  CANCELED_REQUEST: 'muted',
  REVISION_REQUIRED: 'danger',
  RETURNED: 'danger',
}

function StatusPill({ label, tone }: StatusPillProps) {
  const toneClass = tone ? toneClassMap[tone] ?? 'neutral' : 'neutral'
  return <span className={`status-pill status-pill--${toneClass}`}>{label}</span>
}

export default StatusPill
