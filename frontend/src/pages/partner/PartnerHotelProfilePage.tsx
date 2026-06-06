import { useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import type { PartnerLayoutContext } from './PartnerLayout'
import { PartnerDashboardPage } from './PartnerDashboardPage'

export function PartnerHotelProfilePage() {
  const { setActiveView } = useOutletContext<PartnerLayoutContext>()

  useEffect(() => {
    setActiveView('hotel-setup')
  }, [setActiveView])

  return <PartnerDashboardPage />
}
