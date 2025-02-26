$(document).ready(function () {
    // Load danh sách sản phẩm
    $.get("/products", function (data) {
        data.forEach(product => {
            $("#productSelect").append(new Option(product.name, product.id));
        });
    });

    // Khi chọn sản phẩm -> Load danh sách thương hiệu, màu sắc & size
    $("#productSelect").change(function () {
        let productId = $(this).val();
        $.get("/productDetails", {productId: productId}, function (data) {
            $("#brandSelect").empty().append('<option value="">Chọn thương hiệu</option>')
            $("#colorSelect").empty().append('<option value="">Chọn màu sắc</option>')
            $("#sizeSelect").empty().append('<option value="">Chọn size</option>')
            data.brands.forEach(brand => $("brandSelect").append(new Option(brand.name, brand.id)));
            data.colors.forEach(color => $("colorSelect").append(new Option(color.name, color.id)));
            data.sizes.forEach(size => $("#size").append(new Option(size.name, size.id)));
            $("#brandSelect, #colorSelect, #sizeSelect").prop("disable", false);
        });
    });

    // Khi chọn thương hiệu, màu sắc & size -> Hiển thị giá
    $("#brandSelect, #colorSelect, #sizeSelect").change(function () {
        let productId = $("#productSelect").val();
        let brandId = $("#brandSelect").val();
        let colorId = $("#colorSelect").val();
        let sizeId = $("#sizeSelect").val();
        if (brandId && colorId && sizeId){
            $.get("/productPrice", {productId, brandId, colorId, sizeId}, function (data) {
                if (data.error){
                    alert(data.error);
                }else{
                    $("#productPrice").val(data.price);
                    $("#productDetailId").val(data.productDetailId)
                }
            });
        }
    });

    // Xử lý thêm vào đơn hàng
    $("#addProductForm").submit(function (e) {
        e.preventDefault();
        let orderId = 9;
        let productDetailId = $("#productDetailId").val();
        let quantity = $("#quantity").val();
        $.post("/admin/orders/addProductToOrder", {orderId, productDetailId, quantity}, function () {
           alert("Sản phẩm đã được thêm!");
           $("#productModal").modal("hide");
        });
    });
});