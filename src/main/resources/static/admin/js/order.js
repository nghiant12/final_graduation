document.addEventListener("DOMContentLoaded", function () {
    const btnSelectCustomer = document.getElementById("btnSelectCustomer");
    const customerModal = document.getElementById("customerModal");
    const searchCustomer = document.getElementById("searchCustomer");
    const customerList = document.getElementById("customerList");
    const addCustomerForm = document.getElementById("addCustomerForm");

    // // Tải danh sách khách hàng từ backend
    // function loadCustomers() {
    //     fetch("/admin/accounts") // API để lấy danh sách khách hàng
    //         .then(response => response.json())
    //         .then(data => {
    //             renderCustomerList(data);
    //         })
    //         .catch(error => {
    //             console.error('Lỗi:', error);
    //         });
    // }
    //
    // // Hiển thị danh sách khách hàng
    // function renderCustomerList(customers, filter = "") {
    //     customerList.innerHTML = "";
    //     const filteredCustomers = customers.filter((customer) => customer.name.toLowerCase().includes(filter.toLowerCase()) || customer.phone.includes(filter));
    //     if (filteredCustomers.length === 0) {
    //         customerList.innerHTML = "<li class='list-group-item'>Không tìm thấy khách hàng</li>";
    //     } else {
    //         filteredCustomers.forEach((customer) => {
    //             const li = document.createElement("li");
    //             li.className = "list-group-item";
    //             li.textContent = `${customer.name} - ${customer.phone}`;
    //             li.style.cursor = "pointer";
    //             li.addEventListener("click", () => selectCustomer(customer));
    //             customerList.appendChild(li);
    //         });
    //     }
    // }
    //
    // // Chọn khách hàng
    // function selectCustomer(customer) {
    //     alert(`Bạn đã chọn khách hàng: ${customer.name} - ${customer.phone}`);
    //     $(customerModal).modal("hide"); // Đóng modal
    //
    //     // Cập nhật thông tin vào hóa đơn (gửi request tới backend)
    //     const orderId = document.getElementById("orderId").value;
    //     fetch(`/admin/orders/addCustomerToOrder?orderId=${orderId}&accountId=${customer.id}`, {
    //         method: "POST"
    //     })
    //         .then(response => response.json())
    //         .then(data => {
    //             alert("Thêm khách hàng vào hóa đơn thành công!");
    //         })
    //         .catch(error => {
    //             console.error('Lỗi:', error);
    //         });
    // }

    // Tìm kiếm khách hàng
    searchCustomer.addEventListener("input", (e) => {
        renderCustomerList(e.target.value);
    });

    // Hiển thị modal khi nhấn nút "Chọn khách hàng"
    btnSelectCustomer.addEventListener("click", () => {
        loadCustomers(); // Tải lại danh sách khách hàng từ backend
        const modal = new bootstrap.Modal(customerModal);
        modal.show();
    });

    // const orderLinks = document.querySelectorAll(".order-detail-link");
    // orderLinks.forEach(link => {
    //     link.addEventListener("click", function (e) {
    //         e.preventDefault();
    //         const orderId = this.getAttribute("data-id");
    //         const orderDetailsContainer = document.getElementById("orderDetails");
    //         orderDetailsContainer.innerHTML = "<p>Đang tải chi tiết hóa đơn...</p>";
    //         fetch(`/admin/orders/detail?idOrder=${orderId}`)
    //             .then(response => response.json())
    //             .then(data => {
    //                 orderDetailsContainer.innerHTML = `
    //                     <h3>Chi tiết hóa đơn #${data.id}</h3>
    //                     <p>Tổng tiền: ${data.price} đ</p>
    //                     <h4>Danh sách sản phẩm:</h4>
    //                     <ul>
    //                         ${data.orderDetails.map(detail => `
    //                             <li>${detail.productName} - ${detail.quantity} x ${detail.price} đ</li>
    //                         `).join('')}
    //                     </ul>
    //                 `;
    //             })
    //             .catch(error => {
    //                 console.error('Lỗi:', error);
    //                 orderDetailsContainer.innerHTML = "<p>Không thể tải chi tiết hóa đơn. Vui lòng thử lại.</p>";
    //             });
    //     });
    // });



});

