import { useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import type { PartnerLayoutContext } from './PartnerLayout'
import { PartnerDashboardPage } from './PartnerDashboardPage'

export function PartnerBusinessProfilePage() {
  const { setActiveView } = useOutletContext<PartnerLayoutContext>()

  useEffect(() => {
    setActiveView('business-profile')
  }, [setActiveView])

  // Reuse the existing partner business-profile screen so map picker, image upload,
  // asset images, amenities, and setup flows remain intact.
  return <PartnerDashboardPage />
}
