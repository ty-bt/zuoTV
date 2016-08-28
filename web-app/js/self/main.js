/**
 * Created by 勇 on 2016/8/23 0023.
 */
(function(){
    var main = angular.module("main", ['tools', 'ui.router']);
    main.run(['$rootScope', function($rootScope){
        $rootScope.ctx = window.ctx;
    }]);

    main.run(['$rootScope', '$state', '$stateParams',
        function($rootScope, $state, $stateParams) {
            $rootScope.$state = $state;
            $rootScope.$stateParams = $stateParams;
        }
    ]);

    //
    main.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
        $urlRouterProvider
            // .when('/', '/')
            .otherwise('/');


        $stateProvider.state('index', {
            url: '/:offset',
            templateUrl: window.ctx +　'/html/room/index.html',
            controller: 'main.index'
        });
    }]);


    main.controller('main.index', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        // 每页显示
        var max = 120;
        $http({
            url: window.ctx + "room/page",
            params: {max: max, offset: $stateParams.offset || 0}
        }).success(function(data){
            $scope.rooms = data.rooms;
            // 分页
            $scope.paginate = {
                pageSize: max,
                total: data.total,
                current: parseInt($stateParams.offset || 0) / 120 + 1,
                href: function(cur){
                    return $state.href("index", $.extend({}, $stateParams, {offset: (cur - 1) * 120}));
                } 
            }
        });

        // 房间块大小根据窗口大小修改
        var initWidth = 338;
        $scope.roomSize = {width: initWidth, height: 251};
        var ratio = $scope.roomSize.width / $scope.roomSize.height;
        $(window).resize(function(){
            $element.find(".body").height($(window).height() - 45);
            $element.find(".body").width($(window).width() - 10).show();
            // console.log($(window).width() - 10);
            var bodyWidth = $element.find(".body").width() - ($('.body')[0].offsetWidth - $('.body')[0].scrollWidth);
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
