document.addEventListener('DOMContentLoaded', function () {
    // Load provinces on page load
    const provinceSelect = document.getElementById('province');
    const districtSelect = document.getElementById('district');
    const wardSelect = document.getElementById('ward');
    const totalPriceEl = document.getElementById('total-price');

    if (!provinceSelect || !districtSelect || !wardSelect || !totalPriceEl) {
        console.error("Không tìm thấy 1 trong các phần tử: province, district, ward, total-price");
        return;
    }

    fetch('/api/shipping/provinces')
        .then(response => response.json())
        .then(response => {
            console.group('GHN Provinces Response');
            console.log('Response Status:', response.code);
            console.log('Response Message:', response.message);
            console.log('Total Provinces:', response.data ? response.data.length : 0);
            console.log('Provinces Data:', response.data);
            console.groupEnd();
            
            if (response.code === 200 && Array.isArray(response.data)) {
                response.data.forEach(province => {
                    console.log('Adding province:', province.ProvinceName);
                    const option = new Option(province.ProvinceName, province.ProvinceID);
                    provinceSelect.add(option);
                });
            } else {
                console.error('Failed to load provinces:', response.message);
            }
        })
        .catch(error => console.error('Error loading provinces:', error));

    // Province change handler
    provinceSelect.addEventListener('change', function () {
        const provinceId = this.value;

        districtSelect.innerHTML = '<option value="" disabled selected>Chọn quận/huyện</option>';
        wardSelect.innerHTML = '<option value="" disabled selected>Chọn phường/xã</option>';
        districtSelect.disabled = !provinceId;
        wardSelect.disabled = true;

        if (provinceId) {
            fetch(`/api/shipping/districts/${provinceId}`)
                .then(response => response.json())
                .then(response => {
                    if (response.code === 200 && Array.isArray(response.data)) {
                        response.data.forEach(district => {
                            const option = new Option(district.DistrictName, district.DistrictID);
                            districtSelect.add(option);
                        });
                        districtSelect.disabled = false;
                    } else {
                        console.error('Không tải được quận/huyện:', response.message || 'Unknown error');
                    }
                })
                .catch(error => console.error('Lỗi khi tải quận/huyện:', error));
        }
    });

    // District change handler
    districtSelect.addEventListener('change', function () {
        const districtId = this.value;

        wardSelect.innerHTML = '<option value="" disabled selected>Chọn phường/xã</option>';
        wardSelect.disabled = !districtId;

        if (districtId) {
            fetch(`/api/shipping/wards/${districtId}`)
                .then(response => response.json())
                .then(response => {
                    if (response.code === 200 && Array.isArray(response.data)) {
                        response.data.forEach(ward => {
                            const option = new Option(ward.WardName, ward.WardCode);
                            wardSelect.add(option);
                        });
                        wardSelect.disabled = false;
                    } else {
                        console.error('Không tải được phường/xã:', response.message || 'Unknown error');
                    }
                })
                .catch(error => console.error('Lỗi khi tải phường/xã:', error));
        }
    });

   

    // Override confirmOrder
    const originalConfirmOrder = window.confirmOrder;
    window.confirmOrder = function () {
        const form = document.getElementById('order-form');
        if (form.checkValidity()) {
            const shippingAddress = {
                province: provinceSelect.selectedOptions[0].text,
                district: districtSelect.selectedOptions[0].text,
                ward: wardSelect.selectedOptions[0].text,
                address: document.getElementById('user-address').value
            };

            const fullAddress = `${shippingAddress.address}, ${shippingAddress.ward}, ${shippingAddress.district}, ${shippingAddress.province}`;
            document.getElementById('user-address').value = fullAddress;

            return originalConfirmOrder ? originalConfirmOrder() : true;
        }
        return false;
    };
});
