import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import CountdownTimer from '../components/CountdownTimer.jsx';

const BookingPage = () => {
    const { user } = useAuth();

    // Khai báo các thông tin form cơ bản
    const [tenNguoiDat, setTenNguoiDat] = useState('');
    const [sdtNguoiDat, setSdtNguoiDat] = useState('');
    const [emailNguoiDat, setEmailNguoiDat] = useState('');
    const [voucher, setVoucher] = useState('');
    const [tienGiam, setTienGiam] = useState(0);

    // MỚI (Task 4.2): Quản lý loại đơn (KHACH_SAN hoặc NHA_HANG) để ẩn hiện giao diện
    const [loaiDon, setLoaiDon] = useState('KHACH_SAN'); 

    // Danh sách các món ăn giả lập của nhà hàng
    const [menu] = useState([
        { id: 1, tenMon: 'Bò Sốt Tiêu Xanh', gia: 150000 },
        { id: 2, tenMon: 'Gỏi Củ Hủ Dừa Tôm Thịt', gia: 120000 },
        { id: 3, tenMon: 'Lẩu Nấm Hải Sản', gia: 250000 }
    ]);

    // Lưu số lượng món ăn khách chọn (VD: { 1: 2, 2: 0, 3: 1 } nghĩa là 2 món Bò, 1 Lẩu)
    const [gioMonAn, setGioMonAn] = useState({ 1: 0, 2: 0, 3: 0 });

    // Tiền cọc mặc định: Khách sạn 1 triệu, Nhà hàng cọc bàn 200k
    const tienCocGoc = loaiDon === 'KHACH_SAN' ? 1000000 : 200000;
    const [tongTien, setTongTien] = useState(tienCocGoc);

    // Tự động điền thông tin user
    useEffect(() => {
        if (user && user.isLoggedIn) {
            setTenNguoiDat(user.hoTen);
            setSdtNguoiDat(user.sdt);
            setEmailNguoiDat(user.email);
        }
    }, [user]);

    // MỚI (Task 4.2): Tự động tính lại tổng tiền khi khách thay đổi số lượng món ăn hoặc đổi loại đơn
    useEffect(() => {
        if (loaiDon === 'KHACH_SAN') {
            setTongTien(1000000); // Khách sạn mặc định 1 triệu
        } else {
            // Tính tổng tiền món ăn khách đã chọn
            const tienMonAn = Object.keys(gioMonAn).reduce((sum, monId) => {
                const mon = menu.find(m => m.id === parseInt(monId));
                return sum + (mon ? mon.gia * gioMonAn[monId] : 0);
            }, 0);
            // Tổng = Tiền cọc bàn (200k) + Tiền món ăn
            setTongTien(200000 + tienMonAn);
        }
    }, [gioMonAn, loaiDon, menu]);

    // Hàm tăng số lượng món ăn (+)
    const tangMon = (id) => {
        setGioMonAn(prev => ({ ...prev, [id]: prev[id] + 1 }));
    };

    // Hàm giảm số lượng món ăn (-)
    const giamMon = (id) => {
        setGioMonAn(prev => ({ ...prev, [id]: prev[id] > 0 ? prev[id] - 1 : 0 }));
    };

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
            
            {/* BÊN TRÁI: Form điền thông tin và Chọn món */}
            <div style={{ flex: 2, border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
                <h2>Thông tin đặt chỗ</h2>
                
                {/* Thanh chọn loại dịch vụ */}
                <div style={{ marginBottom: '20px' }}>
                    <label style={{ marginRight: '15px', fontWeight: 'bold' }}>Bạn muốn đặt gì?</label>
                    <button 
                        onClick={() => setLoaiDon('KHACH_SAN')}
                        style={{ padding: '8px 15px', backgroundColor: loaiDon === 'KHACH_SAN' ? '#007bff' : '#eee', color: loaiDon === 'KHACH_SAN' ? 'white' : 'black', border: 'none', borderRadius: '4px', cursor: 'pointer', marginRight: '10px' }}
                    >
                        🏨 Khách Sạn
                    </button>
                    <button 
                        onClick={() => setLoaiDon('NHA_HANG')}
                        style={{ padding: '8px 15px', backgroundColor: loaiDon === 'NHA_HANG' ? '#007bff' : '#eee', color: loaiDon === 'NHA_HANG' ? 'white' : 'black', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                    >
                        🍽️ Nhà Hàng
                    </button>
                </div>

                {/* Form nhập thông tin khách */}
                <div style={{ marginBottom: '10px' }}>
                    <label>Họ và tên khách hàng:</label>
                    <input type="text" value={tenNguoiDat} onChange={(e) => setTenNguoiDat(e.target.value)} style={{ width: '100%', padding: '8px', marginTop: '5px' }} />
                </div>
                <div style={{ marginBottom: '10px' }}>
                    <label>Số điện thoại:</label>
                    <input type="text" value={sdtNguoiDat} onChange={(e) => setSdtNguoiDat(e.target.value)} style={{ width: '100%', padding: '8px', marginTop: '5px' }} />
                </div>
                <div style={{ marginBottom: '20px' }}>
                    <label>Email liên hệ:</label>
                    <input type="email" value={emailNguoiDat} onChange={(e) => setEmailNguoiDat(e.target.value)} style={{ width: '100%', padding: '8px', marginTop: '5px' }} />
                </div>

                {/* GIAO DIỆN MỚI (Task 4.2): Hiện thực Thực đơn Pre-order nếu chọn loại đơn là Nhà Hàng */}
                {loaiDon === 'NHA_HANG' && (
                    <div style={{ borderTop: '2px dashed #ccc', paddingTop: '15px', marginTop: '15px' }}>
                        <h3 style={{ color: '#007bff' }}>📋 Thực đơn đặt món trước (Pre-order)</h3>
                        <p style={{ fontSize: '13px', color: '#666' }}>Đặt món trước giúp nhà hàng chuẩn bị chu đáo hơn cho bạn.</p>
                        
                        {menu.map(mon => (
                            <div key={mon.id} style={{ display: 'flex', justifyContent: 'between', alignItems: 'center', padding: '10px 0', borderBottom: '1px solid #eee' }}>
                                <div style={{ flex: 1 }}>
                                    <div style={{ fontWeight: 'bold' }}>{mon.tenMon}</div>
                                    <div style={{ color: '#e44d26', fontSize: '14px' }}>{mon.gia.toLocaleString()} đ</div>
                                </div>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                    <button onClick={() => giamMon(mon.id)} style={{ width: '30px', height: '30px', cursor: 'pointer', fontWeight: 'bold' }}>-</button>
                                    <span style={{ minWidth: '20px', textAlign: 'center', fontWeight: 'bold' }}>{gioMonAn[mon.id]}</span>
                                    <button onClick={() => tangMon(mon.id)} style={{ width: '30px', height: '30px', cursor: 'pointer', fontWeight: 'bold' }}>+</button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                <button style={{ padding: '10px 20px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginTop: '20px', width: '100%', fontSize: '16px', fontWeight: 'bold' }}>
                    Xác nhận đặt chỗ
                </button>
            </div>

            {/* BÊN PHẢI: Giỏ hàng dính (Sticky Sidebar) */}
            <div style={{ flex: 1, border: '1px solid #28a745', padding: '20px', borderRadius: '8px', position: 'sticky', top: '20px', height: 'fit-content', backgroundColor: '#f8f9fa' }}>
                <h3 style={{ color: '#28a745' }}>Tóm tắt đơn hàng</h3>
                <hr/>
                
                <CountdownTimer />

                <p>Loại dịch vụ: <b>{loaiDon === 'KHACH_SAN' ? '🏨 Đặt phòng Khách sạn' : '🍽️ Đặt bàn Nhà hàng'}</b></p>
                <p>Giá tạm tính: {tongTien.toLocaleString()} đ</p>
                <p>Giảm giá: -{tienGiam.toLocaleString()} đ</p>
                <h4 style={{ color: 'red', fontSize: '18px' }}>Tổng thanh toán: {(tongTien - tienGiam > 0 ? tongTien - tienGiam : 0).toLocaleString()} đ</h4>
                
                <div style={{ marginTop: '20px', display: 'flex', gap: '5px' }}>
                    <input type="text" placeholder="Nhập mã voucher..." value={voucher} onChange={(e) => setVoucher(e.target.value)} style={{ padding: '6px', flex: 1 }} />
                    <button onClick={handleApplyVoucher} style={{ padding: '6px', backgroundColor: '#28a745', color: 'white', border: 'none', cursor: 'pointer' }}>Áp dụng</button>
                </div>
            </div>

        </div>
    );
};

export default BookingPage;