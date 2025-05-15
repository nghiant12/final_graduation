
document.addEventListener("DOMContentLoaded", () => {
    const provinceSelect = document.getElementById("province");
    const districtSelect = document.getElementById("district");
    const wardSelect = document.getElementById("ward");

    // Load tỉnh
    fetch("https://provinces.open-api.vn/api/p/")
        .then(res => res.json())
        .then(provinces => {
            provinces.forEach(province => {
                let option = document.createElement("option");
                option.value = province.code;
                option.text = province.name;
                provinceSelect.add(option);
            });
        });

    // Khi chọn tỉnh -> load huyện
    provinceSelect.addEventListener("change", () => {
        let provinceCode = provinceSelect.value;
        fetch(`https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`)
            .then(res => res.json())
            .then(data => {
                districtSelect.innerHTML = "<option disabled selected>Chọn quận/huyện</option>";
                data.districts.forEach(district => {
                    let option = document.createElement("option");
                    option.value = district.code;
                    option.text = district.name;
                    districtSelect.add(option);
                });
                wardSelect.innerHTML = "<option disabled selected>Chọn phường/xã</option>";
            });
    });

    // Khi chọn huyện -> load xã
    districtSelect.addEventListener("change", () => {
        let districtCode = districtSelect.value;
        fetch(`https://provinces.open-api.vn/api/d/${districtCode}?depth=2`)
            .then(res => res.json())
            .then(data => {
                wardSelect.innerHTML = "<option disabled selected>Chọn phường/xã</option>";
                data.wards.forEach(ward => {
                    let option = document.createElement("option");
                    option.value = ward.code;
                    option.text = ward.name;
                    wardSelect.add(option);
                });
            });
    });
});
