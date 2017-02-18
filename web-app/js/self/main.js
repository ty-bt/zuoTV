/**
 * Created by 勇 on 2016/8/23 0023.
 */
(function(){
    var main = angular.module("main", ['tools', 'ui.router']);
    main.run(['$rootScope', function($rootScope){
        // 生成背景图
        var cloneA = $.extend({}, Trianglify.colorbrewer)
        for(var k in cloneA){
            var newArr = [];
            $(cloneA[k]).each(function(i){
                newArr.push(cloneA[k][cloneA[k].length - i -　1]);
            });
            Trianglify.colorbrewer[k + "_f"] = newArr;
        }
        $(".main").css({
            "background-image": "url(" + Trianglify({
                variance: "0.74",
                // seed: 'random',
                width: $(window).width(),
                height: $(window).height(),
                cell_size: 60,
                x_colors: Trianglify.colorbrewer["Spectral_f"]/*,
                x_colors: Trianglify.colorbrewer["Spectral_f"]*/}).png() + ")"
        });
        $rootScope.ctx = window.ctx;
        $rootScope.webName = "zuoTV";
        var defaultTitle = $rootScope.webName + " - 聚合全网直播";
        $rootScope.title = defaultTitle;
        $rootScope.$root.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
            // 统计代码
            if(window._hmt && location.hash){
                _hmt.push(['_trackPageview', location.hash.substr(1)]);
            }
            // google
            if(window.ga && location.hash){
                ga('set', 'page', location.hash.substr(1));
                ga('send', 'pageview');
            }



            var title = "";
            if(toState.title){
                if($.isFunction(toState.title)){
                    title = toState.title.apply(this, arguments);
                }else{
                    title = toState.title;
                }
            }
            $rootScope.title = title ? title + " - " + $rootScope.webName : defaultTitle;
        })
    }]);

    main.run(['$rootScope', '$state', '$stateParams', '$log', '$http', '$window', function($rootScope, $state, $stateParams, $log, $http, $window) {
        $rootScope.$state = $state;
        $rootScope.$stateParams = $stateParams;
        // 弹出窗口管理对象 使用m-window指令
        $rootScope.windows = {};

        // 当前登录用户
        $rootScope.curUser = null;

        /**
         * 打开房间的时候调用
         * @param room 房间对象
         */
        $rootScope.openRoom = function(room){
            $rootScope.curRoom = room;
        };

        /**
         * 返回是否支持页内播放
         * @param flag 平台标识
         * @returns {boolean}
         */
        $rootScope.isInsert = function(flag){
            return flag != "panda"/* && flag != "zhanQi"*/;
        };

        // 刷新登录状态
        $rootScope.loadLogin = function(){
            var colorArr = ["#1abc9c", "#16a085", "##2ecc71", "#27ae60", "#9b59b6", "#8e44ad", "#34495e", "#2c3e50", "#f1c40f", "#f39c12", "#e67e22", "#d35400", "#e74c3c", "#c0392b", "#ecf0f1", "#bdc3c7", "#95a5a6", "#7f8c8d"];
            $http.post(window.ctx + "auth/getCurrentUser").success(function(data){
                if(data.success){
                    $rootScope.curUser = data.data;
                    if($rootScope.curUser){
                        // 随机个性颜色
                        // if(!$rootScope.curUser.color){
                        //     $rootScope.curUser.color = colorArr[parseInt(Math.random() * 100) % colorArr.length]
                        // }
                        $rootScope.loadCollect();
                    }else{
                        // 清空我的关注
                        $rootScope.collects = [];
                        $rootScope.collectMap = {};
                    }



                }else{
                    $log.log(data.message || "系统错误");
                }
            });
        };

        $rootScope.login = function(){
            $rootScope.windows.add({url: 'login-tem'});
        };

        $rootScope.register = function(){
            $rootScope.windows.add({url: 'register-tem'});
        };

        // 退出
        $rootScope.logout = function(){
            $http.post(window.ctx + "auth/logout").success(function(data){
                if(data.success){
                    $rootScope.loadLogin();
                }
            });
        };



        $rootScope.collectMap = {};
        // 加载收藏夹
        $rootScope.loadCollect = function(){
            if(!$rootScope.curUser){
                return;
            }
            $http.post(window.ctx + "center/collect/list").success(function(data){
                if(data.success){
                    // [collects: collects, total: collects.totalCount]
                    $rootScope.collects = data.data.collects;
                    var collectMap = {};
                    $rootScope.onLineCollects= [];
                    $($rootScope.collects).each(function(){
                        collectMap[this.room.id] = this.id;
                        if(this.room.isOnLine){
                            $rootScope.onLineCollects.push(this);
                        }
                    });
                    $rootScope.collectMap = collectMap;
                }else{
                    $log.log(data.message || "系统错误");
                }
            });
        };
        // 浏览器标签页切换事件 自动刷新关注
        (function(){
            // 当前浏览器标签页是否隐藏状态
            $rootScope.browserHidden = false;
            var hiddenProperty = 'hidden' in document ? 'hidden' :
                'webkitHidden' in document ? 'webkitHidden' :
                    'mozHidden' in document ? 'mozHidden' :
                        null;
            var visibilityChangeEvent = hiddenProperty.replace(/hidden/i, 'visibilitychange');
            var onVisibilityChange = function(){

                if (!document[hiddenProperty]) {
                    $rootScope.browserHidden = false;
                    $rootScope.loadCollect();
                }else{
                    $rootScope.browserHidden = true;
                }
            };
            // 三分钟刷新一次
            setTimeout(function(){
                if(!$rootScope.browserHidden){
                    $rootScope.loadCollect();
                }
                setTimeout(arguments.callee, 60000 * 3);
            }, 60000 * 3);
            document.addEventListener(visibilityChangeEvent, onVisibilityChange);
        })();


        // 修改收藏夹状态
        $rootScope.changeCollect = function($event, room){
            $event.stopPropagation();
            $event.preventDefault();
            if(!$rootScope.curUser){
                $rootScope.login();
                return;
            }
            var collectId = $rootScope.collectMap[room.id];
            if(collectId){
                $http.post(window.ctx + "center/collect/delete", $.param({
                    id: collectId
                })).success(function(data){
                    if(data.success){
                        $rootScope.loadCollect();
                    }
                });
            }else{
                $http.post(window.ctx + "center/collect/add", $.param({
                    roomId: room.id
                })).success(function(data){
                    if(data.success){
                        $rootScope.loadCollect();
                    }
                });
            }
        };

        $rootScope.loadLogin();

        //platforms: platforms, types: types]
        // 获取分类和平台
        $http.get(window.ctx + "index/getIndexData").success(function(data){
            $rootScope.topData = data.data;
        });

        // 关键字搜索
        $rootScope.search = {
            kw: "",
            submit: function($event){
                if($event && $event.keyCode != 13){
                    return;
                }
                $rootScope.$state.go('room', {page:0, kw: this.kw}, {reload: true, inherit: true})
            }
        };

        // 获取滚动条宽度
        $rootScope.scrollWidth = (function(){
            var tempDiv = $('<div style="overflow: scroll; height: 101px; "><div style="height: 100px;;"></div></div>');
            $("body").append(tempDiv);
            var scrollWidth = tempDiv[0].offsetWidth - tempDiv[0].scrollWidth;
            tempDiv.remove();
            return scrollWidth;
        })();


        // var initWidth = 338;
        // $rootScope.roomSize = {width: 0, height: 0};
        var initWidth = 338;
        $rootScope.roomSize = {width: initWidth, height: 251};
        var ratio = initWidth / 251;

        /**
         * 左侧滚轮事件
         * @param delta 大于0则往上 小于则往下 0则不变
         */
        var leftMousewheel = function(delta){
            var leftEle = $('.m-search');
            var moveEle = leftEle.find(">div:eq(0)");
            // 实际的位置 直接取可能因为动画效果有延迟
            var curMar = moveEle.data("realTop") || 0;
            // 因为有动画延迟 需要取实时的 计算误差1px以内
            var height = leftEle.height() - parseFloat(moveEle.css("margin-top"));

            var targetMar = curMar + delta * 150;
            // 最小上边距
            var minMar = $(window).height() - leftEle.offset().top - height;
            // console.log(targetMar + "-" + minMar + "-" + height);
            if(targetMar < minMar){
                targetMar = minMar
            }
            if(targetMar > 0){
                targetMar = 0;
            }
            moveEle.data("realTop", targetMar).css("margin-top", targetMar);
        };
        var resizeIsInit = true;
        $(window).resize(function(){
            var rightDiv = $(".main .content>.right");
            var viewDiv = rightDiv.find(".m-view");
            rightDiv.width($(window).width() - rightDiv.offset().left);
            rightDiv.height($(window).height());
            $(".main").height($(window).height());
            viewDiv.height($(window).height() - viewDiv.offset().top);

            // 重新调整滚动位置
            leftMousewheel(0);

            // 计算房间块大小
            var bodyWidth = viewDiv.width() - $rootScope.scrollWidth;
            var size = Math.ceil(bodyWidth / (initWidth + 10));
            var width = Math.floor(bodyWidth / size - 10);
            $rootScope.roomSize = {width: width, height: width / ratio};
            // 第一次resize 大部分模板没有初始化完毕 禁止$apply
            if(resizeIsInit){
                resizeIsInit = false;
                setTimeout(function(){
                    $rootScope.roomShowTrans = true;
                    $rootScope.$apply('roomShowTrans');
                }, 1000);
            }else{
                $rootScope.$apply('roomSize');
            }
        }).resize();

        // 加载左侧滚轮事件
        $('.m-search').mousewheel(function(event, delta) {
            leftMousewheel(delta);
        });
    }]);
    
    // 路由配置
    main.config(['$stateProvider', '$urlRouterProvider', '$httpProvider', function($stateProvider, $urlRouterProvider, $httpProvider) {
        // 无效链接全部转向首页
        $urlRouterProvider.otherwise('/1///');

        // 首页
        $stateProvider.state('room', {
            url: '/:page/:kw/:platformName/:tag',
            templateUrl: "index-tem",
            controller: 'room.index',
            title: function(event, toState, toParams, fromState, fromParams){
                var title = "";
                if(toParams.platformName){
                    title += "/" + toParams.platformName;
                }
                if(toParams.tag){
                    title += "/" + toParams.tag;
                }
                if(toParams.kw){
                    title += "/包含'" + toParams.kw + "'";
                }
                if(title){
                    title = title.substr(1);
                }
                return title;
            }
        }).state('room.insetDetail', {
            url: '/inset-detail/:roomId',
            templateUrl: 'inset-detail-tem',
            controller: "room.insetDetail"
        }).state('room.collect', {
            url: '/collect',
            templateUrl: 'collect-tem',
            title: "我的关注"
        }).state('room.type', {
            url: '/type',
            templateUrl: 'type-tem',
            title: "所有分类"
        }).state('room.recommend', {
            url: '/recommend/:rPage',
            templateUrl: 'recommend-tem',
            controller: "recommend",
            title: "精彩推荐"
        });
        // 改变post提交方式 data所在位置
        $httpProvider.defaults.headers.post = {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        }
    }]);


    /**
     * 显示所有房间
     */
    main.controller('room.index', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        $scope.$stateParams = $stateParams;
        $scope.$root.search.kw = $stateParams.kw;
        // 每页显示
        var max = 120;
        var page = parseInt($stateParams.page) || 1;
        // 加载数据方法
        var loadRoom = function(){
            $http({
                url: window.ctx + "room/page",
                params: {
                    max: max,
                    page: page,
                    tag: $stateParams.tag,
                    kw: $stateParams.kw,
                    platformName: $stateParams.platformName
                }
            }).success(function(data){
                $scope.rooms = data.rooms;
                $scope.recommends = data.recommends;
                // 分页
                $scope.paginate = {
                    pageSize: max,
                    total: data.total,
                    current: page,
                    href: function(cur){
                        return $state.href("room", {page: cur}, {inherit: true});
                    }
                }
            });
        };
        // 如果是子页面则 延迟加载该页面数据
        if($state.current.name != "room"){
            setTimeout(function(){
                loadRoom();
            }, 500)
        }else{
            loadRoom()
        }

        $scope.$on("onRepeatFinish", function(){
            $(window).resize();
        });
    }]);


    main.controller('room.insetDetail', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        var detailEle = $element.find(".r-detail");
        $(window).resize(function(){
            // 如果元素已经被删除 则解除绑定
            if(!detailEle.parents("body").length){
                $(window).unbind('resize', arguments.callee);
                return;
            }
            // detailEle.height($(window).height());
            // detailEle.width($(window).width());
            // 两边留白10px
            var maxWidth = detailEle.width() - 20;
            var maxHeight = detailEle.height() - 110;
            var radio = 1.777;
            if(maxHeight * radio < maxWidth){
                $element.find(".cen").css({
                    'margin-top': 0,
                    width: maxHeight * radio
                }).find(".embed-div").css({
                    height: maxHeight,
                    width: maxHeight * radio
                });
            }else{
                $element.find(".cen").css({
                    'margin-top': (maxHeight - (maxWidth / radio)) / 2,
                    width: maxWidth
                }).find(".embed-div").css({
                    height: maxWidth / radio,
                    width: maxWidth
                });
            }
        });

        var loadEmbed = function(){
            if(!$scope.$root.isInsert($scope.curRoom.platform.flag)){
                window.open($scope.curRoom.url, "_blank");
                $scope.close();
                return;
            }
            $scope.$root.title = $scope.curRoom.name + " - " + $scope.$root.webName;
            if($scope.curRoom.quoteUrl || $scope.curRoom.platform.flag == 'zhanQi'){
                var playEle;
                if($scope.curRoom.quoteUrl){
                    playEle = $('<embed src="' + $scope.curRoom.quoteUrl + '" style="width: 100%; height: 100%" allownetworking="all" allowscriptaccess="always" quality="high" bgcolor="#000" wmode="window" allowfullscreen="true" allowFullScreenInteractive="true" type="application/x-shockwave-flash">')
                }else{
                    playEle = $('<iframe style="width:100%; height:100%; border:none;" src="http://www.zhanqi.tv/live/embed?roomId=' + $scope.curRoom.flag + '"></iframe>')
                }
                $element.find(".embed-div").append(playEle);
                setTimeout(function(){
                    $(window).resize();
                });
            }else{
                $element.find("iframe").attr("src", $scope.curRoom.url);
            }

        };
        // 关闭方法 回到上一个页面
        $scope.close = function(){
            // 如果是ajax加载就不是从页面中点击过来的 回到首页就行
            if($scope.isAjax){
                $state.go('^');
            }else{
                // 如果是页内操作 回退路由
                window.history.back();
            }
        };

        $scope.isAjax = !$scope.curRoom || $scope.curRoom.id != $stateParams.roomId;
        if($scope.isAjax){
            $http({
                url: window.ctx + "room/one",
                params: {id: $stateParams.roomId}
            }).success(function(data){
                $scope.curRoom = data.room;
                loadEmbed();
            });

        }else{
            loadEmbed();
        }
    }]);

    /**
     * 推荐
     */
    main.controller('recommend', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        $scope.$stateParams = $stateParams;
        // 每页显示
        var max = 120;
        var page = parseInt($stateParams.rPage) || 1;
        $http({
            url: window.ctx + "room/getRecommend",
            params: {
                max: max,
                page: page
            }
        }).success(function(data){
            $scope.recommends = data.recommends;
            // 分页
            $scope.rPaginate = {
                pageSize: max,
                total: data.total,
                current: page,
                href: function(cur){
                    return $state.href("room.recommend", {rPage: cur}, {inherit: true});
                }
            }
        });

        $scope.$on("onRepeatFinish", function(){
            $(window).resize();
        });
    }]);

    main.controller('login', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        $scope.loginSubmit = function(){
            $http.post(window.ctx + "auth/login", $.param($scope.login)).success(function(data){
                if(data.success){
                    $.cookie("show-JR", "");
                    $scope.$root.loadLogin();
                    $scope.$root.windows.close($scope.curWindow);
                }else{
                    alert(data.message || "系统错误")
                }
            });
        }
    }]);

    main.controller('register', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        $scope.registerSubmit = function(){
            $http.post(window.ctx + "auth/register", $.param($scope.register)).success(function(data){
                if(data.success){
                    $.cookie("show-JR", "");
                    $scope.$root.loadLogin();
                    $scope.$root.windows.close($scope.curWindow);
                }else{
                    alert(data.message || "系统错误")
                }
            });
        }
    }]);

    main.controller('updatePwd', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        $scope.submit = function(){
            $http.post(window.ctx + "center/user/updatePwd", $.param($scope.sData)).success(function(data){
                if(data.success){
                    alert("修改成功");
                    $scope.$root.windows.close($scope.curWindow);
                }else{
                    alert(data.message || "系统错误")
                }
            });
        }
    }]);
    
    

})();
