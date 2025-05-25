document.addEventListener("DOMContentLoaded", function () {
    showCard();
    showCheckoutCart();
    count(); // Cập nhật số lượng giỏ hàng khi trang được tải
    addEventListeners(); // Thêm sự kiện cho các nút khi DOM tải xong
    totalPrice();
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
async function addToCart() {
    var productName = document.querySelector(".home-product-name")?.textContent || "Không có dữ liệu";
    var productColor = document.querySelector(".product-color")?.textContent || "Không có dữ liệu";
    var productSize = document.querySelector(".product-size")?.textContent || "Không có dữ liệu";
    var productCategory = document.querySelector(".product-category")?.textContent || "Không có dữ liệu";
    var productDetailId = document.querySelector(".product-detail-id")?.textContent || "Không có dữ liệu";
    var productPrice = parseFloat(document.querySelector(".home-product-price")?.textContent.replace(/[^\d.]/g, "")) || 0;
    var mainImage = document.querySelector(".main-img")?.getAttribute("src") || "Không có ảnh";

    var quantityInput = document.getElementById("quantity-product");
    var soLuong = parseInt(quantityInput?.value) || 1;
    if (soLuong < 1) {
        soLuong = 1;
        quantityInput.value = 1;
    }

    // Lấy giỏ hàng từ localStorage
    var gioHang = JSON.parse(localStorage.getItem("cartR")) || [];

    // Kiểm tra sản phẩm đã có trong giỏ hay chưa
    var existingProduct = gioHang.find(item =>
        item.productName === productName &&
        item.productColor === productColor &&
        item.productSize === productSize
    );

    // Lấy tồn kho từ API
    let stock = await getStock(productDetailId);
    let currentQuantity = existingProduct ? existingProduct.soLuong : 0;
    let newTotal = currentQuantity + soLuong;

    if (newTotal > stock) {
        alert(`Số lượng tồn kho chỉ còn ${stock}. Bạn đã có ${currentQuantity} sản phẩm này trong giỏ hàng.`);
        return;
    }

    if (existingProduct) {
        existingProduct.soLuong += soLuong;
    } else {
        var pro = {
            "productName": productName,
            "productColor": productColor,
            "productSize": productSize,
            "productCategory": productCategory,
            "productPrice": productPrice,
            "productDetailId": productDetailId,
            "mainImage": mainImage,
            "soLuong": soLuong
        };
        gioHang.push(pro);
    }

    localStorage.setItem("cartR", JSON.stringify(gioHang));
    count();
    alert(`Đã thêm vào giỏ hàng: ${productName} - ${productColor} - ${productSize} (${soLuong} cái)`);
}

// Hàm gọi API lấy tồn kho
async function getStock(productDetailId) {
    try {
        const response = await fetch(`/api/products/${productDetailId}/stock`);
        const data = await response.json();
        console.log("API Response:", data);  // Kiểm tra lại dữ liệu trả về
        return data; // Trả về trực tiếp giá trị số lượng tồn kho
    } catch (err) {
        console.error("Lỗi khi lấy tồn kho:", err);
        return 0;
    }
}


// confirmOrder function has been moved to checkout.html

// Hàm gọi API cập nhật tồn kho
async function updateStock(productDetailId, quantity) {
    try {
        const response = await fetch(`http://localhost:8080/api/products/${productDetailId}/updateStock`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ quantity })
        });

        if (!response.ok) {
            const errMsg = await response.text();
            console.error("Lỗi khi giảm tồn kho:", errMsg);
            alert("Không thể cập nhật tồn kho cho sản phẩm ID: " + productDetailId);
        }
    } catch (error) {
        console.error("Lỗi khi kết nối API tồn kho:", error);
        alert("Không thể kết nối để cập nhật tồn kho.");
    }
}

