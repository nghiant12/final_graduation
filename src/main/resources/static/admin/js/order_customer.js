document.addEventListener("DOMContentLoaded", function () {
    const forms = document.querySelectorAll(".orderForm");

    forms.forEach(form => {
        const orderDetailIdElement = form.querySelector(".orderDetailId");
        const quantityText = form.querySelector(".quantity-text");
        const decreaseBtn = form.querySelector(".decrease-btn");
        const increaseBtn = form.querySelector(".increase-btn");
        const url = form.dataset.url;

        if (!orderDetailIdElement || !quantityText || !decreaseBtn || !increaseBtn) {
            console.warn("⚠ Một số phần tử trong form bị thiếu, bỏ qua xử lý.");
            return;
        }

        if (!url) {
            console.error("⚠ data-url không được xác định trên form.");
            return;
        }

        const orderDetailId = orderDetailIdElement.value;

        function updateQuantity(action) {
            fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({ action: action })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Cập nhật số lượng hiển thị
                        quantityText.textContent = data.newQuantity;
                        decreaseBtn.disabled = data.newQuantity <= 1;
                        increaseBtn.disabled = data.stockRemaining === 0;

                        console.log(`✅ Cập nhật sản phẩm ID: ${data.productDetailId}, Số lượng mới: ${data.newQuantity}`);

                        // Chờ 300ms để HTML render xong trước khi cập nhật số lượng bảng sản phẩm
                        setTimeout(() => {
                            const productQuantityCell = document.getElementById(`product-quantity-${data.productDetailId}`);
                            if (productQuantityCell) {
                                productQuantityCell.textContent = data.newQuantity;
                                console.log(`✅ Đã cập nhật số lượng cho sản phẩm ID: ${data.productDetailId}`);
                            } else {
                                console.warn(`⚠ Không tìm thấy ô số lượng cho sản phẩm ID: ${data.productDetailId}`);
                            }
                        }, 300);
                    } else {
                        alert(`⚠ Lỗi: ${data.message}`);
                    }
                })
                .catch(error => console.error("Lỗi khi cập nhật số lượng:", error));
        }

        decreaseBtn.addEventListener("click", function (event) {
            event.preventDefault();
            updateQuantity("decrease");
        });

        increaseBtn.addEventListener("click", function (event) {
            event.preventDefault();
            updateQuantity("increase");
        });
    });
});
