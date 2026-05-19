import React, { createContext, useContext, useState } from 'react';

// Tạo ngữ cảnh AuthContext
const AuthContext = createContext(null);

// Nhà cung cấp dữ liệu đăng nhập giả lập cho toàn bộ ứng dụng
public const AuthProvider = ({ children }) => {
    // Giả lập tình huống khách hàng đã đăng nhập tài khoản của họ từ trước
    const [user, setUser] = useState({
        id: 1,
        hoTen: "Nguyễn Văn A",
        sdt: "0901234567",
        email: "vanga@gmail.com",
        isLoggedIn: true
    });

    return (
        <AuthContext.Provider value={{ user, setUser }}>
            {children}
        </AuthContext.Provider>
    );
};

// Hàm Hook ngắn gọn để các trang khác gọi ra xài nhanh
public const useAuth = () => {
    return useContext(AuthContext);
};