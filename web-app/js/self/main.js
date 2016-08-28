/**
 * Created by 勇 on 2016/8/23 0023.
 */
(function(){
    var main = angular.module("main", ['tools', 'ui.router']);
    main.run(['$rootScope', function($rootScope){
        $rootScope.ctx = window.ctx;
    }]);
    //
    // main.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
    //     $urlRouterProvider.otherwise('/');
    //
    //     $stateProvider.state('about', {
    //         url: '/about',
    //         template: '<h1>Welcome to UI-Router Demo</h1>',
    //
    //         // optional below
    //         templateProvider: ['$timeout',
    //             function($timeout) {
    //                 return $timeout(function() {
    //                     return '<p class="lead">UI-Router Resource</p>' +
    //                         '<p>The second line</p>'
    //                 }, 100);
    //             }
    //         ],
    //
    //         templateUrl: window.ctx +　'about.html',
    //
    //         controller: 'main.index'
    //     });
    // }]);


    main.controller('main.index', ['$scope', '$http', function($scope, $http){
        $http({
            url: window.ctx + "room/page",
            params: {max: 120, offset: 0}
        }).success(function(data){
            console.log(data);
            $scope.rooms = data.rooms;
            // 分页
            $scope.paginate = {
                pageSize: 120,
                total: data.total,
                current: 1
            }
        });

        // 房间块大小根据窗口大小修改
        var initWidth = 338;
        $scope.roomSize = {width: initWidth, height: 251};
        var ratio = $scope.roomSize.width / $scope.roomSize.height;
        $(window).resize(function(){
            $(".body").height($(window).height() - 45);
            $(".body").width($(window).width() - 10).show();
            // console.log($(window).width() - 10);
            var bodyWidth = $(".body").width() - ($('.body')[0].offsetWidth - $('.body')[0].scrollWidth);
            var size = Math.ceil(bodyWidth / (initWidth + 10));
            var width = Math.floor(bodyWidth / size - 10);
            $scope.roomSize = {width: width, height: width / ratio};
            $scope.$apply('roomSize');
        });
        setTimeout(function(){
            $(window).resize();
        });
        $scope.$on("onRepeatFinish", function(){
            $(window).resize();
        });

    }]);

})();
