// Currency formatting function
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Function to calculate cart total
function calculateCartTotal() {
    const checkoutCart = JSON.parse(localStorage.getItem('checkoutCart')) || [];
    return checkoutCart.reduce((total, item) => {
        return total + (item.productPrice * item.soLuong);
    }, 0);
}

// Function to update order summary
function updateOrderSummary(subTotal, shippingFee = 0, discountAmount = 0) {
    document.getElementById('summary-subtotal').textContent = formatCurrency(subTotal);
    const rowShipping = document.getElementById('row-shipping');
    if (shippingFee > 0) {
        rowShipping.style.display = '';
        document.getElementById('summary-shipping').textContent = formatCurrency(shippingFee);
    } else {
        rowShipping.style.display = 'none';
    }
    const rowDiscount = document.getElementById('row-discount');
    if (discountAmount > 0) {
        rowDiscount.style.display = '';
        document.getElementById('summary-discount').textContent = formatCurrency(discountAmount);
    } else {
        rowDiscount.style.display = 'none';
    }
    // Tổng thanh toán = subTotal - discountAmount + shippingFee
    const finalTotal = Math.max(0, subTotal - discountAmount + shippingFee);
    document.getElementById('summary-final').textContent = formatCurrency(finalTotal);
    document.getElementById('total-price').textContent = formatCurrency(finalTotal);
}

// Handle VNPay return
function handleVNPayReturn() {
    const urlParams = new URLSearchParams(window.location.search);
    const vnp_ResponseCode = urlParams.get('vnp_ResponseCode');
    
    if (vnp_ResponseCode === '00') {
        localStorage.removeItem("cartR");
        localStorage.removeItem('checkoutCart');
        count();
    }
}

// Hàm áp dụng mã khuyến mãi
async function applyPromotionCode() {
    const code = document.getElementById('promotion-code').value.trim();
    const promotionMessage = document.getElementById('promotion-message');
    const promotionDiscount = document.getElementById('promotion-discount');
    if (!code) {
        promotionMessage.textContent = 'Vui lòng nhập mã khuyến mãi.';
        promotionMessage.style.color = 'red';
        promotionDiscount.textContent = '';
        sessionStorage.removeItem('promotionCode');
        sessionStorage.removeItem('promotionDiscount');
        const subTotal = calculateCartTotal();
        const shippingFee = parseFloat(document.getElementById('shipping-fee').textContent.replace(/[^0-9]/g, '')) || 0;
        updateOrderSummary(subTotal, shippingFee, 0);
        return;
    }
    try {
        const subTotal = calculateCartTotal();
        // Gửi subTotal (KHÔNG cộng shippingFee) lên backend để tính giảm giá
        const res = await fetch('/admin/promotions/validate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `code=${encodeURIComponent(code)}&orderTotal=${subTotal}`
        });
        const data = await res.json();
        const shippingFee = parseFloat(document.getElementById('shipping-fee').textContent.replace(/[^0-9]/g, '')) || 0;
        if (data.valid) {
            promotionMessage.textContent = data.message;
            promotionMessage.style.color = 'green';
            promotionDiscount.textContent = `Giảm giá: -${formatCurrency(data.discountAmount)}`;
            sessionStorage.setItem('promotionCode', code);
            sessionStorage.setItem('promotionDiscount', data.discountAmount);
            updateOrderSummary(subTotal, shippingFee, data.discountAmount);
        } else {
            promotionMessage.textContent = data.message;
            promotionMessage.style.color = 'red';
            promotionDiscount.textContent = '';
            sessionStorage.removeItem('promotionCode');
            sessionStorage.removeItem('promotionDiscount');
            updateOrderSummary(subTotal, shippingFee, 0);
        }
    } catch (e) {
        promotionMessage.textContent = 'Lỗi khi kiểm tra mã khuyến mãi.';
        promotionMessage.style.color = 'red';
        promotionDiscount.textContent = '';
        sessionStorage.removeItem('promotionCode');
        sessionStorage.removeItem('promotionDiscount');
        const subTotal = calculateCartTotal();
        const shippingFee = parseFloat(document.getElementById('shipping-fee').textContent.replace(/[^0-9]/g, '')) || 0;
        updateOrderSummary(subTotal, shippingFee, 0);
    }
}

