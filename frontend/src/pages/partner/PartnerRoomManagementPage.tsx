import { useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import type { PartnerLayoutContext } from './PartnerLayout'
import { PartnerDashboardPage } from './PartnerDashboardPage'

export function PartnerRoomManagementPage() {
  const { setActiveView } = useOutletContext<PartnerLayoutContext>()
  useEffect(() => setActiveView('room-management'), [setActiveView])
  return <PartnerDashboardPage />
}
