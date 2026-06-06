import { useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import type { PartnerLayoutContext } from './PartnerLayout'
import { PartnerDashboardPage } from './PartnerDashboardPage'

export function PartnerMenuManagementPage() {
  const { setActiveView } = useOutletContext<PartnerLayoutContext>()
  useEffect(() => setActiveView('menu-management'), [setActiveView])
  return <PartnerDashboardPage />
}
