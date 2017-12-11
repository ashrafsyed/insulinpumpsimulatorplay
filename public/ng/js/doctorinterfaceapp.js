'use strict';
/**
 * @ngdoc overview
 * @name doctorinterfaceapp
 * @description
 * # doctorinterfaceapp
 *
 * Main module of the application.
 */
var doctorinterfaceapp = angular.module('doctorinterfaceapp', [
    "ngRoute",
    "ngCookies",
    "ngAnimate",
    'ngSanitize',
    'ui.select',
    'ngMessages',
    'ngTouch',
    'ui.bootstrap',
    "ngResource"
]);

doctorinterfaceapp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: insulinpumpapp.assetsPath + "ng/partials/doctorLogin.html",
            controller: 'DoctorLoginCtrl'
        })
        .when('/dashboard', {
            templateUrl: 'views/dashboard.html',
            controller: 'DoctorPanelCtrl'
          })
    /*      .when('/addpost', {
                templateUrl: 'views/addpost.html',
                controller: 'AddpostCtrl'
              })
              .when('/viewpost/:postId', {
                templateUrl: 'views/viewpost.html',
                controller: 'ViewpostCtrl'
              })
              .otherwise({
                redirectTo: '/'
              });
    */
}]);

doctorinterfaceapp.controller('DoctorLoginCtrl',['$scope','$http', '$log','$timeout','$location', '$route', function ($scope, $http, $log, $timeout, $location, $route) {

    $scope.loginFormData = {
        doctorID : "",
        doctorPassword : ""
    }

    $scope.doctorLoginSubmit = function(form){
        if (form.$valid){
            console.log($scope.loginFormData.doctorID);
            $location.url("/dashboard");
            $route.reload();
        }
    }
}]);

doctorinterfaceapp.controller('DoctorPanelCtrl',['$scope','$http', '$log','$timeout','$cookieStore', function ($scope, $http, $log, $timeout, $cookieStore) {
    /**
     * Sidebar Toggle & Cookie Control
     */
    var mobileView = 992;

    $scope.getWidth = function() {
        return window.innerWidth;
    };

    $scope.$watch($scope.getWidth, function(newValue, oldValue) {
        if (newValue >= mobileView) {
            if (angular.isDefined($cookieStore.get('toggle'))) {
                $scope.toggle = ! $cookieStore.get('toggle') ? false : true;
            } else {
                $scope.toggle = true;
            }
        } else {
            $scope.toggle = false;
        }

    });

    $scope.toggleSidebar = function() {
        $scope.toggle = !$scope.toggle;
        $cookieStore.put('toggle', $scope.toggle);
    };

    window.onresize = function() {
        $scope.$apply();
    };
}]);
