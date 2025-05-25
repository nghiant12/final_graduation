// Currency formatting function
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Initialize address handlers
document.addEventListener('DOMContentLoaded', function() {
    // Load provinces on page load
    fetch('/api/shipping/provinces')
        .then(response => response.json())
        .then(provinces => {
            const provinceSelect = document.getElementById('province');
            provinces.forEach(province => {
                const option = new Option(province.ProvinceName, province.ProvinceID);
                provinceSelect.add(option);
            });
        })
        .catch(error => console.error('Error loading provinces:', error));

    // Province change handler
    document.getElementById('province').addEventListener('change', function() {
        const provinceId = this.value;
        const districtSelect = document.getElementById('district');
        const wardSelect = document.getElementById('ward');
        
        // Reset districts and wards
        districtSelect.innerHTML = '<option value="" disabled selected>Chọn quận/huyện</option>';
        wardSelect.innerHTML = '<option value="" disabled selected>Chọn phường/xã</option>';
        districtSelect.disabled = !provinceId;
        wardSelect.disabled = true;

        if (provinceId) {
            fetch(`/api/shipping/districts/${provinceId}`)
                .then(response => response.json())
                .then(districts => {
                    districts.forEach(district => {
                        const option = new Option(district.DistrictName, district.DistrictID);
                        districtSelect.add(option);
                    });
                    districtSelect.disabled = false;
                })
                .catch(error => console.error('Error loading districts:', error));
        }
    });

    // District change handler
    document.getElementById('district').addEventListener('change', function() {
        const districtId = this.value;
        const wardSelect = document.getElementById('ward');
        
        // Reset wards
        wardSelect.innerHTML = '<option value="" disabled selected>Chọn phường/xã</option>';
        wardSelect.disabled = !districtId;

        if (districtId) {
            fetch(`/api/shipping/wards/${districtId}`)
                .then(response => response.json())
                .then(wards => {
                    wards.forEach(ward => {
                        const option = new Option(ward.WardName, ward.WardCode);
                        wardSelect.add(option);
                    });
                    wardSelect.disabled = false;
                })
                .catch(error => console.error('Error loading wards:', error));
        }
    });

    // Ward change handler
    document.getElementById('ward').addEventListener('change', async function() {
        updateShippingAddress();
        await calculateShippingFee();
    });

    // Calculate shipping fee
    async function calculateShippingFee() {
        const districtId = document.getElementById('district').value;
        const wardCode = document.getElementById('ward').value;
        
        if (districtId && wardCode) {
            try {
                const cartItems = JSON.parse(localStorage.getItem('checkoutCart')) || [];
                const totalWeight = cartItems.reduce((sum, item) => sum + (item.weight || 200) * item.soLuong, 0) || 200;
                
                const response = await fetch('/api/shipping/calculate-fee', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        districtId: districtId,
                        wardCode: wardCode,
                        weight: totalWeight
                    })
                });

                if (!response.ok) throw new Error('Failed to calculate shipping fee');
                
                const fee = await response.json();
                document.getElementById('shipping-fee').textContent = formatCurrency(fee);
                document.getElementById('summary-shipping').textContent = formatCurrency(fee);
                if (typeof updateOrderSummary === 'function') {
                    const subTotal = typeof calculateCartTotal === 'function' ? calculateCartTotal() : 0;
                    const discountAmount = parseFloat(sessionStorage.getItem('promotionDiscount')) || 0;
                    updateOrderSummary(subTotal, parseFloat(fee) || 0, discountAmount);
                }
            } catch (error) {
                console.error('Error calculating shipping fee:', error);
                document.getElementById('shipping-fee').textContent = formatCurrency(30000); // Default fee
                document.getElementById('summary-shipping').textContent = formatCurrency(30000); // Default fee
                if (typeof updateOrderSummary === 'function') {
                    const subTotal = typeof calculateCartTotal === 'function' ? calculateCartTotal() : 0;
                    const discountAmount = parseFloat(sessionStorage.getItem('promotionDiscount')) || 0;
                    updateOrderSummary(subTotal, 30000, discountAmount);
                }
            }
        }
    }
});

// Update shipping address
function updateShippingAddress() {
    const province = document.getElementById('province');
    const district = document.getElementById('district');
    const ward = document.getElementById('ward');
    const addressDetail = document.getElementById('user-address');

    if (province.value && district.value && ward.value && addressDetail.value) {
        const fullAddress = `${addressDetail.value}, ${ward.selectedOptions[0].text}, ${district.selectedOptions[0].text}, ${province.selectedOptions[0].text}`;
        // Store the full address in a hidden input or use it as needed
        document.getElementById('full-address').value = fullAddress;
    }
}
