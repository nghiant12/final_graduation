var app = angular.module("myApp", []);

    app.controller("UserCtrl", function ($scope, $http) {
    // username không cần lấy từ input hidden nữa vì API lấy user theo auth

    // Load profile khi trang load
    $http
        .get("/api/profile")
        .then(function (response) {
            $scope.userHome = response.data;
            // Không cần gán username từ input hidden nữa
        })
        .catch(function (error) {
            alert("Không tải được thông tin người dùng!");
        });

    $scope.updateUser = function () {
    // PUT vào /api/profile, không cần username trong URL
    $http
    .put("/api/profile", $scope.userHome)
    .then(function (response) {
    alert("Cập nhật thành công!");
    // Clear password field sau khi cập nhật
    $scope.userHome.password = "";
})
    .catch(function (error) {
    alert("Cập nhật thất bại!");
});
};
});

    $(document).ready(function () {
    $("#eyeIcon").click(function () {
        var passwordField = $("#passwordField");
        var eyeIcon = $(this);
        if (passwordField.attr("type") === "password") {
            passwordField.attr("type", "text");
            eyeIcon.removeClass("fa-eye-slash").addClass("fa-eye");
        } else {
            passwordField.attr("type", "password");
            eyeIcon.removeClass("fa-eye").addClass("fa-eye-slash");
        }
    });
});