import { useEffect, useState } from 'react';
import { Star } from 'lucide-react';
import { api } from '../../../../services/api';
import type { ReviewAspectScoreItem, ReviewResponse } from '../../types/review';

function formatServiceType(type: ReviewResponse['serviceType']) {
  return type === 'KHACH_SAN' ? 'Khách sạn' : 'Nhà hàng';
}

function aspectLabel(aspect: ReviewAspectScoreItem['aspect']) {
  return ({
    CLEANLINESS: 'Vệ sinh',
    SERVICE: 'Dịch vụ',
    LOCATION: 'Vị trí',
    VALUE: 'Giá trị',
    AMENITIES: 'Tiện nghi',
    FOOD_QUALITY: 'Chất lượng món',
    AMBIENCE: 'Không gian',
  } as const)[aspect];
}

type PartnerReviewCardProps = {
  review: ReviewResponse;
  onReply: (review: ReviewResponse) => void;
};

export function PartnerReviewCard({ review, onReply }: PartnerReviewCardProps) {
  const [imageUrls, setImageUrls] = useState<string[]>([]);

  useEffect(() => {
    const revoked: string[] = [];
    let cancelled = false;

    const loadImages = async () => {
      const imageAttachments = (review.attachments ?? []).filter((att) => att.fileType === 'IMAGE');
      if (imageAttachments.length === 0) {
        setImageUrls([]);
        return;
      }

      try {
        const blobs = await Promise.all(
          imageAttachments.map(async (att) => {
            const requestUrl = att.fileUrl.startsWith('/api/') ? att.fileUrl.replace(/^\/api/, '') : att.fileUrl;
            const response = await api.get(requestUrl, { responseType: 'blob' });
            const blobUrl = URL.createObjectURL(response.data);
            revoked.push(blobUrl);
            return blobUrl;
          })
        );

        if (!cancelled) setImageUrls(blobs);
      } catch {
        if (!cancelled) setImageUrls([]);
      }
    };

    void loadImages();

    return () => {
      cancelled = true;
      revoked.forEach((url) => URL.revokeObjectURL(url));
    };
  }, [review.attachments]);

  return (
    <article className="rounded-xl border border-outline-variant/40 bg-white p-6 shadow-sm">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h3 className="text-lg font-semibold text-on-surface">{review.customerName}</h3>
          <p className="text-xs text-on-surface-variant">
            {formatServiceType(review.serviceType)} • {new Date(review.createdAt).toLocaleString('vi-VN')}
          </p>
        </div>
        <span
          className={`rounded-full px-3 py-1 text-xs font-semibold ${
            review.status === 'DA_HIEN_THI' ? 'bg-mint-green text-on-secondary-container' : 'bg-error-container text-error'
          }`}
        >
          {review.status === 'DA_HIEN_THI' ? 'Đang hiển thị' : 'Bị ẩn'}
        </span>
      </div>

      <div className="mt-3 flex items-center gap-1 text-amber-500">
        {Array.from({ length: 5 }).map((_, i) => (
          <Star key={i} size={16} fill={i < review.rating ? 'currentColor' : 'none'} />
        ))}
        <span className="ml-2 text-sm font-semibold text-on-surface">{review.rating}/5</span>
      </div>

      <p className="mt-4 text-sm leading-6 text-on-surface-variant">{review.content}</p>

      {review.aspectScores?.length > 0 && (
        <div className="mt-4 flex flex-wrap gap-2">
          {review.aspectScores.map((aspect) => (
            <span key={`${aspect.aspect}-${aspect.score}`} className="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">
              {aspectLabel(aspect.aspect)}: {aspect.score}/5
            </span>
          ))}
        </div>
      )}

      {imageUrls.length > 0 && (
        <div className="mt-4 grid grid-cols-2 gap-3 sm:grid-cols-3">
          {imageUrls.map((src, index) => (
            <img key={src} src={src} alt={`review-${review.id}-${index}`} className="h-28 w-full rounded-lg object-cover border border-outline-variant/30" />
          ))}
        </div>
      )}

      {review.partnerReply && (
        <div className="mt-5 rounded-xl border border-primary/20 bg-surface-container p-4">
          <p className="text-xs font-bold uppercase tracking-wider text-primary">Phản hồi của đối tác</p>
          <p className="mt-2 text-sm text-on-surface">{review.partnerReply.content}</p>
          <p className="mt-2 text-xs text-on-surface-variant">Cập nhật: {new Date(review.partnerReply.updatedAt).toLocaleString('vi-VN')}</p>
        </div>
      )}

      <div className="mt-5 flex justify-end">
        <button type="button" onClick={() => onReply(review)} className="rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white">
          {review.partnerReply ? 'Sửa phản hồi' : 'Phản hồi'}
        </button>
      </div>
    </article>
  );
}
