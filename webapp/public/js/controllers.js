function UserDBCtrl($scope, $http, $location, $routeParams) {
    $http.get('/api/list').success(function(data, status, headers, config){
        $scope.businesses = data.businesses;
    });
    $scope.test = function() {
        return "hello world";
    }
    $scope.queue = function(id) {
        alert(id);
        $scope.currentBiz = $routeParams.id;
    }
}
