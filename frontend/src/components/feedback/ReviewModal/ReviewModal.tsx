import React, { useState, useRef } from 'react';
import axios from 'axios';
import './ReviewModal.css'; 

interface ReviewModalProps {
  isOpen: boolean;
  bookingId: string;
  hotelName: string;
  onClose: () => void;
}

export default function ReviewModal({ isOpen, bookingId, hotelName, onClose }: ReviewModalProps) {
  const [activeTab] = useState<'REVIEW' | 'COMPLAINT'>('REVIEW');
  const [rating, setRating] = useState<number>(0);
  const [hoverRating, setHoverRating] = useState<number>(0);
  const [noiDung, setNoiDung] = useState<string>('');
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [images, setImages] = useState<File[]>([]);
  const [isDragActive, setIsDragActive] = useState<boolean>(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  if (!isOpen) return null;

  const toggleTag = (tag: string) => {
    setSelectedTags(prev => prev.includes(tag) ? prev.filter(t => t !== tag) : [...prev, tag]);
  };

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
      if (activeTab === 'REVIEW') {
        await axios.post('/api/v1/user/feedback/reviews', {
          idDon: bookingId,
          noiDung: noiDung,
          soSao: rating,
          tags: selectedTags
        });
        alert("Đăng bài đánh giá thành công!");
      } else {
        await axios.post('/api/v1/user/feedback/complaints', {
          idDon: bookingId,
          noiDung: noiDung
        });
        alert("Đã gửi đơn tố cáo tới Ban quản trị xử lý!");
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

        {/* Content Box */}
        <div className="feedback-content">
                <div className="star-rating-box">
                    <p>Trải nghiệm của bạn như thế nào?</p>
                    <div className="stars-wrapper">
                        {[1, 2, 3, 4, 5].map((star) => {
                            const isLightUp = star <= hoverRating || (hoverRating === 0 && star <= rating);

                            return (
                                <button
                                    key={star}
                                    className={`star-item ${isLightUp ? 'active' : ''}`}
                                    onClick={() => setRating(star)}
                                    onMouseEnter={() => setHoverRating(star)}
                                    onMouseLeave={() => setHoverRating(0)}
                                >
                                    ★
                                </button>
                            )
                        }
                        )}
                    </div>
                </div>

          {/* Phân hệ Sentiment Tags (Chỉ ở tab Review) */}
            <div>
              <span className="section-label">Điểm nổi bật?</span>
              <div className="tags-wrapper">
                {['Sạch sẽ', 'Dịch vụ tốt', 'Vị trí', 'Tiện nghi', 'Ẩm thực'].map(tag => (
                  <button
                    key={tag}
                    className={`tag-btn ${selectedTags.includes(tag) ? 'selected' : ''}`}
                    onClick={() => toggleTag(tag)}
                  >
                    {tag}
                  </button>
                ))}
              </div>
            </div>

          {/* Ô nhập Text chi tiết */}
          <div>
            <span className="section-label">
              Đánh giá chi tiết
            </span>
            <textarea
              className="feedback-textarea"
              rows={4}
              placeholder="Hãy kể cho chúng tôi biết thêm về chuyến đi của bạn..."
              value={noiDung}
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

            {/* Danh sách ảnh Preview nhỏ gọn */}
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
            {isSubmitting ? 'Submitting...' : 'Submit Review'}
          </button>
        </div>

      </div>
    </div>
  );
}