// Function to calculate shipping fee
async function calculateShippingFee() {
    const ward = document.getElementById('ward').value;
    if (!ward) return;

    try {
        const response = await fetch(`/api/shipping-fee?wardId=${ward}`);
        const data = await response.json();

        if (data.shippingFee !== undefined) {
            const shippingFee = parseFloat(data.shippingFee) || 0;
            document.getElementById('shipping-fee').textContent = formatCurrency(shippingFee);

            const subTotal = calculateCartTotal();
            const discountAmount = parseFloat(sessionStorage.getItem('promotionDiscount')) || 0;

            updateOrderSummary(subTotal, shippingFee, discountAmount);
        }
    } catch (error) {
        console.error('Error calculating shipping fee:', error);
    }
}

// Function to confirm order
async function confirmOrder() {
    const loadingMessage = document.getElementById('loading-message');
    loadingMessage.style.display = 'block';

    try {
        // Get form data
        const formData = {
            customerId: document.getElementById('user-id').value,
            fullname: document.getElementById('user-fullname').value,
            email: document.getElementById('user-email').value,
            phone: document.getElementById('user-sdt').value,
            address: document.getElementById('user-address').value,
            paymentMethod: document.getElementById('payment-method').value
        };

        // Validate form data
        if (!formData.fullname || !formData.email || !formData.phone || !formData.address) {
            alert('Vui lòng điền đầy đủ thông tin!');
            loadingMessage.style.display = 'none';
            return false;
        }

        // Validate address selection
        const province = document.getElementById('province');
        const district = document.getElementById('district');
        const ward = document.getElementById('ward');

        if (!province.value || !district.value || !ward.value) {
            alert('Vui lòng chọn đầy đủ địa chỉ (Tỉnh/Thành phố, Quận/Huyện, Phường/Xã)!');
            loadingMessage.style.display = 'none';
            return false;
        }

        // Get checkout cart items
        const checkoutCart = JSON.parse(localStorage.getItem('checkoutCart')) || [];
        if (checkoutCart.length === 0) {
            alert('Vui lòng chọn sản phẩm để thanh toán!');
            loadingMessage.style.display = 'none';
            return false;
        }

        // Build full address
        const shippingAddress = {
            province: province.selectedOptions[0].text,
            district: district.selectedOptions[0].text,
            ward: ward.selectedOptions[0].text,
            address: document.getElementById('user-address').value
        };

        const fullAddress = `${shippingAddress.address}, ${shippingAddress.ward}, ${shippingAddress.district}, ${shippingAddress.province}`;

        // Get shipping fee and total price
        const shippingFee = parseInt(document.getElementById('shipping-fee').textContent.replace(/[^\d]/g, ''));
        const totalPrice = parseInt(document.getElementById('total-price').textContent.replace(/[^\d]/g, ''));

        // Get promotion code nếu có
        const promotionCode = sessionStorage.getItem('promotionCode') || '';

        // Create order request
        const orderRequest = {
            customerId: formData.customerId,
            customerName: formData.fullname,
            email: formData.email,
            phone: formData.phone,
            address: fullAddress,
            paymentMethod: formData.paymentMethod,
            totalPrice: totalPrice,
            shippingFee: shippingFee,
            orderDetails: checkoutCart.map(item => ({
                productDetailId: item.productDetailId,
                quantity: item.soLuong,
                price: item.productPrice
            })),
            promotionCode: promotionCode
        };

        // Send order request
        const response = await fetch('/api/orders', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderRequest)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || 'Đã xảy ra lỗi khi đặt hàng');
        }

        if (formData.paymentMethod === 'VNPay') {
            if (data.paymentUrl) {
                window.location.href = data.paymentUrl;
            } else {
                throw new Error('Không nhận được URL thanh toán VNPay');
            }
        } else {
            console.log('Xoá giỏ hàng');
            localStorage.removeItem("cartR");
            localStorage.removeItem('checkoutCart');
            count();

            setTimeout(() => {
                window.location.href = '/thank-you'; // Nếu cần redirect ở đây
            }, 200);
        }

    } catch (error) {
        console.error('Error:', error);
        alert(error.message || 'Đã xảy ra lỗi khi đặt hàng!');
    } finally {
        loadingMessage.style.display = 'none';
    }

    return false;
}

// Initialize event listeners when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Add VNPay return handler
    handleVNPayReturn();

    // Add ward change handler for shipping fee calculation
    const wardSelect = document.getElementById('ward');
    if (wardSelect) {
        wardSelect.addEventListener('change', calculateShippingFee);
    }

    // Khi load trang, chỉ hiển thị tiền hàng, phí ship và giảm giá = 0
    const subTotal = calculateCartTotal();
    updateOrderSummary(subTotal, 0, 0);
});