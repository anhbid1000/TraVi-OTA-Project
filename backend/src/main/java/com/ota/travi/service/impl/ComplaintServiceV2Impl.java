package com.ota.travi.service.impl;

import org.springframework.web.multipart.MultipartFile;
import com.ota.travi.service.AttachmentService;
import com.ota.travi.dto.response.*;
import com.ota.travi.dto.request.*;
import com.ota.travi.entity.*;
import com.ota.travi.enums.*;
import com.ota.travi.repository.*;
import com.ota.travi.service.ComplaintServiceV2;
import com.ota.travi.service.ComplaintCompensationVoucherService;
import com.ota.travi.service.ProfanityFilterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceV2Impl implements ComplaintServiceV2 {

    private final ComplaintRepository complaintRepository;
    private final ComplaintMessageRepository messageRepository;
    private final ComplaintResolutionActionRepository resolutionActionRepository;
    private final ComplaintActivityRepository activityRepository;
    private final KhachHangRepository khachHangRepository;
    private final HoSoKinhDoanhRepository hoSoKinhDoanhRepository;
    private final DoiTacRepository doiTacRepository;
    private final DonKhachSanRepository donKhachSanRepository;
    private final DonNhaHangRepository donNhaHangRepository;
    private final ProfanityFilterService filterService;
    private final AttachmentService attachmentService;
    private final ComplaintCompensationVoucherService complaintCompensationVoucherService;

    public ComplaintServiceV2Impl(
            ComplaintRepository complaintRepository,
            ComplaintMessageRepository messageRepository,
            ComplaintResolutionActionRepository resolutionActionRepository,
            ComplaintActivityRepository activityRepository,
            KhachHangRepository khachHangRepository,
            HoSoKinhDoanhRepository hoSoKinhDoanhRepository,
            DoiTacRepository doiTacRepository,
            DonKhachSanRepository donKhachSanRepository,
            DonNhaHangRepository donNhaHangRepository,
            ProfanityFilterService filterService,
            AttachmentService attachmentService,
            ComplaintCompensationVoucherService complaintCompensationVoucherService
    ) {
        this.complaintRepository = complaintRepository;
        this.messageRepository = messageRepository;
        this.resolutionActionRepository = resolutionActionRepository;
        this.activityRepository = activityRepository;
        this.khachHangRepository = khachHangRepository;
        this.hoSoKinhDoanhRepository = hoSoKinhDoanhRepository;
        this.doiTacRepository = doiTacRepository;
        this.donKhachSanRepository = donKhachSanRepository;
        this.donNhaHangRepository = donNhaHangRepository;
        this.filterService = filterService;
        this.attachmentService = attachmentService;
        this.complaintCompensationVoucherService = complaintCompensationVoucherService;
    }

    // --- Helpers cho Activity & Message ---
    private void logActivity(Complaint complaint, ComplaintActivityType type, String actorId, ComplaintActorRole role, String summary, Map<String, Object> metadataMap) {
        ComplaintActivity activity = new ComplaintActivity();
        activity.setComplaint(complaint);
        activity.setActivityType(type);
        activity.setActorId(actorId);
        activity.setActorRole(role);
        activity.setSummary(summary);
        
        if (metadataMap != null && !metadataMap.isEmpty()) {
            activity.setMetadata(metadataMap.toString());
        }
        activityRepository.save(activity);
    }

    private void createSystemMessage(Complaint complaint, String content) {
        ComplaintMessage msg = new ComplaintMessage();
        msg.setComplaint(complaint);
        msg.setNguoiGuiId("SYSTEM");
        msg.setVaiTroNguoiGui(VaiTroTinNhan.SYSTEM);
        msg.setNoiDung(content);
        messageRepository.save(msg);
        complaint.getMessages().add(msg);
    }

    // --- 1. Service TẠO COMPLAINT ---
    @Override
    @Transactional
    public ComplaintResponse createComplaint(String customerId, ComplaintCreateRequest request, List<MultipartFile> files) {
        validateComplaintInvariant(request);
        HoSoKinhDoanh hoSoKinhDoanh = validateAndGetBusinessProfileForComplaint(customerId, request);

        filterService.kiemDuyetNgonTu(request.tieuDe());
        filterService.kiemDuyetNgonTu(request.noiDungTomTat());

        KhachHang khachHang = khachHangRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));

        Complaint complaint = new Complaint();
        complaint.setKhachHang(khachHang);
        complaint.setHoSoKinhDoanh(hoSoKinhDoanh);
        complaint.setLoaiDichVu(request.loaiDichVu());
        complaint.setBookingId(request.bookingId());
        complaint.setReservationId(request.reservationId());
        complaint.setTieuDe(request.tieuDe());
        complaint.setNoiDungTomTat(request.noiDungTomTat());
        complaint.setCategory(request.category());
        complaint.setMucDo(request.mucDo());
        complaint.setTrangThai(TrangThaiKhieuNai.CHO_PHAN_HOI);
        complaint.setLastCustomerMessageAt(LocalDateTime.now());
        Complaint savedComplaint = complaintRepository.save(complaint);

        // Activity: Created
        logActivity(savedComplaint, ComplaintActivityType.COMPLAINT_CREATED, customerId, ComplaintActorRole.KHACH_HANG, "Khách hàng tạo khiếu nại mới", null);

        ComplaintMessage initialMsg = new ComplaintMessage();
        initialMsg.setComplaint(savedComplaint);
        initialMsg.setNguoiGuiId(customerId);
        initialMsg.setVaiTroNguoiGui(VaiTroTinNhan.KHACH_HANG);
        initialMsg.setNoiDung(request.noiDungTomTat());
        messageRepository.save(initialMsg);
        savedComplaint.getMessages().add(initialMsg);

        // Activity: Customer Message Sent
        logActivity(savedComplaint, ComplaintActivityType.CUSTOMER_MESSAGE_SENT, customerId, ComplaintActorRole.KHACH_HANG, "Khách hàng gửi thông tin ban đầu", null);

        if (files != null && !files.isEmpty()) {
            attachmentService.saveComplaintAttachments(savedComplaint.getId(), customerId, "KHACH_HANG", files);
        }

        return mapToComplaintResponse(savedComplaint);
    }

    // --- 2. Service KHÁCH ĐÓNG COMPLAINT ---
    @Override
    @Transactional
    public ComplaintResponse closeComplaintByCustomer(String customerId, String complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));

        if (!complaint.getKhachHang().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }

        if (complaint.getTrangThai() != TrangThaiKhieuNai.DA_GIAI_QUYET) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được đóng ticket khi đã giải quyết xong");
        }

        complaint.setTrangThai(TrangThaiKhieuNai.DA_DONG);
        complaintRepository.save(complaint);
        
        logActivity(complaint, ComplaintActivityType.COMPLAINT_CLOSED, customerId, ComplaintActorRole.KHACH_HANG, "Khách hàng đóng khiếu nại", null);

        return mapToComplaintResponse(complaint);
    }

    // --- 3. Service KHÁCH GỬI TIN NHẮN ---
    @Override
    @Transactional
    public ComplaintResponse postCustomerMessage(String customerId, String complaintId, ComplaintMessageCreateRequest request, List<MultipartFile> files) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));

        if (!complaint.getKhachHang().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }
        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_DONG) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket khiếu nại đã đóng, không thể gửi thêm tin nhắn");
        }

        filterService.kiemDuyetNgonTu(request.noiDung());

        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_GIAI_QUYET) {
            complaint.setTrangThai(TrangThaiKhieuNai.DANG_XU_LY);
            logActivity(complaint, ComplaintActivityType.COMPLAINT_REOPENED, customerId, ComplaintActorRole.KHACH_HANG, "Khách hàng mở lại khiếu nại", null);
        }

        complaint.setLastCustomerMessageAt(LocalDateTime.now());

        ComplaintMessage message = new ComplaintMessage();
        message.setComplaint(complaint);
        message.setNguoiGuiId(customerId);
        message.setVaiTroNguoiGui(VaiTroTinNhan.KHACH_HANG);
        message.setNoiDung(request.noiDung());
        messageRepository.save(message);
        complaint.getMessages().add(message);

        logActivity(complaint, ComplaintActivityType.CUSTOMER_MESSAGE_SENT, customerId, ComplaintActorRole.KHACH_HANG, "Khách hàng gửi tin nhắn", null);

        if (files != null && !files.isEmpty()) {
            attachmentService.saveComplaintMessageAttachments(message.getId(), customerId, "KHACH_HANG", files);
        }

        return mapToComplaintResponse(complaintRepository.save(complaint));
    }

    // --- 4. Service PARTNER GỬI TIN NHẮN ---
    @Override
    @Transactional
    public ComplaintResponse postPartnerMessage(String partnerId, String complaintId, ComplaintMessageCreateRequest request, List<MultipartFile> files) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));

        if (!complaint.getHoSoKinhDoanh().getDoiTac().getId().equals(partnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }
        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_DONG) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket khiếu nại đã đóng, không thể gửi thêm tin nhắn");
        }

        filterService.kiemDuyetNgonTu(request.noiDung());

        if (complaint.getTrangThai() == TrangThaiKhieuNai.CHO_PHAN_HOI) {
            complaint.setTrangThai(TrangThaiKhieuNai.DANG_XU_LY);
        }

        complaint.setLastPartnerResponseAt(LocalDateTime.now());

        ComplaintMessage message = new ComplaintMessage();
        message.setComplaint(complaint);
        message.setNguoiGuiId(partnerId);
        message.setVaiTroNguoiGui(VaiTroTinNhan.DOI_TAC);
        message.setNoiDung(request.noiDung());
        messageRepository.save(message);
        complaint.getMessages().add(message);

        logActivity(complaint, ComplaintActivityType.PARTNER_MESSAGE_SENT, partnerId, ComplaintActorRole.DOI_TAC, "Đối tác gửi tin nhắn", null);

        if (files != null && !files.isEmpty()) {
            attachmentService.saveComplaintMessageAttachments(message.getId(), partnerId, "DOI_TAC", files);
        }

        return mapToComplaintResponse(complaintRepository.save(complaint));
    }

    // --- 5. RESOLUTION ACTIONS ---

    @Override
    @Transactional
    public ComplaintResponse createResolutionAction(String partnerId, String complaintId, ResolutionActionCreateRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));
        if (!complaint.getHoSoKinhDoanh().getDoiTac().getId().equals(partnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }
        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_DONG) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket khiếu nại đã đóng");
        }

        // Validate 1 active action only
        boolean hasActiveAction = resolutionActionRepository.existsByComplaint_IdAndStatusIn(complaintId, List.of(ComplaintResolutionActionStatus.PROPOSED, ComplaintResolutionActionStatus.IN_PROGRESS, ComplaintResolutionActionStatus.CUSTOMER_ACCEPTED));
        if (hasActiveAction) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đang có phương án xử lý chưa hoàn tất");
        }

        if ((request.actionType() == ComplaintResolutionActionType.FULL_REFUND || request.actionType() == ComplaintResolutionActionType.PARTIAL_REFUND) 
            && (request.amount() == null || request.currency() == null || request.currency().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hoàn tiền bắt buộc phải có amount và currency");
        }

        DoiTac partner = doiTacRepository.findById(partnerId).orElseThrow();

        ComplaintResolutionAction action = new ComplaintResolutionAction();
        action.setComplaint(complaint);
        action.setActionType(request.actionType());
        action.setTieuDe(request.tieuDe());
        action.setMoTa(request.moTa());
        action.setAmount(request.amount());
        action.setCurrency(request.currency());
        action.setVoucherCode(request.voucherCode());
        action.setDiscountPercent(request.discountPercent());
        action.setStatus(ComplaintResolutionActionStatus.PROPOSED);
        action.setProposedByPartner(partner);
        action.setProposedAt(LocalDateTime.now());
        action = resolutionActionRepository.save(action);

        complaint.setTrangThai(TrangThaiKhieuNai.CHO_XAC_NHAN_KHACH);
        complaintRepository.save(complaint);

        createSystemMessage(complaint, "Đối tác đã đề xuất phương án xử lý: " + request.tieuDe());
        logActivity(complaint, ComplaintActivityType.ACTION_PROPOSED, partnerId, ComplaintActorRole.DOI_TAC, "Đối tác đề xuất phương án xử lý", Map.of("actionId", action.getId()));

        return mapToComplaintResponse(complaint);
    }

    @Override
    @Transactional
    public ComplaintResponse acceptResolutionAction(String customerId, String complaintId, String actionId) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow();
        if (!complaint.getKhachHang().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }
        ComplaintResolutionAction action = resolutionActionRepository.findByIdAndComplaint_Id(actionId, complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phương án"));
        
        if (action.getStatus() != ComplaintResolutionActionStatus.PROPOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được chấp nhận phương án đang đề xuất");
        }

        action.setStatus(ComplaintResolutionActionStatus.CUSTOMER_ACCEPTED);
        action.setCustomerRespondedAt(LocalDateTime.now());
        resolutionActionRepository.save(action);

        complaint.setTrangThai(TrangThaiKhieuNai.DANG_THUC_HIEN_PHUONG_AN);
        complaintRepository.save(complaint);

        createSystemMessage(complaint, "Khách hàng đã đồng ý phương án xử lý.");
        logActivity(complaint, ComplaintActivityType.ACTION_ACCEPTED, customerId, ComplaintActorRole.KHACH_HANG, "Khách hàng đồng ý phương án xử lý", Map.of("actionId", action.getId()));

        return mapToComplaintResponse(complaint);
    }

    @Override
    @Transactional
    public ComplaintResponse rejectResolutionAction(String customerId, String complaintId, String actionId, ResolutionActionRejectRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow();
        if (!complaint.getKhachHang().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }
        ComplaintResolutionAction action = resolutionActionRepository.findByIdAndComplaint_Id(actionId, complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phương án"));
        
        if (action.getStatus() != ComplaintResolutionActionStatus.PROPOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được từ chối phương án đang đề xuất");
        }

        action.setStatus(ComplaintResolutionActionStatus.CUSTOMER_REJECTED);
        action.setCustomerResponseNote(request.customerResponseNote());
        action.setCustomerRespondedAt(LocalDateTime.now());
        resolutionActionRepository.save(action);

        complaint.setTrangThai(TrangThaiKhieuNai.DANG_XU_LY);
        complaintRepository.save(complaint);

        createSystemMessage(complaint, "Khách hàng đã từ chối phương án xử lý.");
        logActivity(complaint, ComplaintActivityType.ACTION_REJECTED, customerId, ComplaintActorRole.KHACH_HANG, "Khách hàng từ chối phương án xử lý", Map.of("actionId", action.getId()));

        return mapToComplaintResponse(complaint);
    }

    @Override
    @Transactional
    public ComplaintResponse startResolutionAction(String partnerId, String complaintId, String actionId) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow();
        if (!complaint.getHoSoKinhDoanh().getDoiTac().getId().equals(partnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }
        ComplaintResolutionAction action = resolutionActionRepository.findByIdAndComplaint_Id(actionId, complaintId).orElseThrow();
        
        if (action.getStatus() != ComplaintResolutionActionStatus.CUSTOMER_ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được bắt đầu phương án đã được khách hàng chấp nhận");
        }

        action.setStatus(ComplaintResolutionActionStatus.IN_PROGRESS);
        resolutionActionRepository.save(action);

        // Fit theo tài liệu FINAL: khi partner start action thì complaint phải ở trạng thái đang thực hiện phương án
        complaint.setTrangThai(TrangThaiKhieuNai.DANG_THUC_HIEN_PHUONG_AN);
        complaintRepository.save(complaint);

        createSystemMessage(complaint, "Đối tác bắt đầu thực hiện phương án xử lý.");
        logActivity(complaint, ComplaintActivityType.ACTION_STARTED, partnerId, ComplaintActorRole.DOI_TAC, "Đối tác bắt đầu thực hiện phương án", Map.of("actionId", action.getId()));

        return mapToComplaintResponse(complaint);
    }

    @Override
    @Transactional
    public ComplaintResponse completeResolutionAction(String partnerId, String complaintId, String actionId, ResolutionActionCompleteRequest request, List<MultipartFile> files) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow();
        if (!complaint.getHoSoKinhDoanh().getDoiTac().getId().equals(partnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thao tác trên khiếu nại này");
        }
        ComplaintResolutionAction action = resolutionActionRepository.findByIdAndComplaint_Id(actionId, complaintId).orElseThrow();
        
        if (action.getStatus() != ComplaintResolutionActionStatus.CUSTOMER_ACCEPTED && action.getStatus() != ComplaintResolutionActionStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể hoàn tất phương án ở trạng thái này");
        }

        action.setStatus(ComplaintResolutionActionStatus.COMPLETED);
        action.setPartnerCompletionNote(request.partnerCompletionNote());
        action.setCompletedAt(LocalDateTime.now());
        resolutionActionRepository.save(action);

        if (files != null && !files.isEmpty()) {
            attachmentService.saveResolutionActionAttachments(action.getId(), partnerId, "DOI_TAC", files);
        }

        complaintCompensationVoucherService.handleComplaintResolutionVoucher(
                action,
                complaint.getKhachHang().getId(),
                complaint.getId()
        );

        complaint.setTrangThai(TrangThaiKhieuNai.DA_GIAI_QUYET);
        complaintRepository.save(complaint);

        createSystemMessage(complaint, "Phương án xử lý đã được hoàn tất.");
        logActivity(complaint, ComplaintActivityType.ACTION_COMPLETED, partnerId, ComplaintActorRole.DOI_TAC, "Đối tác hoàn tất phương án", Map.of("actionId", action.getId()));
        logActivity(complaint, ComplaintActivityType.COMPLAINT_RESOLVED, "SYSTEM", ComplaintActorRole.SYSTEM, "Khiếu nại được đánh dấu đã giải quyết", null);

        return mapToComplaintResponse(complaint);
    }

    // --- 6. Service LẤY DANH SÁCH COMPLAINT CỦA KHÁCH ---
    @Override
    @Transactional
    public Page<ComplaintResponse> getCustomerComplaints(String customerId, String status, String mucDo, String category, Pageable pageable) {
        TrangThaiKhieuNai trangThai = (status != null) ? TrangThaiKhieuNai.valueOf(status) : null;
        MucDoKhieuNai doUuTien = (mucDo != null) ? MucDoKhieuNai.valueOf(mucDo) : null;
        ComplaintCategory cat = (category != null) ? ComplaintCategory.valueOf(category) : null;

        complaintRepository.findByKhachHang_Id(customerId)
                .forEach(this::reconcileComplaintStatus);

        Page<Complaint> page;
        if (trangThai != null && doUuTien != null && cat != null) {
            page = complaintRepository.findByKhachHang_IdAndTrangThaiAndMucDoAndCategory(customerId, trangThai, doUuTien, cat, pageable);
        } else if (trangThai != null && cat != null) {
            page = complaintRepository.findByKhachHang_IdAndTrangThaiAndCategory(customerId, trangThai, cat, pageable);
        } else if (doUuTien != null && cat != null) {
            page = complaintRepository.findByKhachHang_IdAndMucDoAndCategory(customerId, doUuTien, cat, pageable);
        } else if (trangThai != null && doUuTien != null) {
            page = complaintRepository.findByKhachHang_IdAndTrangThaiAndMucDo(customerId, trangThai, doUuTien, pageable);
        } else if (trangThai != null) {
            page = complaintRepository.findByKhachHang_IdAndTrangThai(customerId, trangThai, pageable);
        } else if (doUuTien != null) {
            page = complaintRepository.findByKhachHang_IdAndMucDo(customerId, doUuTien, pageable);
        } else if (cat != null) {
            page = complaintRepository.findByKhachHang_IdAndCategory(customerId, cat, pageable);
        } else {
            page = complaintRepository.findByKhachHang_Id(customerId, pageable);
        }

        return page.map(this::mapToComplaintResponse);
    }

    @Override
    @Transactional
    public Page<ComplaintResponse> getPartnerComplaints(String partnerId, String status, String mucDo, String category, Pageable pageable) {
        TrangThaiKhieuNai trangThai = (status != null) ? TrangThaiKhieuNai.valueOf(status) : null;
        MucDoKhieuNai doUuTien = (mucDo != null) ? MucDoKhieuNai.valueOf(mucDo) : null;
        ComplaintCategory cat = (category != null) ? ComplaintCategory.valueOf(category) : null;

        complaintRepository.findByHoSoKinhDoanh_DoiTac_Id(partnerId)
                .forEach(this::reconcileComplaintStatus);

        Page<Complaint> page;
        if (trangThai != null && doUuTien != null && cat != null) {
            page = complaintRepository.findByHoSoKinhDoanh_DoiTac_IdAndTrangThaiAndMucDoAndCategory(partnerId, trangThai, doUuTien, cat, pageable);
        } else if (trangThai != null && cat != null) {
            page = complaintRepository.findByHoSoKinhDoanh_DoiTac_IdAndTrangThaiAndCategory(partnerId, trangThai, cat, pageable);
        } else if (doUuTien != null && cat != null) {
            page = complaintRepository.findByHoSoKinhDoanh_DoiTac_IdAndMucDoAndCategory(partnerId, doUuTien, cat, pageable);
        } else if (trangThai != null && doUuTien != null) {
            page = complaintRepository.findByHoSoKinhDoanh_DoiTac_IdAndTrangThaiAndMucDo(partnerId, trangThai, doUuTien, pageable);
        } else if (trangThai != null) {
            page = complaintRepository.findByHoSoKinhDoanh_DoiTac_IdAndTrangThai(partnerId, trangThai, pageable);
        } else if (doUuTien != null) {
            page = complaintRepository.findByHoSoKinhDoanh_DoiTac_IdAndMucDo(partnerId, doUuTien, pageable);
        } else if (cat != null) {
            page = complaintRepository.findByHoSoKinhDoanh_DoiTac_IdAndCategory(partnerId, cat, pageable);
        } else {
            page = complaintRepository.findByHoSoKinhDoanh_DoiTac_Id(partnerId, pageable);
        }

        return page.map(this::mapToComplaintResponse);
    }

    // --- 7. Service LẤY CHI TIẾT COMPLAINT ---
    @Override
    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintDetailForCustomer(String customerId, String complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));
        if (!complaint.getKhachHang().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập");
        }
        return mapToComplaintResponse(complaint);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintDetailForPartner(String partnerId, String complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại"));
        if (!complaint.getHoSoKinhDoanh().getDoiTac().getId().equals(partnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập");
        }
        return mapToComplaintResponse(complaint);
    }

    // --- Private validation & mapping methods ---
    private void validateComplaintInvariant(ComplaintCreateRequest request) {
        if (request.loaiDichVu() == LoaiDichVu.KHACH_SAN) {
            if (request.bookingId() == null || request.bookingId().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đặt phòng khách sạn bắt buộc phải có bookingId");
            if (request.reservationId() != null && !request.reservationId().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đặt phòng khách sạn không được có reservationId");
        } else if (request.loaiDichVu() == LoaiDichVu.NHA_HANG) {
            if (request.reservationId() == null || request.reservationId().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đặt bàn nhà hàng bắt buộc phải có reservationId");
            if (request.bookingId() != null && !request.bookingId().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đặt bàn nhà hàng không được có bookingId");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại dịch vụ không hợp lệ");
        }
    }

    private HoSoKinhDoanh validateAndGetBusinessProfileForComplaint(String customerId, ComplaintCreateRequest request) {
        if (request.loaiDichVu() == LoaiDichVu.KHACH_SAN) {
            DonKhachSan donKhachSan = donKhachSanRepository.findById(request.bookingId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đặt phòng"));
            if (!donKhachSan.getKhachHang().getId().equals(customerId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền khiếu nại đơn đặt phòng này");
            return donKhachSan.getHoSoKinhDoanh();
        }
        DonNhaHang donNhaHang = donNhaHangRepository.findById(request.reservationId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đặt bàn"));
        if (!donNhaHang.getKhachHang().getId().equals(customerId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền khiếu nại đơn đặt bàn này");
        return donNhaHang.getHoSoKinhDoanh();
    }

    private boolean isOverdue(Complaint complaint) {
        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_DONG || complaint.getTrangThai() == TrangThaiKhieuNai.DA_GIAI_QUYET) return false;
        LocalDateTime custMsg = complaint.getLastCustomerMessageAt();
        LocalDateTime partMsg = complaint.getLastPartnerResponseAt();
        if (custMsg == null) return false;
        if (partMsg == null || partMsg.isBefore(custMsg)) {
            return ChronoUnit.HOURS.between(custMsg, LocalDateTime.now()) > 48;
        }
        return false;
    }

    // Tính toán lại trạng thái dựa trên context thực tế trước khi trả về
    private Complaint reconcileComplaintStatus(Complaint complaint) {
        if (complaint.getTrangThai() == TrangThaiKhieuNai.DA_DONG) {
            return complaint; // Trạng thái cứng, không đổi
        }

        TrangThaiKhieuNai resolvedStatus = complaint.getTrangThai();
        List<ComplaintResolutionAction> resolutionActions =
                resolutionActionRepository.findByComplaint_IdOrderByCreatedAtAsc(complaint.getId());

        // 1. Nếu có action -> quyết định theo action cuối cùng
        if (!resolutionActions.isEmpty()) {
            boolean hasCompleted = resolutionActions.stream()
                    .anyMatch(a -> a.getStatus() == ComplaintResolutionActionStatus.COMPLETED);
            boolean hasInProgress = resolutionActions.stream()
                    .anyMatch(a -> a.getStatus() == ComplaintResolutionActionStatus.IN_PROGRESS
                            || a.getStatus() == ComplaintResolutionActionStatus.CUSTOMER_ACCEPTED);
            boolean hasProposed = resolutionActions.stream()
                    .anyMatch(a -> a.getStatus() == ComplaintResolutionActionStatus.PROPOSED);

            if (hasCompleted) {
                resolvedStatus = TrangThaiKhieuNai.DA_GIAI_QUYET;
            } else if (hasInProgress) {
                resolvedStatus = TrangThaiKhieuNai.DANG_THUC_HIEN_PHUONG_AN;
            } else if (hasProposed) {
                resolvedStatus = TrangThaiKhieuNai.CHO_XAC_NHAN_KHACH;
            } else {
                resolvedStatus = TrangThaiKhieuNai.DANG_XU_LY;
            }
        }
        // 2. Nếu chưa có action nhưng có message từ partner -> DANG_XU_LY
        else if (complaint.getMessages().stream().anyMatch(m -> m.getVaiTroNguoiGui() == VaiTroTinNhan.DOI_TAC)) {
            resolvedStatus = TrangThaiKhieuNai.DANG_XU_LY;
        }

        if (resolvedStatus != complaint.getTrangThai()) {
            complaint.setTrangThai(resolvedStatus);
            return complaintRepository.save(complaint);
        }

        return complaint;
    }

    private ComplaintResponse mapToComplaintResponse(Complaint complaintRaw) {
        Complaint complaint = reconcileComplaintStatus(complaintRaw);

        List<ComplaintMessageResponse> msgResponses = complaint.getMessages().stream().map(msg -> {
            List<AttachmentResponse> msgAttachments = attachmentService.getAttachmentsByOwner(AttachmentOwnerType.COMPLAINT_MESSAGE, msg.getId()).stream()
                    .map(att -> new AttachmentResponse(att.getId(), com.ota.travi.constant.ApiEndpoints.BASE_PREFIX + "/attachments/" + att.getId(), att.getFileName(), att.getFileType().name(), att.getMimeType(), att.getFileSize())).collect(Collectors.toList());
            String senderName = msg.getVaiTroNguoiGui() == VaiTroTinNhan.KHACH_HANG ? complaint.getKhachHang().getHoTen() :
                    (msg.getVaiTroNguoiGui() == VaiTroTinNhan.DOI_TAC ? complaint.getHoSoKinhDoanh().getTenCoSo() : "Hệ thống");
            return new ComplaintMessageResponse(msg.getId(), msg.getVaiTroNguoiGui(), senderName, msg.getNoiDung(), msg.getCreatedAt(), msgAttachments);
        }).toList();

        List<AttachmentResponse> complaintAttachments = attachmentService.getAttachmentsByOwner(AttachmentOwnerType.COMPLAINT, complaint.getId()).stream()
                .map(att -> new AttachmentResponse(att.getId(), com.ota.travi.constant.ApiEndpoints.BASE_PREFIX + "/attachments/" + att.getId(), att.getFileName(), att.getFileType().name(), att.getMimeType(), att.getFileSize())).collect(Collectors.toList());

        List<ResolutionActionResponse> actionResponses = resolutionActionRepository.findByComplaint_IdOrderByCreatedAtAsc(complaint.getId()).stream().map(action -> {
            List<AttachmentResponse> actionAttachments = attachmentService.getAttachmentsByOwner(AttachmentOwnerType.RESOLUTION_ACTION, action.getId()).stream()
                    .map(att -> new AttachmentResponse(att.getId(), com.ota.travi.constant.ApiEndpoints.BASE_PREFIX + "/attachments/" + att.getId(), att.getFileName(), att.getFileType().name(), att.getMimeType(), att.getFileSize())).collect(Collectors.toList());
            return new ResolutionActionResponse(
                    action.getId(), action.getComplaint().getId(), action.getActionType(), action.getTieuDe(), action.getMoTa(),
                    action.getAmount(), action.getCurrency(), action.getVoucherCode(), action.getDiscountPercent(), action.getStatus(),
                    action.getProposedByPartner().getId(), action.getCustomerResponseNote(), action.getPartnerCompletionNote(),
                    action.getProposedAt(), action.getCustomerRespondedAt(), action.getCompletedAt(), action.getCreatedAt(), action.getUpdatedAt(), actionAttachments);
        }).toList();

        List<ComplaintActivityResponse> activityResponses = activityRepository.findByComplaint_IdOrderByCreatedAtAsc(complaint.getId()).stream().map(act -> 
            new ComplaintActivityResponse(act.getId(), act.getComplaint().getId(), act.getActivityType(), act.getActorId(), act.getActorRole(), act.getSummary(), act.getMetadata(), act.getCreatedAt())
        ).toList();

        return new ComplaintResponse(
                complaint.getId(), complaint.getTieuDe(), complaint.getLoaiDichVu(), complaint.getCategory(), complaint.getMucDo(), complaint.getTrangThai(),
                isOverdue(complaint), complaint.getCreatedAt(), complaint.getUpdatedAt(), complaintAttachments, msgResponses, actionResponses, activityResponses
        );
    }
}
