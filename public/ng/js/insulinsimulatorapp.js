'use strict';
/**
 * @ngdoc overview
 * @name insulinsimulatorapp
 * @description
 * # insulinsimulatorapp
 *
 * Main module of the application.
 */
var insulinsimulatorapp = angular.module('insulinsimulatorapp', [
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

insulinsimulatorapp.config(['$routeProvider', function ($routeProvider) {
      $routeProvider
          .when('/', {
            templateUrl: insulinpumpapp.assetsPath + "ng/partials/login.html",
            controller: 'LoginCtrl'
          })
          .when('/signup', {
            templateUrl: insulinpumpapp.assetsPath + "ng/partials/signup.html",
            controller: 'SignupCtrl'
          })

          .when('/dashboard', {
            templateUrl: insulinpumpapp.assetsPath + "ng/partials/dashboard.html",
            controller: 'DashboardCtrl'
          })
/*          .when('/login', {
            templateUrl: 'views/login.html',
            controller: 'LoginCtrl'
          })
          .when('/addpost', {
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

insulinsimulatorapp.directive('rdLoading', rdLoading);
    function rdLoading() {
        var directive = {
            restrict: 'AE',
            template: '<div class="loading"><div class="double-bounce1"></div><div class="double-bounce2"></div></div>'
        };
        return directive;
};

insulinsimulatorapp.directive('rdWidgetBody', rdWidgetBody);
function rdWidgetBody() {
    var directive = {
        requires: '^rdWidget',
        scope: {
            loading: '=?',
            classes: '@?'
        },
        transclude: true,
        template: '<div class="widget-body" ng-class="classes"><rd-loading ng-show="loading"></rd-loading><div ng-hide="loading" class="widget-content" ng-transclude></div></div>',
        restrict: 'E'
    };
    return directive;
};


insulinsimulatorapp.directive('rdWidgetFooter', rdWidgetFooter);
function rdWidgetFooter() {
    var directive = {
        requires: '^rdWidget',
        transclude: true,
        template: '<div class="widget-footer" ng-transclude></div>',
        restrict: 'E'
    };
    return directive;
};

insulinsimulatorapp.directive('rdWidgetHeader', rdWidgetTitle);
function rdWidgetTitle() {
    var directive = {
        requires: '^rdWidget',
        scope: {
            title: '@',
            icon: '@'
        },
        transclude: true,
        template: '<div class="widget-header"><div class="row"><div class="pull-left"><i class="fa" ng-class="icon"></i> {{title}} </div><div class="pull-right col-xs-6 col-sm-4" ng-transclude></div></div></div>',
        restrict: 'E'
    };
    return directive;
};

insulinsimulatorapp.directive('rdWidget', rdWidget);
function rdWidget() {
    var directive = {
        transclude: true,
        template: '<div class="widget" ng-transclude></div>',
        restrict: 'EA'
    };
    return directive;

    function link(scope, element, attrs) {
        /* */
    }
};

/**
 * @ngdoc function
 * @name clientApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the clientApp
 */
insulinsimulatorapp.controller('LoginCtrl',['$scope','$http', '$log', '$location', function ($scope, $http, $log, $location) {
    $scope.userType = [
        {name: 'Patient', value: 'PATIENT'},
        {name: 'Doctor', value: 'DOCTOR'}
    ];

    $scope.login = function() {

        var payload = {
            userType : this.userType,
            password : this.password
        };

        $http.post('/app/login', payload)
/*
            .error(function(data, status){
                if(status === 400) {
                    angular.forEach(data, function(value, key) {
                        if(key === 'email' || key === 'password') {
                            alertService.add('danger', key + ' : ' + value);
                        } else {
                            alertService.add('danger', value.message);
                        }
                    });
                } else if(status === 401) {
                    alertService.add('danger', 'Invalid login or password!');
                } else if(status === 500) {
                    alertService.add('danger', 'Internal server error!');
                } else {
                    alertService.add('danger', data);
                }
            })
*/
            .success(function(response){
                $location.path('/dashboard');
                $log.debug(response);
                if(response.hasOwnProperty('success')) {
                }
            });
    };

}]);

insulinsimulatorapp.controller('SignupCtrl',['$scope','$http', '$log', function ($scope, $http, $log) {
        $scope.signup = function() {
            var payload = {
                email : $scope.email,
                password : $scope.password
            };

            $http.post('app/signup', payload)
                .success(function(data) {
                    $log.debug(data);
                });
        };
    }]);

insulinsimulatorapp.controller('DashboardCtrl',['$scope','$http', '$log','$timeout','$cookieStore', function ($scope, $http, $log, $timeout, $cookieStore) {
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
