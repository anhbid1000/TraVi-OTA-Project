const VIETNAMESE_DIACRITICS_REGEX = /[àáảãạăằắẳẵặâầấẩẫậđèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵ]/i
const ASCII_ONLY_REGEX = /^[\u0020-\u007E]+$/

const TEXT_REPLACEMENTS: Array<[RegExp, string]> = [
  [/\bdang cap nhat\b/gu, 'đang cập nhật'],
  [/\btrai nghiem\b/gu, 'trải nghiệm'],
  [/\bgioi thieu\b/gu, 'giới thiệu'],
  [/\bnoi bat\b/gu, 'nổi bật'],
  [/\btien ich\b/gu, 'tiện ích'],
  [/\btien nghi\b/gu, 'tiện nghi'],
  [/\bkhach san\b/gu, 'khách sạn'],
  [/\bnha hang\b/gu, 'nhà hàng'],
  [/\bresort cao cap\b/gu, 'resort cao cấp'],
  [/\bsang trong\b/gu, 'sang trọng'],
  [/\bcao cap\b/gu, 'cao cấp'],
  [/\bthu gian\b/gu, 'thư giãn'],
  [/\byen tinh\b/gu, 'yên tĩnh'],
  [/\bthoang mat\b/gu, 'thoáng mát'],
  [/\brieng tu\b/gu, 'riêng tư'],
  [/\bgia dinh\b/gu, 'gia đình'],
  [/\btrung tam\b/gu, 'trung tâm'],
  [/\bgan trung tam\b/gu, 'gần trung tâm'],
  [/\bgan bien\b/gu, 'gần biển'],
  [/\btam nhin\b/gu, 'tầm nhìn'],
  [/\bhuong bien\b/gu, 'hướng biển'],
  [/\bhuong pho\b/gu, 'hướng phố'],
  [/\bhuong vuon\b/gu, 'hướng vườn'],
  [/\btoan canh\b/gu, 'toàn cảnh'],
  [/\bban cong\b/gu, 'ban công'],
  [/\bcua so lon\b/gu, 'cửa sổ lớn'],
  [/\bam thuc viet hien dai\b/gu, 'ẩm thực Việt hiện đại'],
  [/\bam thuc viet truyen thong\b/gu, 'ẩm thực Việt truyền thống'],
  [/\bam thuc phap co dien\b/gu, 'ẩm thực Pháp cổ điển'],
  [/\bam thuc dia trung hai\b/gu, 'ẩm thực Địa Trung Hải'],
  [/\bam thuc nhat ban\b/gu, 'ẩm thực Nhật Bản'],
  [/\bam thuc fusion\b/gu, 'ẩm thực Fusion'],
  [/\bam thuc\b/gu, 'ẩm thực'],
  [/\bhai san\b/gu, 'hải sản'],
  [/\bmon nuong\b/gu, 'món nướng'],
  [/\blau\b/gu, 'lẩu'],
  [/\bca phe\b/gu, 'cà phê'],
  [/\btrang mieng\b/gu, 'tráng miệng'],
  [/\bdo uong\b/gu, 'đồ uống'],
  [/\bthuc don\b/gu, 'thực đơn'],
  [/\bmon an\b/gu, 'món ăn'],
  [/\bmo ta\b/gu, 'mô tả'],
  [/\bdia chi\b/gu, 'địa chỉ'],
  [/\bthanh pho\b/gu, 'thành phố'],
  [/\bquan\s+(\d+)\b/gu, 'quận $1'],
  [/\bphuong\s+(\d+)\b/gu, 'phường $1'],
  [/\bduong\b/gu, 'đường'],
  [/\bpho\b/gu, 'phố'],
  [/\bha noi\b/gu, 'Hà Nội'],
  [/\bda nang\b/gu, 'Đà Nẵng'],
  [/\bhoi an\b/gu, 'Hội An'],
  [/\bho chi minh\b/gu, 'Hồ Chí Minh'],
  [/\bsai gon\b/gu, 'Sài Gòn'],
  [/\bviet nam\b/gu, 'Việt Nam'],
  [/\bgio mo cua\b/gu, 'giờ mở cửa'],
  [/\bgio dong cua\b/gu, 'giờ đóng cửa'],
  [/\bgio nhan phong\b/gu, 'giờ nhận phòng'],
  [/\bgio tra phong\b/gu, 'giờ trả phòng'],
  [/\bchinh sach huy\b/gu, 'chính sách hủy'],
  [/\bchinh sach hoan tien\b/gu, 'chính sách hoàn tiền'],
  [/\bquy dinh tre em\b/gu, 'quy định trẻ em'],
  [/\bquy dinh vat nuoi\b/gu, 'quy định vật nuôi'],
  [/\bghi chu khac\b/gu, 'ghi chú khác'],
  [/\bdat ban truoc\b/gu, 'đặt bàn trước'],
  [/\bdat mon truoc\b/gu, 'đặt món trước'],
  [/\bphong rieng\b/gu, 'phòng riêng'],
  [/\bkhu vuc ngoai troi\b/gu, 'khu vực ngoài trời'],
  [/\bbai do xe\b/gu, 'bãi đỗ xe'],
  [/\bwifi mien phi\b/gu, 'Wi‑Fi miễn phí'],
  [/\ble tan 24\/7\b/gu, 'lễ tân 24/7'],
  [/\bdua don san bay\b/gu, 'đưa đón sân bay'],
  [/\bbua sang\b/gu, 'bữa sáng'],
  [/\bbao gom bua sang\b/gu, 'bao gồm bữa sáng'],
  [/\bho boi\b/gu, 'hồ bơi'],
  [/\bboi vo cuc\b/gu, 'bơi vô cực'],
  [/\bspa thu gian\b/gu, 'spa thư giãn'],
  [/\bxong hoi\b/gu, 'xông hơi'],
  [/\bphuc vu phong\b/gu, 'phục vụ phòng'],
  [/\bphong doi\b/gu, 'phòng đôi'],
  [/\bphong don\b/gu, 'phòng đơn'],
  [/\bphong gia dinh\b/gu, 'phòng gia đình'],
  [/\bgiuong doi\b/gu, 'giường đôi'],
  [/\bgiuong don\b/gu, 'giường đơn'],
  [/\bgiuong king\b/gu, 'giường king'],
  [/\bgiuong queen\b/gu, 'giường queen'],
  [/\bphong\b/gu, 'phòng'],
  [/\bkhach\b/gu, 'khách'],
  [/\bgiuong\b/gu, 'giường'],
  [/\bvoi\b/gu, 'với'],
  [/\bva\b/gu, 'và'],
  [/\bco\b/gu, 'có'],
  [/\bkhong\b/gu, 'không'],
  [/\bgan\b/gu, 'gần'],
  [/\bdep\b/gu, 'đẹp'],
  [/\bnhieu may\b/gu, 'nhiều mây'],
  [/\bmua nhe\b/gu, 'mưa nhẹ'],
  [/\bnang dep\b/gu, 'nắng đẹp'],
  [/\bthoi tiet on dinh\b/gu, 'thời tiết ổn định'],
  [/\bao khoac mong\b/gu, 'áo khoác mỏng'],
  [/\bngoai troi\b/gu, 'ngoài trời'],
  [/\btrong nha\b/gu, 'trong nhà'],
]

