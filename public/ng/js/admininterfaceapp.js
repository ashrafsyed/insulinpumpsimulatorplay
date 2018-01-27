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
admininterfaceapp.controller('AdminInterfaceCtrl',['$scope','$http', '$log', '$location', '$route', function ($scope, $http, $log, $location, $route) {
    $scope.adminAuthorized = false;
    $scope.deviceId = "";
    $scope.patientId = "";
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
                        }, 1000)
                    })
                },
                allowOutsideClick: false
            }).then((result)=>{
                if (result.value){
                    var url = '/rest/v1/admininterface/authorize?adminpin=' + result.value;
                    $http.get(url).success(function(response) {
                        if (response.status == "success") {
                            $scope.deviceId = response.deviceId;
                            $scope.patientId = response.patientId;
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
            if ($scope.deviceId != "" && $scope.patientId != ""){
                var getPatientDataUrl = "/rest/v1/admininterface/getpatientdata?patientId="+ $scope.patientId + "&deviceId=" + $scope.deviceId ;
                $http.get(getPatientDataUrl).success(function(response) {
                    if (response.status == "success" && response.hasOwnProperty("data"))
                    {
                        $scope.patientFormData = {
                            patientFirstName: response.data.firstName,
                            patientLastName: response.data.lastName,
                            patientGender: response.data.gender,
                            patientHeight: response.data.height,
                            patientWeight: response.data.weight,
                            patientAge:response.data.age,
                            emergencyContactEmail: response.data.email,
                            emergencyContactMobile:response.data.mobile
                        }
                    }
                })
            }
            $scope.formSelected.deviceConfig = false;
            $scope.formSelected.patientProfile = true;
        }else if (formSelected === "deviceconfig") {
            if ($scope.deviceId != "" && $scope.patientId != ""){
                var getDeviceDataUrl = "/rest/v1/admininterface/getdevicedata?patientId="+ $scope.patientId + "&deviceId=" + $scope.deviceId ;
                $http.get(getDeviceDataUrl).success(function(response) {
                    if (response.status == "success" && response.hasOwnProperty("data"))
                    {
                        $scope.deviceConfigData = {
                            batteryLevel: response.data.battery,
                            insulinLevel: response.data.insulin,
                            glucagonLevel: response.data.glucagon,
                            deviceMode: response.data.deviceMode,
                            bolusMax: response.data.bolusMax,
                            dailyMax:response.data.dailyMax,
                            targetBgl:response.data.targetBgl
                        }
                    }
                })
            }
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

    $scope.deviceConfigData = {
        targetBgl: "",
        deviceMode: "",
        bolusMax: "",
        dailyMax: ""
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
                deviceId: $scope.deviceId,
                patientId: $scope.patientId,
                patientFirstName: $scope.patientFormData.patientFirstName,
                patientLastName: $scope.patientFormData.patientLastName,
                patientGender: $scope.patientFormData.patientGender,
                patientHeight: parseFloat($scope.patientFormData.patientHeight),
                patientWeight: parseFloat($scope.patientFormData.patientWeight),
                patientAge: parseInt($scope.patientFormData.patientAge),
                emailId: $scope.patientFormData.emergencyContactEmail,
                mobileNumber: $scope.patientFormData.emergencyContactMobile
            };

            $http.post(savePatientDataUrl, JSON.stringify(data)).success(function (result) {
                if (result.status == "success"){
                    swal({
                        title: "Saved",
                        type: "success",
                        text: "Please send your device_id - " + result.deviceId + " and patient_id -" + result.patientId + " to your doctor.",
                        showConfirmButton: true
                    });
                    $scope.formSelected = {
                        patientProfile: false,
                        deviceConfig:false
                    }
                    // document.location.href = '/onsitepush/#/overview?type=campaign';
                }else {
                    swal({
                        title: "Sorry",
                        type: "error",
                        text: "Patient Data could not be Saved!!",
                        timer: 1500,
                        showConfirmButton: false
                    });

                }
            });
        } else {
            $scope.showErrMsg = true;
        }
    }

    $scope.saveConfigData = function (form) {
        $scope.showErrMsg = false;
        if (form.$valid){
            var saveConfigDataUrl = "/rest/v1/admininterface/saveconfigdata";
            var data = {
                deviceId: $scope.deviceId,
                patientId: $scope.patientId,
                battery: parseFloat(100.00),
                insulin: parseFloat(100.00),
                glucagon: parseFloat(100.00),
                deviceMode: $scope.deviceConfigData.deviceMode,
                targetBgl: $scope.deviceConfigData.targetBgl,
                bolusMax: $scope.deviceConfigData.bolusMax,
                dailyMax: $scope.deviceConfigData.dailyMax
            };

            $http.post(saveConfigDataUrl, JSON.stringify(data)).success(function (result) {
                if (result.status == "success"){
                    swal({
                        title: "Saved",
                        type: "success",
                        text: "Please send your device_id : " + result.deviceId + " and \n patient_id : " + result.patientId + " to your doctor.",
                        showConfirmButton: true
                    });

                    $scope.formSelected = {
                        patientProfile: false,
                        deviceConfig:false
                    }
                }else {
                    swal({
                        title: "Sorry",
                        type: "error",
                        text: "Configuration data could not be Saved!!",
                        timer: 1500,
                        showConfirmButton: false
                    });

                }
            });
        } else {
            $scope.showErrMsg = true;
        }
    }

}]);

