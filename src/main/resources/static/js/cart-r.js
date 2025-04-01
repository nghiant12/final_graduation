document.addEventListener("DOMContentLoaded", function () {
    showCard();
    count(); // Cập nhật số lượng giỏ hàng khi trang được tải
    addEventListeners(); // Thêm sự kiện cho các nút khi DOM tải xong
});

var cartR = JSON.parse(localStorage.getItem("cartR")) || [];
var gioHang = cartR;

function addEventListeners() {
    var buttons = document.querySelectorAll(".add-to-cartR");
    buttons.forEach(button => {
        button.removeEventListener("click", handleAddToCart);
        button.addEventListener("click", handleAddToCart);
    });
}

function handleAddToCart(event) {
    var button = event.target;
    button.disabled = true;
    setTimeout(() => {
        button.disabled = false;
    }, 1000);
    addToCart();
}

function addToCart() {
    var productName = document.querySelector(".home-product-name")?.textContent || "Không có dữ liệu";
    var productColor = document.querySelector(".product-color")?.textContent || "Không có dữ liệu";
    var productSize = document.querySelector(".product-size")?.textContent || "Không có dữ liệu";
    var productCategory = document.querySelector(".product-category")?.textContent || "Không có dữ liệu";
    var productPrice = parseFloat(document.querySelector(".home-product-price")?.textContent.replace(/[^\d.]/g, "")) || 0;
    var mainImage = document.querySelector(".main-img")?.getAttribute("src") || "Không có ảnh";

    var quantityInput = document.getElementById("quantity-product");
    var soLuong = parseInt(quantityInput?.value) || 1;
    if (soLuong < 1) {
        soLuong = 1;
        quantityInput.value = 1;
    }

    var existingProduct = gioHang.find(item => item.productName === productName && item.productColor === productColor && item.productSize === productSize);
    if (existingProduct) {
        existingProduct.soLuong += soLuong;
    } else {
        var pro = {
            "productName": productName,
            "productColor": productColor,
            "productSize": productSize,
            "productCategory": productCategory,
            "productPrice": productPrice,
            "mainImage": mainImage,
            "soLuong": soLuong
        };
        gioHang.push(pro);
    }

    localStorage.setItem("cartR", JSON.stringify(gioHang));
    count();
    alert(`Đã thêm vào giỏ hàng: ${productName} - ${productColor} - ${productSize} (${soLuong} cái)`);
}

function showCard() {
    var tableBody = document.getElementById("cartR-body");
    if (!tableBody) {
        console.error("Không tìm thấy phần tử #cartR-body. Kiểm tra lại HTML!");
        return;
    }

    var cart = JSON.parse(localStorage.getItem("cartR")) || [];
    console.log("Giỏ hàng từ localStorage:", cart);

    tableBody.innerHTML = "";
    if (cart.length > 0) {
        var kq = "";
        for (let i = 0; i < cart.length; i++) {
            var productPrice = parseFloat(cart[i]["productPrice"]) || 0;
            var soLuong = parseInt(cart[i]["soLuong"]) || 1;
            var total = productPrice * soLuong;

            kq += `
                <tr class="w3-2021-inkwell">
                    <td>${i + 1}</td>
                    <td>${cart[i]["productName"]}</td>
                    <td><img src="${cart[i]["mainImage"]}" alt="Hình ảnh" width="50"></td>
                    <td>${cart[i]["productColor"]}</td>
                    <td>${cart[i]["productSize"]}</td>
                    <td>${cart[i]["productCategory"]}</td>
                    <td>${productPrice.toFixed(2)}</td>
                    <td>
                        <input type="number" class="form-control" value="${soLuong}" min="1" onchange="updateQuantity(${i}, this.value)">
                    </td>
                    <td>${total.toFixed(2)}</td>
                    <td><button class="btn" onclick="if(confirm('Bạn có chắc chắn muốn xóa sản phẩm này không?')) removeItem(${i});">
    <i class="fa-solid fa-trash"></i>
</button>
</td>
                </tr>`;
        }
        tableBody.innerHTML = kq;
        document.getElementById("empty-cartR").style.display = "none";
    } else {
        tableBody.innerHTML = `<tr><td colspan="10">Giỏ hàng trống</td></tr>`;
        document.getElementById("empty-cartR").style.display = "block";
    }

    totalPrice();
}

function count() {
    var cart = JSON.parse(localStorage.getItem("cartR")) || [];
    var totalProducts = cart.length;
    var cartCountElement = document.getElementById("cartR-count");

    if (cartCountElement) {
        cartCountElement.innerText = totalProducts;
    } else {
        console.error("Không tìm thấy phần tử #cartR-count!");
    }

    return totalProducts;
}

function totalPrice() {
    var cart = JSON.parse(localStorage.getItem("cartR")) || [];
    var total = cart.reduce((sum, item) => sum + (item.productPrice * item.soLuong), 0);
    var totalPriceElement = document.getElementById("total-price");

    if (totalPriceElement) {
        totalPriceElement.innerText = total.toLocaleString("vi-VN") + " VND";
    } else {
        console.error("Không tìm thấy phần tử #total-price!");
    }

    return total;
}

function updateQuantity(index, newQuantity) {
    var cart = JSON.parse(localStorage.getItem("cartR")) || [];
    newQuantity = parseInt(newQuantity);

    if (newQuantity < 1) {
        newQuantity = 1;
    }

    cart[index]["soLuong"] = newQuantity;
    localStorage.setItem("cartR", JSON.stringify(cart));
    showCard();
    count();
    totalPrice();
}

function removeItem(index) {
    var cart = JSON.parse(localStorage.getItem("cartR")) || [];
    cart.splice(index, 1);
    localStorage.setItem("cartR", JSON.stringify(cart));
    showCard();
    count();
    totalPrice();
}

document.querySelector(".pay-button").addEventListener("click", function (event) {
    event.preventDefault(); // Ngăn chặn reload trang

    var cart = JSON.parse(localStorage.getItem("cartR")) || [];
    if (cart.length === 0) {
        alert("Giỏ hàng trống!");
        return;
    }

    var orderData = {
        username: document.getElementById("username").value || "Guest",
        phone: document.getElementById("user-sdt").value,
        address: document.getElementById("user-address").value,
        paymentMethod: document.getElementById("payment").value,
        items: cart,
        totalAmount: cart.reduce((sum, item) => sum + (item.productPrice * item.soLuong), 0)
    };

    fetch("/order/checkout", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(orderData)
    })
        .then(response => response.json())
        .then(data => {
            alert("Đơn hàng của bạn đã được xử lý!");
            localStorage.removeItem("cartR"); // Xóa giỏ hàng sau khi thanh toán thành công
            window.location.href = "/order/success"; // Chuyển hướng sau khi đặt hàng
        })
        .catch(error => {
            console.error("Lỗi thanh toán:", error);
            alert("Thanh toán thất bại!");
        });
});