function normalizeWhitespace(value: string) {
  return value
    .replace(/[_]+/g, ' ')
    .replace(/\s*-\s*/g, ' - ')
    .replace(/\s+/g, ' ')
    .trim()
}

function decodeMojibake(value: string) {
  if (!/[ÃÂÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞßáºá»]/u.test(value)) {
    return value
  }

  try {
    const bytes = Uint8Array.from(Array.from(value), (char) => char.charCodeAt(0) & 0xff)
    const decoded = new TextDecoder('utf-8').decode(bytes)
    return VIETNAMESE_DIACRITICS_REGEX.test(decoded) ? decoded : value
  } catch {
    return value
  }
}

function capitalizeFirstLetter(value: string) {
  if (!value) {
    return value
  }
  return value.charAt(0).toLocaleUpperCase('vi-VN') + value.slice(1)
}

function toTitleCase(value: string) {
  return value
    .split(/(\s+|[-/(),])/)
    .map((part) => {
      if (!part || /^(\s+|[-/(),])$/u.test(part)) {
        return part
      }

      if (part.length <= 3 && part === part.toUpperCase()) {
        return part
      }

      return part.charAt(0).toLocaleUpperCase('vi-VN') + part.slice(1)
    })
    .join('')
}

export function normalizeVietnameseText(
  value?: string | null,
  options?: { titleCase?: boolean },
): string {
  if (!value) {
    return ''
  }

  let next = normalizeWhitespace(decodeMojibake(value))
  if (!next) {
    return ''
  }

  if (ASCII_ONLY_REGEX.test(next) && !VIETNAMESE_DIACRITICS_REGEX.test(next)) {
    let normalized = next.toLowerCase()
    for (const [pattern, replacement] of TEXT_REPLACEMENTS) {
      normalized = normalized.replace(pattern, replacement)
    }

    const shouldRecase = next === next.toLowerCase() || next === next.toUpperCase()
    const shouldUseTransformedText = normalized !== next.toLowerCase() || shouldRecase
    if (shouldUseTransformedText) {
      next = options?.titleCase ? toTitleCase(normalized) : capitalizeFirstLetter(normalized)
    }
  } else if (options?.titleCase) {
    next = toTitleCase(next)
  }

  return normalizeWhitespace(next)
}

