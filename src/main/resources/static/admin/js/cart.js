document.addEventListener("DOMContentLoaded", function () {
    showCard();
});

// Lấy danh sách tất cả nút "Thêm vào giỏ hàng"
var buttons = document.querySelectorAll("button");
var cart = JSON.parse(localStorage.getItem("cart")) || [];
var gioHang = cart; // Nếu localStorage có dữ liệu, lấy ra, nếu không thì tạo mảng rỗng

for (let i = 0; i < buttons.length; i++) {
    buttons[i].addEventListener("click", function () {
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

        // Kiểm tra nếu sản phẩm đã tồn tại trong giỏ hàng thì cập nhật số lượng
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

        // Lưu vào localStorage
        localStorage.setItem("cart", JSON.stringify(gioHang));

        alert(`Đã thêm vào giỏ hàng: ${productName} - ${productColor} - ${productSize} (${soLuong} cái)`);
    });
}

function showCard() {
    var tableBody = document.getElementById("cart-body");
    if (!tableBody) {
        console.error("Không tìm thấy phần tử #cart-body. Kiểm tra lại HTML!");
        return;
    }

    var cart = JSON.parse(localStorage.getItem("cart")) || [];
    console.log("Giỏ hàng từ localStorage:", cart);

    tableBody.innerHTML = "";
    if (cart.length > 0) {
        var kq = "";
        for (let i = 0; i < cart.length; i++) {
            var productPrice = parseFloat(cart[i]["productPrice"]) || 0; // Chuyển giá trị thành số
            var soLuong = parseInt(cart[i]["soLuong"]) || 1; // Đảm bảo số lượng hợp lệ
            var totalPrice = productPrice * soLuong;

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
                        <input type="number" value="${soLuong}" min="1" onchange="updateQuantity(${i}, this.value)">
                    </td>
                    <td>${totalPrice.toFixed(2)}</td>
                    <td><button onclick="removeItem(${i})">Xóa</button></td>
                </tr>`;
        }
        tableBody.innerHTML = kq;
        document.getElementById("empty-cart").style.display = "none"; // Ẩn ảnh giỏ hàng trống
    } else {
        tableBody.innerHTML = `<tr><td colspan="10">Giỏ hàng trống</td></tr>`;
        document.getElementById("empty-cart").style.display = "block"; // Hiển thị ảnh giỏ hàng trống
    }
}


function updateQuantity(index, newQuantity) {
    var cart = JSON.parse(localStorage.getItem("cart")) || [];
    newQuantity = parseInt(newQuantity);

    if (newQuantity < 1) {
        newQuantity = 1; // Đảm bảo số lượng không âm hoặc 0
    }

    cart[index]["soLuong"] = newQuantity;
    localStorage.setItem("cart", JSON.stringify(cart));
    showCard();
}

function removeItem(index) {
    var cart = JSON.parse(localStorage.getItem("cart")) || [];
    cart.splice(index, 1);
    localStorage.setItem("cart", JSON.stringify(cart));
    showCard();
}

document.addEventListener("DOMContentLoaded", function () {
    console.log("DOM đã tải xong!");
    var tableBody = document.getElementById("cart-body");

    if (!tableBody) {
        console.error("Không tìm thấy phần tử #cart-body. Kiểm tra lại HTML!");
    } else {
        console.log("Tìm thấy phần tử #cart-body!");
        showCard(); // Gọi hàm hiển thị giỏ hàng
    }
});
