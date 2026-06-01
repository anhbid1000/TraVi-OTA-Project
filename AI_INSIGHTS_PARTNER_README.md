# AI Insights cho Đối Tác - Tài Liệu Tính Năng

## 📋 Tổng Quan

Chức năng **AI Insights** cung cấp các gợi ý hành động thông minh cho các đối tác (nhà hàng, khách sạn, resort) dựa trên phân tích dữ liệu real-time: doanh thu, lượt đặt chỗ, đánh giá khách hàng và health score.

**URL**: `/partner/ai-insights`

---

## 🎯 Tính Năng Chính

### 1. **Dashboard AI Insights**
- **Health Score (0-100)**: Đánh giá tổng thể sức khỏe kinh doanh của đối tác
- **Metric Cards** (4 chỉ số):
  - Điểm sức khỏe AI
  - Doanh thu kỳ này (% so với kỳ trước)
  - Lượt đặt chỗ (% so với kỳ trước)
  - Đánh giá trung bình (/5) + số lượng đánh giá

### 2. **Gợi Ý Hành Động (Insights)**
AI tự động phân tích 5 khía cạnh:

#### **a) Nhu Cầu (DEMAND)**
- **Cao**: Nhu cầu đang thấp (0 đơn) → Kích hoạt ưu đãi, làm mới hình ảnh
- **Trung bình**: Lượt đặt chỗ giảm >15% → Kiểm tra giá, tạo gói ưu đãi

#### **b) Doanh Thu (REVENUE)**
- **Cao**: Doanh thu giảm >15% → Ưu tiên combo, điều chỉnh giá theo ngày thấp điểm
- **Thấp**: Doanh thu tăng >20% → Duy trì giá, mở rộng upsell

#### **c) Đánh Giá (REVIEW)**
- **Cao**: Điểm trung bình < 3.8 → Xử lý feedback, cập nhật quy trình
- **Thấp**: Rating ≥ 3.8 → Dùng review tốt làm bằng chứng marketing

#### **d) Marketing (MARKETING)**
- **Thấp**: Health score ≥ 75 → Sẵn sàng đẩy hiển thị trên AI recommendations

#### **e) Vận Hành (OPERATIONS)**
- **Cao**: Health score < 50 → Cải thiện review, tồn kho, thử nghiệm ưu đãi

#### **f) Tóm Tắt (SUMMARY)**
- **Thấp**: Không bất thường → Tiếp tục theo dõi, cập nhật nội dung định kỳ

---

## 🛠️ Kiến Trúc Kỹ Thuật

### **Backend Stack**
- **Framework**: Spring Boot 4.0.5
- **Database**: PostgreSQL (schema hybrid: cột legacy + cột mới)
- **Service**: `PartnerAiInsightService.java`
- **Controller**: `PartnerAiController.java` → endpoint `/api/partners/{id}/ai-insights?days=30`

### **Database Schema**
Bảng: `ai_partner_insight`

```sql
CREATE TABLE ai_partner_insight (
  id VARCHAR(36) PRIMARY KEY,
  -- Schema cũ (legacy - vẫn bắt buộc)
  doi_tac_id VARCHAR(36) NOT NULL,
  ho_so_kinh_doanh_id VARCHAR(36),
  priority VARCHAR(20) NOT NULL,
  description VARCHAR(1000),
  action VARCHAR(1000),
  
  -- Schema mới
  partner_id VARCHAR(36) NOT NULL,
  business_profile_id VARCHAR(36),
  insight_type VARCHAR(50) NOT NULL,
  severity VARCHAR(50) NOT NULL,
  title VARCHAR(255) NOT NULL,
  message VARCHAR(1000) NOT NULL,
  recommended_action VARCHAR(1000),
  
  -- Metrics
  metric_name VARCHAR(100),
  metric_value NUMERIC(18, 2),
  
  -- Status & Audit
  status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
  model_version VARCHAR(50) DEFAULT 'partner-insight-v1',
  generated_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### **Frontend Stack**
- **Framework**: React 18 + TypeScript
- **Page**: `PartnerAiInsightsPage.tsx`
- **Icons**: lucide-react (BrainCircuit, TrendingUp, Wallet, Star, AlertTriangle, Activity, Lightbulb)
- **Styling**: `dashboard.css` (component-based)

---

## 📊 Luồng Dữ Liệu

```
1. User truy cập /partner/ai-insights
   ↓
2. Frontend gọi API: GET /api/partners/{partnerId}/ai-insights?days=30
   ↓
3. Backend (PartnerAiInsightService):
   a) Lấy business profile của partner
   b) Query metrics từ don_dat_cho (bookings, revenue)
   c) Query metrics từ review_danh_gia (ratings)
   d) Compare với kỳ trước
   e) Tính Health Score dựa trên formula
   f) Build list InsightDraft
   g) Archive insights cũ, INSERT insights mới vào DB
   h) Return insights đã lưu
   ↓
4. Frontend hiển thị:
   - Header: tên business, date range, nút cập nhật
   - KPI Cards: 4 metric chính
   - Insights List: gợi ý theo severity (HIGH/MEDIUM/LOW)
