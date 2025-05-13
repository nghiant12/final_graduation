document.addEventListener("DOMContentLoaded", function () {
    updateChangeAmount()
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
        const customerPaidInput = document.getElementById("customerPaid");
        const finalPriceElement = document.getElementById("finalPrice");
        const changeAmountElement = document.getElementById("changeAmount");
        const confirmOrderBtn1 = document.getElementById("confirmOrderBtn1");

        // Lấy giá trị và chuyển đổi về số
        const customerPaid = parseCurrency(customerPaidInput.value || "0");
        const finalPrice =
            parseCurrency(finalPriceElement.getAttribute("data-final-price")) ||
            parseCurrency(finalPriceElement.textContent || "0");

        console.log("Khách đưa:", customerPaid);
        console.log("Tổng tiền cần thanh toán:", finalPrice);

        const changeAmount = customerPaid - finalPrice;

        // Hiển thị tiền thừa
        changeAmountElement.textContent = changeAmount >= 0 ? formatCurrency(changeAmount) + " VND" : "0 VND";

        // Disable nút nếu tiền khách đưa không hợp lệ hoặc không đủ
        confirmOrderBtn1.disabled = isNaN(customerPaid) || customerPaid < finalPrice;
    }


    function parseCurrency(value) {
        if (!value) return 0;
        return parseInt(value.replace(/\D/g, "")) || 0; // Chỉ lấy số
    }

    function formatCurrency(value) {
        return value.toLocaleString("vi-VN");
    }
});
