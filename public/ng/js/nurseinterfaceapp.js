'use strict';
/**
 * @ngdoc overview
 * @name nurseinterfaceapp
 * @description
 * # nurseinterfaceapp
 *
 * Main module of the application.
 */
var nurseinterfaceapp = angular.module('nurseinterfaceapp', [
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

nurseinterfaceapp.config(['$routeProvider', function ($routeProvider) {
      $routeProvider
          .when('/', {
            templateUrl: insulinpumpapp.assetsPath + "ng/partials/nurse.html",
            controller: 'NurseCtrl'
          })
    }]);

/**
 * @ngdoc function
 * @name nurseinterfaceapp.controller:NurseCtrl
 * @description
 * # NurseCtrl
 * Controller of the clientApp
 */
nurseinterfaceapp.controller('NurseCtrl',['$scope','$http', '$log', '$location', function ($scope, $http, $log, $location) {
    $scope.deviceId = "";
    $scope.patientId = "";
    $scope.powerOptionValue = false;
    $scope.simulationFormScreen = false;
    $scope.simulationCompleted = false;
    $scope.simulatorFormData = {
        deviceMode: "AUTO",
        startingBgl: "",
        breakfastCHO: "",
        lunchCHO: "",
        dinnerCHO: ""
    }

    $scope.deviceModes = [
        {id: 1, value: 'Auto'},
        {id: 2, value: 'Manual'},
    ];

    $scope.exerciseList = [
        {id: 1, value: 'MILD'},
        {id: 2, value: 'AVERAGE'},
        {id: 3, value: 'RIGOROUS'},
    ];

    $scope.durationList = [
        {id: 1, value: '1 Day'},
        {id: 2, value: '2 Days'},
        {id: 3, value: '3 Days'},
        {id: 4, value: '4 Days'},
        {id: 5, value: '5 Days'},
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

    $scope.runSimulator = function(form) {
        // $scope.simulationFormScreen = false;
        $scope.simulationCompleted = true;
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

    Highcharts.chart('bglchart', {
        chart: {
            type: 'spline',
            animation: Highcharts.svg, // don't animate in old IE
            marginRight: 10,
            events: {
                load: function () {

                    // set up the updating of the chart each second
                    var series = this.series[0];
                    setInterval(function () {
                        var x = (new Date()).getTime(), // current time
                            y = Math.random()*(130-70)+70;
                        series.addPoint([x, y], true, true);
                    }, 5000);
                }
            }
        },
        title: {
            text: 'Patient Sugar Level'
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 150
        },
        yAxis: {
            title: {
                text: 'Blood Glucose Level'
            },
            plotLines: [{
                value: 50,
                width: 6,
                color: '#808080'
            },
            {
                value: 70,
                width: 2,
                color: 'red',
                dashStyle: 'largedash',
                label: {
                    text: 'Minimum Safe Sugar Level',
                    align:'center'
                }
            },
            {
                value: 110,
                width: 2,
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
        legend: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        series: [{
            name: 'Blood Sugar Level',
            data: (function () {
                // generate an array of random data
                var data = [],
                    time = (new Date()).getTime(),
                    i;

                for (i = -19; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: Math.random()
                    });
                }
                return data;
            }())
        }]
    });


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

nurseinterfaceapp.controller('SignupCtrl',['$scope','$http', '$log', function ($scope, $http, $log) {
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

nurseinterfaceapp.controller('DashboardCtrl',['$scope','$http', '$log','$timeout','$cookieStore', function ($scope, $http, $log, $timeout, $cookieStore) {
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
