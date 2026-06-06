import { useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import type { PartnerLayoutContext } from './PartnerLayout'
import { PartnerDashboardPage } from './PartnerDashboardPage'

export function PartnerTableLayoutPage() {
  const { setActiveView } = useOutletContext<PartnerLayoutContext>()
  useEffect(() => setActiveView('table-layout'), [setActiveView])
  return <PartnerDashboardPage />
}