function parseNumericToken(token: string) {
  const digits = token
    .replace(/\.(?=\d{3}(\D|$))/g, '')
    .replace(/,(?=\d{3}(\D|$))/g, '')
    .replace(/[^\d]/g, '')
  if (!digits) {
    return null
  }

  const parsed = Number(digits)
  return Number.isFinite(parsed) ? parsed : null
}

export function formatVnd(value?: number | null): string {
  const safeValue = Number(value)
  if (!Number.isFinite(safeValue)) {
    return '0 VNĐ'
  }

  return `${new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 0 }).format(safeValue)} VNĐ`
}

export function formatPriceRange(value?: string | null): string {
  if (!value) {
    return 'Đang cập nhật'
  }

  const matches = value.match(/\d[\d.,\s]*/g) ?? []
  const amounts = matches
    .map(parseNumericToken)
    .filter((amount): amount is number => amount !== null)

  if (amounts.length >= 2) {
    const [min, max] = [Math.min(...amounts), Math.max(...amounts)]
    return `${formatVnd(min)} - ${formatVnd(max)}`
  }

  if (amounts.length === 1) {
    return `Từ ${formatVnd(amounts[0])}`
  }

  return normalizeVietnameseText(value)
}

export function formatPriceBounds(minPrice?: number | null, maxPrice?: number | null): string {
  const min = typeof minPrice === 'number' && Number.isFinite(minPrice) && minPrice > 0 ? minPrice : null
  const max = typeof maxPrice === 'number' && Number.isFinite(maxPrice) && maxPrice > 0 ? maxPrice : null

  if (min !== null && max !== null) {
    return `${formatVnd(min)} - ${formatVnd(max)}`
  }

  if (min !== null) {
    return `Từ ${formatVnd(min)}`
  }

  if (max !== null) {
    return `Đến ${formatVnd(max)}`
  }

  return 'Chưa chọn khoảng giá'
}

export function formatTimeValue(value?: string | null): string {
  if (!value) {
    return 'Đang cập nhật'
  }

  const match = value.match(/^(\d{2}:\d{2})(?::\d{2})?$/)
  if (match) {
    return match[1]
  }

  return normalizeVietnameseText(value)
}

export function formatFilterLabel(value?: string | null): string {
  return normalizeVietnameseText(value, { titleCase: true })
}



