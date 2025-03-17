document.addEventListener("DOMContentLoaded", function () {
    // Xử lý sự kiện khi bấm tăng/giảm số lượng
    document.querySelectorAll(".increase-btn, .decrease-btn").forEach(button => {
        button.addEventListener("click", function () {
            let form = this.closest(".orderForm");
            let orderDetailId = form.querySelector(".orderDetailId").value;
            let action = this.classList.contains("increase-btn") ? "increase" : "decrease";

            fetch(`/admin/orders/updateQuantity/${orderDetailId}`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({ action: action })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        location.reload(); // Reload để cập nhật giao diện
                    } else {
                        alert("Lỗi: " + data.message);
                    }
                })
                .catch(error => console.error("Lỗi AJAX:", error));
        });
    });

    // Xử lý sự kiện nhập số tiền khách đưa
    let customerPaidInput = document.getElementById("customerPaid");
    if (customerPaidInput) {
        customerPaidInput.addEventListener("input", calculateChange);
    }
});

// 📌 Hàm tính tiền thừa
function calculateChange() {
    let customerPaidInput = document.getElementById("customerPaid");
    let changeAmountElement = document.getElementById("changeAmount");
    let finalPriceElement = document.getElementById("finalPrice");

    if (!customerPaidInput || !changeAmountElement || !finalPriceElement) {
        console.error("Không tìm thấy các phần tử cần thiết để tính tiền thừa.");
        return;
    }

    let customerPaid = parseCurrency(customerPaidInput.value);
    let finalPrice = parseCurrency(finalPriceElement.getAttribute("data-final-price")) || 0;
    let changeAmount = customerPaid - finalPrice;

    customerPaidInput.value = customerPaid > 0 ? formatCurrency(customerPaid) : "";
    changeAmountElement.innerText = (changeAmount >= 0 ? formatCurrency(changeAmount) : "0 VND");
}

// 📌 Hàm cập nhật tổng tiền sau khi áp dụng mã giảm giá
function updateTotalPrice(discountSelect) {
    let totalPriceElement = document.getElementById("totalPrice");
    let finalPriceElement = document.getElementById("finalPrice");

    if (!totalPriceElement || !finalPriceElement || !discountSelect) {
        console.error("Không tìm thấy các phần tử tính tổng tiền.");
        return;
    }

    let totalPrice = parseCurrency(totalPriceElement.getAttribute("data-total-price"));
    let discountPercentage = parseFloat(discountSelect.options[discountSelect.selectedIndex].getAttribute("data-discount")) || 0;

    let discountAmount = (totalPrice * discountPercentage) / 100;
    let finalPrice = totalPrice - discountAmount;

    finalPriceElement.setAttribute("data-final-price", finalPrice);
    finalPriceElement.innerText = finalPrice.toLocaleString("vi-VN") + " VND";

    // Cập nhật tiền thừa nếu khách đã nhập tiền
    calculateChange();
}

// 📌 Gửi AJAX cập nhật số lượng sản phẩm trên server
function updateOrderQuantity(orderDetailId, newQuantity) {
    fetch(`/admin/orders/updateQuantity/${orderDetailId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ action: "increase" })
        // body: new URLSearchParams({ action: "increase" })
    })
        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                alert("Cập nhật số lượng thất bại. Vui lòng thử lại!");
            }
        })
        .catch(error => console.error("Lỗi khi gửi AJAX:", error));
}

// 📌 Chuyển đổi định dạng tiền tệ
function parseCurrency(value) {
    return parseFloat(value.replace(/[^\d.-]/g, "")) || 0;
}

function formatCurrency(value) {
    return value.toLocaleString("vi-VN") + " VND";
}
