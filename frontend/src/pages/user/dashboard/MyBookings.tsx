import { useState, useEffect } from 'react';
import { feedbackService } from '../../../services/feedbackService';
import ReviewModal from '../../../components/feedback/ReviewModal/ReviewModal';
import ReportModal from '../../../components/feedback/ReviewModal/ReportModal';
import './MyBookings.css';

interface BookingItem {
  id: string;
  hotelName: string;
  type: string;
  categoryIcon: string;
  date: string;
  price: string;
  status: 'Completed' | 'Pending';
}

const ITEMS_PER_PAGE = 15;

export default function MyBookings() {
  const [allBookings, setAllBookings] = useState<BookingItem[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [currentPage, setCurrentPage] = useState<number>(1);

  // --- GỌI API ---
  useEffect(() => {
    const fetchCustomerBookings = async () => {
      try {
        setIsLoading(true);
        
        const responseData = await feedbackService.getCustomerBookings();
        
        setAllBookings(responseData || []); 
        
      } catch (error) {
        console.error("Lỗi lấy danh sách đặt đơn từ API:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchCustomerBookings();
  }, []);

  // Tính toán chia mảng dữ liệu
  const totalItems = allBookings.length;
  const totalPages = Math.ceil(totalItems / ITEMS_PER_PAGE);
  
  // Xác định vị trí cắt mảng theo Trang Hiện Tại
  const indexOfLastItem = currentPage * ITEMS_PER_PAGE;
  const indexOfFirstItem = indexOfLastItem - ITEMS_PER_PAGE;
  const currentDisplayedBookings = allBookings.slice(indexOfFirstItem, indexOfLastItem);

  // --- STATE MODAL ĐÁNH GIÁ ---
  const [selectedBooking, setSelectedBooking] = useState<BookingItem | null>(null);
  const [isReviewOpen, setIsReviewOpen] = useState<boolean>(false);
  const [isReportOpen, setIsReportOpen] = useState<boolean>(false);

  const openReviewModal = (booking: BookingItem) => {
    setSelectedBooking(booking);
    setIsReviewOpen(true);
  };

  const openReportModal = (booking: BookingItem) => {
    setSelectedBooking(booking);
    setIsReportOpen(true);
  };

  if (isLoading) {
    return (
      <div className="bookings-loading-container">
        <div className="loading-spinner"></div>
        <p className="loading-text">Đang kết nối hệ thống và tải danh sách đơn đặt phòng...</p>
      </div>
    );
  }

  return (
    <div className="dashboard-layout">
      
      {/* 1. SIDEBAR BÊN TRÁI */}
      <aside className="dashboard-sidebar">
        <div className="sidebar-top-group">
          <div className="sidebar-logo">TraVi-OTA</div>
          <ul className="sidebar-menu">
            <li>
              <a href="#overview" className="menu-item-link">
                Tổng quan
              </a>
            </li>
            <li>
              <a href="#bookings" className="menu-item-link active">
                Chuyến đi của tôi
              </a>
            </li>
            <li>
              <a href="#rewards" className="menu-item-link">
                Thành tựu
              </a>
            </li>
            <li>
              <a href="#settings" className="menu-item-link">
                Cài đặt
              </a>
            </li>
          </ul>
        </div>

        <div className="sidebar-footer-group">
          <ul>
            <li>
                <a href="#logout" className="menu-item-link" >
                    Trung tâm hỗ trợ
                </a>
            </li>
            <li>
                <a href="#logout" className="menu-item-link" style={{ color: '#ba1a1a' }}>
                    Đăng xuất
                </a>
            </li>
          </ul>
        </div>
      </aside>

      {/* KHỐI NỘI DUNG BÊN PHẢI */}
      <div className="dashboard-main">
        
        {/* TOPBAR */}
        <header className="dashboard-topbar">
          <button className="btn-icon-notification">
            <span className="material-symbols-outlined">notifications</span>
          </button>
          <div className="user-profile-badge">
            <span style={{ fontSize: '14px', fontWeight: 500 }}>Nhat Nam</span>
            <img 
              className="user-avatar-circle" 
              src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=256&auto=format&fit=crop" 
              alt="Avatar" 
            />
          </div>
        </header>

        {/* MÀN HÌNH CHÍNH */}
        <main className="content-container">
          <div className="page-title-section">
            <h1>Quản lý chuyến đi</h1>
          </div>

          <div className="bookings-card-panel">
            <div className="custom-table-responsive">
              <table className="bookings-data-table">
                <thead>
                  <tr>
                    <th>Dịch vụ / Điểm đến</th>
                    <th>Ngày</th>
                    <th>Giá</th>
                    <th>Trạng thái</th>
                    <th style={{ textAlign: 'right' }}>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {currentDisplayedBookings.map((booking) => (
                    <tr key={booking.id}>
                      <td>
                        <div className="hotel-meta-cell">
                          <div className="hotel-icon-wrapper">
                            <span className="material-symbols-outlined">{booking.categoryIcon}</span>
                          </div>
                          <div>
                            <p className="hotel-name-text">{booking.hotelName}</p>
                            <p className="hotel-type-text">{booking.type} • ID: {booking.id}</p>
                          </div>
                        </div>
                      </td>
                      <td>{booking.date}</td>
                      <td>{booking.price}</td>
                      <td>
                        <span className={`status-badge ${booking.status === 'Completed' ? 'completed' : 'pending'}`}>
                          {booking.status}
                        </span>
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        {booking.status === 'Completed' ? (
                          <div className="actions-cell-flex" style={{ justifyContent: 'flex-end' }}>
                            <button 
                                className="btn-action-review"
                                onClick={() => openReviewModal(booking)}
                                >
                                Đánh giá
                                </button>
                                <button 
                                className="btn-action-complaint"
                                onClick={() => openReportModal(booking)}
                                >
                                Báo cáo
                            </button>
                          </div>
                        ) : (
                          <span style={{ fontSize: '13px', color: '#717970', fontStyle: 'italic' }}>
                            In Progress
                          </span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* THANH ĐIỀU HƯỚNG PHÂN TRANG ĐỘNG (DƯỚI 15 DÒNG/TRANG) */}
            <div className="pagination-wrapper">
              <div>
                Showing <b>{indexOfFirstItem + 1}</b> to <b>{Math.min(indexOfLastItem, totalItems)}</b> of {totalItems} entries
              </div>
              <div className="pagination-buttons">
                {/* Nút lùi trang */}
                <button 
                  className="btn-page-nav" 
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage(prev => prev - 1)}
                >
                  &lt;
                </button>
                
                {/* Khởi tạo danh sách số trang động */}
                {Array.from({ length: totalPages }, (_, idx) => idx + 1).map(pageNumber => (
                  <button
                    key={pageNumber}
                    className={`btn-page-nav ${currentPage === pageNumber ? 'active' : ''}`}
                    onClick={() => setCurrentPage(pageNumber)}
                  >
                    {pageNumber}
                  </button>
                ))}

                {/* Nút tiến trang */}
                <button 
                  className="btn-page-nav" 
                  disabled={currentPage === totalPages}
                  onClick={() => setCurrentPage(prev => prev + 1)}
                >
                  &gt;
                </button>
              </div>
            </div>

          </div>
        </main>
      </div>

      {/* MODAL ĐÁNH GIÁ */}
        {selectedBooking && (
            <ReviewModal
                isOpen={isReviewOpen}
                bookingId={selectedBooking.id}
                hotelName={selectedBooking.hotelName}
                onClose={() => { setIsReviewOpen(false); setSelectedBooking(null); }}
            />
        )}

        {selectedBooking && (
            <ReportModal
                isOpen={isReportOpen}
                bookingId={selectedBooking.id}
                hotelName={selectedBooking.hotelName}
                onClose={() => { setIsReportOpen(false); setSelectedBooking(null); }}
            />
        )}
    </div>
  );
}
