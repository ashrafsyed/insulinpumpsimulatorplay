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
        .when('/dashboard/:id', {
            templateUrl: insulinpumpapp.assetsPath + 'ng/partials/dashboard.html',
            controller: 'DoctorPanelCtrl'
          })
        .when('/register', {
            templateUrl: insulinpumpapp.assetsPath + 'ng/partials/doctorRegistration.html',
            controller: 'DoctorRegistrationCtrl'
          })
    /*       .when('/viewpost/:postId', {
                templateUrl: 'views/viewpost.html',
                controller: 'ViewpostCtrl'
              })
              .otherwise({
                redirectTo: '/'
              });
    */
}]);

doctorinterfaceapp.controller('DoctorLoginCtrl',['$scope','$http', '$log','$timeout','$location', '$route', function ($scope, $http, $log, $timeout, $location, $route) {

    $scope.doctorLogo = insulinpumpapp.assetsPath + "images/doctor.png";
    $scope.loginFormData = {
        doctorID : "",
        doctorPassword : ""
    }

    $scope.doctorLoginSubmit = function(form){
        $scope.loginfailed = false;
        if (form.$valid){
            console.log($scope.loginFormData.doctorID);
            var getDeviceDataUrl = "/rest/v1/doctorinterface/doctorlogin?doctorId="+ $scope.loginFormData.doctorID + "&password=" + $scope.loginFormData.doctorPassword ;
            $http.get(getDeviceDataUrl).success(function(response) {
                if (response.status == "success") {
                    $location.url("/dashboard/"+response.doctorId);
                    $route.reload();
                } else {
                    $scope.loginfailed = true;
                }

            })
        }
    }

    $scope.doctorRegister = function () {
        $location.url("/register");
        $route.reload();
    }
}]);

doctorinterfaceapp.controller('DoctorPanelCtrl',['$scope','$http', '$log','$timeout','$cookieStore', '$uibModal', function ($scope, $http, $log, $timeout, $cookieStore, $uibModal) {
    $scope.doctorId = window.location.hash.split("/")[2];
    $scope.doctorName = "";
    $scope.selectedLink = "";
    $scope.patientList = [];
    $scope.totalPatient = 0;

    var getDeviceDataUrl = "/rest/v1/doctorinterface/fetchdoctorprofile?doctorId="+ $scope.doctorId;
    $http.get(getDeviceDataUrl).success(function(response) {
        if (response.status == "success") {
            $scope.doctorName = response.doctorName;
        }

    })

    $scope.getPatientList = function () {
        var getPatientList = "/rest/v1/doctorinterface/getpatientlist?doctorId="+ $scope.doctorId;
        $http.get(getPatientList).success(function(response) {
            if (response.status == "success") {
                if (response.patientList.length == 0){
                    $scope.totalPatient = response.patientList.count;
                    console.log("Currently no patient has been added under you.")
                } else{
                    $scope.patientList = response.patientList;
                    $scope.totalPatient = response.patientList.count;
                }

                $scope.doctorName = response.doctorName;
            }

        })
    }

    $scope.linkClicked = function (linkName){
        console.log (linkName);
        if (linkName === "patientList"){
            $scope.getPatientList();
        } else if (linkName === 'addPatient'){
            swal({
                    title: 'Patient Data Form',
                    html:
                    '<div class="form-group"><label class="col-md-4 control-label">Patient Id</label><div class="col-md-offset-1 col-md-6"><input id="patientId" type="text" class="form-control" name="patientId" ng-required></div></div>' +
                    '<div class="form-group"><label class="col-md-4 control-label">Device Id</label><div class="col-md-offset-1 col-md-6"><input id="deviceId" type="text" class="form-control" name="deviceId" ng-required></div></div>',
                    focusConfirm: false,
                    preConfirm:()=>{
                    return[
                        $('#patientId').val(),
                        $('#deviceId').val()
                    ]
                }
            }).then(function (result){
                var addPatientUrl = "/rest/v1/doctorinterface/addpatient?doctorId="+ $scope.doctorId + "&patientId=" + result.value[0] + "&deviceId=" + result.value[1];
                $http.get(addPatientUrl).success(function(response) {
                    if (response.status == "success") {
                        swal({
                            title: "Patient added!",
                            type: "success",
                            text: "Patient added in your List successfully!!",
                            timer: 1500,
                            showConfirmButton: false
                        });

                    }else {
                        swal({
                            title: "Sorry",
                            type: "error",
                            text: "Patient addition failed!!",
                            timer: 1500,
                            showConfirmButton: false
                        });
                    }
                })
            }).catch(swal.noop)
        }
        $scope.selectedLink = linkName;
    }
    
    $scope.viewPatientRecord = function (patId, devId) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: insulinpumpapp.assetsPath + 'ng/partials/patientRecord.html',
            controller: 'PatientRecordCtrl',
            controllerAs: '$scope',
            size: 'lg',
            windowClass: 'right',
            resolve: {
                patientData: function () {
                    return {patientId: patId, deviceId: devId};
                }
            }
        });
        modalInstance.result.then(function (formData) {
            if (index>=0) {
                $scope.formData.actions[index] = formData;
            } else {
                $scope.formData.actions.push(formData);
            }
        }, function () {
            console.log("action Modal result promise rejected");
        });

/*
        $uibModal.open({
            templateUrl: insulinpumpapp.assetsPath + "ng/partials/patientRecord.html",
            controller: function ($scope, $uibModalInstance) {
                $scope.ok = function () {
                    $uibModalInstance.close();
                };

                $scope.cancel = function () {
                    $uibModalInstance.dismiss('cancel');
                };
            }
        })
*/
    }

}]);

