import { Link } from 'react-router-dom'

export function ForbiddenPage() {
  return (
    <main>
      <h1>403 - Không có quyền truy cập</h1>
      <p>Tài khoản của bạn không có quyền truy cập trang này.</p>
      <Link to="/">Quay về trang chủ</Link>
    </main>
  )
}