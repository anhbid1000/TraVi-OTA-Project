# Commit Messages - AI Insights Feature

Đây là danh sách các commit cần thực hiện. Hãy chạy từng lệnh theo thứ tự dưới đây.

---

## 📌 COMMIT 1: Fix Backend SQL Schema (NOT NULL Constraint)

**File thay đổi:**
- `backend/src/main/java/com/ota/travi/service/PartnerAiInsightService.java`

**Lệnh chạy:**
```bash
git add backend/src/main/java/com/ota/travi/service/PartnerAiInsightService.java

git commit -m "fix(ai): sửa NOT NULL constraint violation cho ai_partner_insight

- Thêm cột priority vào INSERT statement (map từ severity: HIGH/MEDIUM/LOW)
- Bổ sung cột legacy: doi_tac_id, ho_so_kinh_doanh_id, description, action
- Schema DB có cột legacy bắt buộc, code chỉ insert vào cột mới → constraint violation
- Giải pháp: tương thích cả schema cũ + schema mới (hybrid schema support)
- Backend đã compile, restart, và test thành công
- Insights giờ được tạo mà không lỗi SQL"
```

---

## 📌 COMMIT 2: Vietnamize Frontend - PartnerAiInsightsPage

**File thay đổi:**
- `frontend/src/pages/partner/PartnerAiInsightsPage.tsx`

**Lệnh chạy:**
```bash
git add frontend/src/pages/partner/PartnerAiInsightsPage.tsx

git commit -m "feat(ai): Vietnamize AI Insights frontend UI (thêm dấu tiếng Việt)

- Sửa tất cả label không dấu thành có dấu:
  * 'AI health score' → 'Điểm sức khỏe AI'
  * 'Doanh thu ky nay' → 'Doanh thu kỳ này'
  * 'Luot dat cho' → 'Lượt đặt chỗ'
  * 'Danh gia TB' → 'Đánh giá trung bình'
  * 'Goi y hanh dong' → 'Gợi ý hành động'

- Sửa severity labels tiếng Việt:
  * 'Uu tien cao' → 'Ưu tiên cao'
  * 'Can theo doi' → 'Cần theo dõi'
  * 'Tin hieu tot' → 'Tín hiệu tốt'

- Sửa loading/error messages:
  * 'Dang phan tich AI partner...' → 'Đang phân tích AI đối tác...'
  * 'Chua the tai AI insight' → 'Không thể tải AI insights'
  * 'Thu lai' → 'Thử lại'

- Ẩn profileSummary debug text (không cần thiết, thông tin đã có trong KPI)
- Ẩn hint modelVersion ('partner-insight-v1' là ID nội bộ)
- Giao diện sạch sẽ, chuyên nghiệp, toàn tiếng Việt có dấu"
```

---

## 📌 COMMIT 3: Vietnamize Backend - Insight Messages

**File thay đổi:**
- `backend/src/main/java/com/ota/travi/service/PartnerAiInsightService.java`

**Lệnh chạy:**
```bash
git add backend/src/main/java/com/ota/travi/service/PartnerAiInsightService.java

git commit -m "feat(ai): Vietnamize AI insight messages (thêm dấu tiếng Việt)

Backend insight titles, descriptions, recommendations đã được chuẩn hóa:

DEMAND insights:
- 'Nhu cau dang thap' → 'Nhu cầu đang thấp'
- 'Luong dat cho dang giam' → 'Lượng đặt chỗ đang giảm'

REVENUE insights:
- 'Doanh thu co xu huong giam' → 'Doanh thu có xu hướng giảm'
- 'Doanh thu tang tot' → 'Doanh thu tăng tốt'

REVIEW insights:
- 'Diem danh gia can cai thien' → 'Điểm đánh giá cần cải thiện'
- 'Tin hieu review on dinh' → 'Tín hiệu review ổn định'

OPERATIONS/MARKETING insights:
- 'Can uu tien toi uu van hanh' → 'Cần ưu tiên tối ưu vận hành'
- 'San sang day hien thi' → 'Sẵn sàng đẩy hiển thị'
- 'Hoat dong dang on dinh' → 'Hoạt động đang ổn định'

Tất cả descriptions và recommended_action cũng được thêm dấu tiếng Việt chuẩn
Khi partner xem insights, họ sẽ thấy text chuyên nghiệp, dễ đọc"
```

---

## 📌 COMMIT 4: Fix Header Layout + Add README

**File thay đổi:**
- `frontend/src/pages/dashboard.css`
- `AI_INSIGHTS_PARTNER_README.md` (file mới)

**Lệnh chạy:**
```bash
git add frontend/src/pages/dashboard.css AI_INSIGHTS_PARTNER_README.md

git commit -m "fix(ai): sửa layout header + thêm README tài liệu

Layout header cải thiện:
- Sửa .partner-dashboard-header-actions từ display: grid → display: flex
- 2 nút (date range + update button) giờ xếp ngang (horizontal)
- Căn lề phải (justify-content: flex-end)
- Gap: 12px (khoảng cách nhất quán)
- Kết quả: header sạch sẽ, cân đối, chuyên nghiệp

README mới: AI_INSIGHTS_PARTNER_README.md
- 📋 Tổng quan chức năng AI Insights
- 🎯 Chi tiết các loại insights (DEMAND, REVENUE, REVIEW, OPERATIONS, MARKETING)
- 🛠️ Kiến trúc kỹ thuật (backend, frontend, database)
- 📊 Luồng dữ liệu từ user đến DB
- 💾 Formula tính Health Score
- 🔧 Các lỗi đã sửa + cách sửa
- 📝 Migration SQL
- 🚀 Hướng dùng cho đối tác + admin
- 🔐 Bảo mật & performance optimization
- 🐛 Edge cases handling
- 📞 Troubleshooting guide"
```

