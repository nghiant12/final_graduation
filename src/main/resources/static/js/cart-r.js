// Khởi tạo khi DOM được tải
document.addEventListener("DOMContentLoaded", () => {
    showCard();
    showCheckoutCart();
    count();
    addEventListeners();
    totalPrice();
});

let cartR = JSON.parse(localStorage.getItem("cartR")) || [];

function addEventListeners() {
    document.querySelectorAll(".add-to-cartR").forEach(button => {
        button.removeEventListener("click", handleAddToCart);
        button.addEventListener("click", handleAddToCart);
    });
}

function handleAddToCart(event) {
    const button = event.target;
    button.disabled = true;
    setTimeout(() => (button.disabled = false), 1000);
    addToCart();
}

async function addToCart() {
    const productName = document.querySelector(".home-product-name")?.textContent || "Không có dữ liệu";
    const productColor = document.querySelector(".product-color")?.textContent || "Không có dữ liệu";
    const productSize = document.querySelector(".product-size")?.textContent || "Không có dữ liệu";
    const productCategory = document.querySelector(".product-category")?.textContent || "Không có dữ liệu";
    const productDetailId = document.querySelector(".product-detail-id")?.textContent || "Không có dữ liệu";
    const productPrice = parseFloat(document.querySelector(".home-product-price")?.textContent.replace(/[^\d.]/g, "")) || 0;
    const mainImage = document.querySelector(".main-img")?.getAttribute("src") || "Không có ảnh";

    let quantity = parseInt(document.getElementById("quantity-product")?.value) || 1;
    if (quantity < 1) quantity = 1;

    let cart = JSON.parse(localStorage.getItem("cartR")) || [];

    const existing = cart.find(item =>
        item.productName === productName &&
        item.productColor === productColor &&
        item.productSize === productSize
    );

    const stock = await getStock(productDetailId);
    const currentQty = existing ? existing.soLuong : 0;
    const newTotal = currentQty + quantity;

    if (newTotal > stock) {
        alert(`Tồn kho còn ${stock}. Đang có ${currentQty} trong giỏ hàng.`);
        return;
    }

    if (existing) {
        existing.soLuong += quantity;
    } else {
        cart.push({ productName, productColor, productSize, productCategory, productPrice, productDetailId, mainImage, soLuong: quantity });
    }

    localStorage.setItem("cartR", JSON.stringify(cart));
    count();
    alert(`Đã thêm vào giỏ: ${productName} - ${productColor} - ${productSize} (${quantity})`);
}

async function getStock(id) {
    try {
        const res = await fetch(`/api/products/${id}/stock`);
        return await res.json();
    } catch {
        return 0;
    }
}

async function updateStock(id, quantity) {
    try {
        const res = await fetch(`/api/products/${id}/updateStock`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ quantity })
        });

        if (!res.ok) {
            const msg = await res.text();
            console.error("Lỗi cập nhật tồn kho:", msg);
        }
    } catch (e) {
        console.error("Lỗi kết nối API tồn kho:", e);
    }
}

function count() {
    const total = (JSON.parse(localStorage.getItem("cartR")) || []).length;
    const countEl = document.getElementById("cartR-count");
    if (countEl) countEl.innerText = total;
    return total;
}

function totalPrice() {
    const checkoutCart = JSON.parse(localStorage.getItem("checkoutCart")) || [];
    const total = checkoutCart.reduce((sum, item) => sum + item.productPrice * item.soLuong, 0);
    const el = document.getElementById("total-price");
    if (el) el.innerText = total.toLocaleString("vi-VN") + " VND";
    return total;
}

function removeItem(index) {
    const cart = JSON.parse(localStorage.getItem("cartR")) || [];
    cart.splice(index, 1);
    localStorage.setItem("cartR", JSON.stringify(cart));
    showCard();
    count();
    totalPrice();
}

function toggleCheckoutItem(index, isChecked) {
    const cart = JSON.parse(localStorage.getItem("cartR")) || [];
    let checkout = JSON.parse(localStorage.getItem("checkoutCart")) || [];
    const item = cart[index];

    if (isChecked) {
        if (!checkout.some(x => JSON.stringify(x) === JSON.stringify(item))) {
            checkout.push(item);
        }
    } else {
        checkout = checkout.filter(x => JSON.stringify(x) !== JSON.stringify(item));
    }

    localStorage.setItem("checkoutCart", JSON.stringify(checkout));
    totalPrice();
}

