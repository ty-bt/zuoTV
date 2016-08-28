/**
 * Created by 勇 on 2016/8/23 0023.
 */
window.tool = {
};

(function(){
    var tools = angular.module("tools", []);
    // 房间人数过滤器
    tools.filter('wanNum',function(){
        return function(text){
            var num = parseInt(text);
            if(num && num >= 10000){
                return (parseInt(num / 100) / 100) + "万"
            }
            return text
        }
    });

    // 生成数组
    tools.filter('arr',function(){
        /**
         * start: 开始数字
         * count: 生成数量
         */
        return function(start, count){
            var result = [];
            var end = start + count;
            for(var a = start; a < end; a++){
                result.push(a);
            }
            return result;
        }
    });


    /**
     * repeat完成 指令
     */
    tools.directive('repeatFinish', function ($timeout) {
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

    /**
     * repeat完成 指令
     */
    tools.directive('tPaginate', function ($timeout) {

        var defaultConfig = {
            total: 200,
            pageSize: 10,
            current: 1,
            // 除了当前页,第一页和最后一页 还显示的页码数量
            showCount: 6,
            // url生成方法
            href: $.noop
        };

        return {
            restrict: 'A',
            templateUrl: window.ctx + '/html/paginate.html',
            scope: {
                config: '=tPaginate'
            },
            link: function(scope, element, attr) {

                var config = scope.config = $.extend({}, defaultConfig, scope.config);
                config.pageCount = Math.ceil(config.total / config.pageSize);
                // 计算当前页左边和右边显示的页码数量
                var right = config.showCount;
                var left = 0;
                var pCount = right / 2;
                for(var a = 0; a < pCount; a++){
                    if(config.current - a - 1 > 1){
                        right--;
                        left++;
                    }else{
                        break;
                    }
                }
                config.leftCount = left;
                config.rightCount = right;
                // 生成链接方法  去除无效的页码
                scope.createHref = function(num){
                    if(num && num > 0 && num <= config.pageCount){
                        return config.href(num);
                    }
                    return "javascript:void(0);"
                };
                // console.log(config )

            }
        };
    });
})();
