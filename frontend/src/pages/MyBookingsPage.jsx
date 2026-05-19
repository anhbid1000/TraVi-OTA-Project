import React, { useState } from 'react';

const MyBookingsPage = () => {
    // 1. Khai báo trạng thái Tab hiện tại (Mặc định là UPCOMING - Sắp đi)
    const [activeTab, setActiveTab] = useState('UPCOMING');
    
    // 2. Quản lý trạng thái đóng/mở Modal xác nhận hủy đơn
    const [showCancelModal, setShowCancelModal] = useState(false);
    const [selectedBookingId, setSelectedBookingId] = useState(null);

    // Dữ liệu đơn hàng giả lập để hiển thị lên giao diện
    const [bookings, setBookings] = useState([
        { id: 1, maDon: 'TVT882A', loaiDon: 'KHACH_SAN', tenDichVu: 'Phòng Deluxe - Khách sạn Moonlight', ngay: '2026-06-01', tongTien: 1000000, trangThai: 'UPCOMING' },
        { id: 2, maDon: 'TVT104B', loaiDon: 'NHA_HANG', tenDichVu: 'Đặt bàn 4 người - Nhà hàng Hải Sản Biển Đông', ngay: '2026-05-25', tongTien: 200000, trangThai: 'UPCOMING' },
        { id: 3, maDon: 'TVT441Z', loaiDon: 'KHACH_SAN', tenDichVu: 'Phòng Suite - Khách sạn Rex', ngay: '2026-04-12', tongTien: 2500000, trangThai: 'COMPLETED' },
        { id: 4, maDon: 'TVT009X', loaiDon: 'NHA_HANG', tenDichVu: 'Đặt bàn 2 người - Nhà hàng Sky Bar', ngay: '2026-03-01', tongTien: 200000, trangThai: 'CANCELLED' }
    ]);

    // Lọc danh sách đơn hàng tương ứng với Tab đang chọn
    const filteredBookings = bookings.filter(b => b.trangThai === activeTab);

    // Hàm mở Modal khi khách bấm nút "Hủy đơn"
    const openCancelModal = (id) => {
        setSelectedBookingId(id);
        setShowCancelModal(true);
    };

    // Hàm xác nhận hủy đơn thực tế (Đổi trạng thái đơn thành CANCELLED)
    const handleConfirmCancel = () => {
        setBookings(prev => 
            prev.map(b => b.id === selectedBookingId ? { ...b, trangThai: 'CANCELLED' } : b)
        );
        setShowCancelModal(false);
        alert("Hủy đơn thành công! Hệ thống sẽ xử lý hoàn tiền (nếu có).");
    };

    return (
        <div style={{ padding: '20px', fontFamily: 'Arial', maxWidth: '800px', margin: '0 auto' }}>
            <h2>Thư mục đơn hàng của tôi</h2>
            
            {/* THIẾT LẬP 3 TAB (Task 4.3a) */}
            <div style={{ display: 'flex', borderBottom: '2px solid #ccc', marginBottom: '20px' }}>
                {['UPCOMING', 'COMPLETED', 'CANCELLED'].map((tab) => (
                    <button
                        key={tab}
                        onClick={() => setActiveTab(tab)}
                        style={{
                            flex: 1,
                            padding: '12px',
                            cursor: 'pointer',
                            border: 'none',
                            background: 'none',
                            fontWeight: 'bold',
                            fontSize: '15px',
                            color: activeTab === tab ? '#007bff' : '#555',
                            borderBottom: activeTab === tab ? '4px solid #007bff' : 'none',
                        }}
                    >
                        {tab === 'UPCOMING' ? '📅 Sắp đi' : tab === 'COMPLETED' ? '✅ Đã hoàn thành' : '❌ Đã hủy'}
                    </button>
                ))}
            </div>

            {/* DANH SÁCH ĐƠN HÀNG */}
            <div>
                {filteredBookings.length === 0 ? (
                    <p style={{ textAlign: 'center', color: '#666', marginTop: '20px' }}>Không có đơn hàng nào trong mục này.</p>
                ) : (
                    filteredBookings.map(don => (
                        <div key={don.id} style={{ border: '1px solid #ddd', padding: '15px', borderRadius: '6px', marginBottom: '15px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <div>
                                <span style={{ backgroundColor: '#eee', padding: '3px 8px', borderRadius: '4px', fontSize: '12px', fontWeight: 'bold' }}>Mã: {don.maDon}</span>
                                <h4 style={{ margin: '8px 0 4px 0' }}>{don.tenDichVu}</h4>
                                <small style={{ color: '#666' }}>Ngày sử dụng: {don.ngay}</small>
                                <div style={{ fontWeight: 'bold', color: '#e44d26', marginTop: '5px' }}>Tổng: {don.tongTien.toLocaleString()} đ</div>
                            </div>
                            
                            {/* NÚT HỦY ĐƠN (Chỉ hiển thị ở Tab Sắp Đi - Task 4.3b) */}
                            {activeTab === 'UPCOMING' && (
                                <button 
                                    onClick={() => openCancelModal(don.id)}
                                    style={{ padding: '8px 15px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
                                >
                                    Hủy đơn
                                </button>
                            )}
                        </div>
                    ))
                )}
            </div>

            {/* MODAL CẢNH BÁO PHÍ PHẠT KHI HỦY ĐƠN (Task 4.3b) */}
            {showCancelModal && (
                <div style={{ position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                    <div style={{ backgroundColor: 'white', padding: '25px', borderRadius: '8px', maxWidth: '400px', width: '90%', textAlign: 'center' }}>
                        <h3 style={{ color: '#dc3545', margin: '0 0 15px 0' }}>⚠️ Cảnh báo hủy sát giờ!</h3>
                        <p style={{ lineHeight: '1.5', color: '#333' }}>
                            Bạn có chắc chắn muốn hủy đơn hàng này không? <br/>
                            <b style={{ color: 'red' }}>Lưu ý:</b> Bạn sẽ bị trừ <b>10% phí phạt</b> dựa trên tổng giá trị đơn hàng do thực hiện hủy sát giờ quy định.
                        </p>
                        <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
                            <button 
                                onClick={() => setShowCancelModal(false)}
                                style={{ flex: 1, padding: '10px', backgroundColor: '#eee', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
                            >
                                Quay lại
                            </button>
                            <button 
                                onClick={handleConfirmCancel}
                                style={{ flex: 1, padding: '10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
                            >
                                Đồng ý hủy
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default MyBookingsPage;