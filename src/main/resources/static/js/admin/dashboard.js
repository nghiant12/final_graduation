app.controller("dashboard-ctrl", function($scope, $http) {
  var currentDate = new Date();
  var sixDaysAgo = new Date(currentDate);
  sixDaysAgo.setDate(currentDate.getDate() - 6);

	$http.get("/rest/orders/totalAmountCurrentDay").then(resp => {
		$scope.sale = resp.data;
	})

	$http.get("/rest/accounts/signupCurrentDay").then(resp => {
		$scope.signupCurrentDay = resp.data;
	})

	$http.get("/rest/orders/totalProductSold").then(resp => {
		$scope.totalProductSold = resp.data;
	})

	const top3chart = document.getElementById('top3product');

  $.ajax({
    url: '/rest/orders/top5ProductAWeek',
    type: 'GET',
    success: function(data) {
			const limit_data = data.slice(0,3);
      const labels = limit_data.map((item) => item[0]);
      const data_labels = limit_data.map((item) => item[1]);
      const data_chart = {
        labels: labels,
        datasets: [{
          label: 'Quantity',
          data: data_labels,
          backgroundColor: [
            'rgb(255, 99, 132)',
            'rgb(54, 162, 235)',
            'rgb(255, 205, 86)'
          ],
          hoverOffset: 4
        }]
      };
      const config = {
        type: 'doughnut',
        data: data_chart,
      };

      new Chart(top3chart, config);
    },
    error: function(xhr, status, error) {
      console.error(xhr.responseText);
    }
  });
  
  const revenueAWeekChart = document.getElementById('revenue');

  $scope.loadAvenue = function(startDay, endDay) {
    $.ajax({
      url: '/rest/orders/revenueAWeek',
      type: 'GET',
      data: {
        start: startDay,
        end: endDay
      },
      success: function(data) {
        const labels = data.map((item) => item[0]);
        const data_labels = data.map((item) => item[1]);
        const data_chart = {
          labels: labels,
          datasets: [{
            label: 'Revenue',
            data: data_labels,
            fill: false,
            borderColor: 'rgb(75, 192, 192)',
            tension: 0.1
          }]
        };
        const config = {
          type: 'line',
          data: data_chart,
          options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
          }
        };
  
        if (window.revenueChart) {
          window.revenueChart.destroy();
        }

        window.revenueChart = new Chart(revenueAWeekChart, config);
        },
      error: function(xhr, status, error) {
        console.error(xhr.responseText);
      }
    });
  }

  $scope.loadAvenue(formatDate(sixDaysAgo), formatDate(currentDate));
  
  $scope.startDate = sixDaysAgo;
  $scope.endDate = currentDate;

  $scope.filterAvenue = function() {
    $scope.loadAvenue(formatDate($scope.startDate), formatDate($scope.endDate));
  }
  
  function formatDate(date) {
    var year = date.getFullYear();
    var month = ('0' + (date.getMonth() + 1)).slice(-2);
    var day = ('0' + date.getDate()).slice(-2);
    return year + '-' + month + '-' + day;
  }
}); 