function showCard() {
    var checkoutCart = JSON.parse(localStorage.getItem("checkoutCart")) || [];
    var tableBody = document.getElementById("cartR-body");
    if (!tableBody) {
        console.error("Không tìm thấy phần tử #cartR-body. Kiểm tra lại HTML!");
        return;
    }

    var cart = JSON.parse(localStorage.getItem("cartR")) || [];
    console.log("Giỏ hàng từ localStorage:", cart);
    tableBody.innerHTML = "";
    var emptyCartElement = document.getElementById("empty-cartR");

    if (cart.length > 0) {
        var kq = "";
        for (let i = 0; i < cart.length; i++) {
            var productPrice = parseFloat(cart[i]["productPrice"]) || 0;
            var soLuong = parseInt(cart[i]["soLuong"]) || 1;
            var total = productPrice * soLuong;

            const detailUrl = `http://localhost:8080/product/${cart[i].productDetailId}`;

            kq += `
<tr style="cursor: pointer;" onmouseover="this.style.backgroundColor='#f0f0f0'" onmouseout="this.style.backgroundColor=''" class="w3-2021-inkwell">
    <td>
        <input type="checkbox"
            onchange="toggleCheckoutItem(${i}, this.checked)"
            ${checkoutCart.some(item => JSON.stringify(item) === JSON.stringify(cart[i])) ? "checked" : ""}>
    </td>
    <td>${i + 1}</td>
    <td onclick="window.open('${detailUrl}', '_blank')">${cart[i]["productName"]}</td>
    <td onclick="window.open('${detailUrl}', '_blank')"><img src="${cart[i]["mainImage"]}" alt="Hình ảnh" width="50"></td>
    <td onclick="window.open('${detailUrl}', '_blank')">${cart[i]["productColor"]}</td>
    <td onclick="window.open('${detailUrl}', '_blank')">${cart[i]["productSize"]}</td>
    <td onclick="window.open('${detailUrl}', '_blank')">${cart[i]["productCategory"]}</td>
    <td onclick="window.open('${detailUrl}', '_blank')">${productPrice.toFixed(2)}</td>
    <td>
        <input type="number" class="form-control" value="${soLuong}" min="1" onchange="updateQuantity(${i}, this.value)">
    </td>
    <td>${total.toFixed(2)}</td>
    <td><button class="btn" onclick="if(confirm('Bạn có chắc chắn muốn xóa sản phẩm này không?')) removeItem(${i});">
        <i class="fa-solid fa-trash"></i>
    </button></td>
</tr>`;




        }
        tableBody.innerHTML = kq;
        if (emptyCartElement) emptyCartElement.style.display = "none";
    } else {
        tableBody.innerHTML = `<tr><td colspan="11">Giỏ hàng trống</td></tr>`;
        if (emptyCartElement) emptyCartElement.style.display = "block";
    }

    totalPrice();
}

function toggleCheckoutItem(index, isChecked) {
    const cart = JSON.parse(localStorage.getItem("cartR")) || [];
    let checkoutCart = JSON.parse(localStorage.getItem("checkoutCart")) || [];
    const selectedItem = cart[index];

    if (isChecked) {
        if (!checkoutCart.some(item => JSON.stringify(item) === JSON.stringify(selectedItem))) {
            checkoutCart.push(selectedItem);
            console.log("Thêm sản phẩm vào checkoutCart", selectedItem);
        }
    } else {
        checkoutCart = checkoutCart.filter(item => JSON.stringify(item) !== JSON.stringify(selectedItem));
        console.log("Xóa sản phẩm khỏi checkoutCart", selectedItem);
    }

    localStorage.setItem("checkoutCart", JSON.stringify(checkoutCart));
    console.log("Giỏ hàng thanh toán hiện tại:", checkoutCart);
    totalPrice();
}



function toggleSelectAll(masterCheckbox) {
    const isChecked = masterCheckbox.checked;
    const cart = JSON.parse(localStorage.getItem("cartR")) || [];
    const checkboxes = document.querySelectorAll("#cartR-body input[type='checkbox']");
    let checkoutCart = [];

    checkboxes.forEach((cb, index) => {
        cb.checked = isChecked;
        if (isChecked) {
            checkoutCart.push(cart[index]);
        }
    });

    if (!isChecked) {
        localStorage.removeItem("checkoutCart");
    } else {
        localStorage.setItem("checkoutCart", JSON.stringify(checkoutCart));
    }

    totalPrice();
}

window.onload = function () {
    showCheckoutCart();
    showCard();
    totalPrice();
};

function showCheckoutCart() {
    const checkoutCart = JSON.parse(localStorage.getItem("checkoutCart")) || [];
    const container = document.getElementById("checkout-cart-list");

    if (!container) {
        console.error("Không tìm thấy phần tử #checkout-cart-list");
        return;
    }



    let html = "";
    checkoutCart.forEach((item) => {
        const priceFormatted = item.productPrice.toLocaleString("vi-VN") + " VND";
        const totalPrice = (item.productPrice * item.soLuong).toLocaleString("vi-VN") + " VND";

        html += `
        <div class="d-flex align-items-center border rounded-3 p-3 mb-3">
            <img src="${item.mainImage}" alt="Product" class="me-3" width="100" height="100">
            <div>
                <div style="display: flex; align-items: center;">
                    <h6>${item.productName}</h6>
                    <h6 style="margin: 0 10px">-</h6>
                    <h6>${item.productSize}</h6>
                    <h6 style="margin: 0 10px">-</h6>
                    <h6>${item.productColor}</h6>
                    <h6 style="margin: 0 10px">-</h6>
                    <h6>${priceFormatted}</h6>
                </div>
                <div style="display: flex">
                    <h6 style="margin-right: 5px">Số lượng: </h6>
                    <h6>${item.soLuong}</h6>
                </div>
                <div style="display: flex">
                    <h6 style="margin-right: 5px">Đơn giá: </h6>
                    <h6 class="text-danger fw-bold">${totalPrice}</h6>
                </div>
            </div>
        </div>`;
    });

    container.innerHTML = html;
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
    var checkoutCart = JSON.parse(localStorage.getItem("checkoutCart")) || [];
    console.log("CheckoutCart:", checkoutCart);

    var total = checkoutCart.reduce((sum, item) => {
        var price = parseFloat(item.productPrice) || 0;
        var quantity = parseInt(item.soLuong) || 1;
        return sum + (price * quantity);
    }, 0);

    var totalPriceElement = document.getElementById("total-price");

    if (totalPriceElement) {
        totalPriceElement.innerText = total.toLocaleString("vi-VN") + " VND";
    } else {
        console.error("Không tìm thấy phần tử #total-price!");
    }

    return total;
}