---

## 🎬 Cách Chạy Tất Cả Commits

### **Cách 1: Chạy từng commit một (An toàn nhất)**

```bash
cd "L:\UIT\Nam3_Ki2\IE303\DoAn\TraVi-OTA-Project"

# Commit 1
git add backend/src/main/java/com/ota/travi/service/PartnerAiInsightService.java
git commit -m "fix(ai): sửa NOT NULL constraint violation cho ai_partner_insight

- Thêm cột priority vào INSERT statement (map từ severity: HIGH/MEDIUM/LOW)
- Bổ sung cột legacy: doi_tac_id, ho_so_kinh_doanh_id, description, action
- Schema DB có cột legacy bắt buộc, code chỉ insert vào cột mới → constraint violation
- Giải pháp: tương thích cả schema cũ + schema mới (hybrid schema support)
- Backend đã compile, restart, và test thành công
- Insights giờ được tạo mà không lỗi SQL"

# Commit 2
git add frontend/src/pages/partner/PartnerAiInsightsPage.tsx
git commit -m "feat(ai): Vietnamize AI Insights frontend UI (thêm dấu tiếng Việt)

- Sửa tất cả label không dấu thành có dấu:
  * 'AI health score' → 'Điểm sức khỏe AI'
  * 'Doanh thu ky nay' → 'Doanh thu kỳ này'
  * 'Luot dat cho' → 'Lượt đặt chỗ'
  * 'Danh gia TB' → 'Đánh giá trung bình'

- Sửa severity labels: Uu tien cao → Ưu tiên cao, Can theo doi → Cần theo dõi
- Ẩn profileSummary debug text và modelVersion hint
- Giao diện sạch sẽ, chuyên nghiệp, toàn tiếng Việt có dấu"

# Commit 3
git add backend/src/main/java/com/ota/travi/service/PartnerAiInsightService.java
git commit -m "feat(ai): Vietnamize AI insight messages (thêm dấu tiếng Việt)

Backend insight titles, descriptions, recommendations chuẩn hóa:
- DEMAND: 'Nhu cau dang thap' → 'Nhu cầu đang thấp', 'Luong dat cho dang giam' → 'Lượng đặt chỗ đang giảm'
- REVENUE: 'Doanh thu co xu huong giam' → 'Doanh thu có xu hướng giảm'
- REVIEW: 'Diem danh gia can cai thien' → 'Điểm đánh giá cần cải thiện'
- OPERATIONS: 'Can uu tien toi uu van hanh' → 'Cần ưu tiên tối ưu vận hành'

Tất cả descriptions, recommendations cũng thêm dấu tiếng Việt chuẩn"

# Commit 4
git add frontend/src/pages/dashboard.css AI_INSIGHTS_PARTNER_README.md
git commit -m "fix(ai): sửa layout header + thêm README tài liệu

Layout header:
- .partner-dashboard-header-actions: grid → flex (ngang)
- 2 nút (date + update) giờ trên 1 dòng, cân đối, gap 12px

README: AI_INSIGHTS_PARTNER_README.md
- 📋 Tổng quan, tính năng, kiến trúc, luồng dữ liệu
- 💾 Health Score formula, SQL schema
- 🔧 Các lỗi + cách sửa, edge cases
- 📞 Troubleshooting guide"
```

---

## 🔍 Kiểm Tra Trước Khi Commit

```bash
# Xem các file thay đổi
git status

# Xem diff chi tiết
git diff backend/src/main/java/com/ota/travi/service/PartnerAiInsightService.java
git diff frontend/src/pages/partner/PartnerAiInsightsPage.tsx
git diff frontend/src/pages/dashboard.css

# Xem lịch sử commits
git log --oneline -5
```

---

## ✅ Sau Khi Commit

1. **Push code**:
   ```bash
   git push origin Latest-promotion-AI
   ```

2. **Refresh UI để verify**:
   - Mở `http://localhost:5173/partner/ai-insights`
   - Ctrl+F5 để clear cache
   - Kiểm tra:
     - ✅ Tiêu đề: "PARTNER AI" + tên khách sạn
     - ✅ Header: date + button "Cập nhật" trên 1 dòng
     - ✅ KPI cards: 4 metric với tiêu đề tiếng Việt có dấu
     - ✅ Insights: "Ưu tiên cao" + "Nhu cầu đang thấp" (không còn "Uu tien cao", "Nhu cau dang thap")

3. **Backend verify** (nếu cần):
   ```bash
   cd "L:\UIT\Nam3_Ki2\IE303\DoAn\TraVi-OTA-Project"
   docker compose logs backend | tail -50
   ```

---

## 📋 Summary Các Thay Đổi

| Commit | Loại | File | Mục Tiêu |
|--------|------|------|----------|
| 1 | Fix | Backend Service | Sửa lỗi SQL constraint |
| 2 | Feat | Frontend TSX | Vietnamize UI + ẩn debug |
| 3 | Feat | Backend Service | Vietnamize messages |
| 4 | Fix + Docs | CSS + README | Header layout + docs |

---

## 🎯 Kết Quả Mong Đợi

Sau 4 commits:
- ✅ Không còn lỗi SQL
- ✅ UI hiển thị tiếng Việt chuẩn (có dấu)
- ✅ Layout header đẹp, cân đối
- ✅ README chi tiết cho team
- ✅ Feature ready for production
