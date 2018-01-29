'use strict';
/**
 * @ngdoc overview
 * @name patientinterfaceapp
 * @description
 * # patientinterfaceapp
 *
 * Main module of the application.
 */
var alphabetapumpapp = angular.module('alphabetapumpapp', [
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

alphabetapumpapp.config(['$routeProvider', function ($routeProvider) {
      $routeProvider
          .when('/', {
            templateUrl: insulinpumpapp.assetsPath + "ng/partials/alphabetapump.html",
            controller: 'AlphaBetaPumpCtrl'
          })
    }]);

/**
 * @ngdoc function
 * @name alphabetapumpapp.controller:AlphaBetaPumpCtrl
 * @description
 * # AlphaBetaPumpCtrl
 */
alphabetapumpapp.controller('AlphaBetaPumpCtrl',['$scope','$http', '$log', '$location', function ($scope, $http, $log, $location) {
    $scope.nurseInterface = false;
    $scope.patientInterface = false;
    $scope.NoManualMode = false;
    $scope.deviceId = "";
    $scope.patientId = "";
    $scope.powerOptionValue = false;
    $scope.simulationFormScreen = false;
    $scope.simulationCompleted = false;
    $scope.manualBglList = [];
    $scope.insulinStatus = 100;
    $scope.glucagonStatus = 100;
    $scope.hardwareAssembly = {
        needleCheck: false,
        insulinResCheck: false,
        glucagonResCheck: false,
        batteryCheck: false,
        pumpFailCheck: false
    }

    $scope.resetFormData = function () {
        $scope.simulatorFormData = {
            deviceMode: "AUTO",
            startingBgl: "",
            breakfastCHO: "",
            lunchCHO: "",
            dinnerCHO: "",
            breakfastGI: "",
            lunchGI: "",
            dinnerGI: "",
            exercise: "",
            simulatorDuration: 0
        }
    }
    $scope.exerciseList = [
        {id: 1, value: 'MILD (15-30 minutes)'},
        {id: 2, value: 'AVERAGE (31 - 60 minutes)'},
        {id: 3, value: 'RIGOROUS (1 - 2 hours)'},
    ];

    $scope.durationList = [
        {id: '1 Day', value: 1440},
        {id: '2 Days', value: 2880},
        {id: '3 Days', value: 4320},
        {id: '4 Days', value: 5760},
        {id: '5 Days', value: 7200},
    ];

    $scope.userInterface = function () {
        if (window.location.pathname.includes("patient")){
            $scope.nurseInterface = false;
            $scope.patientInterface = true;
        }else {
            $scope.nurseInterface = true;
            $scope.patientInterface = false;
        }
    }

    $scope.powerSwitch = function () {
        $scope.resetFormData();
        if ($scope.powerOptionValue) {
            var getDeviceDataUrl = "/rest/v1/admininterface/getDeviceConfig";
            $http.get(getDeviceDataUrl).success(function (response) {
                if (response.status == "success") {
                    $scope.deviceId = response.deviceId;
                    $scope.patientId = response.patientId;
                    $scope.userInterface();     //To check if it is nurseInterface or Patient
                    if (response.hasOwnProperty("deviceMode") && $scope.patientInterface){
                        if (response.deviceMode == "AUTO"){ //this means only AUTO Mode is allowed for the patient
                            $scope.NoManualMode = true;
                        } else {
                            $scope.NoManualMode = false;
                        }
                    }
                    $scope.simulationFormScreen = true;
                }
                if (response.status == "error") {
                    swal('Oops...', response.message, 'error');
                }
            })
        } else {
            $scope.simulationFormScreen = false;
            $scope.simulationCompleted = false;
        }
    }

    /*Progress Bar Code for Insulin or Glucagon Reservoir*/
    $scope.reservoirProgressBar = function(reservoirType, value) {
        var refillMessage = "";
        var showWarningPopup = false;
        var type = '';
         var statusText = '';
        if (value < 20) {
            type = 'danger';
             var statusText = 'LOW'
            showWarningPopup = true;
        } else if (value < 50) {
             var statusText = 'LOW'
            type = 'warning';
        } else if (value < 75) {
            type = 'info';
        } else {
             var statusText = 'FULL'
            type = 'success';
        }
        if (reservoirType === "insulinType"){
            refillMessage = "Low Insulin Reservoir.";
            $scope.showInsulinWarning = type === 'danger' || type === 'warning';
            $scope.insulinStatus = value;
            $scope.insulinStatusText = statusText;
            $scope.insulinType = type;
        } else {
            refillMessage = "Low Glucagon Reservoir.";
            $scope.showGlucagonWarning = type === 'danger' || type === 'warning';
            $scope.glucagonStatus = value;
            $scope.glucagonStatusText = statusText;
            $scope.glucagonType = type;
        }

        if(showWarningPopup){
            swal({
                title: refillMessage + ' Please refill ASAP!',
                confirmButtonText: 'Refill Now',
                showLoaderOnConfirm: true,
                allowOutsideClick: false
            }).then((result)=>{
                if (result.value){
                    $scope.refill(reservoirType);
                }
             });

            // swal(refillMessage, "Please refill ASAP","error");
        }

    };

    /*HighCharts Code for BGL*/
    var bglChartOptionsAutoMode = {
        chart: {
            renderTo: 'bglchartautomode',
            type: 'spline',
            borderColor: '#EBBA95',
            borderWidth: 2,
            borderRadius: 20,
            marginRight: 20
        },
        title: {
            text: 'Patient Sugar Level'
        },
        subtitle: {
            text: 'Source: Alpha-Beta Pump Simulator (AUTO MODE)'
        },
        xAxis: {
            type: 'datetime',
            dateTimeLabelFormats: {minute: '%H:%M'},
            title: {text: 'Time (24 Hours)'},
            tickInterval: 3600 * 1000
        },
        yAxis: {
            title: {
                text: 'Blood Glucose Level (mg/DL)'
            },
            min: 0,
            tickAmount: 8,
            tickInterval: 50,
            lineWidth: 1,
            minorGridLineWidth: 0,
            minorTickInterval: 'auto',
            minorTickWidth: 10,
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
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        series: [{
            name: 'Blood Sugar Level',
            data: [],
            pointStart: (new Date()).setHours(8,0),
            pointInterval: 60 * 1000
        }]
    };

    /*HighCharts Code for BGL*/
    var bglChartOptionsManualMode = {
        chart: {
            renderTo: 'bglchartmanualmode',
            type: 'spline',
            borderColor: '#EBBA95',
            borderWidth: 2,
            borderRadius: 20,
            marginRight: 20,
            events: {
                load: function () {

                    // set up the updating of the chart each second
                    var series = this.series[0];
                    setInterval(function () {

                        var y = [];
                        var x = (new Date()).getTime(); // current time
                            // y = series.data[series.data.length - 1] + Math.floor(Math.random() * (20 - 1 + 1)) + 1;
                            y = $scope.bglChangeManualMode();
                            y.forEach(function(element) {
                                series.addPoint([element], true, false);
                            })

                    }, 2000);
                }
            }
        },
        title: {
            text: 'Patient Sugar Level'
        },
        subtitle: {
            text: 'Source: Alpha-Beta Pump Simulator (MANUAL MODE)'
        },
        xAxis: {
            type: 'datetime',
            dateTimeLabelFormats: {minute: '%H:%M'}
        },
        yAxis: {
            title: {
                text: 'Blood Glucose Level (mg/DL)'
            },
            min: 0,
            tickAmount: 8,
            tickInterval: 50,
            lineWidth: 1,
            minorGridLineWidth: 0,
            minorTickInterval: 'auto',
            minorTickWidth: 10,
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
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        series: [{
            name: 'Blood Sugar Level',
            data: [],
            pointStart: (new Date()).setHours(8,0),
            pointInterval: 60 * 1000
        }]
    };

    /*Highcharts code for Battery Level*/

    var batteryGuageOptions = {

        chart: {
            renderTo: 'container-battery',
            type: 'solidgauge',
            height:150,
            width:150,
            margin:0,
            // Edit chart spacing
            spacingBottom: 0,
            spacingTop: 0,
            spacingLeft: 0,
            spacingRight: 0,
        },

        title: null,

        pane: {
            center: ['50%', '50%'],
            size: '100%',
            startAngle: -90,
            endAngle: 90,
            background: {
                backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
                innerRadius: '60%',
                outerRadius: '100%',
                shape: 'arc'
            }
        },

        tooltip: {
            enabled: false
        },

        // the value axis
        yAxis: {
            min: 0,
            max: 100,
            title: {
                text: 'Battery',
                y: -70
            },
            stops: [
                [0.1, '#55BF3B'], // green
                [0.5, '#DDDF0D'], // yellow
                [0.9, '#DF5353'] // red
            ],
            lineWidth: 0,
            minorTickInterval: null,
            tickAmount: 2,
            labels: {
                y: 16
            }
        },

        plotOptions: {
            solidgauge: {
                dataLabels: {
                    y: 5,
                    borderWidth: 0,
                    useHTML: true
                }
            }
        },
        credit: {
            enabled: false
        },
        series: [{
            name: 'Battery Status',
            data: [],
            dataLabels: {
                format: '<div style="text-align:center; margin: -26px"><span style="font-size:15px;color:' +
                ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y}%</span><br/>' +
                '<span style="font-size:12px;color:silver"></span></div>'
            },
            tooltip: {
                valueSuffix: '%'
            }
        }]

    };

    $scope.runSimulator = function(form) {
        $scope.showErrMsg = false;
        if (form.$valid) {
            var simulatorUrl = "/rest/v1/simulator/runsimulation";
            var data = {};
            if ($scope.simulatorFormData.deviceMode === "AUTO") {
                data = {
                    deviceMode: $scope.simulatorFormData.deviceMode,
                    startBgl: parseFloat($scope.simulatorFormData.startingBgl),
                    cho1: parseFloat($scope.simulatorFormData.breakfastCHO),
                    cho2: parseFloat($scope.simulatorFormData.lunchCHO),
                    cho3: parseFloat($scope.simulatorFormData.dinnerCHO),
                    breakfastGI: parseFloat($scope.simulatorFormData.breakfastGI),
                    lunchGI: parseFloat($scope.simulatorFormData.lunchGI),
                    dinnerGI: parseFloat($scope.simulatorFormData.dinnerGI),
                    exercise: $scope.simulatorFormData.exercise,
                    duration: parseInt($scope.simulatorFormData.simulatorDuration),
                    deviceId: $scope.deviceId,
                    patientId: $scope.patientId,
                };
            } else {
                data = {
                    deviceMode: $scope.simulatorFormData.deviceMode,
                    startBgl: parseFloat($scope.simulatorFormData.startingBgl),
                    manualModeCHO: parseFloat($scope.simulatorFormData.manualModeCHO),
                    manualModeGI: parseFloat($scope.simulatorFormData.manualModeGI),
                    manualModeInsulinUnit: parseFloat($scope.simulatorFormData.manualModeInsulinUnit),
                    exercise: $scope.simulatorFormData.exercise,
                    duration: parseInt($scope.simulatorFormData.simulatorDuration),
                    deviceId: $scope.deviceId,
                    patientId: $scope.patientId,
                };

                var computedInsulin = $scope.computeInsulin(data.manualModeCHO, data.manualModeGI, data.startBgl);
                if (computedInsulin < data.manualModeInsulinUnit) {
                    swal({
                        title: 'High Insulin Alert!',
                        text: "The insulin entered appears to be more than required. Are you sure you want to continue? Click No to use the system calculated value.",
                        type: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#3085d6',
                        cancelButtonColor: '#d33',
                        cancelButtonText: 'Continue',
                        confirmButtonText: 'No'
                    }).then((result)=>{
                        if(result.value){
                        data.manualModeInsulinUnit = computedInsulin;
                    }
                })
                }
            }

            $http.post(simulatorUrl, JSON.stringify(data)).success(function (result) {
                if (result.status == "success") {
                    $scope.insulinStatus = result.insulinStatus;
                    $scope.reservoirProgressBar("insulinType", $scope.insulinStatus);
                    $scope.glucagonStatus = result.glucagonStatus;
                    $scope.reservoirProgressBar("glucagonType", $scope.glucagonStatus);
                    //Populate Battery Highchart Guage Data
                    batteryGuageOptions.series[0].data = [result.batteryStatus];
                    var batteryChart = new Highcharts.Chart(batteryGuageOptions);

                    //Populate BGL Highchart Data
                    if (data.deviceMode === "AUTO") {
                        bglChartOptionsAutoMode.series[0].data = result.bglData;
                        var bglChartAutoMode = new Highcharts.Chart(bglChartOptionsAutoMode);
                    } else {
                        bglChartOptionsManualMode.series[0].data = result.bglData.splice(0, 50);
                        var bglChartManualMode = new Highcharts.Chart(bglChartOptionsManualMode);
                        $scope.manualBglList = result.bglData;

                    }
                }
            })
            $scope.simulationCompleted = true;
        } else {
            $scope.showErrMsg = true;
        }
    }

    $scope.bglChangeManualMode = function() {
        var y = [];
        var series = bglChartOptionsManualMode.series[0];
        y = $scope.manualBglList.splice(0,10);
        return y;
    }

    $scope.onChangeDeviceMode = function(form) {
        $scope.simulatorFormData.breakfastCHO = "";
        $scope.simulatorFormData.lunchCHO = "";
        $scope.simulatorFormData.dinnerCHO = "";
        $scope.simulatorFormData.breakfastGI = "";
        $scope.simulatorFormData.lunchGI = "";
        $scope.simulatorFormData.dinnerGI = "";
        $scope.simulatorFormData.manualModeCHO = "";
        $scope.simulatorFormData.manualModeGI = "";
        $scope.simulatorFormData.manualModeInsulinUnit = "";
        form.$setPristine();
        form.$setUntouched();
    }

    $scope.SOS = function () {
        swal({
            title: 'Do you need Assistance?',
            text: "Please confirm to notify the person in emergency contact",
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Call Emergency'
        }).then((result)=>{
            if(result.value){
                var sosUrl = "/rest/v1/simulator/sos";
                $http.get(sosUrl).success(function (res) {
                    if (res.status == "success") {
                        swal({title: "Emergency Contacts Notified", text: res.message, timer: 3000});
                    } else if (res.status == "error") {
                        swal("Oops !", res.message, "error");
                    }
                });
            }
        })
    }

    $scope.discharge = function (progressbar) {
        var value = 100 - 85;
        $scope.reservoirProgressBar(progressbar, value);
    }

    $scope.refill = function (progressbar) {
        var value = 100;
        $scope.reservoirProgressBar(progressbar, value);
    }

    $scope.computeInsulin = function(manualModeCHO, manualModeGI, startBgl){
        var getComputedInsulinUrl = '/rest/v1/patient/computedInsulin';
        var computedInsulin = 0.0;
        var data = {
            deviceId: $scope.deviceId,
            patientId: $scope.patientId,
            startBgl: startBgl,
            manualModeCHO: manualModeCHO,
            manualModeGI: manualModeGI
        };

        var config = {
            params: data,
            headers : {'Accept' : 'application/json'}
        };
        $http.get(getComputedInsulinUrl, config).success(function (response) {
            if (response.status == "success") {
                computedInsulin = response.computedInsulin;
            }
        })
        return computedInsulin;
    };

    $scope.dispatchPushNotif = function(component){
        var dispatchNotifUrl = '/rest/v1/push/set-notifpushtype?notiftype='+component;
        $http.get(dispatchNotifUrl).success(function (response) {
            if (response.status == "success"){
                console.log("Push Notification sent for " + component);
            }
        })
    };

    $scope.hardwareCheck = function (component) {

        var errorTitle = "";
        var notification = "";
        var buttonText = "";
        $scope.dispatchPushNotif(component);

        switch (component) {
            case 'needleCheck':
                errorTitle = "Hardware Component Missing!";
                notification = "Needle Assembly not attached!";
                buttonText = "Attach Needle";
                $scope.hardwareAssembly.needleCheck = false;
                break;
            case 'insulinResCheck':
                errorTitle = "Hardware Component Missing!";
                notification = "Insulin Reservoir not attached!";
                buttonText = "Attach Reservoir";
                $scope.hardwareAssembly.insulinResCheck = false;
                break;
            case 'glucagonResCheck':
                errorTitle = "Hardware Component Missing!";
                notification = "Glucagon Reservoir not attached!";
                buttonText = "Attach Reservoir";
                $scope.hardwareAssembly.glucagonResCheck = false;
                break;
            case 'batteryCheck':
                errorTitle = "Low Battery";
                notification = "Please recharge the battery";
                buttonText = "Recharge";
                batteryGuageOptions.series[0].data = [0];
                var batteryChart = new Highcharts.Chart(batteryGuageOptions);

                $scope.hardwareAssembly.batteryCheck = false;
                break;
            case 'pumpFailCheck':
                errorTitle = "Alpha Beta Pump Failure";
                notification = "Pump is unable to inject Insulin. Kindly Check!";
                buttonText = "Device Fixed";
                $scope.hardwareAssembly.pumpFailCheck = false;
                break;
        }
        swal({
            title: errorTitle,
            text: notification,
            type: 'warning',
            showCancelButton: false,
            confirmButtonColor: '#3085d6',
            confirmButtonText: buttonText
        }).then((result)=>{
            if(result.value && component == "batteryCheck"){
            batteryGuageOptions.series[0].data = [100];
            var batteryChart = new Highcharts.Chart(batteryGuageOptions);
            console.log("Hardware Problem Fixed!!");
        }
    })

    }
}]);
