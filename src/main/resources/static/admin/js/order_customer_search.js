document.getElementById('customerSearch').addEventListener('input', function () {
    let query = this.value.trim();
    let resultContainer = document.getElementById('customerResults');

    if (query.length === 0) {
        resultContainer.style.display = 'none'; // Ẩn danh sách nếu không có input
        return;
    }

    fetch(`/admin/orders/searchCustomer?query=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(data => {
            console.log("Dữ liệu API trả về:", data); // Kiểm tra API trả về gì

            resultContainer.innerHTML = ''; // Xóa danh sách cũ
            if (data.length === 0) {
                resultContainer.style.display = 'none'; // Ẩn nếu không có kết quả
                return;
            }
            data.forEach(customer => {
                let customerName = customer.fullname || "Không có tên";
                let customerEmail = customer.email || "Không có email";
                let customerPhone = customer.phoneNumber || "Không có số điện thoại";

                let div = document.createElement('div');
                div.classList.add('dropdown-item');
                div.innerHTML = `
                    <div class="customer-name">${customerName}</div>
                    <div class="customer-email">${customerEmail}</div>
                    <div class="customer-phone">${customerPhone}</div>
                `;
                div.onclick = function () {
                    document.getElementById('customerSearch').value = customerName;
                    resultContainer.style.display = 'none'; // Ẩn dropdown khi chọn xong
                    selectCustomer(customer.id); // Gọi API khi chọn khách hàng
                };
                resultContainer.appendChild(div);
            });
            resultContainer.style.display = 'block'; // Hiện dropdown khi có kết quả
        })
        .catch(error => console.error('Lỗi khi tìm kiếm khách hàng:', error));
});

// Gửi yêu cầu chọn khách hàng
function selectCustomer(customerId) {
    let orderId = new URLSearchParams(window.location.search).get("idOrder"); // Lấy orderId từ URL
    if (!orderId) {
        alert("Không tìm thấy đơn hàng!");
        return;
    }

    fetch(`/admin/orders/selectCustomer/${orderId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `customerId=${customerId}&idOrder=${orderId}`
    })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url; // Chuyển hướng đến trang chi tiết đơn hàng
            } else {
                return response.text();
            }
        })
        .then(data => console.log("Kết quả từ server:", data))
        .catch(error => console.error("Lỗi khi chọn khách hàng:", error));
}

// Ẩn dropdown khi click ra ngoài
document.addEventListener('click', function (event) {
    let dropdown = document.getElementById('customerResults');
    if (!document.getElementById('customerSearch').contains(event.target)) {
        dropdown.style.display = 'none';
    }
});
