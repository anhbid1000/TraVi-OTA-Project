import { useState, useRef, useEffect } from 'react';
import { disputeService } from '../../../services/disputeService';
import './DisputeCenter.css';

interface DisputeItem {
  id: string;
  bookingId: string;
  customerName: string;
  serviceName: string;
  serviceType: 'hotel' | 'restaurant';
  reason: string;
  createdAt: string;
  status: 'Investigation' | 'Action_Required' | 'Resolved';
  timeline: TimelineEvent[];
}

interface TimelineEvent {
  id: string;
  sender: 'System' | 'Customer' | 'Admin' | 'Partner';
  senderName: string;
  time: string;
  message: string;
  attachments?: { name: string; size: string; type: string }[];
}

export default function DisputeCenter() {
  // 1. CHUYỂN THÀNH MẢNG RỖNG ĐỂ HỨNG DATA TỪ API
  const [disputes, setDisputes] = useState<DisputeItem[]>([]);
  const [selectedDisputeId, setSelectedDisputeId] = useState<string>("");
  const [chatMessage, setChatMessage] = useState<string>('');
  const [uploadedFiles, setUploadedFiles] = useState<File[]>([]);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    const fetchDisputes = async () => {
      try {
        const data = await disputeService.getPartnerDisputes();
        if (data && data.length > 0) {
          setDisputes(data);
          setSelectedDisputeId(data[0].id); // Tự động chọn ca đầu tiên từ API
        }
      } catch (error) {
        console.error("Lỗi fetch data tranh chấp:", error);
      }
    };
    fetchDisputes();
  }, []);

  // Lấy dữ liệu của ca tranh chấp đang chọn
  const activeDispute = disputes.find(d => d.id === selectedDisputeId) || disputes[0];

  // Xử lý khi Partner bấm chọn file giải trình
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setUploadedFiles(Array.from(e.target.files));
    }
  };

  // Gửi phản hồi / chứng cứ biện hộ lên cho Admin
  const handleSendExplanation = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!chatMessage.trim() && uploadedFiles.length === 0) return;

    try {
      // Gọi service nộp file + text lên Spring Boot
      await disputeService.submitExplanation(selectedDisputeId, chatMessage, uploadedFiles);
      
      // Đồng bộ tạm thời hiển thị lên màn hình (Giữ nguyên logic tạo nốt cũ của bạn)
      const newEvent: TimelineEvent = {
        id: `TL-${Date.now()}`,
        sender: 'Partner',
        senderName: 'Bạn (Đối tác)',
        time: 'Vừa xong',
        message: chatMessage,
        attachments: uploadedFiles.map(f => ({
          name: f.name,
          size: `${(f.size / (1024 * 1024)).toFixed(1)} MB`,
          type: f.type.includes('image') ? 'image' : 'document'
        }))
      };

    // Cập nhật mảng trạng thái
    setDisputes(prev => prev.map(dispute => {
      if (dispute.id === selectedDisputeId) {
        return {
          ...dispute,
          timeline: [...dispute.timeline, newEvent]
        };
      }
      return dispute;
    }));

    // Reset Form gõ
    setChatMessage("");
    setUploadedFiles([]);
    if (fileInputRef.current) fileInputRef.current.value = '';
    } catch (error) {
      console.error("Lỗi gửi giải trình:", error);
      alert("Có lỗi xảy ra khi gửi giải trình. Vui lòng thử lại.");
    }
  };

  const removeUploadFile = (index: number) => {
    setUploadedFiles(prev => prev.filter((_, i) => i !== index));
  };

  return (
    <div className="dispute-dashboard">
      
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
            <a href="#reviews">
              <span className="material-symbols-outlined">rate_review</span>
              <span>Đánh giá</span>
            </a>
            <a href="#disputes" className="menu-item-active">
              <span className="material-symbols-outlined">gavel</span>
              <span>Trung tâm Tranh chấp</span>
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
          <h1>Trung tâm Tranh chấp & Khiếu nại</h1>
          <p>Khu vực phối hợp cùng Trọng tài Admin để điều tra, làm rõ và xử lý các tố cáo từ Khách hàng</p>
        </header>

        {/* WORKSPACE LAYOUT (SPLIT 2 COLUMNS) */}
        <div className="dispute-workspace">
          
          {/* CỘT TRÁI: DANH SÁCH CÁC VỤ TRANH CHẤP */}
          <section className="dispute-list-pane">
            <div className="pane-title-box">
              <h2>Danh sách vụ việc ({disputes.length})</h2>
            </div>
            
            <div className="dispute-cards-container">
              {disputes.map((item) => {
                const isActive = item.id === selectedDisputeId;
                return (
                  <div 
                    key={item.id}
                    className={`dispute-summary-card ${isActive ? 'card-active' : ''}`}
                    onClick={() => { setSelectedDisputeId(item.id); setUploadedFiles([]); }}
                  >
                    <div className="card-top-meta">
                      <span className="dispute-id">{item.id}</span>
                      <span className="dispute-date">{item.createdAt}</span>
                    </div>

                    <h3 className="card-service-title">{item.serviceName}</h3>
                    <p className="card-reason-preview">{item.reason}</p>

                    <div className="card-bottom-row">
                      <span className="booking-link">Mã đặt: <strong>{item.bookingId}</strong></span>
                      
                      {/* Trạng thái Label màu */}
                      {item.status === 'Investigation' && (
                        <span className="status-dispute badge-investigation">Đang điều tra</span>
                      )}
                      {item.status === 'Action_Required' && (
                        <span className="status-dispute badge-action-required">Cần phản hồi gấp</span>
                      )}
                      {item.status === 'Resolved' && (
                        <span className="status-dispute badge-resolved">Đã đóng / Xong</span>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </section>

          {/* CỘT PHẢI: CHI TIẾT VỤ VIỆC & TIMELINE CHAT BIỆN HỘ */}
          <section className="dispute-detail-chat-pane">
            
            {/* TIÊU ĐỀ CHI TIẾT VỤ TRANH CHẤP ĐANG CHỌN */}
            <div className="active-pane-header">
              <div className="header-main-info">
                <span className={`service-type-tag ${activeDispute.serviceType}`}>
                  {activeDispute.serviceType === 'hotel' ? 'Khách sạn' : 'Nhà hàng'}
                </span>
                <h2>{activeDispute.id} / Đơn gốc: {activeDispute.bookingId}</h2>
              </div>
              <p className="plaintiff-text">Nguyên đơn tố cáo: <strong>{activeDispute.customerName}</strong></p>
            </div>

            {/* VÙNG HIỂN THỊ LUỒNG TIMELINE / DIỄN BIẾN TRÒ CHUYỆN ĐỐI THOẠI */}
            <div className="dispute-timeline-chat-flow">
              
              {/* KHỐI NGUYÊN NHÂN TỐ CÁO GỐC (LUÔN GHIM Ở ĐẦU) */}
              <div className="original-complaint-box">
                <div className="complaint-title">
                  <span className="material-symbols-outlined text-danger">warning</span>
                  <h4>Nội dung tố cáo vi phạm ban đầu:</h4>
                </div>
                <p className="complaint-text-content">{activeDispute.reason}</p>

                {/* ================= BỔ SUNG: XEM MINH CHỨNG TỪ KHÁCH HÀNG ================= */}
                {/* Lọc tìm sự kiện đầu tiên của Customer để lấy danh sách tệp đính kèm gốc của họ */}
                {activeDispute.timeline.find(e => e.sender === 'Customer')?.attachments && (
                  <div className="customer-proofs-zone">
                    <div className="proofs-title">
                      <span className="material-symbols-outlined text-danger">folder_open</span>
                      <span>Tệp minh chứng đính kèm từ Người dùng:</span>
                    </div>

                    <div className="customer-attachments-grid">
                      {activeDispute.timeline
                        .find(e => e.sender === 'Customer')
                        ?.attachments?.map((file, fIdx) => (
                          <div key={fIdx} className="customer-file-card">
                            <span className="material-symbols-outlined icon-type">
                              {file.type === 'image' ? 'image' : 'description'}
                            </span>
                            <div className="customer-file-meta">
                              <span className="cf-name" title={file.name}>{file.name}</span>
                              <span className="cf-size">{file.size}</span>
                            </div>
                            {/* Nút hỗ trợ Đối tác bấm Tải xuống hoặc Xem file */}
                            <button
                              type="button"
                              className="btn-download-proof"
                              onClick={() => alert(`Đang tải xuống minh chứng: ${file.name}`)}
                              title="Tải xuống tệp bằng chứng này"
                            >
                              <span className="material-symbols-outlined">download</span>
                            </button>
                          </div>
                        ))}
                    </div>
                  </div>
                )}
                {/* ========================================================================= */}

              </div>

              {/* VÒNG LẶP TIMELINE EVENTS */}
              {activeDispute.timeline.map((event) => {
                return (
                  <div key={event.id} className={`timeline-card-node node-${event.sender.toLowerCase()}`}>
                    <div className="node-avatar-indicator">
                      <span className="material-symbols-outlined">
                        {event.sender === 'System' ? 'settings_suggest' : 
                         event.sender === 'Customer' ? 'person' : 
                         event.sender === 'Admin' ? 'gavel' : 'business_center'}
                      </span>
                    </div>

                    <div className="node-content-body">
                      <div className="node-meta-top">
                        <span className="node-sender-name">{event.senderName}</span>
                        <span className="node-time">{event.time}</span>
                      </div>
                      <div className="node-message-text">
                        <p>{event.message}</p>
                      </div>

                      {/* Hiển thị các File tài liệu đính kèm nếu có */}
                      {event.attachments && (
                        <div className="node-attachments-grid">
                          {event.attachments.map((file, fIdx) => (
                            <div key={fIdx} className="attachment-chip-item">
                              <span className="material-symbols-outlined">
                                {file.type === 'image' ? 'image' : 'description'}
                              </span>
                              <div className="file-meta">
                                <span className="f-name">{file.name}</span>
                                <span className="f-size">{file.size}</span>
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}

            </div>

            {/* KHU VỰC CHAT / FORM TẢI FILE GIẢI TRÌNH (PARTNER FOOTER INPUT) */}
            <div className="partner-reply-composer-zone">
              {activeDispute.status === 'Resolved' ? (
                <div className="dispute-closed-alert">
                  <span className="material-symbols-outlined">lock</span>
                  <p>Vụ việc này đã được Trọng tài xử lý đóng lại. Bạn không thể gửi thêm tài liệu giải trình.</p>
                </div>
              ) : (
                <form onSubmit={handleSendExplanation} className="composer-form-wrapper">
                  
                  {/* Danh sách các file đang chờ gửi lên BE */}
                  {uploadedFiles.length > 0 && (
                    <div className="staging-upload-files-strip">
                      {uploadedFiles.map((f, index) => (
                        <div key={index} className="staging-file-card">
                          <span className="material-symbols-outlined text-green">draft</span>
                          <span className="staging-name">{f.name}</span>
                          <button 
                            type="button" 
                            className="btn-remove-staging"
                            onClick={() => removeUploadFile(index)}
                          >
                            <span className="material-symbols-outlined">close</span>
                          </button>
                        </div>
                      ))}
                    </div>
                  )}

                  {/* Vùng gõ nội dung văn bản */}
                  <div className="input-row-controls">
                    <button 
                      type="button" 
                      className="btn-trigger-upload-file"
                      title="Tải lên tệp tài liệu giải trình chứng cứ"
                      onClick={() => fileInputRef.current?.click()}
                    >
                      <span className="material-symbols-outlined">upload_file</span>
                      <span>Đính kèm file</span>
                    </button>

                    <input 
                      type="file" 
                      ref={fileInputRef} 
                      className="hidden-native-input" 
                      multiple
                      onChange={handleFileChange}
                    />

                    <input 
                      type="text"
                      className="text-explanation-input"
                      value={chatMessage}
                      onChange={(e) => setChatMessage(e.target.value)}
                      placeholder="Nhập nội dung giải trình, phản hồi lại yêu cầu của Trọng tài Admin..."
                    />

                    <button 
                      type="submit" 
                      className="btn-send-dispute-reply"
                      disabled={!chatMessage.trim() && uploadedFiles.length === 0}
                    >
                      <span>Gửi giải trình</span>
                      <span className="material-symbols-outlined">send</span>
                    </button>
                  </div>

                </form>
              )}
            </div>

          </section>

        </div>
      </main>

    </div>
  );
}
