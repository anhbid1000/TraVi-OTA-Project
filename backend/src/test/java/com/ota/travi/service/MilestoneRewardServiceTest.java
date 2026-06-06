package com.ota.travi.service;

import com.ota.travi.entity.KhachHang;
import com.ota.travi.entity.LichSuDiem;
import com.ota.travi.entity.MilestoneProgress;
import com.ota.travi.enums.LoaiGiaoDichDiem;
import com.ota.travi.repository.CustomerVoucherRepository;
import com.ota.travi.repository.KhachHangRepository;
import com.ota.travi.repository.LichSuDiemRepository;
import com.ota.travi.repository.MilestoneProgressRepository;
import com.ota.travi.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MilestoneRewardServiceTest {

    @Mock
    private MilestoneProgressRepository milestoneProgressRepository;

    @Mock
    private CustomerVoucherRepository customerVoucherRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private NotificationEventService notificationEventService;

    @Mock
    private KhachHangRepository khachHangRepository;

    @Mock
    private LichSuDiemRepository lichSuDiemRepository;

    @InjectMocks
    private MilestoneRewardService service;

    private MilestoneProgress progress;
    private KhachHang customer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "milestone5Points", 100);

        progress = new MilestoneProgress();
        progress.setCustomerId("cust-1");
        progress.setCompletedBookings(4);
        progress.setMilestone5Granted(false);
        progress.setMilestone10Granted(false);
        progress.setMilestone20Granted(false);

        customer = new KhachHang();
        customer.setId("cust-1");
        customer.setDiemThanhVien(50);
    }

    @Test
    void incrementBookingCount_GrantsMilestone5PointsOnce() {
        when(milestoneProgressRepository.findByCustomerIdForUpdate("cust-1")).thenReturn(Optional.of(progress));
        when(milestoneProgressRepository.save(any(MilestoneProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(khachHangRepository.findByIdForUpdate("cust-1")).thenReturn(Optional.of(customer));

        service.incrementBookingCount("cust-1");

        assertEquals(5, progress.getCompletedBookings());
        assertTrue(progress.getMilestone5Granted());
        assertEquals(150, customer.getDiemThanhVien());
        verify(lichSuDiemRepository, times(1)).save(argThat(history ->
                history.getLoaiGiaoDichDiem() == LoaiGiaoDichDiem.MILESTONE_REWARD
                        && history.getSoDiemThayDoi() == 100
                        && history.getDiemTruocGiaoDich() == 50
                        && history.getDiemSauGiaoDich() == 150
        ));
        verify(notificationEventService, times(1)).emitMilestoneRewardGranted("cust-1", 5, 100);
    }

    @Test
    void incrementBookingCount_DoesNotGrantMilestone5Again() {
        progress.setCompletedBookings(5);
        progress.setMilestone5Granted(true);
        when(milestoneProgressRepository.findByCustomerIdForUpdate("cust-1")).thenReturn(Optional.of(progress));
        when(milestoneProgressRepository.save(any(MilestoneProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.incrementBookingCount("cust-1");

        assertEquals(6, progress.getCompletedBookings());
        verifyNoInteractions(khachHangRepository, lichSuDiemRepository, notificationEventService);
    }
}
