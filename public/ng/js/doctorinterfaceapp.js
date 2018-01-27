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

    });

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
    };

    $scope.updateDeviceConfig = function(targetBgl, dailyMax, bolusMax, deviceMode){
        var saveConfigDataUrl = "/rest/v1/admininterface/saveconfigdata";
        var data = {
            deviceId: $scope.deviceId,
            patientId: $scope.patientId,
            battery: parseFloat(100.00),
            insulin: parseFloat(100.00),
            glucagon: parseFloat(100.00),
            deviceMode: deviceMode,
            targetBgl: targetBgl,
            bolusMax: bolusMax,
            dailyMax: dailyMax
        };

        $http.post(saveConfigDataUrl, JSON.stringify(data)).success(function (result) {
            if (result.status == "success"){
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

    };

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
    };
    
    $scope.viewPatientRecord = function (patId, devId) {
        $scope.patientId = patId;
        $scope.deviceId = devId;
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
        modalInstance.result.then(function (dataFromModal) {
            $scope.updateDeviceConfig(dataFromModal.targetBgl, dataFromModal.dailyMax, dataFromModal.bolusMax, dataFromModal.deviceMode);
        }, function () {
            console.log("action Modal result promise rejected");
        });
    }

    $scope.removePatient = function (patId, devId) {
        var removePatientUrl = "/rest/v1/doctorinterface/removepatient?doctorId="+ $scope.doctorId + "&patientId=" + patId + "&deviceId=" + devId;
        swal({title: "Are you sure?",
                text: "You will not be able to undo!",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",   //previous value #55ACEE
                confirmButtonText: "Yes, delete it!"
        }).then((result)=>{
            if (result.value){
                $http.delete(removePatientUrl).success(function(response) {
                    if (response.status === "success"){
                        swal({
                            title: "Removed",
                            type: "success",
                            text: "Patient removed from your list successfully!",
                            showConfirmButton: true
                        });
                    }else if(response.status == "error"){
                        swal("Oops !", "Could not remove the patient due to some error!", "error");
                    }
                })
            }
        })
    }

}]);

doctorinterfaceapp.controller('PatientRecordCtrl',['$scope','$http', '$log','$timeout', '$route', '$uibModalInstance', 'patientData', function ($scope, $http, $log, $timeout, $route, $uibModalInstance, patientData) {

    $scope.patientId = patientData.patientId;
    $scope.patientName = "";
    $scope.deviceId = patientData.deviceId;
    $scope.patient = {
        patientFirstName: "",
        patientLastName: "",
        patientGender: "",
        patientHeight: 0,
        patientWeight: 0,
        patientAge: 0,
        emergencyContactEmail: "",
        emergencyContactMobile: "",
        targetBgl: "",
        deviceMode: "",
        bolusMax: "",
        dailyMax: "",
        hba1c: ""
    };

    var date1 = new Date();
    var date2 = new Date();

    $scope.savePatientData = function (targetBgl, dailyMax, bolusMax) {
        console.log(targetBgl, dailyMax, bolusMax);
        var dataFromModal = {
            targetBgl: targetBgl,
            dailyMax: dailyMax,
            bolusMax: bolusMax,
            deviceMode: $scope.patient.deviceMode
        };
        $scope.$close(dataFromModal);
    };

    /*Highchart Code*/

    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    var bglChartOptions = {
        chart: {
            renderTo: 'bglchart',
            type: 'spline',
            borderColor: '#EBBA95',
            borderWidth: 2,
            borderRadius: 20,
            marginRight: 10,
        },
        title: {
            text: 'Patient Sugar Level (Last 2 Weeks)'
        },
        subtitle: {
            text: 'Source: Alpha-Beta Pump Simulator'
        },
        xAxis: {
            type: 'datetime',
            title: {text: 'Last 2 Weeks BGL'},
            dateTimeLabelFormats: {day: '%e of %b'}
        },
        yAxis: {
            title: {
                text: 'Blood Glucose Level (mg/dl)'
            },
            plotLines: [{
                value: 50,
                width: 6,
                color: '#808080'
            },
                {
                    value: 70,
                    width: 4,
                    color: 'red',
                    dashStyle: 'largedash',
                    label: {
                        text: 'Minimum Safe Sugar Level',
                        align:'center'
                    }
                },
                {
                    value: 200,
                    width: 4,
                    color: 'red',
                    dashStyle: 'largedash',
                    label: {
                        text: 'Maximum Safe Sugar Level',
                        align:'center'
                    }
                }
            ]
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.dateFormat('%e of %b', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        legend: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        series: [{
                name: 'BglMaxSeries',
                pointInterval: 24 * 3600 * 1000, // one day
                pointStart: (date1.getDate() - 7),
                data: [176, 102, 110, 170, 180, 120, 105, 98, 200, 140, 150, 199, 230, 130]
            },
            {
                name: 'BglMinSeries',
                pointInterval: 24 * 3600 * 1000, // one day
                pointStart: (date2.getDate() - 7),
                data: [105, 82, 60, 60, 80, 70, 85, 78, 70, 140, 90, 99, 130, 130]
            }
        ]
    };

    /*Highchart Code*/


    $scope.getPatientData = function() {
        if ($scope.deviceId != "" && $scope.patientId != "") {
            var getPatientDataUrl = "/rest/v1/admininterface/getpatientdata?patientId=" + $scope.patientId + "&deviceId=" + $scope.deviceId;
            $http.get(getPatientDataUrl).success(function (response) {
                if (response.status == "success" && response.hasOwnProperty("data")) {
                    $scope.patient = {
                        patientFirstName: response.data.firstName,
                        patientLastName: response.data.lastName,
                        patientGender: response.data.gender,
                        patientHeight: response.data.height,
                        patientWeight: response.data.weight,
                        patientAge: response.data.age,
                        emergencyContactEmail: response.data.email,
                        emergencyContactMobile: response.data.mobile,
                        targetBgl: response.data.targetBgl,
                        deviceMode: response.data.deviceMode,
                        bolusMax: response.data.bolusMax,
                        dailyMax: response.data.dailyMax,
                        hba1c: response.data.hba1c,
                    }
                    var bglChart = new Highcharts.Chart(bglChartOptions);
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
