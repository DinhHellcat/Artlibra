package org.herukyatto.artlibra.backend.entity;

/**
 * Enum định nghĩa các trạng thái trong vòng đời của một Commission.
 */
public enum CommissionStatus {
    OPEN,               // Mới tạo, đang mở cho các họa sĩ ứng tuyển
    PENDING_PAYMENT,    // Khách hàng đã chọn họa sĩ, đang chờ thanh toán ký quỹ
    IN_PROGRESS,        // Đã thanh toán, họa sĩ đang làm việc
    PENDING_APPROVAL,   // Họa sĩ đã nộp sản phẩm, đang chờ khách hàng duyệt
    COMPLETED,          // Khách hàng đã chấp nhận, giao dịch hoàn thành
    CANCELLED,          // Yêu cầu đã bị hủy
    DISPUTED            // Đang có tranh chấp, cần Admin xử lý
}