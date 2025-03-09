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

            if (newQuantity < 1) return; // Không cho giảm xuống 0

            // Cập nhật số lượng hiển thị
            quantityElement.innerText = newQuantity;

            // Cập nhật số lượng trong bảng sản phẩm
            let productQuantityElement = document.getElementById(`product-quantity-${orderDetailId}`);
            if (productQuantityElement) {
                productQuantityElement.innerText = parseInt(productQuantityElement.innerText) - (isIncrease ? 1 : -1);
            }

            // Lấy giá sản phẩm từ `data-price`
            let itemPrice = parseFloat(form.closest(".d-flex").querySelector(".text-danger").getAttribute("data-price"));
            let newTotalPrice = parseFloat(totalPriceElement.getAttribute("data-total-price")) + (isIncrease ? itemPrice : -itemPrice);
            totalPriceElement.setAttribute("data-total-price", newTotalPrice);
            totalPriceElement.innerText = newTotalPrice.toLocaleString("vi-VN") + " VND";

            // Cập nhật tổng số tiền cần thanh toán
            let discountSelect = document.querySelector("select[name='promotionId']");
            updateTotalPrice(discountSelect);

            // Cập nhật tiền thừa nếu khách đã nhập tiền
            calculateChange();

            // Gửi AJAX để cập nhật số lượng trên server
            updateOrderQuantity(orderDetailId, newQuantity);
        });
    });

    // Cập nhật tiền thừa khi khách nhập số tiền đưa
    document.getElementById("customerPaid").addEventListener("input", calculateChange);
});

// Hàm tính tiền thừa
function calculateChange() {
    let customerPaidInput = document.getElementById("customerPaid");
    let changeAmountElement = document.getElementById("changeAmount");
    let finalPriceElement = document.getElementById("finalPrice");

    let customerPaid = parseCurrency(customerPaidInput.value);
    let finalPrice = parseCurrency(finalPriceElement.getAttribute("data-final-price")); // Lấy giá trị chính xác

    let changeAmount = customerPaid - finalPrice;

    customerPaidInput.value = customerPaid > 0 ? formatCurrency(customerPaid) : "";
    changeAmountElement.innerText = (changeAmount >= 0 ? formatCurrency(changeAmount) : "0 VND");
}


// Cập nhật tổng tiền sau khi áp dụng mã giảm giá



// Gửi yêu cầu AJAX cập nhật số lượng sản phẩm trên server
function updateOrderQuantity(orderDetailId, newQuantity) {
    fetch(`/admin/orders/updateQuantity/${orderDetailId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ quantity: newQuantity }),
    })
        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                alert("Cập nhật số lượng thất bại. Vui lòng thử lại!");
            }
        })
        .catch(error => console.error("Lỗi khi gửi AJAX:", error));
}

// Chuyển đổi định dạng tiền tệ
