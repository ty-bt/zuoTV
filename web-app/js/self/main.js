/**
 * Created by 勇 on 2016/8/23 0023.
 */
(function(){
    var main = angular.module("main", []);
    main.run(['$rootScope', function($rootScope){
        $rootScope.ctx = window.ctx;
    }]);
    main.controller('main.index', ['$scope', '$http', function($scope, $http){
        $http({
            url: window.ctx + "room/page",
            params: {max: 120, offset: 0}
        }).success(function(data){
            console.log(data);
            $scope.rooms = data.rooms;
        });

        // 房间块大小根据窗口大小修改
        var initWidth = 338;
        $scope.roomSize = {width: initWidth, height: 251};
        var ratio = $scope.roomSize.width / $scope.roomSize.height;
        $(window).resize(function(){
            $(".body").height($(window).height() - 45);
            $(".body").width($(window).width() - 10).show();
            console.log($(window).width() - 10);
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

    /**
     * repeat完成
     */
    main.directive('repeatFinish', function ($timeout) {
        return {
            restrict: 'A',
            link: function(scope, element, attr) {
                if (scope.$last === true) {
                    $timeout(function() {
                        scope.$emit('onRepeatFinish');
                    });
                }
            }
        };
    });
})();
