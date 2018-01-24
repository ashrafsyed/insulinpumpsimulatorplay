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

doctorinterfaceapp.controller('DoctorPanelCtrl',['$scope','$http', '$log','$timeout','$cookieStore', function ($scope, $http, $log, $timeout, $cookieStore) {
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
        }
        $scope.selectedLink = linkName;
    }

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
