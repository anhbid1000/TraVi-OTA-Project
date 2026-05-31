import { useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import type { PartnerLayoutContext } from './PartnerLayout'
import { PartnerDashboardPage } from './PartnerDashboardPage'

export function PartnerRestaurantProfilePage() {
  const { setActiveView } = useOutletContext<PartnerLayoutContext>()

  useEffect(() => {
    setActiveView('restaurant-setup')
  }, [setActiveView])

  return <PartnerDashboardPage />
}
