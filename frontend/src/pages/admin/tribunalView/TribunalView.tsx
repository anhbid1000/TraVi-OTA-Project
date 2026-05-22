import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { disputeService } from '../../../services/disputeService';
import './TribunalView.css';

interface DisputeCase {
  id: string;
  bookingId: string;
  createdAt: string;
  // Bên khiếu nại (Khách hàng)
  customerName: string;
  complaintReason: string;
  customerAttachments: { name: string; size: string; type: 'image' | 'document' }[];
  // Bên bị khiếu nại (Partner)
  partnerName: string;
  partnerExplanation: string;
  partnerAttachments: { name: string; size: string; type: 'image' | 'document' }[];

  tieuDe?: string;
  noiDung?: string;
  trangThai?: string;
}

export default function TribunalView() {
  const [activeCase, setActiveCase] = useState<DisputeCase | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const { caseId } = useParams<{ caseId: string }>();
  const [adminNote, setAdminNote] = useState<string>("");

  useEffect(() => {
    const fetchDisputeCase = async () => {
      if (!caseId) return;
      try {
        setIsLoading(true);
        const response = await disputeService.getPartnerDisputes(); 
        const currentCase = response?.find((c: any) => c.id === caseId);
        if (currentCase) {
          setActiveCase(currentCase);
        } else {
          console.error("Không tìm thấy vụ tranh chấp tương ứng với ID này");
        }
      } catch (error) {
        console.error("Lỗi khi tải thông tin vụ việc:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDisputeCase();
  }, [caseId]);

  const handleVerdict = async (verdict: 'KHONG_VI_PHAM' | 'VI_PHAM_NHE' | 'VI_PHAM_NANG') => {
    if (!activeCase || !activeCase.id) {
      alert("Không tìm thấy mã hồ sơ phán quyết (Case ID)!");
      return;
    }
    try {
      const result = await disputeService.submitAdminVerdict(activeCase.id, verdict);
      alert(result.message || "Đã thực thi phán quyết thành công!");
    } catch (error) {
      console.error("Lỗi khi gửi phán quyết:", error);
      alert("Không thể thực thi phán quyết.");
    }
  };

  if (isLoading) {
    return (
      <div className="loading-container">
        <span className="material-symbols-outlined spin">autorenew</span>
        <p>Đang tải thông tin vụ việc từ hệ thống...</p>
      </div>
    );
  }

  return (
    <div className="tribunal-dashboard">
      
      {/* HEADER PANEL */}
      <header className="tribunal-header">
        <div className="header-left">
          <h1>Phân Xử Vi Phạm</h1>
          <p>Mã hồ sơ: <strong>{activeCase?.id}</strong> • Liên kết đơn đặt: <strong>{activeCase?.bookingId}</strong> • Ngày tạo: {activeCase?.createdAt}</p>
        </div>
        <div className="header-right-logo">TRAVI</div>
      </header>

      {/* GIAO DIỆN CHIA 2 CỘT SO SÁNH ĐỐI LẬP */}
      <div className="tribunal-split-workspace">
        
        {/* CỘT TRÁI: NGUYÊN ĐƠN (KHÁCH HÀNG TỐ CÁO) */}
        <section className="tribunal-column column-plaintiff">
          <div className="column-title-bar">
            <span className="material-symbols-outlined icon-alert">gavel</span>
            <h2>1. Bên Tố Cáo: {activeCase?.customerName} (Khách hàng)</h2>
          </div>
          
          <div className="column-inner-content">
            <div className="statement-bubble">
              <label>Nội dung tố cáo vi phạm:</label>
              <p>{activeCase?.complaintReason}</p>
            </div>

            <div className="evidences-box">
              <label>Tệp tin minh chứng gốc từ Người dùng:</label>
              <div className="evidence-grid">
                {activeCase?.customerAttachments?.map((file, idx) => (
                  <div key={idx} className="evidence-media-card">
                    <span className="material-symbols-outlined file-icon">
                      {file.type === 'image' ? 'image' : 'video_library'}
                    </span>
                    <div className="file-info">
                      <span className="file-name" title={file.name}>{file.name}</span>
                      <span className="file-size">{file.size}</span>
                    </div>
                    <button className="btn-preview-file" onClick={() => alert(`Xem trước file: ${file.name}`)}>
                      <span className="material-symbols-outlined">visibility</span>
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </section>

        {/* CỘT PHẢI: BỊ ĐƠN (PARTNER GIẢI TRÌNH) */}
        <section className="tribunal-column column-defendant">
          <div className="column-title-bar">
            <span className="material-symbols-outlined icon-partner">business_center</span>
            <h2>2. Bên Bị Tố Cáo: {activeCase?.partnerName} (Đối tác)</h2>
          </div>

          <div className="column-inner-content">
            <div className="statement-bubble">
              <label>Lời giải trình & Biện hộ từ Đối tác:</label>
              <p>{activeCase?.partnerExplanation}</p>
            </div>

            <div className="evidences-box">
              <label>Tài liệu & Chứng cứ phản biện đính kèm:</label>
              <div className="evidence-grid">
                {activeCase?.partnerAttachments?.map((file, idx) => (
                  <div key={idx} className="evidence-media-card">
                    <span className="material-symbols-outlined file-icon">
                      {file.type === 'image' ? 'image' : 'description'}
                    </span>
                    <div className="file-info">
                      <span className="file-name" title={file.name}>{file.name}</span>
                      <span className="file-size">{file.size}</span>
                    </div>
                    <button className="btn-preview-file" onClick={() => alert(`Xem trước file: ${file.name}`)}>
                      <span className="material-symbols-outlined">visibility</span>
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </section>

      </div>

      {/* DƯỚI CÙNG: PANEL ĐƯA RA PHÁN QUYẾT (VERDICT PANEL) */}
      <footer className="tribunal-verdict-panel">
        <div className="panel-inner-box">
          
          <div className="verdict-note-area">
            <div className="note-label-row">
              <span className="material-symbols-outlined text-gold">edit_note</span>
              <label htmlFor="admin-note-input">Ghi chú phân xử công khai (Bắt buộc - Gửi cho cả 2 bên xem):</label>
            </div>
            <textarea
              id="admin-note-input"
              value={adminNote}
              onChange={(e) => setAdminNote(e.target.value)}
              placeholder="Nhập lập luận pháp lý, căn cứ biên bản để đưa ra kết luận đóng hồ sơ vụ việc..."
              className="verdict-textarea"
            />
          </div>

          <div className="verdict-actions-control">
            <div className="action-tip">
              <span className="material-symbols-outlined">shield_alert</span>
              <span>Chọn một trong các hành động phán quyết bên phải để thực thi lập tức.</span>
            </div>

            <div className="buttons-verdict-group">
              {/* NÚT XANH: BÁC ĐƠN TỐ CÁO */}
              <button 
                type="button" 
                className="btn-verdict verdict-dismiss"
                onClick={() => handleVerdict('KHONG_VI_PHAM')}
              >
                <span className="material-symbols-outlined">verified</span>
                <div className="btn-text-wrapper">
                  <span className="btn-main-label">Bác đơn tố cáo</span>
                  <span className="btn-sub-desc">Giải tỏa đóng băng</span>
                </div>
              </button>

              {/* NÚT VÀNG: CẢNH CÁO PARTNER */}
              <button 
                type="button" 
                className="btn-verdict verdict-warn"
                onClick={() => handleVerdict('VI_PHAM_NHE')}
              >
                <span className="material-symbols-outlined">warning</span>
                <div className="btn-text-wrapper">
                  <span className="btn-main-label">Cảnh cáo Partner</span>
                  <span className="btn-sub-desc">Trừ điểm uy tín cơ sở</span>
                </div>
              </button>

              {/* NÚT ĐỎ: KHÓA PARTNER & ĐỀN BÙ KHÁCH HÀNG */}
              <button 
                type="button" 
                className="btn-verdict verdict-ban"
                onClick={() => handleVerdict('VI_PHAM_NANG')}
              >
                <span className="material-symbols-outlined">gavel</span>
                <div className="btn-text-wrapper">
                  <span className="btn-main-label">Khóa & Đền bù</span>
                  <span className="btn-sub-desc">Phạt tiền, hạ bệ cơ sở</span>
                </div>
              </button>
            </div>
          </div>

        </div>
      </footer>

    </div>
  );
}
