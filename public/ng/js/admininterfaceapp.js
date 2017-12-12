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
        if ($scope.powerOptionValue == true){
            swal({
                title: 'Please enter the 5 digit Admin PIN!',
                input: 'password',
                showCancelButton: true,
                confirmButtonText: 'Submit',
                inputPlaceholder: 'Enter your 5 digit PIN',
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
                            //TODO Fetch data frpm backend if already exists
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

    $scope.patientFormData = {
        patientFirstName: "",
        patientLastName: "",
        patientGender: "",
        patientHeight: 0,
        patientWeight: 0,
        patientAge:0,
        emergencyContactEmail:"",
        emergencyContactMobile:""
    }

    $scope.deviceConfig = angular.copy($scope.originalDeviceConfig);

    $scope.resetForm = function (form) {
        $scope.initForm();
    }

    $scope.savePatientData = function (form) {
        $scope.showErrMsg = false;
        if (form.$valid){
            var savePatientDataUrl = "/rest/v1/admininterface/savepatientdata";
            var data = {
                patientFirstName: $scope.patientFormData.patientFirstName,
                patientLastName: $scope.patientFormData.patientLastName,
                patientGender: $scope.patientFormData.patientGender,
                patientHeight: parseInt($scope.patientFormData.patientHeight),
                patientWeight: parseInt($scope.patientFormData.patientWeight),
                patientAge: parseInt($scope.patientFormData.patientAge),
                emailId: $scope.patientFormData.emergencyContactEmail,
                mobileNumber: $scope.patientFormData.emergencyContactMobile
            };

            $http.post(savePatientDataUrl, JSON.stringify(data)).success(function (result) {
                swal({
                    title: "Saved",
                    type: "success",
                    text: "Patient Data Saved Successfully!!",
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

