angular.module('myApp', []).
    config(function ($routeProvider, $locationProvider) {
        $routeProvider.
            when('/', {
                templateUrl: 'partial/dashboard-home-partial',
                controller: UserDBCtrl
            }).
            when('/test-dashboard', {
                templateUrl: 'partial/tests-partial',
                controller: UserDBCtrl
            }).
            when('/api/queue/:id', {
                templateUrl: 'partial/dashboard-home-partial',
                controller: UserDBCtrl
            }).
            otherwise({
                redirectTo: '/user-dashboard'
            });
});
