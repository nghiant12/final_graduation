const app = angular.module("cart", []);

app.controller("cart-ctrl", function ($scope, $http, $location) {
	$scope.userHome = {};

	const usernameProfile = $('#user-profile').val();
	if (usernameProfile !== undefined) {
		$http.get(`/rest/accounts/profile?username=${usernameProfile}`).then( resp => {
			console.log(resp.data)
			$scope.userHome = resp.data;
		})
	}
		
	$scope.updateUser = function() {
		const user = angular.copy($scope.userHome);
		$http.put('/rest/accounts/update', user).then(resp => {
			alert('Cập nhật tài khoản thành công!')
		}).catch(e => console.log(e));
	}

	$scope.reviewProduct = []

	const productIdDetail = $('.feedback').attr('product-id');

	if(productIdDetail !== undefined) {
		$http.get(`/rest/feedbacks?id=${productIdDetail}`).then(resp =>{
			$scope.reviewProduct = resp.data;
		}).catch(e => console.log(e))
	}

	$(".cart-hover").each(function () {
		const itemId = $(this).attr("item-size-id");
		const url = "/rest/products/size/" + itemId;

		$.ajax({
			url: url,
			type: "GET",
			success: function (response) {
				response.forEach(data => {
					const li = $('<li></li>')
						.text(`Sizes ${data.size} (${data.quantity})`)
						.click(function () {
							if (data.quantity > 0) {
								$scope.cart.add(itemId, data.size, 1);
							} else {
								return false;
							}
						})
						.css('cursor', data.quantity === 0 ? 'not-allowed' : 'pointer');

					$(this).append(li);
				});
			}.bind(this),
			error: function (xhr, status, error) {
				console.log(error);
			}
		});
	})

	$scope.isRatingChecked = function(star, rating) {
    return star === rating;
	};

	$scope.averageRating = function(reviewProduct) {
    var totalStars = 0;
    angular.forEach(reviewProduct, function(feedback) {
        totalStars += feedback.star;
    });

		if (totalStars === 0) {
			return 'chưa có dữ liệu'
		} else {
			return Math.round((totalStars / reviewProduct.length) * 10) / 10;
		}
	};

	$scope.detailReviews = []

	$scope.loadDetailReview = function(id) {
		$http.get(`/rest/orders/detail?id=${id}`).then(resp => {
			const needReivew = resp.data.filter(item => item.reviewstatus === false);
			$scope.detailReviews = needReivew;
		}).catch(e => {
			console.log(e);
		});
	}

	$scope.feedback = {
		reviewday: new Date(),
		star: '',
		review: '',
		product: {
			id: null
		},
		account: {
			username: null
		}
	}

	$scope.sendReview = function(order, id, index) {
		order.reviewstatus = true;
		let review = angular.copy($scope.feedback);
		review.product.id =  $(`#review-product-id-${id}`).val();
		review.account.username = $('#review-username').val();
		review.star = $('input[name="rating' + id + '"]:checked').val();
		review.review =  $(`#review-textarea-${id}`).val();

		$http.post('/rest/feedbacks/send', review).then(resp => {
			$scope.detailReviews.splice(index, 1);
			$http.put('/rest/orders/updateDetail', order);
			alert("Đánh giá thành công!")
		}).catch(e => {
			console.log(e)
		})
	}

	$scope.cart = {
		items: [],

		cancelOrder(id) {
			const confirm = window.confirm('Bạn có muốn hủy đơn hàng này?');
			if (confirm) {
				$http.get(`/rest/orders/${id}`).then(resp => {
					resp.data.status = "cancel";

					$http.put(`/rest/orders/update`, resp.data).then(resp => {
						location.href = '/order/list';
						alert('Hủy đơn hàng thành công');
					})
				}).catch(e => {
					console.log(e);
				});
			} else {
				return;
			}
		},

		//add items to cart
		add(id, size, quantity) {
			let item = this.items.find(item => item.id == id && item.size == size);
			console.log(item + 'test');
			if (item) {
				alert("Thêm vào giỏ hàng thành công!")
				item.qty++;
				this.saveToLocalStorage();
			} else {
				$http.get(`/rest/products/${id}`).then(resp => {
					alert("Thêm vào giỏ hàng thành công!")
					resp.data.qty = quantity;
					resp.data.size = size;
					this.items.push(resp.data);
					console.log(this.items);
					this.saveToLocalStorage();
				})
			}
		},

		addAndPay(id, size, quantity) {
			let item = this.items.find(item => item.id == id && item.size == size);
			if (item) {
				item.qty++;
				this.saveToLocalStorage();
				location.href = '/cart/view';
			} else {
				$http.get(`/rest/products/${id}`).then(resp => {
					resp.data.qty = quantity;
					resp.data.size = size;
					this.items.push(resp.data);
					console.log(this.items);
					this.saveToLocalStorage();
					location.href = '/cart/view';
				})
			}
		},

		getsize() {
			return $('#size-product').val();
		},

		getquantity() {
			return parseInt($('#quantity-product').val());
		},

		//remove item of cart
		remove(id) {
			let index = this.items.findIndex(item => item.id == id);
			this.items.splice(index, 1);
			this.saveToLocalStorage();
		},

		// clear all items in cart
		clear() {
			this.items = []
			this.saveToLocalStorage();
		},

		//return total of items
		get count() {
			return this.items.length
			// .map(item => item.qty)
			// .reduce((total, qty) => total += qty, 0);
		},

		//return total money of items

		get amount() {
			return this.items
				.map(item => item.qty * item.price)
				.reduce((total, qty) => total += qty, 0)
		},



		//save the cart at localStorage
		saveToLocalStorage() {
			let json = JSON.stringify(angular.copy(this.items));
			localStorage.setItem("cart", json);
		},

		//display data to table
		loadFromLocalStorage() {
			let json = localStorage.getItem("cart");
			this.items = json ? JSON.parse(json) : [];
		}

	}
	$scope.cart.loadFromLocalStorage();

	$scope.order = {
		createDate: new Date(),
		address: $('#user-address').val(),
		phoneNumber: $('#user-sdt').val(),
		account: {
			username: $("#username").val()
		},
		status: 'confirmed',
		pay: false,
		totalAmount: $scope.cart.amount,
		get orderDetails() {
			return $scope.cart.items.map(item => {
				return {
					product: { id: item.id },
					size: item.size,
					quantity: item.qty,
					reviewstatus: false
				}
			});
		},

		purchase() {
			const payment = $('#payment').val();
			if (payment === '2') {
				const bank = selectedValue = $('input[name="bank"]:checked').val();
				const money = parseInt($scope.cart.amount);
				$http.post(`/createPayment?amount=${money}&bankCode=${bank}`).then(resp => {
					location.href = resp.data.data;
					let order = angular.copy(this);
					order.pay = true;
					$http.post("/rest/orders", order).then(resp => {
						console.log('success!')
					}).catch(error => {
						console.log(error);
					})
				}).catch(e => console.log(e));
			} else {
				let order = angular.copy(this);

				$http.post("/rest/orders", order).then(resp => {
					alert("Đặt hàng thành công!");
					$scope.cart.clear();
					location.href = "/order/detail/" + resp.data.id;
				}).catch(error => {
					console.log(error);
				})
			}
		}
	}

	if ($location.absUrl().includes('/order/list') && $location.absUrl().includes('vnp_ResponseCode')){
		alert('Đặt hàng thành công');
		$scope.cart.clear();
	}
})
