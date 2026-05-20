package com.ota.travi.service;

import com.ota.travi.dto.response.AnhResponse;
import com.ota.travi.dto.response.AdminApprovalResponse;
import com.ota.travi.dto.response.BanResponse;
import com.ota.travi.dto.response.ChinhSachResponse;
import com.ota.travi.dto.response.ComboResponse;
import com.ota.travi.dto.response.HoSoKinhDoanhResponse;
import com.ota.travi.dto.response.KhachSanResponse;
import com.ota.travi.dto.response.MonAnResponse;
import com.ota.travi.dto.response.NhaHangResponse;
import com.ota.travi.dto.response.PhongResponse;
import com.ota.travi.dto.response.TaiSanResponse;
import com.ota.travi.dto.response.ThucDonResponse;
import com.ota.travi.dto.response.TienIchKhachSanResponse;
import com.ota.travi.dto.response.TienIchNhaHangResponse;
import com.ota.travi.entity.AnhKhachSan;
import com.ota.travi.entity.AnhNhaHang;
import com.ota.travi.entity.AnhPhong;
import com.ota.travi.entity.Ban;
import com.ota.travi.entity.ChinhSach;
import com.ota.travi.entity.Combo;
import com.ota.travi.entity.HoSoKinhDoanh;
import com.ota.travi.entity.KhachSan;
import com.ota.travi.entity.MonAn;
import com.ota.travi.entity.NhaHang;
import com.ota.travi.entity.Phong;
import com.ota.travi.entity.TaiSan;
import com.ota.travi.entity.ThucDon;
import com.ota.travi.entity.TienIchKhachSan;
import com.ota.travi.entity.TienIchNhaHang;
import com.ota.travi.exception.ResourceNotFoundException;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PartnerAssetMapper {
    public HoSoKinhDoanhResponse toHoSoResponse(HoSoKinhDoanh hoSo) {
        return new HoSoKinhDoanhResponse(
                hoSo.getIdHoSo(),
                hoSo.getDoiTac().getId(),
                hoSo.getTenCoSo(),
                hoSo.getSdtLienHe(),
                hoSo.getLoaiDichVu(),
                hoSo.getMaSoThue(),
                hoSo.getGiayPhepKinhDoanh(),
                hoSo.getToaDoGPS(),
                hoSo.getTrangThaiKiemDuyet(),
                hoSo.getThoiGianDangKy(),
                hoSo.getThoiGianDuyet(),
                toChinhSachResponse(hoSo.getChinhSach()),
                hoSo.getDanhSachTaiSan().stream().map(this::toTaiSanResponse).toList()
        );
    }

    public AdminApprovalResponse toAdminApprovalResponse(HoSoKinhDoanh hoSo) {
        return new AdminApprovalResponse(
                hoSo.getIdHoSo(),
                hoSo.getDoiTac().getId(),
                hoSo.getDoiTac().getEmail(),
                hoSo.getDoiTac().getHoTen(),
                hoSo.getTenCoSo(),
                hoSo.getSdtLienHe(),
                hoSo.getLoaiDichVu(),
                hoSo.getMaSoThue(),
                hoSo.getGiayPhepKinhDoanh(),
                hoSo.getToaDoGPS(),
                hoSo.getTrangThaiKiemDuyet(),
                hoSo.getThoiGianDangKy(),
                hoSo.getThoiGianDuyet(),
                toChinhSachResponse(hoSo.getChinhSach()),
                hoSo.getDanhSachTaiSan().stream().map(this::toTaiSanResponse).toList()
        );
    }

    public KhachSanResponse toKhachSanResponse(KhachSan khachSan) {
        return new KhachSanResponse(
                khachSan.getIdTaiSan(),
                khachSan.getHoSoKinhDoanh().getIdHoSo(),
                khachSan.getTen(),
                khachSan.getMoTa(),
                khachSan.getTrangThai(),
                khachSan.getGiaCoBan(),
                khachSan.getIsDynamicPricing(),
                khachSan.getHangSao(),
                khachSan.getGioNhanPhong(),
                khachSan.getGioTraPhong(),
                khachSan.getDanhSachPhong().stream().map(this::toPhongResponse).toList(),
                khachSan.getDanhSachAnh().stream().map(this::toAnhResponse).toList(),
                khachSan.getTienIch().stream().map(this::toTienIchKhachSanResponse).collect(Collectors.toSet())
        );
    }

    public NhaHangResponse toNhaHangResponse(NhaHang nhaHang) {
        return new NhaHangResponse(
                nhaHang.getIdTaiSan(),
                nhaHang.getHoSoKinhDoanh().getIdHoSo(),
                nhaHang.getTen(),
                nhaHang.getMoTa(),
                nhaHang.getTrangThai(),
                nhaHang.getGiaCoBan(),
                nhaHang.getIsDynamicPricing(),
                nhaHang.getSucChua(),
                nhaHang.getLoaiAmThuc(),
                nhaHang.getGioMoCua(),
                nhaHang.getGioDongCua(),
                nhaHang.getDanhSachBan().stream().map(this::toBanResponse).toList(),
                nhaHang.getDanhSachAnh().stream().map(this::toAnhResponse).toList(),
                nhaHang.getTienIch().stream().map(this::toTienIchNhaHangResponse).toList(),
                nhaHang.getThucDon().stream().map(this::toThucDonResponse).toList()
        );
    }

    public PhongResponse toPhongResponse(Phong phong) {
        return new PhongResponse(
                phong.getId(),
                phong.getKhachSan().getIdTaiSan(),
                phong.getSoPhong(),
                phong.getLoaiPhong(),
                phong.getSucChuaToiDa(),
                phong.getDienTich(),
                phong.getTrangThai(),
                phong.getTienIch(),
                phong.getPhanTramGiamGia(),
                phong.getDanhSachAnh().stream().map(this::toAnhResponse).toList()
        );
    }

    public BanResponse toBanResponse(Ban ban) {
        return new BanResponse(
                ban.getId(),
                ban.getNhaHang().getIdTaiSan(),
                ban.getViTriSanh(),
                ban.getSoChoNgoi(),
                ban.getTrangThai()
        );
    }

    public MonAnResponse toMonAnResponse(MonAn monAn) {
        return new MonAnResponse(
                monAn.getId(),
                monAn.getThucDon().getId(),
                monAn.getTenMon(),
                monAn.getGiaBan(),
                monAn.getTrangThai(),
                monAn.getDuongDanUrl(),
                monAn.getTheNguCanh()
        );
    }

    private ChinhSachResponse toChinhSachResponse(ChinhSach chinhSach) {
        if (chinhSach == null) {
            throw new ResourceNotFoundException("Ho so kinh doanh chua co chinh sach");
        }
        return new ChinhSachResponse(
                chinhSach.getId(),
                chinhSach.getHoSoKinhDoanh().getIdHoSo(),
                chinhSach.getLoaiChinhSach(),
                chinhSach.getNoiDung(),
                chinhSach.getNgayApDung()
        );
    }

    private TaiSanResponse toTaiSanResponse(TaiSan taiSan) {
        return new TaiSanResponse(
                taiSan.getIdTaiSan(),
                taiSan.getHoSoKinhDoanh().getIdHoSo(),
                taiSan.getMoTa(),
                taiSan.getTrangThai(),
                taiSan.getGiaCoBan(),
                taiSan.getIsDynamicPricing()
        );
    }

    private ThucDonResponse toThucDonResponse(ThucDon thucDon) {
        return new ThucDonResponse(
                thucDon.getId(),
                thucDon.getNhaHang().getIdTaiSan(),
                thucDon.getPhanLoai(),
                thucDon.getMonAn().stream().map(this::toMonAnResponse).toList(),
                thucDon.getCombo().stream().map(this::toComboResponse).toList()
        );
    }

    private ComboResponse toComboResponse(Combo combo) {
        return new ComboResponse(
                combo.getId(),
                combo.getThucDon().getId(),
                combo.getTenCombo(),
                combo.getMoTa(),
                combo.getGiaCombo(),
                combo.getTrangThai(),
                combo.getNgayBatDau(),
                combo.getNgayKetThuc(),
                combo.getMonAn().stream().map(MonAn::getId).collect(Collectors.toSet())
        );
    }

    private AnhResponse toAnhResponse(AnhKhachSan anh) {
        return new AnhResponse(anh.getId(), anh.getKhachSan().getIdTaiSan(), anh.getDuongDanUrl(), anh.getMoTaAnh(), anh.getLaAnhDaiDien(), anh.getNgayTaiLen());
    }

    private AnhResponse toAnhResponse(AnhNhaHang anh) {
        return new AnhResponse(anh.getId(), anh.getNhaHang().getIdTaiSan(), anh.getDuongDanUrl(), anh.getMoTaAnh(), anh.getLaAnhDaiDien(), anh.getNgayTaiLen());
    }

    private AnhResponse toAnhResponse(AnhPhong anh) {
        return new AnhResponse(anh.getId(), anh.getPhong().getId(), anh.getDuongDanUrl(), anh.getMoTaAnh(), anh.getLaAnhDaiDien(), anh.getNgayTaiLen());
    }

    private TienIchKhachSanResponse toTienIchKhachSanResponse(TienIchKhachSan tienIch) {
        return new TienIchKhachSanResponse(tienIch.getId(), tienIch.getTenTienIch(), tienIch.getLoaiTienIch(), tienIch.getMoTa());
    }

    private TienIchNhaHangResponse toTienIchNhaHangResponse(TienIchNhaHang tienIch) {
        return new TienIchNhaHangResponse(
                tienIch.getId(),
                tienIch.getNhaHang().getIdTaiSan(),
                tienIch.getTenTienIch(),
                tienIch.getLoaiTienIch(),
                tienIch.getMoTa(),
                tienIch.getCoThuPhi(),
                tienIch.getPhiSuDung()
        );
    }
}