```

---

## 💾 Tính Toán Health Score

```java
score = 55 (base)
+ 12 nếu có bookings > 0
+ 12 nếu revenue growth > 10%
- 12 nếu revenue growth < -10%
+ 8 nếu booking growth > 10%
- 8 nếu booking growth < -10%
+ 10 nếu avg_rating ≥ 4.2 (có reviews)
- 12 nếu avg_rating < 3.5 (có reviews)
- 8 nếu low_reviews (rating ≤ 3) ≥ 2

Final: clamp(0, 100)
```

---

## 🔧 Các Lỗi Đã Sửa

### **Lỗi 1: NOT NULL Constraint Violation (priority)**
- **Nguyên nhân**: Schema DB có cột legacy `priority NOT NULL`, nhưng code chỉ insert vào cột mới
- **Sửa**: Thêm cột `priority` vào INSERT statement, map từ `severity`
- **File**: `PartnerAiInsightService.java` (dòng 355-373)

### **Lỗi 2: Debug Text Hiển Thị Trên UI**
- **Nguyên nhân**: `profileSummary` (text debug từ backend) được render trong header
- **Sửa**: Comment dòng `<p>{data.profileSummary}</p>`
- **File**: `PartnerAiInsightsPage.tsx` (dòng 118)

### **Lỗi 3: Tiếng Việt Không Dấu**
- **Nguyên nhân**: Text UI có sử dụng tiếng Việt không dấu (Uu tien cao, Goi y...)
- **Sửa**: Cập nhật tất cả text trong frontend + backend với dấu chuẩn
- **File**: 
  - Frontend: `PartnerAiInsightsPage.tsx` (severity labels, KPI labels, section titles)
  - Backend: `PartnerAiInsightService.java` (insight titles, descriptions, actions)

### **Lỗi 4: Layout Header Không Cân Đối**
- **Nguyên nhân**: 2 nút (date range + update button) xếp dọc (grid) thay vì ngang (flex)
- **Sửa**: Đổi `.partner-dashboard-header-actions` từ `display: grid` → `display: flex`
- **File**: `dashboard.css` (dòng 2942-2946)

---

## 📝 SQL Migration

File: `V6__ai_training_partner_insight_schema.sql`

Tạo bảng `ai_partner_insight` với các cột bắt buộc:
- `id`, `partner_id`, `insight_type`, `severity`, `title`, `message`, `model_version`, `status`, `created_at`
- Legacy: `doi_tac_id`, `ho_so_kinh_doanh_id`, `priority`, `description`, `action`

---

## 🚀 Cách Sử Dụng

### **Từ Đối Tác (Partner)**
1. Đăng nhập vào dashboard partner
2. Click menu **"AI Insight"** (sidebar)
3. Xem health score và insights
4. Click **"Cập nhật"** để refresh dữ liệu mới nhất

### **Từ Admin (Nếu cần debug)**
```bash
# Docker: xem logs
docker compose logs -f backend

# SQL: query insights cho partner
SELECT * FROM ai_partner_insight 
WHERE partner_id = '...' 
ORDER BY created_at DESC;

# SQL: xem profile
SELECT * FROM ai_partner_profile WHERE partner_id = '...';
```

---

## 🔐 Bảo Mật

- ✅ Endpoint `/api/partners/{id}/ai-insights` có xác thực JWT
- ✅ Partner chỉ xem được insights của chính mình
- ✅ Filter: `partner_id = current_partner`

---

## 📈 Performance Optimization

- **Caching**: Insights được lưu trong DB, không tính lại mỗi lần request
- **Query**: Indexes trên `partner_id`, `created_at` cho tốc độ query
- **Frontend**: Metrics cards sử dụng `useMemo()` để tránh re-render

---

## 🐛 Các Trường Hợp Edge Case

| Trường Hợp | Xử Lý |
|-----------|-------|
| Partner chưa có booking | Insight: "Nhu cầu đang thấp" (severity HIGH) |
| 0 reviews | Health score: không tính review trend |
| Negative growth | Health score trừ điểm, insight cảnh báo |
| Empty insights list | Hiển thị insight mặc định: "Hoạt động đang ổn định" |

---

## 🔄 Versioning

- **Model Version**: `partner-insight-v1` (stored in DB)
- **Khi upgrade**: Thêm version mới, cập nhật `MODEL_VERSION` constant
- **Backward Compatibility**: Insights cũ vẫn lưu được, chỉ tạo mới với version mới

---

## 📞 Support & Troubleshooting

### Q: Insights không update?
**A**: Bấm nút "Cập nhật", hoặc kiểm tra backend logs để xem có lỗi SQL không.

### Q: Health score luôn 0?
**A**: Kiểm tra dữ liệu bookings/reviews trong DB. Có thể partner chưa có đơn nào.

### Q: Lỗi "Không thể tải AI insights"?
**A**: 
- Kiểm tra API endpoint `/api/partners/{id}/ai-insights`
- Xem backend logs: `docker compose logs backend`
- Kiểm tra authentication token

---

## 📚 Tài Liệu Liên Quan

- `PartnerAiInsightService.java`: Logic AI chính
- `PartnerAiController.java`: HTTP endpoints
- `dashboard.css`: Styling components
- `PartnerAiInsightsPage.tsx`: React component
- DB migrations: `V3__ai_context_schema.sql`, `V6__ai_training_partner_insight_schema.sql`

---

**Cập nhật lần cuối**: June 1, 2026  
**Version**: 1.0.0  
**Status**: ✅ Production Ready
