/**
 * Created by 勇 on 2016/8/23 0023.
 */
(function(){
    var main = angular.module("main", ['tools', 'ui.router']);
    main.run(['$rootScope', function($rootScope){
        $rootScope.ctx = window.ctx;
    }]);

    main.run(['$rootScope', '$state', '$stateParams', '$log', '$http', '$window', function($rootScope, $state, $stateParams, $log, $http, $window) {
        $rootScope.$state = $state;
        $rootScope.$stateParams = $stateParams;
        // 弹出窗口管理对象 使用m-window指令
        $rootScope.windows = {};

        // 当前登录用户
        $rootScope.curUser = null;

        // 刷新登录状态
        $rootScope.loadLogin = function(){
            $http.post(window.ctx + "auth/getCurrentUser").success(function(data){
                if(data.success){
                    $rootScope.curUser = data.data;
                    if($rootScope.curUser){
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
            $rootScope.windows.add({url: window.ctx + 'html/user/login.html'});
        };

        $rootScope.register = function(){
            $rootScope.windows.add({url: window.ctx + 'html/user/register.html'});
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
                if($rootScope.browserHidden){
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
        $http.post(window.ctx + "index/getIndexData").success(function(data){
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
            var tempDiv = $('<div style="overflow: scroll"></div>');
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

        $(window).resize(function(){
            var rightDiv = $(".main .content>.right");
            var viewDiv = rightDiv.find(".m-view");
            rightDiv.width($(window).width() - rightDiv.offset().left);
            rightDiv.height($(window).height());
            viewDiv.height($(window).height() - viewDiv.offset().top);

            // 计算房间块大小
            var bodyWidth = viewDiv.width() - $rootScope.scrollWidth - 10;
            var size = Math.ceil(bodyWidth / (initWidth + 10));
            var width = Math.floor(bodyWidth / size - 10);
            $rootScope.roomSize = {width: width, height: width / ratio};
            $rootScope.$apply('roomSize');
        }).resize();

        // 加载左侧滚轮事件
        // http://cdn.bootcss.com/jquery-mousewheel/3.1.13/jquery.mousewheel.min.js
        setTimeout(function(){
            $.getScript('http://cdn.bootcss.com/jquery-mousewheel/3.1.13/jquery.mousewheel.min.js', function(){

            });
        }, 500);

    }]);

    // 路由配置
    main.config(['$stateProvider', '$urlRouterProvider', '$httpProvider', function($stateProvider, $urlRouterProvider, $httpProvider) {
        // 无效链接全部转向首页
        $urlRouterProvider.otherwise('/1///');

        // 首页
        $stateProvider.state('room', {
            url: '/:page/:kw/:platformName/:tag',
            templateUrl: window.ctx +　'html/room/index.html',
            controller: 'room.index'
        }).state('room.insetDetail', {
            url: '/inset-detail/:roomId',
            templateUrl: window.ctx +　'html/room/inset-detail.html',
            controller: "room.insetDetail"
        }).state('room.collect', {
            url: '/collect',
            templateUrl: window.ctx +　'html/collect.html'
        }).state('room.type', {
            url: '/type',
            templateUrl: window.ctx +　'html/type.html'
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
        $scope.$root.search.kw = $stateParams.kw;
        // 每页显示
        var max = 120;
        var page = parseInt($stateParams.page) || 1;
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
            $(data.rooms).each(function(){
                this.href = this.quoteUrl ? $state.href("room.insetDetail", {roomId: this.id}, {inherit: true}) : this.url;
            });
            $scope.rooms = data.rooms;
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
                }).find("embed").css({
                    height: maxHeight,
                    width: maxHeight * radio
                });
            }else{
                $element.find(".cen").css({
                    'margin-top': (maxHeight - (maxWidth / radio)) / 2,
                    width: maxWidth
                }).find("embed").css({
                    height: maxWidth / radio,
                    width: maxWidth
                });
            }
        });

        var loadEmbed = function(){
            // $element.find("embed").attr("src", $scope.curRoom.quoteUrl)
            var embedEle = $('<embed src="' + $scope.curRoom.quoteUrl + '" width="640" height="360" allownetworking="all" allowscriptaccess="always" quality="high" bgcolor="#000" wmode="window" allowfullscreen="true" allowFullScreenInteractive="true" type="application/x-shockwave-flash">')
            $element.find(".embed-div").append(embedEle);
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
            });

        }else{
            loadEmbed();
        }
    }]);

    main.controller('login', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        $scope.loginSubmit = function(){
            $http.post(window.ctx + "auth/login", $.param($scope.login)).success(function(data){
                if(data.success){
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
