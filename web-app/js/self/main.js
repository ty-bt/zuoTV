/**
 * Created by 勇 on 2016/8/23 0023.
 */
(function(){
    var main = angular.module("main", ['tools', 'ui.router']);
    main.run(['$rootScope', function($rootScope){
        // logo动画延迟
        setTimeout(function(){
            $(".main .content > .left h1 span").addClass("animation")
        }, 2000);
        // 生成背景图
        var cloneA = $.extend({}, Trianglify.colorbrewer)
        for(var k in cloneA){
            var newArr = [];
            $(cloneA[k]).each(function(i){
                newArr.push(cloneA[k][cloneA[k].length - i -　1]);
            });
            Trianglify.colorbrewer[k + "_f"] = newArr;
        }
        var currentDate = new Date();
        $(".left-bg").css({
            "background-image": "url(" + Trianglify({
                // variance: "0.74",
                seed: currentDate.getYear() + currentDate.getDay() + currentDate.getDate() + currentDate.getHours(),
                // width: $(window).width(),
                width: 245,
                height: $(window).height(),
                cell_size: 30,
                x_colors: "random"}).png() + ")"
        });
        $rootScope.ctx = window.ctx;
        $rootScope.webName = "zuoTV";
        // 是否显示聊天窗口
        $rootScope.showChat = false;
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



        // 分屏数据管理
        $rootScope.splitScreen = {
            url: "",
            data: [],
            dataMap: {1: null, 2: null, 3: null, 4: null},
            localName: "splitScreenData",
            save: function(){
                window.localStorage[this.localName] = JSON.stringify(this.data);
            },
            move: function(room, change){
                var _this = this;
                var data = $rootScope.splitScreen.data;
                $(data).each(function(i){
                    if(this == room){
                        var targetIndex = this.splitSort + change;
                        targetIndex = targetIndex > data.length ? 1 : targetIndex;
                        targetIndex = targetIndex < 1 ? data.length : targetIndex;
                        _this.dataMap[targetIndex].splitSort = room.splitSort;
                        room.splitSort = targetIndex;
                        _this.reloadSort();
                        _this.save();
                        return false;
                    }

                });
            },
            reloadSort: function(){
                var _this = this;
                var data = $rootScope.splitScreen.data;
                $(data).sort(function(a, b){
                    return (b.splitSort || 0) - (a.splitSort || 0);
                }).each(function(i){
                    var sort = data.length - i;
                    _this.dataMap[sort] = this;
                    this.splitSort = sort;
                });
            },
            loadUrl: function(){
                var ids = [];
                $($rootScope.splitScreen.data).each(function(){
                    ids.push(this.id);
                });
                return "http://" + location.host + window.ctx + $state.href("room.split", {ids: JSON.stringify(ids)}, {inherit: true});
            },
            load: function(){
                var localData = window.localStorage[this.localName];
                if(localData){
                    this.data = JSON.parse(localData);
                }else{
                    this.data = [];
                }
                this.reloadSort();
            },
            add: function(room, $event){
                if(this.indexOf(room.id) == -1){
                    if(this.data.length >= 4){
                        alert("分屏数量已达到上限");
                    }else{
                        this.data.push(room);
                        this.reloadSort();
                        this.save();
                    }
                }
                if($event){
                    $event.stopPropagation();
                    $event.preventDefault();
                }
            },
            remove: function(id, $event){
                var _this = this;
                var index = this.indexOf(id);
                if(index != -1){
                    this.data.splice(index, 1);
                    _this.reloadSort();
                    this.save();
                }
                if($event){
                    $event.stopPropagation();
                    $event.preventDefault();
                }
            },
            clear: function(){
                this.data = [];
                this.save();
            },
            indexOf: function(id){
                for(var a = 0; a < this.data.length; a++){
                    if(this.data[a].id == id){
                        return a;
                    }
                }
                return -1;
            },
            // 使用rooms中的前四个可以分屏的房间覆盖以前的分屏列表,并进入分屏界面
            coverAll: function(rooms){
                var splitRooms = [];
                $(rooms).each(function(){
                    if($rootScope.isInsert(this.platform.flag)){
                        splitRooms.push(this);
                    }
                    if(splitRooms.length == 4){
                        return false;
                    }
                });
                if(splitRooms.length < 2){
                    alert("可分屏的房间太少(分屏不支持熊猫)");
                    return;
                }
                this.data = splitRooms;
                this.reloadSort();
                this.save();
                $rootScope.$state.go('room.split', {ids: ''}, {inherit: true});
            }
        };
        $rootScope.splitScreen.load();

        // 复制链接
        $('#copy-split').zclip({
            path:'http://cdn.bootcss.com/zclip/1.1.2/ZeroClipboard.swf',
            copy:function(){
                return $rootScope.splitScreen.loadUrl();
            },
            afterCopy: function(){

            }
        });

        /**
         * 打开房间的时候调用
         * @param room 房间对象
         */
        $rootScope.openRoom = function(room, $event){
            // 不可以嵌入直接打开
            if(!$rootScope.isInsert(room.platform.flag)){
                $event.stopPropagation();
                $event.preventDefault();
                window.open(room.url, "_blank")
                return;
            }
            $rootScope.curRoom = room;
        };

        /**
         * 返回是否支持页内播放
         * @param flag 平台标识
         * @returns {boolean}
         */
        $rootScope.isInsert = function(flag){
            return flag != "panda" && flag != "quanMin";
        };

        /**
         * 返回嵌入iframe播放的地址  没有则返回null
         * @param room 房间对象
         * @returns {boolean}
         */
        $rootScope.getIframeUrl = function(room){
            switch (room.platform.flag){
                case 'zhanQi':
                    return "http://www.zhanqi.tv/live/embed?roomId=" + room.flag;
                case 'huoMao':
                    return "https://www.huomao.com/outplayer/index/" + room.flag;
                default:
                    return null;
            }
        };

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
            $rootScope.windows.add({url: 'login-tem'});
        };

        $rootScope.register = function(){
            $rootScope.windows.add({url: 'register-tem'});
        };

        $rootScope.showChart = function(room, $event){
            $rootScope.windows.add({url: 'one-chart-tem', room: room, width: '80%'});
            $event.stopPropagation();
            $event.preventDefault();
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
                    var collectMap = {};
                    // 以前是获取所有的 现在只获取在线的
                    $rootScope.collectsTotal = data.data.total;
                    $rootScope.onLineCollects= data.data.collects;
                    $(data.data.collects).each(function(){
                        collectMap[this.id] = true;
                    });
                    $rootScope.collects = data.data.collects;
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
            var isCollect = $rootScope.collectMap[room.id];
            if(isCollect){
                $http.post(window.ctx + "center/collect/delete", $.param({
                    roomId: room.id
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

        //platforms: platforms, types: types]
        // 获取分类和平台
        $http.get(window.ctx + "index/getIndexData").success(function(data){
            $rootScope.topData = data.data;
        });

        // 刷新登录状态
        $rootScope.loadLogin();


        // 聊天室加载
        $rootScope.chat = ({
            socket: new SockJS(window.ctx + "stomp"),
            client: null,
            logs: [],
            show: false,
            init: function(){
                var _this = this;
                $http.post(window.ctx + "chat/latelyLog").success(function(data){
                    if(data.success){
                        _this.logs = data.data.concat(_this.logs);
                        $(_this.logs).each(function(i){
                            // 间隔超过5分钟或第一条要显示时间
                            if(!i || new Date(this.d).getTime() - new Date(_this.logs[i - 1].d).getTime() >= 300000){
                                this.longTime = true;
                            }
                        });
                        _this.setScrollBottom();
                    }else{
                        $log.log("加载最近聊天记录错误", data);
                    }
                });
                var client = this.client = Stomp.over(this.socket);

                client.connect({}, function() {
                    client.subscribe("/topic/chatMsg", function(message) {
                        // 有双层转换
                        var data = JSON.parse(JSON.parse(message.body));
                        if(data.msg){
                            $rootScope.$apply(function(){
                                if(!_this.logs.length || new Date(new Date(data.d).getTime() - _this.logs[_this.logs.length - 1].d).getTime() >= 300000){
                                    // 间隔超过5分钟要显示时间
                                    data.longTime = true;
                                }
                                _this.logs.push(data);
                                _this.setScrollBottom();

                                var top = 0;
                                var lastSp = $(".danmu span:last");
                                if(lastSp.length){
                                    var lastTop = parseInt(lastSp.css("top"))
                                    if(lastTop < 50){
                                        top = lastTop + 25;
                                    }
                                }
                                var sp = $("<span></span>").text(data.msg).css({top: top});
                                // 74
                                $(".danmu").append(sp);
                                setTimeout(function(){
                                    sp.remove();
                                    $(".chat-icon").removeClass("hover");
                                }, 3000);
                                $(".chat-icon").addClass("hover");
                            });
                        }
                    });
                });

                return this;
            },
            showHide: function(){
                this.show = !this.show;
                this.setScrollBottom(true);
                setTimeout(function(){
                    // 重新调整滚动位置
                    leftMousewheel(0);
                }, 100);
            },
            send: function(msg){
                if(!msg.trim()){
                    alert("不能空值");
                    return;
                }
                if(!$rootScope.curUser){
                    alert("同志, 要登录才能玩...");
                    return;
                }
                var _this = this;
                $http.post(window.ctx + "chat/send", $.param({msg: msg})).success(function(data){
                    if(!data.success){
                        alert(data.message || "系统错误")
                    }else{
                        _this._sendMsg = '';
                    }
                });

            },
            /**
             * 将滚动条拉到最底
             */
            setScrollBottom: function(noIf){
                var scrollEle = $(".chat .chat-logs").stop(true, true);
                var allHeightEle = $(".chat .chat-logs-height");
                // 滚动条距离底部低于30，则强制跳到最下方
                if(noIf || allHeightEle.innerHeight() - scrollEle.height() - scrollEle.scrollTop() <= 30){
                    // 等待angular加载页面
                    setTimeout(function(){
                        var maxTop = allHeightEle.innerHeight() - scrollEle.height();
                        // scrollEle.scrollTop(maxTop);
                        scrollEle.animate({
                            scrollTop: maxTop
                        }, 200);
                    });
                }
            },
            // 输入框绑定值
            _sendMsg: "",
            keyupSend: function($event){
                if($event && $event.keyCode !== 13){
                    return;
                }
                if(!this._sendMsg.trim()){
                    return;
                }
                this.send(this._sendMsg);
                if($event){
                    $event.stopPropagation();
                    $event.preventDefault();
                }

            }
        }).init();
        var socket = new SockJS(window.ctx + "stomp");
        var client = Stomp.over(socket);



        $("#helloButton").click(function() {
            client.send("/app/chatMsg", {}, JSON.stringify({content: $(".msg").val()}));
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
        $rootScope.roomSize = {width: initWidth, height: 241};
        // 图片比例
        var ratio = 320 / 180;

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
            if(targetMar < minMar){
                targetMar = minMar
            }
            if(targetMar > 0){
                targetMar = 0;
            }
            moveEle.data("realTop", targetMar).css("margin-top", targetMar);
        };
        $(window).resize(function(){
            var rightDiv = $(".main .content>.right");
            var viewDiv = rightDiv.find(".m-view");
            // rightDiv.width($(window).width() - rightDiv.offset().left);
            // rightDiv.height($(window).height());
            // $(".main").height($(window).height());
            // viewDiv.height($(window).height() - viewDiv.offset().top);

            // 重新调整滚动位置
            leftMousewheel(0);

            // 计算房间块大小
            var bodyWidth = viewDiv.width() - $rootScope.scrollWidth;
            var size = Math.ceil(bodyWidth / (initWidth + 10));
            var width = Math.floor(bodyWidth / size - 10);
            var roomSize = {width: width, height: width / ratio + 48};
            $rootScope.roomSize = roomSize;
            $(".room").css(roomSize);
            $(".room .photo").css("height", roomSize.height - 48);
        }).resize();

        // 加载左侧滚轮事件
        $('.m-search').mousewheel(function(event, delta) {
            leftMousewheel(delta);
        });
    }]);
    
    // 路由配置
    main.config(['$stateProvider', '$urlRouterProvider', '$httpProvider', function($stateProvider, $urlRouterProvider, $httpProvider) {
        // 无效链接全部转向首页
        // $urlRouterProvider.otherwise('/1////');
        $urlRouterProvider.otherwise(function(a, cur){
            // 以前首页只有四个参数 现在有五个 老链接跳转
            try{
                var strArr = cur.$$path.match(/^((\/[^\/]*){4})(\/[^\d]+(\/[^\/]*)*)$/);
                if(strArr){
                    return strArr[1] + "/" + strArr[3]
                }
            }catch(e){
            }
            return '/1////';

        });

        // 首页
        $stateProvider.state('room', {
            url: '/:page/:kw/:platformName/:tag/:order',
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
            controller: "collectCtrl",
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
        }).state('room.split', {
            url: '/split/:ids',
            templateUrl: 'split-tem',
            controller: "room.split",
            title: "分屏观看"
        }).state('room.log', {
            url: '/log/:lPage/:lMax/:order/:orderType/:roomId',
            templateUrl: 'log-tem',
            controller: "room.log",
            title: "观众数量波动"
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
        $(window).resize();
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
                    platformName: $stateParams.platformName,
                    order: $stateParams.order
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
            // $(window).resize();
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
            var maxHeight = detailEle.height() - 70;
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
            var room = $scope.curRoom;
            if(!$scope.$root.isInsert(room.platform.flag)){
                window.open(room.url, "_blank");
                $scope.close();
                return;
            }

            room.iframeUrl = $scope.$root.getIframeUrl(room);
            $scope.$root.title = room.name + " - " + $scope.$root.webName;
            if(room.quoteUrl || room.iframeUrl){
                var playEle;
                if(room.quoteUrl){
                    playEle = $('<embed src="' + $scope.curRoom.quoteUrl + '" style="width: 100%; height: 100%" allownetworking="all" allowscriptaccess="always" quality="high" bgcolor="#000" wmode="window" allowfullscreen="true" allowFullScreenInteractive="true" type="application/x-shockwave-flash">')
                }else{
                    playEle = $('<iframe style="width:100%; height:100%; border:none;" src="' + room.iframeUrl + '"></iframe>')
                }
                $element.find(".embed-div").append(playEle);
                setTimeout(function(){
                    $(window).resize();
                });
            }else{
                $element.find("iframe").attr("src", room.url);
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


    main.controller('room.split', ['$scope', '$http', '$element', '$stateParams', '$state', '$sce', function($scope, $http, $element, $stateParams, $state, $sce){
        if($stateParams.ids){
            $scope.noLocal = true;
            $http({
                url: window.ctx + "room/split",
                params: {
                    ids: $stateParams.ids
                }
            }).success(function(data){
                if(data.success){
                    $scope.splitRooms = data.rooms;
                }else{
                    alert(data.message || "系统错误")
                }
            });

        }
        $scope.trustSrc = function(src) {
            return $sce.trustAsResourceUrl(src);
        }


    }]);

    // 观众数量变化图表
    main.controller('room.log', ['$scope', '$http', '$element', '$stateParams', '$state', '$sce', function($scope, $http, $element, $stateParams, $state, $sce){
        $http({
            url: window.ctx + "room/getRoomLog",
            params: {
                page: $stateParams.lPage,
                max: $stateParams.lMax,
                order: $stateParams.order,
                orderType: $stateParams.orderType,
                roomId: $stateParams.roomId
            }
        }).success(function(data){
            $(window).resize();
            if(data.success){
                if(window.echarts){
                    $scope.roomLogs = data.data.roomLogs;
                }else{
                    $.getScript("http://cdn.bootcss.com/echarts/3.5.0/echarts.min.js", function(){
                        $scope.roomLogs = data.data.roomLogs;
                        $scope.$apply("roomLogs");
                    });

                }
            }else{
                alert(data.message || "系统错误")
            }
        });
    }]);

    // 单个观众数量
    main.controller('oneLog', ['$scope', '$http', '$element', '$stateParams', '$state', '$sce', function($scope, $http, $element, $stateParams, $state, $sce){
        var room = $scope.curWindow.room;
        $http({
            url: window.ctx + "roomLog/one",
            params: {
                roomId: room.id
            }
        }).success(function(data){
            $(window).resize();
            if(data.success){
                if(window.echarts){
                    $scope.roomLog = data.data;
                }else{
                    $.getScript("http://cdn.bootcss.com/echarts/3.5.0/echarts.min.js", function(){
                        $scope.roomLog = data.data;
                        $scope.$apply("roomLog");
                    });

                }
            }else{
                $scope.$root.windows.close($scope.curWindow);
                alert(data.message || "系统错误");
            }
        });
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

    main.controller('collectCtrl', ['$scope', '$http', '$element', '$stateParams', '$state', function($scope, $http, $element, $stateParams, $state){
        if(!$scope.$root.curUser){
            $state.go('^');
            return;
        }
        $http.post(window.ctx + "center/collect/list1").success(function(data){
            if(data.success){
                $scope.allCollect = data.data.collects;
            }else{
                $log.log(data.message || "系统错误");
            }
        });
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
