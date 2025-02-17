$(document).ready(function () {
    // Khi nhấn vào nút chọn sản phẩm
    $('.select-product').click(function () {
        let productId = $(this).data('product-id'); // Lấy ID sản phẩm từ nút chọn

        $.ajax({
            url: '/getProductDetails',
            type: 'GET',
            data: { productId: productId },
            success: function (response) {
                if (response.error) {
                    alert(response.error);
                } else {
                    $('#productDetailId').val(productId);
                    $('#productPrice').val(response.price);

                    // Xóa dữ liệu cũ trong dropdown
                    $('#styleSelect').empty().append('<option value="">Chọn kiểu dáng</option>').prop("disabled", true);
                    $('#colorSelect').empty().append('<option value="">Chọn màu sắc</option>').prop("disabled", true);
                    $('#sizeSelect').empty().append('<option value="">Chọn size</option>').prop("disabled", true);

                    // Cập nhật danh sách kiểu dáng
                    response.styles.forEach(function (style) {
                        $('#styleSelect').append(new Option(style.name, style.id));
                    });

                    $('#styleSelect').prop("disabled", false);
                    $('#productModal').modal('show');
                }
            }
        });
    });

    // Khi chọn kiểu dáng
    $('#styleSelect').change(function () {
        let productId = $('#productDetailId').val();
        let styleId = $(this).val();

        $('#colorSelect').empty().append('<option value="">Chọn màu sắc</option>').prop("disabled", true);
        $('#sizeSelect').empty().append('<option value="">Chọn size</option>').prop("disabled", true);

        if (styleId) {
            $.ajax({
                url: '/getColorsByStyle',
                type: 'GET',
                data: { productId: productId, styleId: styleId },
                success: function (response) {
                    response.colors.forEach(function (color) {
                        $('#colorSelect').append(new Option(color.name, color.id));
                    });
                    $('#colorSelect').prop("disabled", false);
                }
            });
        }
    });

    // Khi chọn màu sắc
    $('#colorSelect').change(function () {
        let productId = $('#productDetailId').val();
        let styleId = $('#styleSelect').val();
        let colorId = $(this).val();

        $('#sizeSelect').empty().append('<option value="">Chọn size</option>').prop("disabled", true);

        if (colorId) {
            $.ajax({
                url: '/getSizesByColor',
                type: 'GET',
                data: { productId: productId, styleId: styleId, colorId: colorId },
                success: function (response) {
                    response.sizes.forEach(function (size) {
                        $('#sizeSelect').append(new Option(size.name, size.id));
                    });
                    $('#sizeSelect').prop("disabled", false);
                }
            });
        }
    });

    // Xử lý khi submit form thêm sản phẩm vào đơn hàng
    $('#addProductForm').submit(function (event) {
        event.preventDefault();

        let orderId = $('#orderId').val();
        let productId = $('#productDetailId').val();
        let styleId = $('#styleSelect').val();
        let colorId = $('#colorSelect').val();
        let sizeId = $('#sizeSelect').val();
        let quantity = $('#quantity').val();

        $.ajax({
            url: '/addProduct',
            type: 'POST',
            data: {
                orderId: orderId,
                productId: productId,
                styleId: styleId,
                colorId: colorId,
                sizeId: sizeId,
                quantity: quantity
            },
            success: function (response) {
                alert("Sản phẩm đã được thêm!");
                $('#productModal').modal('hide');
                location.reload(); // Reload lại trang để cập nhật đơn hàng
            }
        });
    });
});
