import React, { useState } from 'react';

const PartnerDashboardPage = () => {
    // 1. Quản lý danh sách trạng thái Phòng/Bàn cho sơ đồ Live Map (Task 5.2)
    const [rooms, setRooms] = useState([
        { id: 101, ten: 'Phòng 101 (Deluxe)', trangThai: 'SAP_CHECKIN', khachHang: 'Nguyễn Văn A' },
        { id: 102, ten: 'Phòng 102 (Standard)', trangThai: 'TRONG', khachHang: '' },
        { id: 103, ten: 'Phòng 103 (Suite)', trangThai: 'DANG_SUDUNG', khachHang: 'Trần Thị B' },
        { id: 104, ten: 'Bàn số 1 (4 Người)', trangThai: 'TRONG', khachHang: '' },
        { id: 105, ten: 'Bàn số 2 (2 Người)', trangThai: 'SAP_CHECKIN', khachHang: 'Lê Văn C' },
        { id: 106, ten: 'Bàn số 3 (VIP)', trangThai: 'DANG_SUDUNG', khachHang: 'Phạm Minh M' }
    ]);

    // 2. Quản lý trạng thái đóng/mở Modal xử lý Check-in nhanh từ sơ đồ
    const [selectedRoom, setSelectedRoom] = useState(null);
    const [showModal, setShowModal] = useState(false);

    // Hàm xử lý khi click vào một ô Phòng/Bàn trên sơ đồ
    const handleRoomClick = (room) => {
        if (room.trangThai === 'SAP_CHECKIN') {
            setSelectedRoom(room);
            setShowModal(true);
        } else {
            alert(`${room.ten} hiện đang ở trạng thái: ${room.trangThai === 'TRONG' ? 'Trống' : 'Đang phục vụ khách'}`);
        }
    };

    // Hàm bấm nút Xác nhận Check-in trên Modal (Đổi trạng thái từ Vàng sang Đỏ - Task 5.2)
    const handleConfirmCheckIn = () => {
        setRooms(prev => 
            prev.map(r => r.id === selectedRoom.id ? { ...r, trangThai: 'DANG_SUDUNG' } : r)
        );
        setShowModal(false);
        alert(`Đã hoàn tất Check-in cho ${selectedRoom.ten}!`);
    };

    // Hàm lấy màu sắc động dựa trên trạng thái (Task 5.2)
    const getStatusColor = (status) => {
        switch (status) {
            case 'TRONG': return '#28a745'; // Xanh lá
            case 'DANG_SUDUNG': return '#dc3545'; // Đỏ
            case 'SAP_CHECKIN': return '#ffc107'; // Vàng
            default: return '#6c757d';
        }
    };

    return (
        <div style={{ padding: '20px', fontFamily: 'Arial', maxWidth: '1000px', margin: '0 auto' }}>
            <h2 style={{ borderBottom: '2px solid #333', paddingBottom: '10px' }}>Dashboard điều hành của Đối tác</h2>

            {/* PHẦN 1: SƠ ĐỒ VẬN HÀNH TRỰC QUAN - LIVE MAP (Task 5.2) */}
            <div style={{ marginTop: '20px', marginBottom: '40px' }}>
                <h3>🗺️ Sơ đồ Phòng / Bàn thời gian thực (Live Map)</h3>
                
                {/* Chú thích màu sắc */}
                <div style={{ display: 'flex', gap: '20px', marginBottom: '15px', fontSize: '14px' }}>
                    <div><span style={{ display: 'inline-block', width: '15px', height: '15px', backgroundColor: '#28a745', marginRight: '5px', borderRadius: '3px' }}></span> Trống (Sẵn sàng)</div>
                    <div><span style={{ display: 'inline-block', width: '15px', height: '15px', backgroundColor: '#ffc107', marginRight: '5px', borderRadius: '3px' }}></span> Sắp Check-in (Click để xử lý)</div>
                    <div><span style={{ display: 'inline-block', width: '15px', height: '15px', backgroundColor: '#dc3545', marginRight: '5px', borderRadius: '3px' }}></span> Đang sử dụng</div>
                </div>

                {/* Grid hiển thị các ô vuông Phòng/Bàn */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', gap: '15px' }}>
                    {rooms.map(room => (
                        <div 
                            key={room.id}
                            onClick={() => handleRoomClick(room)}
                            style={{
                                backgroundColor: getStatusColor(room.trangThai),
                                color: room.trangThai === 'SAP_CHECKIN' ? 'black' : 'white',
                                padding: '20px',
                                borderRadius: '8px',
                                textAlign: 'center',
                                fontWeight: 'bold',
                                cursor: 'pointer',
                                boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
                                transition: 'transform 0.2s'
                            }}
                        >
                            <div style={{ fontSize: '16px' }}>{room.ten}</div>
                            <div style={{ fontSize: '12px', marginTop: '10px', fontWeight: 'normal' }}>
                                {room.trangThai === 'TRONG' && '[ Trống ]'}
                                {room.trangThai === 'DANG_SUDUNG' && '• Đang ở'}
                                {room.trangThai === 'SAP_CHECKIN' && '⏳ Chờ khách'}
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {/* PHẦN 2: BẢNG QUẢN LÝ ĐƠN HÀNG - TABLE (Task 5.1) */}
            <div>
                <h3>📋 Bảng theo dõi trạng thái đơn hàng (Mô phỏng đẩy Real-time)</h3>
                <p style={{ fontSize: '13px', color: '#666' }}>Hệ thống tự động đồng bộ hóa danh sách khi có khách đặt mới thông qua WebSocket.</p>
                
                <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px', textAlign: 'left' }}>
                    <thead>
                        <tr style={{ backgroundColor: '#f1f1f1', borderBottom: '2px solid #ddd' }}>
                            <th style={{ padding: '12px' }}>Phòng/Bàn</th>
                            <th style={{ padding: '12px' }}>Tên Khách Hàng</th>
                            <th style={{ padding: '12px' }}>Trạng thái vận hành</th>
                        </tr>
                    </thead>
                    <tbody>
                        {rooms.map(room => (
                            <tr key={room.id} style={{ borderBottom: '1px solid #ddd' }}>
                                <td style={{ padding: '12px', fontWeight: 'bold' }}>{room.ten}</td>
                                <td style={{ padding: '12px', color: room.khachHang ? '#000' : '#aaa' }}>
                                    {room.khachHang || '---'}
                                </td>
                                <td style={{ padding: '12px' }}>
                                    <span style={{
                                        padding: '4px 8px',
                                        borderRadius: '4px',
                                        fontSize: '12px',
                                        fontWeight: 'bold',
                                        backgroundColor: room.trangThai === 'TRONG' ? '#e2f0d9' : room.trangThai === 'DANG_SUDUNG' ? '#fce4d6' : '#fff2cc',
                                        color: room.trangThai === 'TRONG' ? '#385723' : room.trangThai === 'DANG_SUDUNG' ? '#c65911' : '#7f6000'
                                    }}>
                                        {room.trangThai === 'TRONG' ? 'TRỐNG' : room.trangThai === 'DANG_SUDUNG' ? 'ĐANG PHỤC VỤ' : 'CHỜ CHECK-IN'}
                                    </span>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* MODAL XỬ LÝ CHECK-IN NHANH (Task 5.2) */}
            {showModal && selectedRoom && (
                <div style={{ position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
                    <div style={{ backgroundColor: 'white', padding: '25px', borderRadius: '8px', maxWidth: '400px', width: '90%' }}>
                        <h3 style={{ margin: '0 0 15px 0', color: '#007bff' }}>🛎️ Xử lý nhận phòng/bàn nhanh</h3>
                        <p>Bạn đang thực hiện thủ tục cho: <b>{selectedRoom.ten}</b></p>
                        <p>Tên khách đặt: <b>{selectedRoom.khachHang}</b></p>
                        <p style={{ fontSize: '13px', color: '#666' }}>Hệ thống sẽ chuyển trạng thái ô sang màu <b style={{ color: 'red' }}>ĐỎ</b> và ghi nhận khách đang sử dụng dịch vụ.</p>
                        
                        <div style={{ display: 'flex', gap: '10px', marginTop: '25px' }}>
                            <button 
                                onClick={() => setShowModal(false)}
                                style={{ flex: 1, padding: '10px', backgroundColor: '#eee', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
                            >
                                Đóng lại
                            </button>
                            <button 
                                onClick={handleConfirmCheckIn}
                                style={{ flex: 1, padding: '10px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
                            >
                                Xác nhận Check-in
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PartnerDashboardPage;