app.controller("product-ctrl", function ($scope, $http) {
	const productButton = $('.product-button');
	const productTab = $('.tab-content');
	const cancelBtn = $('.cancel-delete-btn');
	const modalConfirm = $('#confirm');
	const deleteBtn = $('.btn-delete-product');

	deleteBtn.on('click', function () {
		modalConfirm.css('display', 'block');
	})

	cancelBtn.on('click', function () {
		modalConfirm.css('display', 'none');
	})

	productButton.on('click', function (e) {
		const btnTarget = e.target;
		if (btnTarget.matches('button')) {
			$(this).children().removeClass('active')
			$(btnTarget).addClass('active')
		}

		productTab.removeClass('active-tab');
		$(`#${btnTarget.id}-tab`).addClass('active-tab');
	})

	const listBtn = $('#admin-list-product');
	const createBtn = $('#admin-create-product');
	const listTab = $('#admin-list-product-tab');
	const createTab = $('#admin-create-product-tab');

	$scope.items = [];
	$scope.cates = [];
	$scope.form = {};
	$scope.initializeProductSize = function() {
		$scope.s = {
			id: null,
			size: "S",
			quantity: 0,
			product: {
				id: null
			}
		};
		$scope.m = {
			id: null,
			size: "M",
			quantity: 0,
			product: {
				id: null
			}
		};
		$scope.l = {
			id: null,
			size: "L",
			quantity: 0,
			product: {
				id: null
			}
		};
		$scope.xl = {
			id: null,
			size: "XL",
			quantity: 0,
			product: {
				id: null
			}
		};
		$scope.xxl = {
			id: null,
			size: "XXL",
			quantity: 0,
			product: {
				id: null
			}
		};	
	}

	$scope.initializeForm = function (){
		$scope.form = {
			createDate: new Date(),
			image: "upload.png",
			image1: "upload.png",
			image2: "upload.png",
			image3: "upload.png",
			available: true,
			price: 0
		}
	}

	$scope.initialize = function() {
		$scope.initializeForm();
		
		//load prodcuts
		$http.get("/rest/products").then(resp => {
			$scope.items = resp.data;
			$scope.items.forEach(item => {
				item.createDate = new Date(item.createDate)
			})
		})
		//load categories
		$http.get("/rest/categories").then(resp => {
			$scope.cates = resp.data;
		})
	}
	//initialize
	$scope.initialize();
	$scope.initializeProductSize();
	//delete form

	$scope.reset = function() {
		$scope.initializeForm();
		$scope.initializeProductSize();
	}
	//dísplay to the form
	$scope.edit = function (item) {
		$scope.form = angular.copy(item);
		let sizes = [$scope.s, $scope.m, $scope.l, $scope.xl, $scope.xxl];
		$http.get(`/rest/productsize?id=${item.id}`).then(resp => {
			let productSizes = resp.data;
			sizes.forEach((size, index) => {
				angular.extend(size, productSizes[index]);
			});
		})
		console.log(sizes)
		
		listBtn.removeClass('active');
		createBtn.addClass('active');
		listTab.removeClass('active-tab');
		createTab.addClass('active-tab');
	}
	//add new item

	$scope.create = function () {
		var item = angular.copy($scope.form);
		let sizes = [$scope.s, $scope.m, $scope.l, $scope.xl, $scope.xxl];
		$http.post(`/rest/products`, item).then(resp => {
			let productId = resp.data.id;
			resp.data.createDate = new Date(resp.data.createDate)
			$scope.items.push(resp.data);

			angular.forEach(sizes, function(size) {
				size.product.id = productId;
				$http.post('/rest/productsize', size);
			});

			$scope.reset();
			$scope.changeDefaultTab();
			alert("Create successfully!")
		}).catch(error => {
			alert("Error! Please try again");
			console.log("Error :", error);
		})
	
	}

	$scope.changeDefaultTab = function () {
		listBtn.addClass('active');
		createBtn.removeClass('active');
		listTab.addClass('active-tab');
		createTab.removeClass('active-tab');
	}

	//update the item
	$scope.update = function () {
		var item = angular.copy($scope.form);
		let sizes = [$scope.s, $scope.m, $scope.l, $scope.xl, $scope.xxl];
		let sizesOk = sizes.map((item) => angular.copy(item));
		angular.forEach(sizesOk, function(size) {
			$http.put('/rest/productsize/update', size);
		});

		$http.put(`/rest/products/${item.id}`, item).then(resp => {
			var index = $scope.items.findIndex(p => p.id == item.id);
			$scope.items[index] = item;
			$scope.changeDefaultTab();
			alert("Update successfully!");
		}).catch(error => {
			alert("Error");
			console.log("Error :", error);
		})
	}

	//delete the item
	$scope.delete = function (item) {
		var item = angular.copy($scope.form);
		let sizes = [$scope.s, $scope.m, $scope.l, $scope.xl, $scope.xxl];
		let sizesOk = sizes.map((item) => angular.copy(item));
		angular.forEach(sizesOk, function(size) {
			console.log(size.id)
			$http.delete(`/rest/productsize/delete/${size.id}`);
		});

		$http.delete(`/rest/products/${item.id}`).then(resp => {
			var index = $scope.items.findIndex(p => p.id == item.id);
			$scope.items.splice(index, 1);
			$scope.reset();
			$scope.changeDefaultTab();
			alert("Delete successfully!");
		}).catch(error => {
			alert("Error");
			console.log("Error :", error);
		})
	}

	$scope.imageChanged = function(files, imageName) {
    var data = new FormData();
    data.append('file', files[0]);
    $http.post('/rest/upload/images', data, {
        transformRequest: angular.identity,
        headers: { 'Content-Type': undefined }
    }).then(resp => {
        $scope.form[imageName] = resp.data.name;
    }).catch(error => {
        alert("Error");
        console.log("Error :", error);
    });
	}

	$scope.pager = {
		page: 0,
		size: 5,
		get items() {
			var start = this.page * this.size;
			return $scope.items.slice(start, start + this.size);
		},
		get count() {
			return Math.ceil(1.0 * $scope.items.length / this.size);
		},
		first() {
			this.page = 0;
		},
		prev() {
			this.page--;
			if (this.page < 0) {
				this.last();
			}
		},
		next() {
			this.page++;
			if (this.page >= this.count) {
				this.first();
			}
		},
		last() {
			this.page = this.count - 1;
		}
	}
});