doctorinterfaceapp.controller('PatientRecordCtrl',['$scope','$http', '$log','$timeout', '$route', '$uibModalInstance', 'patientData', function ($scope, $http, $log, $timeout, $route, $uibModalInstance, patientData) {

    $scope.patientId = patientData.patientId;
    $scope.patientName = "";
    $scope.deviceId = patientData.deviceId;

    $scope.getPatientData = function() {
        if ($scope.deviceId != "" && $scope.patientId != "") {
            var getPatientDataUrl = "/rest/v1/admininterface/getpatientdata?patientId=" + $scope.patientId + "&deviceId=" + $scope.deviceId;
            $http.get(getPatientDataUrl).success(function (response) {
                if (response.status == "success" && response.hasOwnProperty("data")) {
                    $scope.patientFormData = {
                        patientFirstName: response.data.firstName,
                        patientLastName: response.data.lastName,
                        patientGender: response.data.gender,
                        patientHeight: response.data.height,
                        patientWeight: response.data.weight,
                        patientAge: response.data.age,
                        emergencyContactEmail: response.data.email,
                        emergencyContactMobile: response.data.mobile
                    }
                    $scope.patientName = response.data.firstName;
                }
            })
        }
    }

    $scope.getPatientData();
}]);

doctorinterfaceapp.controller('DoctorRegistrationCtrl',['$scope','$http', '$log','$timeout','$cookieStore', '$location', '$route', function ($scope, $http, $log, $timeout, $cookieStore, $location, $route) {

    $scope.resetForm = function(){
        $scope.formdata = {
            doctorFirstName: "",
            doctorLastName: "",
            doctorId: "",
            password: "",
            doctorGender: "",
            doctorEmail: "",
            doctorMobile: ""
        }
    }

    $scope.submitDoctorRegistration = function(form){
        $scope.showErrMsg = false;
        if (form.$valid){
            var doctorRegistrationUrl = "/rest/v1/doctorinterface/registerdoctor";
            var data = {
                doctorFirstName: $scope.formdata.doctorFirstName,
                doctorLastName: $scope.formdata.doctorLastName,
                doctorId: $scope.formdata.doctorId,
                password: $scope.formdata.password,
                doctorGender: $scope.formdata.doctorGender,
                emailId: $scope.formdata.doctorEmail,
                mobileNumber: $scope.formdata.doctorMobile
            };

            $http.post(doctorRegistrationUrl, JSON.stringify(data)).success(function (result) {
                if (result.status == "success"){
                    swal({
                        title: "Saved",
                        type: "success",
                        text: "Registration Successful!!",
                        timer: 2000,
                        showConfirmButton: false
                    });
                    $location.url("/");
                    $route.reload();
                }else {
                    swal({
                        title: "Sorry",
                        type: "error",
                        text: "Registration Failed due to some issue in the back end!",
                        timer: 1500,
                        showConfirmButton: false
                    });

                }
            });
        } else {
            $scope.showErrMsg = true;
        }
    }

    $scope.resetForm();
}]);
