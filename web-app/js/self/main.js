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

    // 路由配置
    main.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
        // 无效链接全部转向首页
        $urlRouterProvider
            .otherwise('/0');

        // 首页
        $stateProvider.state('room', {
            url: '/:offset',
            templateUrl: window.ctx +　'/html/room/index.html',
            controller: 'room.index'
        }).state('room.insetDetail', {
            url: '/inset-detail/:roomId',
            templateUrl: window.ctx +　'/html/room/inset-detail.html',
            controller: "room.insetDetail"
        });
    }]);


    /**
     * 显示所有房间
     */
    main.controller('room.index', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        // 每页显示
        var max = 120;
        $http({
            url: window.ctx + "room/page",
            params: {max: max, offset: $stateParams.offset || 0}
        }).success(function(data){
            $(data.rooms).each(function(){
                this.href = this.quoteUrl ? $state.href("room.insetDetail", $.extend({}, $stateParams, {roomId: this.id})) : this.url;
            });
            $scope.rooms = data.rooms;
            // 分页
            $scope.paginate = {
                pageSize: max,
                total: data.total,
                current: parseInt($stateParams.offset || 0) / 120 + 1,
                href: function(cur){
                    return $state.href("room", $.extend({}, $stateParams, {offset: (cur - 1) * 120}));
                } 
            }
        });

        // 房间块大小根据窗口大小修改
        var initWidth = 338;
        $scope.roomSize = {width: initWidth, height: 251};
        var ratio = $scope.roomSize.width / $scope.roomSize.height;
        var bodyEle = $element.find(".body");
        $(window).resize(function(){
            // 如果元素已经被删除 则解除绑定
            if(!bodyEle.parents("body").length){
                $(window).unbind('resize', arguments.callee);
                return;
            }
            bodyEle.height($(window).height() - 45);
            bodyEle.width($(window).width() - 10).show();
            // console.log($(window).width() - 10);
            var bodyWidth = bodyEle.width() - (bodyEle[0].offsetWidth - bodyEle[0].scrollWidth);
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

        $scope.openRoom = function(room, e){
            $scope.curRoom = room;
            $scope.curTarget = e.currentTarget;
        };
    }]);


    main.controller('room.insetDetail', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        var detailEle = $element.find(".r-detail");
        $(window).resize(function(){
            // 如果元素已经被删除 则解除绑定
            if(!detailEle.parents("body").length){
                $(window).unbind('resize', arguments.callee);
                return;
            }
            detailEle.height($(window).height());
            detailEle.width($(window).width());
            var maxWidth = $(window).width();
            var maxHeight = $(window).height() - 41 * 2;
            var radio = 1.777;
            if(maxHeight * radio < maxWidth){
                $element.find("embed").css({
                    height: maxHeight,
                    width: maxHeight * radio
                });
            }else{
                $element.find("embed").css({
                    height: maxWidth / radio,
                    width: maxWidth
                })
            }
        });

        var loadEmbed = function(){
            // $element.find("embed").attr("src", $scope.curRoom.quoteUrl)
            var embedEle = $('<embed src="' + $scope.curRoom.quoteUrl + '" width="640" height="360" allownetworking="all" allowscriptaccess="always" quality="high" bgcolor="#000" wmode="window" allowfullscreen="true" allowFullScreenInteractive="true" type="application/x-shockwave-flash">')
            $element.find(".cen").append(embedEle);
            setTimeout(function(){
                $(window).resize();
            })
        };

        if(!$scope.curRoom){
            $http({
                url: window.ctx + "room/one",
                params: {id: $stateParams.roomId}
            }).success(function(data){
                $scope.curRoom = data.room;
                loadEmbed();
                console.log($scope.curRoom);
            });

        }else{
            loadEmbed();
        }
    }]);

})();
