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

    $scope.patientAgelist = [
        {id: 10, ageValue: 10},
        {id: 11, ageValue: 11},
        {id: 12, ageValue: 12},
        {id: 13, ageValue: 13},
        {id: 14, ageValue: 14},
    ];




    $scope.powerSwitch = function () {
        console.log($scope.powerOptionValue)
        if ($scope.powerOptionValue == true){
            swal({
                title: 'Please enter the 5 digit Admin PIN!',
                input: 'password',
                showCancelButton: true,
                confirmButtonText: 'Submit',
                inputPlaceholder: 'Enter your password',
                showLoaderOnConfirm: true,
                inputAttributes: {
                    'maxlength': 5,
                    'autocapitalize': 'off',
                    'autocorrect': 'off'
                },
                preConfirm: (password) => {
                    return new Promise((resolve) => {
                        setTimeout(() => {
                            if (password === '') {
                                swal.showValidationError('Incorrect PIN!!');
                            }
                            resolve()
                        }, 2000)
                    })
                },
                allowOutsideClick: false
            }).then((result) => {
                if (result.value) {
                    var url = '/rest/v1/admininterface/authorize?adminpin=' + result.value;
                    $http.get(url).success(function(response) {
                        if (response.status == "success") {
                            $scope.adminAuthorized = true;
                            swal.close();
                        }
                        if (response.status == "error") {
                            swal.showValidationError(response.message);
                        }
                    })
                    .error(function(response){
                        swal.showValidationError("Incorrect PIN");
                    })
                }
            })
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
        patientAge:""
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
                patientAge: $scope.formData.patientAge
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


}]);

