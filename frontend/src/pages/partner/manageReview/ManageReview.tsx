import { useState, useEffect } from 'react';
import { feedbackService } from '../../../services/feedbackService';
import './ManageReview.css';

interface ReviewItem {
  id: string;
  customerName: string;
  serviceType: 'hotel' | 'restaurant';
  unitName: string;                     
  rating: number;
  date: string;
  content: string;
  tags: string[];
  officialReply?: string;
}
const ITEMS_PER_PAGE = 10;

export default function ManageReviews() {
  const [reviews, setReviews] = useState<ReviewItem[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [currentPage, setCurrentPage] = useState<number>(1);
  
  const [expandedReviewId, setExpandedReviewId] = useState<string | null>(null);
  const [replyContent, setReplyContent] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  useEffect(() => {
    const fetchReviews = async () => {
      try {
        setIsLoading(true);
        // Gọi API từ Service bốc dữ liệu từ PostgreSQL về
        const responseData = await feedbackService.getPartnerReviews();
        setReviews(responseData || []);
      } catch (error) {
        console.error("Lỗi khi tải danh sách đánh giá:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchReviews();
  }, []);

  const handleSaveReply = async (reviewId: string) => {
    if (!replyContent.trim()) return;

    try {
      setIsSubmitting(true);
      await feedbackService.replyToReview(reviewId, replyContent);

      setReviews(prevReviews =>
        prevReviews.map(item =>
          item.id === reviewId ? { ...item, officialReply: replyContent } : item
        )
      );

      setReplyContent('');
      setExpandedReviewId(null);
      alert("Gửi phản hồi đánh giá thành công!");
    } catch (error) {
      console.error("Lỗi gửi phản hồi:", error);
      alert("Không thể gửi phản hồi. Vui lòng kiểm tra lại kết nối mạng hoặc Backend!");
    } finally {
      setIsSubmitting(false);
    }
  };

  // --- PHÂN TRANG ---
  const totalItems = reviews.length;
  const totalPages = Math.ceil(totalItems / ITEMS_PER_PAGE);
  const indexOfLastItem = currentPage * ITEMS_PER_PAGE;
  const indexOfFirstItem = indexOfLastItem - ITEMS_PER_PAGE;
  const currentReviewsOnPage = reviews.slice(indexOfFirstItem, indexOfLastItem);

  const toggleExpand = (id: string) => {
    if (expandedReviewId === id) {
      setExpandedReviewId(null);
      setReplyContent('');
    } else {
      setExpandedReviewId(id);
      const currentReview = reviews.find(r => r.id === id);
      setReplyContent(currentReview?.officialReply || '');
    }
  };

  if (isLoading) {
    return (
      <div className="bookings-loading-container">
        <div className="loading-spinner"></div>
        <p className="loading-text">Đang kết nối hệ thống và tải danh sách đánh giá từ khách hàng...</p>
      </div>
    );
  }

  return (
    <div className="review-dashboard">
      
      {/* SIDEBAR PANEL */}
      <aside className="sidebar-panel">
        <div className="sidebar-top-group">
          <div className="sidebar-logo">Heritage</div>
          <nav className="sidebar-menu">
            <a href="#dashboard">
              <span className="material-symbols-outlined">dashboard</span>
              <span>Tổng quan</span>
            </a>
            <a href="#bookings">
              <span className="material-symbols-outlined">calendar_month</span>
              <span>Đặt phòng / Bàn</span>
            </a>
            <a href="#reviews" className="menu-item-active">
              <span className="material-symbols-outlined">rate_review</span>
              <span>Đánh giá</span>
            </a>
            <a href="#rooms">
              <span className="material-symbols-outlined">bed</span>
              <span>Quản lý vận hành</span>
            </a>
          </nav>
        </div>
        <div className="sidebar-footer">
          <a href="#settings">
            <span className="material-symbols-outlined">settings</span>
            <span>Cài đặt</span>
          </a>
        </div>
      </aside>

      {/* MAIN CONTAINER */}
      <main className="main-content-panel">
        <header className="page-header">
          <h1>Quản lý Đánh giá</h1>
          <p>Phản hồi khách hàng & Điều phối chất lượng dịch vụ Khách sạn & Nhà hàng</p>
        </header>

        {/* REVIEWS TABLE CARD CONTAINER */}
        <section className="reviews-table-card">
          
          {/* THANH TIÊU ĐỀ: Thay chữ "Hạng phòng" bằng "Dịch vụ" để bao quát tổng thể */}
          <div className="table-header-row">
            <span className="col-header col-customer">Khách hàng / Dịch vụ</span>
            <span className="col-header col-content">Nội dung tóm tắt</span>
            <span className="col-header col-rating">Đánh giá</span>
            <span className="col-header col-status">Trạng thái</span>
            <span className="col-header col-action"></span>
          </div>

          {/* KHỐI NỘI DUNG DANH SÁCH BÊN DƯỚI */}
          <div className="table-body-container">
            {isLoading ? (
              <div className="table-loading-state">
                <span className="material-symbols-outlined loading-spinner">sync</span>
                <p>Đang tải dữ liệu đánh giá tổng hợp từ hệ thống...</p>
              </div>
            ) : currentReviewsOnPage.length === 0 ? (
              <div className="table-empty-state">
                <p>Không có dữ liệu đánh giá nào.</p>
              </div>
            ) : (
              currentReviewsOnPage.map((review) => {
                const isExpanded = expandedReviewId === review.id;
                const hasReplied = !!review.officialReply;

                return (
                  <div 
                    key={review.id} 
                    className={`review-row-wrapper ${isExpanded ? 'row-expanded-active' : ''}`}
                  >
                    {/* ROW SUMMARY CLICKABLE */}
                    <div className="row-summary-trigger" onClick={() => toggleExpand(review.id)}>
                      
                      {/* Cột 1: Khách hàng và Dịch vụ tương ứng */}
                      <div className="customer-info-box">
                        <div className="avatar-circle">
                          {review.customerName.charAt(0)}
                        </div>
                        <div className="meta-text">
                          <span className="customer-name">{review.customerName}</span>
                          {/* Hiển thị linh hoạt tag phân loại và tên dịch vụ */}
                          <span className="room-name flex-center-gap">
                            <span className={`service-mini-tag ${review.serviceType}`}>
                              {review.serviceType === 'hotel' ? 'Hotel' : 'Dining'}
                            </span>
                            <span className="unit-text-ellipsis">{review.unitName}</span>
                          </span>
                        </div>
                      </div>

                      {/* Cột 2: Tóm tắt nội dung */}
                      <div className="content-preview-box">
                        <p className="preview-text">{review.content}</p>
                      </div>

                      {/* Cột 3: Điểm đánh giá sao */}
                      <div className="rating-box">
                        <div className="star-rating-badge">
                          <span className="star-score">{review.rating}</span>
                          <div className="stars-list">
                            {Array.from({ length: 5 }).map((_, i) => (
                              <span 
                                key={i} 
                                className={`material-symbols-outlined ${i < review.rating ? 'fill' : ''}`}
                              >
                                star
                              </span>
                            ))}
                          </div>
                        </div>
                      </div>

                      {/* Cột 4: Trạng thái phản hồi */}
                      <div className="status-box">
                        {hasReplied ? (
                          <span className="status-badge badge-replied">Đã phản hồi</span>
                        ) : (
                          <span className="status-badge badge-pending">Chưa phản hồi</span>
                        )}
                      </div>

                      {/* Cột 5: Chevron Action */}
                      <div className="action-box">
                        <span className={`material-symbols-outlined chevron-icon ${isExpanded ? 'rotate-180' : ''}`}>
                          keyboard_arrow_down
                        </span>
                      </div>

                    </div>

                    {/* VÙNG CHI TIẾT KHI MỞ RỘNG */}
                    {isExpanded && (
                      <div className="review-collapse-details animation-row-expand">
                        <div className="details-internal-card">
                          
                          <div className="full-meta-row">
                            <span className="review-date">Ngày nhận: {review.date}</span>
                            <div className="tags-list">
                              {review.tags.map(tag => (
                                <span key={tag} className="tag-item">#{tag}</span>
                              ))}
                            </div>
                          </div>

                          <div className="full-content-bubble">
                            <p>{review.content}</p>
                          </div>

                          <div className="reply-form-section">
                            <label className="reply-label">Phản hồi chính thức từ Ban quản trị</label>
                            
                            <div className="reply-textarea-container">
                              <textarea
                                value={replyContent}
                                onChange={(e) => setReplyContent(e.target.value)}
                                placeholder="Nhập nội dung phản hồi chính thức gửi đến khách hàng..."
                                className="review-textarea-input"
                                disabled={isSubmitting}
                              />
                              
                              <div className="reply-textarea-footer">
                                <span className="info-tip">
                                  <span className="material-symbols-outlined">info</span>
                                  {review.officialReply ? "Nội dung mới sẽ lưu đè lên câu trả lời cũ." : "Phản hồi sẽ được gửi trực tiếp đến hệ thống."}
                                </span>
                                
                                <button
                                  type="button"
                                  onClick={() => handleSaveReply(review.id)}
                                  disabled={isSubmitting || !replyContent.trim()}
                                  className="btn-submit-reply"
                                >
                                  <span>{isSubmitting ? 'Đang gửi...' : 'Gửi phản hồi'}</span>
                                  <span className="material-symbols-outlined">send</span>
                                </button>
                              </div>
                            </div>

                          </div>

                        </div>
                      </div>
                    )}
                  </div>
                );
              })
            )}
          </div>

          {/* THANH PHÂN TRANG (PAGINATION PANEL) */}
          {!isLoading && totalPages > 1 && (
            <div className="pagination-container-row">
              <span className="pagination-text-summary">
                Hiển thị <strong>{indexOfFirstItem + 1}</strong> - <strong>{Math.min(indexOfLastItem, reviews.length)}</strong> trên tổng số <strong>{reviews.length}</strong> đánh giá
              </span>
              
              <div className="pagination-buttons-group">
                <button 
                  className="btn-page-action"
                  disabled={currentPage === 1}
                  onClick={() => { setCurrentPage(prev => prev - 1); setExpandedReviewId(null); }}
                >
                  <span className="material-symbols-outlined">chevron_left</span>
                </button>

                {Array.from({ length: totalPages }, (_, idx) => idx + 1).map(pageNumber => (
                  <button
                    key={pageNumber}
                    className={`btn-page-number ${currentPage === pageNumber ? 'page-active' : ''}`}
                    onClick={() => { setCurrentPage(pageNumber); setExpandedReviewId(null); }}
                  >
                    {pageNumber}
                  </button>
                ))}

                <button 
                  className="btn-page-action"
                  disabled={currentPage === totalPages}
                  onClick={() => { setCurrentPage(prev => prev + 1); setExpandedReviewId(null); }}
                >
                  <span className="material-symbols-outlined">chevron_right</span>
                </button>
              </div>
            </div>
          )}

        </section>
      </main>

    </div>
  );
}
