document.addEventListener("DOMContentLoaded", function () {
    let customerPaidInput = document.getElementById("customerPaid");
    let changeAmountElement = document.getElementById("changeAmount");
    let finalPriceElement = document.getElementById("finalPrice");
    let confirmOrderBtn1 = document.getElementById("confirmOrderBtn1");

    // Xử lý nhập liệu, chặn ký tự không hợp lệ
    customerPaidInput.addEventListener("input", function () {
        // Xóa các ký tự không phải số
        let rawValue = this.value.replace(/[^\d]/g, "");
        let customerPaid = parseCurrency(rawValue);

        // Hiển thị giá trị có định dạng số
        this.value = formatCurrency(customerPaid);

        // Tính toán tiền thừa
        updateChangeAmount();
    });

    function updateChangeAmount() {
        let customerPaid = parseCurrency(customerPaidInput.value);
        let finalPrice = parseCurrency(finalPriceElement.getAttribute("data-final-price")) || parseCurrency(finalPriceElement.textContent);

        console.log("Khách đưa:", customerPaid);
        console.log("Tổng tiền cần thanh toán:", finalPrice);

        let changeAmount = customerPaid - finalPrice;

        // Hiển thị tiền thừa
        changeAmountElement.textContent = changeAmount >= 0 ? formatCurrency(changeAmount) + " VND" : "0 VND";

        // Disable nút xác nhận nếu tiền chưa đủ
        confirmOrderBtn1.disabled = customerPaid < finalPrice;
    }

    function parseCurrency(value) {
        if (!value) return 0;
        return parseInt(value.replace(/\D/g, "")) || 0; // Chỉ lấy số
    }

    function formatCurrency(value) {
        return value.toLocaleString("vi-VN");
    }
});
