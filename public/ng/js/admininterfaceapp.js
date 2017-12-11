'use strict';
/**
 * @ngdoc overview
 * @name admininterfaceapp
 * @description
 * # admininterfaceapp
 *
 * Main module of the application.
 */
var admininterfaceapp = angular.module('admininterfaceapp', [
    "ngRoute",
    "ngCookies",
    "ngAnimate",
    'ngSanitize',
    'ui.select',
    'ngMessages',
    'ngTouch',
    "ngResource",
    "ui.toggle",
    'ui.bootstrap'
]);

admininterfaceapp.directive('dateAsMs', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope,elem,attrs,ngModelCtrl) {
            ngModelCtrl.$parsers.push(function(value){
                if (value && value.getTime) {
                    return value.getTime();
                } else {
                    return value;
                }
            });
        }
    };
});

admininterfaceapp.config(['$routeProvider', function ($routeProvider) {
      $routeProvider
          .when('/', {
            templateUrl: insulinpumpapp.assetsPath + "ng/partials/admininterface.html",
            controller: 'AdminInterfaceCtrl'
          })
    }]);

/**
 * @ngdoc function
 * @name admininterfaceapp.controller:AdminInterfaceCtrl
 * @description
 * # AdminInterfaceCtrl
 * Controller of the AdminInterfaceCtrl
 */
admininterfaceapp.controller('AdminInterfaceCtrl',['$scope','$http', '$log', '$location', function ($scope, $http, $log, $location) {
    $scope.adminAuthorized = false;
    $scope.formSelected = {
        patientProfile: false,
        deviceConfig:false
    }

    $scope.powerSwitch = function () {
        console.log($scope.powerOptionValue)
        if ($scope.powerOptionValue == true){
            swal({
                title: "Are you the Admin?",
                text: "Please enter the PIN",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                inputPlaceholder: "5 digit PIN"
            }, function (inputValue) {
                if (inputValue === false) {
                    $scope.powerOptionValue = false;
                    swal.close();
                    return false;
                }
                if (inputValue === "") {
                    swal.showInputError("Incorrect PIN");
                    return false
                }

                var url = '/rest/v1/admininterface/authorize?adminpin=' + inputValue;
                $http.get(url).success(function(result) {
                        if (result.status == "success") {
                            $scope.adminAuthorized = true;
                            swal.close();
                        }
                        if (result.status == "error") {
                            swal.showInputError(result.message);
                        }
                    })
                    .error(function(result){
                        swal.showInputError("Incorrect PIN");
                    });

                });
            } else {
                $scope.adminAuthorized = false;
            }
        }

    $scope.formDisplay = function (formSelected) {
        if (formSelected === "patientprofile"){
            $scope.formSelected.deviceConfig = false;
            $scope.formSelected.patientProfile = true;
        }else if (formSelected === "deviceconfig") {
            $scope.formSelected.patientProfile = false;
            $scope.formSelected.deviceConfig = true;
        }
    }

    $scope.formData = {
        patientFirstName: "",
        patientLastName: "",
        patientGender: "",
        patientHeight: "",
        patientWeight: "",
        patientDOB:""
    }

    $scope.deviceConfig = angular.copy($scope.originalDeviceConfig);

    $scope.resetForm = function (form) {
        $scope.initForm();
    }

    $scope.saveConfig = function (form) {
        $scope.showErrMsg = false;
        if (form.$valid){
            var saveConfigUrl = "/rest/v1/admininterface/saveconfig";
            var data = {
                patientFirstName: $scope.formData.patientFirstName,
                patientLastName: $scope.formData.patientLastName,
                patientGender: $scope.formData.patientGender,
                patientHeight: $scope.formData.patientHeight,
                patientWeight: $scope.formData.patientWeight,
                patientDOB: $scope.formData.patientDOB
            };

            $http.post(saveConfigUrl, JSON.stringify(data)).success(function (result) {
                swal({
                    title: "Saved",
                    type: "success",
                    text: "Configurations Saved Successfully!!",
                    timer: 1500,
                    showConfirmButton: false
                });
                // document.location.href = '/onsitepush/#/overview?type=campaign';
            });
        } else {
            $scope.showErrMsg = true;
        }
    }

    $scope.clear = function() {
        $scope.formData.patientDOB = null;
    };
    $scope.dateOptions = {
        formatYear: 'yy',
        maxDate: new Date(),
        minDate: new Date(1900,1,1),
        startingDay: 1
    };
    $scope.open2 = function() {
        $scope.popup2.opened = true;
    };
    $scope.setDate = function(year, month, day) {
        $scope.formData.patientDOB = new Date(year, month, day);
    };

    $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
    $scope.format = $scope.formats[0];
    $scope.altInputFormats = ['M!/d!/yyyy'];

    $scope.popup1 = {
        opened: false
    };

    $scope.popup2 = {
        opened: false
    };

}]);

