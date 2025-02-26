function previewImage(input) {
    var preview = document.getElementById('preview');
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
        }
        reader.readAsDataURL(input.files[0]);
    } else {
        preview.src = '#';
        preview.style.display = 'none';
    }
}

$(document).ready(function() {
    $('#addProductDetailForm').submit(function(e) {
        e.preventDefault();
        var formData = new FormData(this);
        $.ajax({
            url: '/admin/product-details/add',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(newProduct) {
                $('#addProductDetailModal').modal('hide');
                $('body').removeClass('modal-open');
                $('.modal-backdrop').remove();
                addProductToTable(newProduct);
                $('#addProductDetailForm')[0].reset();
                $('#preview').attr('src', '#').hide();
            },
            error: function(xhr, status, error) {
                console.error(error);
                alert('Failed to add product detail. Please try again.');
            }
        });
    });

    function addProductToTable(product) {
        var newRow = `
    <tr>
        <td>${product.id}</td>
        <td>${product.product.name}</td>
        <td>
            ${product.image ? `<img src="${product.image}" alt="Product Image" width="50" height="50">` : '<span>No image</span>'}
        </td>
        <td>${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.price)}</td>
        <td>${product.quantity}</td>
        <td>${new Date(product.createdDate).toLocaleDateString('vi-VN', {day: '2-digit', month: '2-digit', year: 'numeric'})}</td>
        <td>${product.available ? 'Đang kinh doanh' : 'Ngưng kinh doanh'}</td>
        <td>${product.category.name}</td>
        <td>${product.size.name}</td>
        <td>${product.color.name}</td>
        <td>${product.brand.name}</td>
        <td>
            <a href="/admin/product-details/edit/${product.id}" class="btn btn-primary btn-sm">
                <i class="fas fa-edit"></i>
            </a>
            <a href="/admin/product-details/delete/${product.id}" class="btn btn-danger btn-sm" onclick="return confirm('Bạn có chắc chắn muốn xóa?')">
                <i class="fas fa-trash"></i>
            </a>
            <a href="/admin/product-details/detail/${product.id}" class="btn btn-info btn-sm">
                <i class="fas fa-eye"></i>
            </a>
        </td>
    </tr>
    `;
        $('#productDetailTable tbody').prepend(newRow);
    }

    $('#addProductDetailModal').on('hidden.bs.modal', function () {
        $('body').removeClass('modal-open');
        $('.modal-backdrop').remove();
        $('#addProductDetailForm')[0].reset();
        $('#preview').attr('src', '#').hide();
    });
});
//Update
function updateProductInTable(product) {
    var row = $('tr[data-id="' + product.id + '"]');
    row.find('td:eq(1)').text(product.product.name);
    row.find('td:eq(2)').html(product.image ? `<img src="${product.image}" alt="Product Image" width="50" height="50">` : 'No image');
    row.find('td:eq(3)').text(new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.price));
    row.find('td:eq(4)').text(product.quantity);
    row.find('td:eq(5)').text(new Date(product.createdDate).toLocaleDateString('vi-VN'));
    row.find('td:eq(6)').text(product.available ? 'Đang kinh doanh' : 'Ngưng kinh doanh');
    row.find('td:eq(7)').text(product.category.name);
    row.find('td:eq(8)').text(product.size.name);
    row.find('td:eq(9)').text(product.color.name);
    row.find('td:eq(10)').text(product.brand.name);
}

function populateUpdateForm(product) {
    $('#updateId').val(product.id);
    $('#updateProduct').val(product.product.id);
    $('#updatePrice').val(product.price);
    $('#updateQuantity').val(product.quantity);
    if (product.createdDate) {
        $('#updateCreatedDate').val(new Date(product.createdDate).toISOString().split('T')[0]);
    }
    $('#updateAvailable').val(product.available.toString());
    $('#updateCategory').val(product.category.id);
    $('#updateSize').val(product.size.id);
    $('#updateColor').val(product.color.id);
    $('#updateBrand').val(product.brand.id);
    $('#updatePreviewImage').attr('src', product.image)[product.image ? 'show' : 'hide']();
}

$('#updateProductDetailModal').on('show.bs.modal', function (event) {
    var productId = $(event.relatedTarget).data('id');
    $.ajax({
        url: '/admin/product-details/' + productId,
        type: 'GET',
        success: populateUpdateForm,
        error: function(xhr, status, error) {
            console.error('Error fetching product details:', error);
            toastr.error('Failed to load product details. Please try again.');
        }
    });
});

$('#updateProductDetailForm').submit(function(e) {
    e.preventDefault();
    var formData = new FormData(this);
    $.ajax({
        url: '/admin/product-details/update/' + formData.get('id'),
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            $('#updateProductDetailModal').modal('hide');
            updateProductInTable(response);
            toastr.success('Sản phẩm đã được cập nhật thành công');
            setTimeout(function() {
                location.reload();
            }, 1000);
        },
        error: function(xhr, status, error) {
            console.error('Error updating product:', error);
            toastr.error(xhr.responseJSON?.message || 'Có lỗi xảy ra khi cập nhật sản phẩm');
        }
    });
});

$('#updateProductDetailBtn').click(function() {
    $('#updateProductDetailForm').submit();
});

