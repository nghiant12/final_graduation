document.addEventListener("DOMContentLoaded", function () {
    const forms = document.querySelectorAll(".orderForm");

    forms.forEach(form => {
        const orderDetailId = form.querySelector(".orderDetailId").value;
        const quantityText = form.querySelector(".quantity-text");
        const decreaseBtn = form.querySelector(".decrease-btn");
        const increaseBtn = form.querySelector(".increase-btn");

        function updateQuantity(action) {
            const url = form.dataset.url;
            if (!url) {
                console.error("data-url không được xác định!");
                return;
            }

            fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: new URLSearchParams({
                    orderDetailId: orderDetailId,
                    action: action
                })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Cập nhật số lượng trong form
                        quantityText.textContent = data.newQuantity;
                        decreaseBtn.disabled = data.newQuantity <= 1;
                        increaseBtn.disabled = data.stockRemaining === 0;

                        console.log(`Cập nhật sản phẩm ID: ${data.productDetailId}, Số lượng mới: ${data.newQuantity}`);

                        // Chờ 500ms để đảm bảo HTML được render trước khi cập nhật
                        setTimeout(() => {
                            const productQuantityCell = document.getElementById(`product-quantity-${data.productDetailId}`);
                            if (productQuantityCell) {
                                productQuantityCell.textContent = data.newQuantity;
                                console.log(`✅ Đã cập nhật số lượng cho sản phẩm ID: ${data.productDetailId}`);
                            } else {
                                console.warn(`⚠ Không tìm thấy ô số lượng cho sản phẩm ID: ${data.productDetailId}`);
                            }
                        }, 500);
                    } else {
                        alert(data.message);
                    }
                })
                .catch(error => console.error("Lỗi:", error));
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