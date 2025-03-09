document.addEventListener("DOMContentLoaded", function () {
    const totalPriceElement = document.getElementById("totalPrice"); // Tổng tiền
    const customerPaidInput = document.getElementById("customerPaid"); // Ô nhập tiền khách đưa
    const changeAmountElement = document.getElementById("changeAmount"); // Tiền thừa
    const confirmOrderBtn1 = document.getElementById("confirmOrderBtn1"); // Nút xác nhận đơn
    const finalPriceElement = document.getElementById("finalPrice"); // Nút xác nhận đơn

    function parseCurrency(value) {
        return parseInt(value.replace(/[^\d]/g, ""), 10) || 0; // Lấy số từ chuỗi, bỏ ký tự khác
    }

    function formatCurrency(value) {
        return value.toLocaleString("vi-VN"); // Định dạng số theo kiểu 1,000,000
    }

    // Khi nhập số vào ô input
    customerPaidInput.addEventListener("input", function (event) {
        let cursorPosition = customerPaidInput.selectionStart; // Lưu vị trí con trỏ
        let oldLength = customerPaidInput.value.length;

        updateChangeAmount();

        // Giữ nguyên vị trí con trỏ sau khi format
        let newLength = customerPaidInput.value.length;
        let diff = newLength - oldLength;
        customerPaidInput.setSelectionRange(cursorPosition + diff, cursorPosition + diff);
    });

    // Khi focus vào ô input, chỉ hiển thị số (không có dấu phẩy)
    customerPaidInput.addEventListener("focus", function () {
        customerPaidInput.value = parseCurrency(customerPaidInput.value);
    });

    // Khi mất focus, định dạng lại số tiền có dấu phẩy
    customerPaidInput.addEventListener("blur", function () {
        updateChangeAmount();
    });

    // Khi load trang, disable nút nếu ô tiền khách đưa trống
    updateChangeAmount();
});