function toggleSelectAll(master) {
    const isChecked = master.checked;
    const cart = JSON.parse(localStorage.getItem("cartR")) || [];
    const checkboxes = document.querySelectorAll("#cartR-body input[type='checkbox']");
    let checkout = [];

    checkboxes.forEach((cb, i) => {
        cb.checked = isChecked;
        if (isChecked) checkout.push(cart[i]);
    });

    if (!isChecked) {
        localStorage.removeItem("checkoutCart");
    } else {
        localStorage.setItem("checkoutCart", JSON.stringify(checkout));
    }
    totalPrice();
}

async function updateQuantity(index, newQty) {
    const cart = JSON.parse(localStorage.getItem("cartR")) || [];
    let checkout = JSON.parse(localStorage.getItem("checkoutCart")) || [];

    newQty = parseInt(newQty);
    if (newQty < 1) newQty = 1;

    const stock = await getStock(cart[index].productDetailId);
    if (newQty > stock) {
        alert(`Tối đa còn ${stock}`);
        newQty = stock;
    }

    cart[index].soLuong = newQty;
    localStorage.setItem("cartR", JSON.stringify(cart));

    const item = checkout.find(x =>
        x.productName === cart[index].productName &&
        x.productColor === cart[index].productColor &&
        x.productSize === cart[index].productSize &&
        x.productCategory === cart[index].productCategory
    );

    if (item) {
        item.soLuong = newQty;
        localStorage.setItem("checkoutCart", JSON.stringify(checkout));
    }

    showCard();
    showCheckoutCart();
    count();
    totalPrice();
}

function showCard() {
    const cart = JSON.parse(localStorage.getItem("cartR")) || [];
    const checkoutCart = JSON.parse(localStorage.getItem("checkoutCart")) || [];
    const tbody = document.getElementById("cartR-body");
    if (!tbody) return;

    tbody.innerHTML = cart.length
        ? cart.map((item, i) => {
            const total = item.productPrice * item.soLuong;
            const checked = checkoutCart.some(x => JSON.stringify(x) === JSON.stringify(item)) ? "checked" : "";
            return `
<tr>
    <td><input type="checkbox" onchange="toggleCheckoutItem(${i}, this.checked)" ${checked}></td>
    <td>${i + 1}</td>
    <td>${item.productName}</td>
    <td><img src="${item.mainImage}" width="50"></td>
    <td>${item.productColor}</td>
    <td>${item.productSize}</td>
    <td>${item.productCategory}</td>
    <td>${item.productPrice.toLocaleString("vi-VN")} VND</td>
    <td><input type="number" min="1" value="${item.soLuong}" onchange="updateQuantity(${i}, this.value)"></td>
    <td>${total.toLocaleString("vi-VN")} VND</td>
<td>
  <button onclick="if(confirm('Xóa sản phẩm?')) removeItem(${i})" class="btn">
    <i class="fa-solid fa-trash"></i>
  </button>
</td>
</tr>`;
        }).join("")
        : `<tr><td colspan="11">Giỏ hàng trống</td></tr>`;

    totalPrice();
}

function showCheckoutCart() {
    const checkoutCart = JSON.parse(localStorage.getItem("checkoutCart")) || [];
    const container = document.getElementById("checkout-cart-list");
    if (!container) return;

    container.innerHTML = checkoutCart.map(item => `
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
            <h6>${item.productPrice.toLocaleString("vi-VN")} VND</h6>
        </div>
        <div style="display: flex">
            <h6 style="margin-right: 5px">Số lượng: </h6><h6>${item.soLuong}</h6>
        </div>
        <div style="display: flex">
            <h6 style="margin-right: 5px">Đơn giá: </h6>
            <h6 class="text-danger fw-bold">${(item.productPrice * item.soLuong).toLocaleString("vi-VN")} VND</h6>
        </div>
    </div>
</div>`).join("");

    totalPrice();
}
