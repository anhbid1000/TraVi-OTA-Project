package com.ota.travi.service.impl;

import java.util.stream.Collectors;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ota.travi.enums.AttachmentOwnerType;
import com.ota.travi.service.AttachmentService;
import com.ota.travi.dto.response.AttachmentResponse;
import com.ota.travi.dto.request.PartnerReviewReplyRequest;
import com.ota.travi.dto.request.ReviewCreateRequest;
import com.ota.travi.dto.response.ReviewReplyResponse;
import com.ota.travi.dto.response.ReviewResponse;
import com.ota.travi.entity.DoiTac;
import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.entity.DonNhaHang;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.Review;
import com.ota.travi.entity.ReviewReply;
import com.ota.travi.enums.LoaiDichVu;
import com.ota.travi.enums.TrangThaiDanhGia;
import com.ota.travi.enums.TrangThaiDon;
import com.ota.travi.repository.DoiTacRepository;
import com.ota.travi.repository.DonKhachSanRepository;
import com.ota.travi.repository.DonNhaHangRepository;
import com.ota.travi.repository.HoSoKinhDoanhRepository;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.repository.ReviewReplyRepository;
import com.ota.travi.repository.ReviewRepository;
import com.ota.travi.service.ProfanityFilterService;
import com.ota.travi.service.ReviewServiceV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewServiceV2Impl implements ReviewServiceV2 {

    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository replyRepository;
    private final DonKhachSanRepository donKhachSanRepository;
    private final DonNhaHangRepository donNhaHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final HoSoKinhDoanhRepository hoSoKinhDoanhRepository;
    private final DoiTacRepository doiTacRepository;
    private final ProfanityFilterService filterService;
    private final AttachmentService attachmentService;

    public ReviewServiceV2Impl(
            ReviewRepository reviewRepository,
            ReviewReplyRepository replyRepository,
            DonKhachSanRepository donKhachSanRepository,
            DonNhaHangRepository donNhaHangRepository,
            KhachHangRepository khachHangRepository,
            HoSoKinhDoanhRepository hoSoKinhDoanhRepository,
            DoiTacRepository doiTacRepository,
            ProfanityFilterService filterService,
            AttachmentService attachmentService
    ) {
        this.reviewRepository = reviewRepository;
        this.replyRepository = replyRepository;
        this.donKhachSanRepository = donKhachSanRepository;
        this.donNhaHangRepository = donNhaHangRepository;
        this.khachHangRepository = khachHangRepository;
        this.hoSoKinhDoanhRepository = hoSoKinhDoanhRepository;
        this.doiTacRepository = doiTacRepository;
        this.filterService = filterService;
        this.attachmentService = attachmentService;
    }

    // --- 1. Service TẠO REVIEW ---
    @Override
    @Transactional
    public ReviewResponse createReview(String customerId, ReviewCreateRequest request, List<MultipartFile> files) {
        KhachHang khachHang = khachHangRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));

        // 1. Validate invariant booking/reservation theo loại dịch vụ
        validateReviewInvariant(request);

        // 2. Validate ownership + trạng thái đơn + chống review trùng
        HoSoKinhDoanh hoSoKinhDoanh = validateAndGetBusinessProfile(customerId, request);

        // 3. Chạy profanity filter
        // Rule Module 5: review vi phạm vẫn lưu nhưng chuyển BI_AN
        TrangThaiDanhGia finalStatus = TrangThaiDanhGia.DA_HIEN_THI;
        try {
            filterService.kiemDuyetNgonTu(request.noiDung());
        } catch (ResponseStatusException ex) {
            finalStatus = TrangThaiDanhGia.BI_AN;
        }

        // 4. Tạo và lưu review
        Review review = new Review();
        review.setKhachHang(khachHang);
        review.setHoSoKinhDoanh(hoSoKinhDoanh);
        review.setLoaiDichVu(request.loaiDichVu());
        review.setBookingId(request.bookingId());
        review.setReservationId(request.reservationId());
        review.setSoSao(request.soSao());
        review.setNoiDung(request.noiDung());
        review.setTrangThai(finalStatus);

        Review saved = reviewRepository.save(review);

        // 5. Lưu danh sách file đính kèm nếu có (tối đa 5 file ảnh)
        if (files != null && !files.isEmpty()) {
            attachmentService.saveReviewAttachments(saved.getId(), customerId, "KHACH_HANG", files);
        }


        // 6. Nếu review hiển thị công khai thì cập nhật rating/reviewCount
        if (finalStatus == TrangThaiDanhGia.DA_HIEN_THI) {
            recalculateBusinessProfileRating(hoSoKinhDoanh.getIdHoSo());
        }

        return mapToReviewResponse(saved);
    }

    // --- 2. Service PARTNER REPLY REVIEW ---
    @Override
    @Transactional
    public ReviewResponse upsertPartnerReply(String partnerId, String reviewId, PartnerReviewReplyRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đánh giá"));

        // 1. Partner chỉ được reply review thuộc cơ sở của mình
        if (!review.getHoSoKinhDoanh().getDoiTac().getId().equals(partnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền phản hồi đánh giá của cơ sở khác");
        }

        // 2. Rule Module 5: partner reply vi phạm => từ chối lưu (400)
        filterService.kiemDuyetNgonTu(request.noiDung());

        // 3. Upsert reply
        ReviewReply reply = replyRepository.findByReview_Id(reviewId).orElseGet(() -> {
            ReviewReply newReply = new ReviewReply();
            newReply.setReview(review);
            DoiTac partner = doiTacRepository.findById(partnerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đối tác"));
            newReply.setPartner(partner);
            return newReply;
        });

        reply.setNoiDung(request.noiDung());
        replyRepository.save(reply);

        // 4. Đồng bộ 2 chiều để map response ổn định
        review.setReply(reply);
        return mapToReviewResponse(review);
    }

    // --- 3. Service LẤY REVIEW CÔNG KHAI ---
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getPublicReviews(String businessProfileId, Pageable pageable) {
        return reviewRepository.findByHoSoKinhDoanh_IdHoSoAndTrangThai(
                        businessProfileId,
                        TrangThaiDanhGia.DA_HIEN_THI,
                        pageable
                )
                .map(this::mapToReviewResponse);
    }

    // --- 4. Service LẤY REVIEW CỦA KHÁCH ---
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getCustomerReviews(String customerId, Pageable pageable) {
        return reviewRepository.findByKhachHang_Id(customerId, pageable)
                .map(this::mapToReviewResponse);
    }

    // --- Private validation & mapping methods ---

    private void validateReviewInvariant(ReviewCreateRequest request) {
        if (request.loaiDichVu() == LoaiDichVu.KHACH_SAN) {
            if (request.bookingId() == null || request.bookingId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đặt phòng khách sạn bắt buộc phải có bookingId");
            }
            if (request.reservationId() != null && !request.reservationId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đặt phòng khách sạn không được có reservationId");
            }
        } else if (request.loaiDichVu() == LoaiDichVu.NHA_HANG) {
            if (request.reservationId() == null || request.reservationId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đặt bàn nhà hàng bắt buộc phải có reservationId");
            }
            if (request.bookingId() != null && !request.bookingId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đặt bàn nhà hàng không được có bookingId");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại dịch vụ không hợp lệ");
        }
    }

    private HoSoKinhDoanh validateAndGetBusinessProfile(String customerId, ReviewCreateRequest request) {
        if (request.loaiDichVu() == LoaiDichVu.KHACH_SAN) {
            // 1. Chống review trùng theo booking
            if (reviewRepository.existsByKhachHang_IdAndBookingId(customerId, request.bookingId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Bạn đã đánh giá đơn đặt phòng này rồi");
            }

            // 2. Kiểm tra booking tồn tại
            DonKhachSan donKhachSan = donKhachSanRepository.findById(request.bookingId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đặt phòng"));

            // 3. Kiểm tra ownership
            if (!donKhachSan.getKhachHang().getId().equals(customerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền đánh giá đơn đặt phòng này");
            }

            // 4. Kiểm tra đơn đã hoàn tất
            if (donKhachSan.getTrangThaiDon() != TrangThaiDon.DA_HOAN_THANH) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được đánh giá khi đơn đặt phòng đã hoàn tất");
            }

            return donKhachSan.getHoSoKinhDoanh();
        }

        // NHA_HANG
        // 1. Chống review trùng theo reservation
        if (reviewRepository.existsByKhachHang_IdAndReservationId(customerId, request.reservationId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bạn đã đánh giá đơn đặt bàn này rồi");
        }

        // 2. Kiểm tra reservation tồn tại
        DonNhaHang donNhaHang = donNhaHangRepository.findById(request.reservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đặt bàn"));

        // 3. Kiểm tra ownership
        if (!donNhaHang.getKhachHang().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền đánh giá đơn đặt bàn này");
        }

        // 4. Kiểm tra đơn đã hoàn tất
        if (donNhaHang.getTrangThaiDon() != TrangThaiDon.DA_HOAN_THANH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được đánh giá khi đơn đặt bàn đã hoàn tất");
        }

        return donNhaHang.getHoSoKinhDoanh();
    }

    private void recalculateBusinessProfileRating(String businessProfileId) {
        // 1. Kiểm tra hồ sơ kinh doanh tồn tại
        HoSoKinhDoanh profile = hoSoKinhDoanhRepository.findById(businessProfileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ kinh doanh"));

        // 2. Query lại dữ liệu review hiển thị để phục vụ thống kê sau này
        long reviewCount = reviewRepository.countByHoSoKinhDoanh_IdHoSoAndTrangThai(
                businessProfileId,
                TrangThaiDanhGia.DA_HIEN_THI
        );

        double ratingAverage = reviewRepository.findByHoSoKinhDoanh_IdHoSoAndTrangThai(
                        businessProfileId,
                        TrangThaiDanhGia.DA_HIEN_THI,
                        Pageable.unpaged()
                )
                .stream()
                .mapToInt(Review::getSoSao)
                .average()
                .orElse(0.0);

        // 3. Update rating cho entity tài sản tương ứng
        profile.getDanhSachTaiSan().forEach(taiSan -> {
            taiSan.setRatingAverage(ratingAverage);
            taiSan.setReviewCount((int) reviewCount);
        });
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        ReviewReplyResponse replyResponse = null;
        if (review.getReply() != null) {
            replyResponse = new ReviewReplyResponse(
                    review.getReply().getNoiDung(),
                    review.getReply().getCreatedAt(),
                    review.getReply().getUpdatedAt()
            );
        }

        // Map danh sách file đính kèm của Review
        List<AttachmentResponse> attachmentResponses = attachmentService.getAttachmentsByOwner(AttachmentOwnerType.REVIEW, review.getId())
                .stream()
                .map(att -> new AttachmentResponse(
                        att.getId(),
                        com.ota.travi.constant.ApiEndpoints.BASE_PREFIX + "/attachments/" + att.getId(),
                        att.getFileName(),
                        att.getFileType().name(),
                        att.getMimeType(),
                        att.getFileSize()
                ))
                .collect(Collectors.toList());

        return new ReviewResponse(
                review.getId(),
                review.getKhachHang().getHoTen(),
                review.getHoSoKinhDoanh().getIdHoSo(),
                review.getLoaiDichVu(),
                review.getSoSao(),
                review.getNoiDung(),
                review.getTrangThai(),
                review.getCreatedAt(),
                attachmentResponses,
                replyResponse
        );
    }
}
