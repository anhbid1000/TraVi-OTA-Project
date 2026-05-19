import React, { useState, useEffect } from 'react';

const CountdownTimer = () => {
    // 15 phút = 15 * 60 = 900 giây
    const [timeLeft, setTimeLeft] = useState(900); 

    useEffect(() => {
        // Nếu thời gian chạy về 0 thì dừng lại
        if (timeLeft <= 0) return;

        // Cứ mỗi 1 giây (1000ms) thì trừ đi 1 giây trong bộ đếm
        const timerId = setInterval(() => {
            setTimeLeft(prevTime => prevTime - 1);
        }, 1000);

        // Dọn dẹp bộ nhớ khi component bị đóng
        return () => clearInterval(timerId);
    }, [timeLeft]);

    // Hàm chuyển đổi số giây thành định dạng Phút:Giây (VD: 14:59)
    const formatTime = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
    };

    return (
        <div style={{
            backgroundColor: '#fff3cd',
            border: '1px solid #ffeba5',
            color: '#856404',
            padding: '10px',
            borderRadius: '4px',
            textAlign: 'center',
            marginBottom: '15px',
            fontWeight: 'bold'
        }}>
            ⏳ Đơn hàng của bạn đang được tạm giữ! <br/>
            Vui lòng thanh toán trong: <span style={{ color: '#dc3545', fontSize: '18px' }}>{formatTime(timeLeft)}</span>
        </div>
    );
};

export default CountdownTimer;