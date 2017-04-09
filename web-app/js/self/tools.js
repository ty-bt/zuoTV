/**
 * Created by 勇 on 2016/8/23 0023.
 */
window.tool = {
};

(function(){

    /**
     * jquery扩展
     * @param callback 一个方法或者字符串,
     *          方法则会将方法的返回值组成数组返回,
     *          字符串则会将对应的属性组成数组返回
     */
    $.fn.collect = function(callback){
        var result = [];
        if($.isFunction(callback)){
            this.each(function(){
                result.push(callback.apply(this, arguments));
            });
        }else{
            this.each(function(){
                result.push(this[callback]);
            });
        }
        return result;
    };

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
     * 房间显示指令 外部scope必须有名字为room的对象
     */
    tools.directive('roomShow', function ($timeout) {
        return {
            restrict: 'A',
            templateUrl: 'room-show-tem',
            replace: true,
            // scope: {
            //     room: '=roomShow'
            // },
            link: function(scope, element, attr) {
            }
        };
    });

    /**
     * 房间显示指令 外部scope必须有名字为roomLog的对象
     */
    tools.directive('logChart', function ($timeout) {
        return {
            restrict: 'A',
            templateUrl: 'log-chart-tem',
            scope: {
                roomLog: '=logChart'
            },
            replace: true,
            link: function(scope, element, attr) {
                var myChart = echarts.init(element.find(".chart").get(0));
                var roomLog = scope.roomLog;
                var contentData = scope.contentData = JSON.parse(roomLog.content);
                // 波动范围统计
                var radioRange = window.testRange ? window.testRange.concat() : [0.05, 0.1, 0.15];
                var rangeStatistics = $(radioRange).collect(function(){
                    return {
                        value: this,
                        title: (this * 100) + "%",
                        count: 0
                    }
                });
                rangeStatistics.push({
                    value: -1,
                    title: "其他",
                    count: 0
                });
                $(contentData).each(function(i){
                    if(!i){
                        return;
                    }
                    var log = this;
                    var wave = Math.abs(log.n / contentData[i - 1].n - 1);
                    // 测试语句
                    window.testLogFun && window.testLogFun.call(roomLog, log, contentData[i - 1], wave);
                    $(rangeStatistics).each(function(){
                        if(wave <= this.value || this.value == -1){
                            this.count++;
                            return false;
                        }
                    });
                });
                scope.rangeStatistics= rangeStatistics;
                var option = {
                    title: {
                        text: roomLog.room.anchor + " - " + roomLog.room.name
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    toolbox: {
                        feature: {
                            saveAsImage: {}
                        }
                    },
                    xAxis: {
                        type: 'category',
                            boundaryGap: false,
                            data: $(contentData).collect(function(){
                                var date = new Date(this.t);
                                return date.getHours() + ":" + date.getMinutes();
                            })
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            name:'观看人数',
                            type:'line',
                            data: $(contentData).collect("n")
                        }
                    ]
                };
                myChart.setOption(option);
            }
        };
    });

    /**
     * 分页指令
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
            templateUrl: 'paginate-tem',
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

            }
        };
    });


    /**
     * 弹出窗口管理
     */
    tools.directive('mWindow', function ($timeout) {

        var wIndex = 1;
        var defaultConfig = {
            windows: []

        };

        return {
            restrict: 'A',
            templateUrl: 'm-window-tem',
            scope: {
                config: '=mWindow'
            },
            link: function(scope, element, attr) {
                var config = scope.config = $.extend({}, defaultConfig, scope.config);
                config.add = function(options){
                    // options = {url: '', success: function, hide: true}
                    options.index = wIndex++;
                    config.windows.push(options)
                };

                config.hide = function(win){
                    win.hide = true;
                };

                config.show = function(win){
                    win.hide = false;
                };

                config.close = function(win){
                    $(config.windows).each(function(i){
                        if(win == this){
                            config.windows.splice(i, 1);
                        }
                    });
                };

                config.resize = function(win){
                    var winEle = element.find("[index=" + win.index + "]");
                    winEle.css({
                        'margin-left': -winEle.width() / 2,
                        'margin-top': -winEle.height() / 2
                    });
                }

            }
        };
    });
})();
