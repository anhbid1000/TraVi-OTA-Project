import React, { useState, useRef } from 'react';
import axios from 'axios';
import './ReviewModal.css'; 

interface ReportModalProps {
  isOpen: boolean;
  bookingId: string;
  hotelName: string;
  onClose: () => void;
}

export default function ReportModal({ isOpen, bookingId, hotelName, onClose }: ReportModalProps) {
  const [activeTab, setActiveTab] = useState<'COMPLAINT' | 'REPORT'>('COMPLAINT');
  const [noiDung, setNoiDung] = useState<string>('');
  const [images, setImages] = useState<File[]>([]);
  const [isDragActive, setIsDragActive] = useState<boolean>(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  if (!isOpen) return null;

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    if (e.type === "dragenter" || e.type === "dragover") setIsDragActive(true);
    else if (e.type === "dragleave") setIsDragActive(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const validFiles = Array.from(e.dataTransfer.files).filter(file => file.type.startsWith('image/'));
      setImages(prev => [...prev, ...validFiles]);
    }
  };

  const handleSubmit = async () => {
    if (!noiDung.trim()) {
      alert("Vui lòng nhập nội dung phản hồi!");
      return;
    }
    setIsSubmitting(true);
    try {
      if (activeTab === 'COMPLAINT') {
        await axios.post('/api/v1/user/feedback/complaints', {
          idDon: bookingId,
          noiDung: noiDung
        });
        alert("Gửi đơn khiếu nại thành công!");
      } else {
        await axios.post('/api/v1/user/feedback/reports', {
          idDon: bookingId,
          noiDung: noiDung
        });
        alert("Gửi đơn tố cáo thành công!");
      }
      setNoiDung('');
      setImages([]);
      onClose();
    } catch (error) {
      console.error(error);
      alert("Gửi thất bại, vui lòng thử lại!");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="feedback-glass-backdrop" onClick={onClose}>
      <div className="feedback-modal-container" onClick={e => e.stopPropagation()}>
        
        {/* Header */}
        <div className="feedback-header">
          <div>
            <h2>Đánh giá</h2>
            <p>{hotelName || "InterContinental Danang Sun Peninsula Resort"}</p>
          </div>
          <button className="btn-close-modal" onClick={onClose}>&times;</button>
        </div>

        {/* Tabs */}
        <div className="feedback-tabs-bar">
          <button 
            className={`feedback-tab-btn ${activeTab === 'COMPLAINT' ? 'active' : ''}`}
            onClick={() => { setActiveTab('COMPLAINT'); setNoiDung(''); }}
          >
            Khiếu nại
          </button>
          <button 
            className={`feedback-tab-btn ${activeTab === 'REPORT' ? 'active' : ''}`}
            onClick={() => { setActiveTab('REPORT'); setNoiDung(''); }}
          >
            Tố cáo
          </button>
        </div>

        {/* Content Box */}
        <div className="feedback-content">
              {/* Ô nhập Text chi tiết */}
          <div>
            <span className="section-label">
              {activeTab === 'COMPLAINT' ? 'Lý do / Chi tiết khiếu nại' : 'Lý do / Chi tiết tố cáo'}
            </span>
            <textarea
              className="feedback-textarea"
              rows={4}
              placeholder="Mô tả vấn đề hoặc vi phạm một cách chi tiết..."
              onChange={e => setNoiDung(e.target.value)}
            />
          </div>

          {/* Kéo thả Ảnh */}
          <div>
            <span className="section-label">Thêm ảnh (Tùy chọn)</span>
            <input 
              type="file" 
              ref={fileInputRef} 
              multiple 
              accept="image/*" 
              className="hidden" 
              onChange={e => e.target.files && setImages(prev => [...prev, ...Array.from(e.target.files!)])}
            />
            <div 
              className={`feedback-upload-zone ${isDragActive ? 'active' : ''}`}
              onDragEnter={handleDrag}
              onDragOver={handleDrag}
              onDragLeave={handleDrag}
              onDrop={handleDrop}
              onClick={() => fileInputRef.current?.click()}
            >
              <div className="upload-icon-box">📷</div>
              <p>Chọn ảnh</p>
              <span>hoặc thả ảnh ở đây</span>
            </div>

            {/* Danh sách ảnh COMPLAINT nhỏ gọn */}
            {images.length > 0 && (
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '10px' }}>
                {images.map((img, i) => (
                  <div key={i} style={{ background: '#edeef0', padding: '4px 10px', borderRadius: '4px', fontSize: '12px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                    🖼️ {img.name.substring(0, 15)}...
                    <b style={{ color: '#ba1a1a', cursor: 'pointer' }} onClick={(e) => { e.stopPropagation(); setImages(images.filter((_, idx) => idx !== i)); }}>&times;</b>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Footer Actions */}
        <div className="feedback-footer">
          <button className="btn-feedback-cancel" onClick={onClose}>Cancel</button>
          <button 
            className="btn-feedback-submit" 
            onClick={handleSubmit} 
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Submitting...' : activeTab === 'COMPLAINT' ? 'Gửi khiếu nại' : 'Gửi tố cáo'}
          </button>
        </div>

      </div>
    </div>
  );
}
