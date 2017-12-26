'use strict';
/**
 * @ngdoc overview
 * @name patientinterfaceapp
 * @description
 * # patientinterfaceapp
 *
 * Main module of the application.
 */
var patientinterfaceapp = angular.module('patientinterfaceapp', [
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

patientinterfaceapp.config(['$routeProvider', function ($routeProvider) {
      $routeProvider
          .when('/', {
            templateUrl: insulinpumpapp.assetsPath + "ng/partials/patient.html",
            controller: 'PatientCtrl'
          })
    }]);

/**
 * @ngdoc function
 * @name patientinterfaceapp.controller:PatientCtrl
 * @description
 * # PatientCtrl
 * Controller of the clientApp
 */
patientinterfaceapp.controller('PatientCtrl',['$scope','$http', '$log', '$location', function ($scope, $http, $log, $location) {
    $scope.deviceId = "";
    $scope.patientId = "";
    $scope.powerOptionValue = false;
    $scope.simulationFormScreen = false;
    $scope.simulationCompleted = false;
    $scope.plotBglChart = [];
    $scope.simulatorFormData = {
        deviceMode: "",
        startingBgl: "",
        breakfastCHO: "",
        lunchCHO: "",
        dinnerCHO: "",
        breakfastGI: "",
        lunchGI: "",
        dinnerGI: "",
        exercise: "",
        simulatorDuration:0
    }

    $scope.deviceModes = [];

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

    $scope.onChangeDeviceMode = function () {
        console.log($scope.simulatorFormData.deviceMode);
    }

    $scope.powerSwitch = function () {
        if ($scope.powerOptionValue) {
            var getDeviceDataUrl = "/rest/v1/admininterface/getDeviceConfig";
            $http.get(getDeviceDataUrl).success(function (response) {
                if (response.status == "success") {
                    $scope.deviceId = response.deviceId;
                    $scope.patientId = response.patientId;
                    if (response.hasOwnProperty("deviceMode")){
                        if (response.deviceMode == "AUTO"){
                            $scope.deviceModes = [{id: 1, value: "Auto"}]
                        } else {
                            $scope.deviceModes = [{id: 1, value: "Auto"}, {id: 2, value: "Manual"}]
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
        $scope.random();
    }

    /*Progress Bar Code*/
    $scope.max = 200;

    $scope.random = function() {
        var value = Math.floor(Math.random() * 100 + 1);
        var type;

        if (value < 20) {
            type = 'danger';
        } else if (value < 50) {
            type = 'warning';
        } else if (value < 75) {
            type = 'info';
        } else {
            type = 'success';
        }
        $scope.showWarning = type === 'danger' || type === 'warning';
        $scope.dynamic = value;
        $scope.type = type;
    };

    /*HighCharts Code for BGL*/
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
            marginRight: 20
        },
        title: {
            text: 'Patient Sugar Level'
        },
        subtitle: {
            text: 'Source: Alpha-Beta Pump Simulator'
        },
        xAxis: {
            type: 'datetime',
            dateTimeLabelFormats: {minute: '%H:%M'},
            title: {text: 'Time (24 Hours)'},
            tickInterval: 3600 * 1000
        },
        yAxis: {
            title: {
                text: 'Blood Glucose Level'
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

    $scope.runSimulator = function(form) {
        $scope.showErrMsg = false;
        if (form.$valid){
            var simulatorUrl = "/rest/v1/simulator/runsimulation";
            var data = {
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

            $http.post(simulatorUrl, JSON.stringify(data)).success(function (result) {
                if (result.status == "success") {
                    bglChartOptions.series[0].data = result.bglData;
                    var bglChart = new Highcharts.Chart(bglChartOptions);
                }
            })


            // $scope.simulationFormScreen = false;
            $scope.simulationCompleted = true;
        }else {
            $scope.showErrMsg = true;
        }

    }


    /*Highcharts code for Battery Level*/

    var gaugeOptions = {

        chart: {
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
            stops: [
                [0.1, '#55BF3B'], // green
                [0.5, '#DDDF0D'], // yellow
                [0.9, '#DF5353'] // red
            ],
            lineWidth: 0,
            minorTickInterval: null,
            tickAmount: 2,
            title: {
                y: -70
            },
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
        }
    };

// The battery gauge
    var chartSpeed = Highcharts.chart('container-battery', Highcharts.merge(gaugeOptions, {
        yAxis: {
            min: 0,
            max: 100,
            title: {
                text: 'Battery'
            }
        },

        credits: {
            enabled: false
        },

        series: [{
            name: 'Time',
            data: [50],
            dataLabels: {
                format: '<div style="text-align:center; margin: -26px"><span style="font-size:15px;color:' +
                ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y}%</span><br/>' +
                '<span style="font-size:12px;color:silver"></span></div>'
            },
            tooltip: {
                valueSuffix: '%'
            }
        }]

    }));

// Bring life to the dials
    setInterval(function () {
        // Speed
        var point,
            newVal,
            inc;

        if (chartSpeed) {
            point = chartSpeed.series[0].points[0];
            inc = Math.floor(Math.random() * 100 + 1);
            newVal = point.y + inc;

            if (newVal < 0 || newVal > 200) {
                newVal = point.y - inc;
            }

            point.update(newVal);
        }

    }, 5000);

}]);

patientinterfaceapp.controller('SignupCtrl',['$scope','$http', '$log', function ($scope, $http, $log) {
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

patientinterfaceapp.controller('DashboardCtrl',['$scope','$http', '$log','$timeout','$cookieStore', function ($scope, $http, $log, $timeout, $cookieStore) {
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
