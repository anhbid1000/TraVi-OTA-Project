import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import CountdownTimer from '../components/CountdownTimer'; // Đưa dòng này xuống đây là chuẩn bài!

const BookingPage = () => {
    // 1. Lấy thông tin user đang đăng nhập từ Context API ra (Task 4.1a)
    const { user } = useAuth();

    // Khai báo các ô nhập liệu trên Form
    const [tenNguoiDat, setTenNguoiDat] = useState('');
    const [sdtNguoiDat, setSdtNguoiDat] = useState('');
    const [emailNguoiDat, setEmailNguoiDat] = useState('');
    const [voucher, setVoucher] = useState('');
    const [tongTien, setTongTien] = useState(1000000); // Giả lập giá gốc 1 triệu
    const [tienGiam, setTienGiam] = useState(0);

    // Tự động điền (Auto-fill) thông tin nếu phát hiện user đã đăng nhập thành công
    useEffect(() => {
        if (user && user.isLoggedIn) {
            setTenNguoiDat(user.hoTen);
            setSdtNguoiDat(user.sdt);
            setEmailNguoiDat(user.email);
        }
    }, [user]);

    // Hàm bấm nút áp dụng mã giảm giá
    const handleApplyVoucher = () => {
        if (voucher.toUpperCase() === 'CHILLTRAVIOTA') {
            setTienGiam(50000);
            alert("Áp dụng mã giảm giá thành công! Bạn được giảm 50.000đ");
        } else {
            alert("Mã giảm giá không hợp lệ!");
        }
    };

    return (
        <div style={{ display: 'flex', gap: '20px', padding: '20px', fontFamily: 'Arial' }}>
            {/* BÊN TRÁI: Form điền thông tin đặt chỗ */}
            <div style={{ flex: 2, border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
                <h2>Thông tin đặt chỗ</h2>
                <div style={{ marginBottom: '10px' }}>
                    <label>Họ và tên khách hàng:</label>
                    <input 
                        type="text" 
                        value={tenNguoiDat} 
                        onChange={(e) => setTenNguoiDat(e.target.value)}
                        style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                    />
                </div>
                <div style={{ marginBottom: '10px' }}>
                    <label>Số điện thoại:</label>
                    <input 
                        type="text" 
                        value={sdtNguoiDat} 
                        onChange={(e) => setSdtNguoiDat(e.target.value)}
                        style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                    />
                </div>
                <div style={{ marginBottom: '10px' }}>
                    <label>Email liên hệ:</label>
                    <input 
                        type="email" 
                        value={emailNguoiDat} 
                        onChange={(e) => setEmailNguoiDat(e.target.value)}
                        style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                    />
                </div>
                <button style={{ padding: '10px 20px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    Xác nhận đặt chỗ
                </button>
            </div>

            {/* BÊN PHẢI: Component Giỏ hàng dính (Sticky Sidebar - Task 4.1b) */}
            <div style={{ 
                flex: 1, 
                border: '1px solid #28a745', 
                padding: '20px', 
                borderRadius: '8px', 
                position: 'sticky', 
                top: '20px', 
                height: 'fit-content',
                backgroundColor: '#f8f9fa'
            }}>
                <h3 style={{ color: '#28a745' }}>Tóm tắt đơn hàng</h3>
                <hr/>
                
                {/* ĐÃ GHÉP THÀNH CÔNG: Gọi đồng hồ đếm ngược hiển thị ở đây (Task 4.1c) */}
                <CountdownTimer />

                <p>Mặt hàng: <b>Phòng Deluxe Khách Sạn</b></p>
                <p>Giá gốc: {tongTien.toLocaleString()} đ</p>
                <p>Giảm giá: -{tienGiam.toLocaleString()} đ</p>
                <h4 style={{ color: 'red' }}>Tổng thanh toán: {(tongTien - tienGiam).toLocaleString()} đ</h4>
                
                {/* Khung nhập Voucher */}
                <div style={{ marginTop: '20px', display: 'flex', gap: '5px' }}>
                    <input 
                        type="text" 
                        placeholder="Nhập mã voucher..." 
                        value={voucher}
                        onChange={(e) => setVoucher(e.target.value)}
                        style={{ padding: '6px', flex: 1 }}
                    />
                    <button onClick={handleApplyVoucher} style={{ padding: '6px', backgroundColor: '#28a745', color: 'white', border: 'none', cursor: 'pointer' }}>
                        Áp dụng
                    </button>
                </div>
            </div>
        </div>
    );
};

export default BookingPage;