async function updateQuantity(index, newQuantity) {
    var cart = JSON.parse(localStorage.getItem("cartR")) || [];
    var checkoutCart = JSON.parse(localStorage.getItem("checkoutCart")) || [];

    newQuantity = parseInt(newQuantity);
    if (newQuantity < 1) newQuantity = 1;

    // Gọi API để lấy số lượng tồn kho từ cơ sở dữ liệu
    var maxStock = await getMaxStockForProduct(cart[index]); // Chờ đợi kết quả từ API
    console.log("SL tồn kho:", maxStock);

    // Kiểm tra số lượng không vượt quá tồn kho
    if (newQuantity > maxStock) {
        alert("Số lượng yêu cầu vượt quá số lượng tồn kho! Tối đa là " + maxStock);
        newQuantity = maxStock;  // Đặt số lượng về số lượng tồn kho
    }

    // Cập nhật số lượng trong cartR
    cart[index]["soLuong"] = newQuantity;
    localStorage.setItem("cartR", JSON.stringify(cart));

    // Cập nhật số lượng trong checkoutCart nếu sản phẩm đang được chọn
    const itemInCheckout = checkoutCart.find(item =>
        item.productName === cart[index].productName &&
        item.productColor === cart[index].productColor &&
        item.productSize === cart[index].productSize &&
        item.productCategory === cart[index].productCategory
    );

    if (itemInCheckout) {
        itemInCheckout.soLuong = newQuantity;
        localStorage.setItem("checkoutCart", JSON.stringify(checkoutCart));
    }

    // Cập nhật lại hiển thị
    showCard();
    showCheckoutCart();
    count();
    totalPrice();
}



async function getMaxStockForProduct(cartItem) {
    try {
        const response = await fetch(`/api/products/${cartItem.productDetailId}/stock`); // Gọi API để lấy số lượng tồn kho
        const data = await response.json();
        console.log("API Response:", data);  // Kiểm tra lại dữ liệu trả về
        return data; // Trả về trực tiếp giá trị số lượng tồn kho
    } catch (error) {
        console.error("Lỗi khi lấy thông tin tồn kho:", error);
        return 0; // Nếu gặp lỗi, giả sử không có hàng
    }
}



function removeItem(index) {
    var cart = JSON.parse(localStorage.getItem("cartR")) || [];
    cart.splice(index, 1);
    localStorage.setItem("cartR", JSON.stringify(cart));
    showCard();
    count();
    totalPrice();
}

//
// document.querySelector(".pay-button").addEventListener("click", function (event) {
//     event.preventDefault(); // Ngăn chặn reload trang
//
//     var cart = JSON.parse(localStorage.getItem("cartR")) || [];
//     if (cart.length === 0) {
//         alert("Giỏ hàng trống!");
//         return;
//     }
//
//     var orderData = {
//         username: document.getElementById("username").value || "Guest",
//         phone: document.getElementById("user-sdt").value,
//         address: document.getElementById("user-address").value,
//         paymentMethod: document.getElementById("payment").value,
//         items: cart,
//         totalAmount: cart.reduce((sum, item) => sum + (item.productPrice * item.soLuong), 0)
//     };
//
//     fetch("/order/checkout", {
//         method: "POST",
//         headers: {"Content-Type": "application/json"},
//         body: JSON.stringify(orderData)
//     })
//         .then(response => response.json())
//         .then(data => {
//             alert("Đơn hàng của bạn đã được xử lý!");
//             localStorage.removeItem("cartR"); // Xóa giỏ hàng sau khi thanh toán thành công
//             window.location.href = "/order/success"; // Chuyển hướng sau khi đặt hàng
//         })
//         .catch(error => {
//             console.error("Lỗi thanh toán:", error);
//             alert("Thanh toán thất bại!");
//         });
// });