
    document.addEventListener("DOMContentLoaded", function () {
    // Xử lý sự kiện khi bấm tăng/giảm số lượng
    document.querySelectorAll(".increase-btn, .decrease-btn").forEach(button => {
        button.addEventListener("click", function () {
            let form = this.closest(".orderForm");
            let quantityElement = form.querySelector(".quantity-text");
            let orderDetailId = form.querySelector(".orderDetailId").value;
            let isIncrease = this.classList.contains("increase-btn");
            let totalPriceElement = document.getElementById("totalPrice");
            let finalPriceElement = document.getElementById("finalPrice");

            // Chuyển đổi số lượng hiện tại
            let currentQuantity = parseInt(quantityElement.innerText);
            let newQuantity = isIncrease ? currentQuantity + 1 : currentQuantity - 1;

            if (newQuantity < 1) return;

            // Cập nhật số lượng hiển thị
            quantityElement.innerText = newQuantity;

            // Cập nhật số lượng trong bảng sản phẩm
            let productQuantityElement = document.getElementById(`product-quantity-${orderDetailId}`);
            if (productQuantityElement) {
                productQuantityElement.innerText = parseInt(productQuantityElement.innerText) - (isIncrease ? 1 : -1);
            }

            // Cập nhật tổng tiền sản phẩm
            let itemPrice = parseFloat(form.closest(".d-flex").querySelector(".text-danger").innerText.replace(/\D/g, ''));
            let newTotalPrice = parseFloat(totalPriceElement.getAttribute("data-total-price")) + (isIncrease ? itemPrice : -itemPrice);
            totalPriceElement.setAttribute("data-total-price", newTotalPrice);
            totalPriceElement.innerText = newTotalPrice.toLocaleString("vi-VN") + " VND";

            // Cập nhật tổng số tiền cần thanh toán
            let discountSelect = document.querySelector("select[name='promotionId']");
            updateTotalPrice(discountSelect);

            // Cập nhật tiền thừa nếu khách đã nhập tiền
            calculateChange();
        });
    });

    // Cập nhật tiền thừa khi khách nhập số tiền đưa
    document.getElementById("customerPaid").addEventListener("input", calculateChange);
});

    // Hàm tính tiền thừa
    function calculateChange() {
    let customerPaid = parseFloat(document.getElementById("customerPaid").value) || 0;
    let finalPrice = parseFloat(document.getElementById("finalPrice").innerText.replace(/\D/g, ''));
    let changeAmount = customerPaid - finalPrice;
    document.getElementById("changeAmount").innerText = (changeAmount >= 0 ? changeAmount.toLocaleString("vi-VN") : "0") + " VND";
}

    // Cập nhật tổng tiền sau khi áp dụng mã giảm giá
    function updateTotalPrice(select) {
    let discount = parseFloat(select.options[select.selectedIndex].getAttribute("data-discount")) || 0;
    let minValue = parseFloat(select.options[select.selectedIndex].getAttribute("data-min-value")) || 0;
    let remaining = parseInt(select.options[select.selectedIndex].getAttribute("data-remaining")) || 0;
    let totalPriceElement = document.getElementById("totalPrice");
    let totalPrice = parseFloat(totalPriceElement.getAttribute("data-total-price"));

    if (totalPrice < minValue || remaining <= 0) {
    alert("Mã giảm giá không hợp lệ! Vui lòng chọn mã khác.");
    select.value = "";
    return;
}

    let discountAmount = (totalPrice * discount) / 100;
    let finalPrice = totalPrice - discountAmount;

    document.getElementById("finalPrice").innerText = finalPrice.toLocaleString("vi-VN") + " VND";
    calculateChange();
}
