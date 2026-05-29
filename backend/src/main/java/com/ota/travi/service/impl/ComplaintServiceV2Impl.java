package com.ota.travi.service.impl;

import com.ota.travi.dto.request.ComplaintCreateRequest;
import com.ota.travi.dto.request.ComplaintMessageCreateRequest;
import com.ota.travi.dto.request.ComplaintStatusUpdateRequest;
import com.ota.travi.dto.response.ComplaintMessageResponse;
import com.ota.travi.dto.response.ComplaintResponse;
import com.ota.travi.entity.Complaint;
import com.ota.travi.entity.ComplaintMessage;
import com.ota.travi.entity.DonKhachSan;
import com.ota.travi.entity.DonNhaHang;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.KhachHang;
import com.ota.travi.enums.*;
import com.ota.travi.repository.ComplaintMessageRepository;
import com.ota.travi.repository.ComplaintRepository;
import com.ota.travi.repository.DonKhachSanRepository;
import com.ota.travi.repository.DonNhaHangRepository;
import com.ota.travi.repository.HoSoKinhDoanhRepository;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.service.ComplaintServiceV2;
import com.ota.travi.service.ProfanityFilterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ComplaintServiceV2Impl implements ComplaintServiceV2 {

    private final ComplaintRepository complaintRepository;
    private final ComplaintMessageRepository messageRepository;
    private final KhachHangRepository khachHangRepository;
    private final HoSoKinhDoanhRepository hoSoKinhDoanhRepository;
    private final DonKhachSanRepository donKhachSanRepository;
    private final DonNhaHangRepository donNhaHangRepository;
    private final ProfanityFilterService filterService;

    public ComplaintServiceV2Impl(
            ComplaintRepository complaintRepository,
            ComplaintMessageRepository messageRepository,
            KhachHangRepository khachHangRepository,
            HoSoKinhDoanhRepository hoSoKinhDoanhRepository,
            DonKhachSanRepository donKhachSanRepository,
            DonNhaHangRepository donNhaHangRepository,
            ProfanityFilterService filterService
    ) {
        this.complaintRepository = complaintRepository;
        this.messageRepository = messageRepository;
        this.khachHangRepository = khachHangRepository;
        this.hoSoKinhDoanhRepository = hoSoKinhDoanhRepository;
        this.donKhachSanRepository = donKhachSanRepository;
        this.donNhaHangRepository = donNhaHangRepository;
        this.filterService = filterService;
    }

    // --- 1. Service TẠO COMPLAINT ---
    @Override
    @Transactional
    public ComplaintResponse createComplaint(String customerId, ComplaintCreateRequest request) {
        // 1. Validate invariant booking/reservation theo loại dịch vụ
        validateComplaintInvariant(request);

        // 2. Validate ownership + trạng thái đơn
        HoSoKinhDoanh hoSoKinhDoanh = validateAndGetBusinessProfileForComplaint(customerId, request);

        // 3. Chạy profanity filter cho tiêu đề và nội dung
        // Rule Module 5: complaint vi phạm => từ chối lưu (400)
        filterService.kiemDuyetNgonTu(request.tieuDe());
        filterService.kiemDuyetNgonTu(request.noiDungTomTat());

        // 4. Lấy khách hàng
        KhachHang khachHang = khachHangRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));

        // 5. Tạo complaint
        Complaint complaint = new Complaint();
        complaint.setKhachHang(khachHang);
        complaint.setHoSoKinhDoanh(hoSoKinhDoanh);
        complaint.setLoaiDichVu(request.loaiDichVu());
        complaint.setBookingId(request.bookingId());
        complaint.setReservationId(request.reservationId());
        complaint.setTieuDe(request.tieuDe());
        complaint.setNoiDungTomTat(request.noiDungTomTat());
        complaint.setMucDo(request.mucDo());
        complaint.setTrangThai(TrangThaiKhieuNai.CHO_PHAN_HOI);
        complaint.setLastCustomerMessageAt(LocalDateTime.now());

        Complaint savedComplaint = complaintRepository.save(complaint);

        // 6. Tạo tin nhắn đầu tiên từ khách
        ComplaintMessage initialMsg = new ComplaintMessage();
        initialMsg.setComplaint(savedComplaint);
        initialMsg.setNguoiGuiId(customerId);
        initialMsg.setVaiTroNguoiGui(VaiTroTinNhan.KHACH_HANG);
        initialMsg.setNoiDung(request.noiDungTomTat());
        messageRepository.save(initialMsg);

        savedComplaint.getMessages().add(initialMsg);
        return mapToComplaintResponse(savedComplaint);
    }

    // --- 2. Service KHÁCH ĐÓNG COMPLAINT ---
    @Override
    @Transactional
    public ComplaintResponse closeComplaintByCustomer(String customerId, String complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));

        // 1. Kiểm tra ownership
        if (!complaint.getKhachHang().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }

        // 2. Chuyển trạng thái sang DA_DONG
        complaint.setTrangThai(TrangThaiKhieuNai.DA_DONG);
        return mapToComplaintResponse(complaintRepository.save(complaint));
    }

    // --- 3. Service KHÁCH GỬI TIN NHẮN ---
    @Override
    @Transactional
    public ComplaintResponse postCustomerMessage(String customerId, String complaintId, ComplaintMessageCreateRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));

        // 1. Kiểm tra ownership
        if (!complaint.getKhachHang().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }

        // 2. Kiểm tra ticket chưa đóng
        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_DONG) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket khiếu nại đã đóng, không thể gửi thêm tin nhắn");
        }

        // 3. Chạy profanity filter
        filterService.kiemDuyetNgonTu(request.noiDung());

        // 4. Nếu partner đã đề xuất giải quyết nhưng khách gửi thêm => chuyển lại DANG_XU_LY
        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_GIAI_QUYET) {
            complaint.setTrangThai(TrangThaiKhieuNai.DANG_XU_LY);
        }

        // 5. Update timestamp tin nhắn cuối từ khách
        complaint.setLastCustomerMessageAt(LocalDateTime.now());

        // 6. Tạo tin nhắn
        ComplaintMessage message = new ComplaintMessage();
        message.setComplaint(complaint);
        message.setNguoiGuiId(customerId);
        message.setVaiTroNguoiGui(VaiTroTinNhan.KHACH_HANG);
        message.setNoiDung(request.noiDung());
        messageRepository.save(message);

        complaint.getMessages().add(message);
        return mapToComplaintResponse(complaintRepository.save(complaint));
    }

    // --- 4. Service PARTNER GỬI TIN NHẮN ---
    @Override
    @Transactional
    public ComplaintResponse postPartnerMessage(String partnerId, String complaintId, ComplaintMessageCreateRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));

        // 1. Kiểm tra partner sở hữu cơ sở
        if (!complaint.getHoSoKinhDoanh().getDoiTac().getId().equals(partnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }

        // 2. Kiểm tra ticket chưa đóng
        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_DONG) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket khiếu nại đã đóng, không thể gửi thêm tin nhắn");
        }

        // 3. Chạy profanity filter
        filterService.kiemDuyetNgonTu(request.noiDung());

        // 4. Nếu ticket đang chờ phản hồi => chuyển sang DANG_XU_LY
        if (complaint.getTrangThai() == TrangThaiKhieuNai.CHO_PHAN_HOI) {
            complaint.setTrangThai(TrangThaiKhieuNai.DANG_XU_LY);
        }

        // 5. Update timestamp tin nhắn cuối từ partner
        complaint.setLastPartnerResponseAt(LocalDateTime.now());

        // 6. Tạo tin nhắn
        ComplaintMessage message = new ComplaintMessage();
        message.setComplaint(complaint);
        message.setNguoiGuiId(partnerId);
        message.setVaiTroNguoiGui(VaiTroTinNhan.DOI_TAC);
        message.setNoiDung(request.noiDung());
        messageRepository.save(message);

        complaint.getMessages().add(message);
        return mapToComplaintResponse(complaintRepository.save(complaint));
    }

    // --- 5. Service PARTNER CẬP NHẬT TRẠNG THÁI ---
    @Override
    @Transactional
    public ComplaintResponse updateComplaintStatusByPartner(String partnerId, String complaintId, ComplaintStatusUpdateRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));

        // 1. Kiểm tra partner sở hữu cơ sở
        if (!complaint.getHoSoKinhDoanh().getDoiTac().getId().equals(partnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }

        // 2. Kiểm tra ticket chưa đóng
        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_DONG) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket khiếu nại đã đóng, không thể đổi trạng thái");
        }

        // 3. Cập nhật trạng thái
        complaint.setTrangThai(request.status());
        return mapToComplaintResponse(complaintRepository.save(complaint));
    }

    // --- 6. Service LẤY DANH SÁCH COMPLAINT CỦA KHÁCH ---
    @Override
    @Transactional(readOnly = true)
    public Page<ComplaintResponse> getCustomerComplaints(String customerId, String status, String mucDo, Pageable pageable) {
        TrangThaiKhieuNai trangThai = (status != null) ? TrangThaiKhieuNai.valueOf(status) : null;
        MucDoKhieuNai doUuTien = (mucDo != null) ? MucDoKhieuNai.valueOf(mucDo) : null;

        if (trangThai != null && doUuTien != null) {
            return complaintRepository.findByKhachHang_IdAndTrangThaiAndMucDo(customerId, trangThai, doUuTien, pageable)
                    .map(this::mapToComplaintResponse);
        } else if (trangThai != null) {
            return complaintRepository.findByKhachHang_IdAndTrangThai(customerId, trangThai, pageable)
                    .map(this::mapToComplaintResponse);
        } else if (doUuTien != null) {
            return complaintRepository.findByKhachHang_IdAndMucDo(customerId, doUuTien, pageable)
                    .map(this::mapToComplaintResponse);
        }
        return complaintRepository.findByKhachHang_Id(customerId, pageable)
                .map(this::mapToComplaintResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplaintResponse> getPartnerComplaints(String partnerId, String status, String mucDo, Pageable pageable) {
        TrangThaiKhieuNai trangThai = (status != null) ? TrangThaiKhieuNai.valueOf(status) : null;
        MucDoKhieuNai doUuTien = (mucDo != null) ? MucDoKhieuNai.valueOf(mucDo) : null;

        if (trangThai != null && doUuTien != null) {
            return complaintRepository.findByHoSoKinhDoanh_DoiTac_IdAndTrangThaiAndMucDo(partnerId, trangThai, doUuTien, pageable)
                    .map(this::mapToComplaintResponse);
        } else if (trangThai != null) {
            return complaintRepository.findByHoSoKinhDoanh_DoiTac_IdAndTrangThai(partnerId, trangThai, pageable)
                    .map(this::mapToComplaintResponse);
        } else if (doUuTien != null) {
            return complaintRepository.findByHoSoKinhDoanh_DoiTac_IdAndMucDo(partnerId, doUuTien, pageable)
                    .map(this::mapToComplaintResponse);
        }
        return complaintRepository.findByHoSoKinhDoanh_DoiTac_Id(partnerId, pageable)
                .map(this::mapToComplaintResponse);
    }


    // --- 8. Service LẤY CHI TIẾT COMPLAINT CHO KHÁCH ---
    @Override
    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintDetailForCustomer(String customerId, String complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));

        // 1. Kiểm tra ownership
        if (!complaint.getKhachHang().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập");
        }

        return mapToComplaintResponse(complaint);
    }

    // --- 9. Service LẤY CHI TIẾT COMPLAINT CHO PARTNER ---
    @Override
    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintDetailForPartner(String partnerId, String complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));

        // 1. Kiểm tra partner sở hữu cơ sở
        if (!complaint.getHoSoKinhDoanh().getDoiTac().getId().equals(partnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập");
        }

        return mapToComplaintResponse(complaint);
    }

    // --- Private validation & mapping methods ---

    private void validateComplaintInvariant(ComplaintCreateRequest request) {
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

    private HoSoKinhDoanh validateAndGetBusinessProfileForComplaint(String customerId, ComplaintCreateRequest request) {
        if (request.loaiDichVu() == LoaiDichVu.KHACH_SAN) {
            // 1. Kiểm tra booking tồn tại
            DonKhachSan donKhachSan = donKhachSanRepository.findById(request.bookingId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đặt phòng"));

            // 2. Kiểm tra ownership
            if (!donKhachSan.getKhachHang().getId().equals(customerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền khiếu nại đơn đặt phòng này");
            }

            return donKhachSan.getHoSoKinhDoanh();
        }

        // NHA_HANG
        // 1. Kiểm tra reservation tồn tại
        DonNhaHang donNhaHang = donNhaHangRepository.findById(request.reservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đặt bàn"));

        // 2. Kiểm tra ownership
        if (!donNhaHang.getKhachHang().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền khiếu nại đơn đặt bàn này");
        }

        return donNhaHang.getHoSoKinhDoanh();
    }

    private boolean isOverdue(Complaint complaint) {
        // 1. Nếu ticket đã giải quyết hoặc đóng => không tính SLA
        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_DONG || 
            complaint.getTrangThai() == TrangThaiKhieuNai.DA_GIAI_QUYET) {
            return false;
        }

        LocalDateTime custMsg = complaint.getLastCustomerMessageAt();
        LocalDateTime partMsg = complaint.getLastPartnerResponseAt();

        // 2. Nếu chưa có tin nhắn từ khách => không tính SLA
        if (custMsg == null) return false;

        // 3. Rule SLA 48h từ spec Module 5:
        // Nếu partner chưa phản hồi hoặc phản hồi trước tin nhắn cuối của khách
        // => tính từ tin nhắn cuối của khách đến bây giờ
        if (partMsg == null || partMsg.isBefore(custMsg)) {
            long hoursDiff = ChronoUnit.HOURS.between(custMsg, LocalDateTime.now());
            return hoursDiff > 48;
        }

        return false;
    }

    private ComplaintResponse mapToComplaintResponse(Complaint complaint) {
        // 1. Map danh sách tin nhắn
        List<ComplaintMessageResponse> msgResponses = complaint.getMessages().stream()
                .map(msg -> new ComplaintMessageResponse(
                        msg.getVaiTroNguoiGui(),
                        msg.getNoiDung(),
                        msg.getCreatedAt()
                )).toList();

        // 2. Tạo response với computed field overdue
        return new ComplaintResponse(
                complaint.getId(),
                complaint.getTieuDe(),
                complaint.getLoaiDichVu(),
                complaint.getMucDo(),
                complaint.getTrangThai(),
                isOverdue(complaint),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt(),
                msgResponses
        );
    }
}

