<%--
  Created by IntelliJ IDEA.
  User: 勇
  Date: 2016/8/23 0023
  Time: 20:00
--%>

<%@ page import="grails.util.Metadata" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE HTML>
<html ng-app="main">
<head>
    <title ng-bind="title">zuoTV - 聚合全网直播</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <%String version = Metadata.getCurrent()[Metadata.APPLICATION_VERSION]%>
    <meta name="description" content="zuo TV,一站聚合六个直播平台百万主播,不用一个一个平台去找喜爱的主播. 包含妹纸直播(星秀),LOL直播,户外直播,一起看电影(放映室), 现已收录斗鱼,龙珠,虎牙,全民,战旗,熊猫平台..." />
    <meta name="keywords" content="zuotv,聚合直播,作TV,nozuonodie,直播人数统计,直播平台统计,Zuo,Zuo TV,直播导航,直播推荐,妹纸直播,星秀,一起看电影"/>
    %{--<link href="${resource(file: '/css/font-awesome/css/font-awesome.min.css')}" rel="stylesheet" />--}%
    <link href="http://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    %{--<link href="${resource(file: '/css/normalize.css')}" rel="stylesheet" />--}%
    <link href="http://cdn.bootcss.com/normalize/5.0.0/normalize.css" rel="stylesheet">
    <link href="${resource(file: '/css/base.css')}?v=${version}" rel="stylesheet" />
    <script type="text/javascript">window.ctx = "${createLink(uri:'/')}";</script>
    %{--<script type="text/javascript" src="${resource(file: '/js/jquery-3.1.0.min.js')}"></script>--}%
    <script type="text/javascript" src="http://cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
    <script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
    <script src="http://cdn.bootcss.com/jquery-mousewheel/3.1.13/jquery.mousewheel.min.js"></script>
    <script type="text/javascript" src="http://cdn.bootcss.com/angular.js/1.5.6/angular.min.js"></script>
    %{--<script type="text/javascript" src="${resource(file: '/js/angular/angular.min.js')}"></script>--}%
    %{--<script type="text/javascript" src="${resource(file: '/js/angular/angular-ui-router.min.js')}"></script>--}%
    <script src="http://cdn.bootcss.com/angular-ui-router/0.4.2/angular-ui-router.min.js"></script>
    <script type="text/javascript" src="${resource(file: '/js/self/tools.js')}?v=${version}"></script>
    <script type="text/javascript" src="${resource(file: '/js/self/main.js')}?v=${version}"></script>
    %{-- 所有分类页面 --}%
    <script type="text/ng-template" id="type-tem">
        <div class="types" ng-show="$root.topData.platforms">
            <div>
                <h2 class="show-params"><span><i class="fa fa-ship"></i>平台</span></h2>
                <div class="t">
                    <a ng-repeat="pla in $root.topData.platforms"
                       ui-sref="room({page:1, platformName: pla.name})"
                       ui-sref-opts="{reload: true, inherit: true}"
                       ng-class="{selected: pla.name == $stateParams.platformName}">
                        <span class="ellipsis">{{pla.name}}</span>
                        <span class="min ellipsis"><i class="fa fa-child"></i>{{pla.onLineAd | wanNum}}</span>
                        <span class="min ellipsis"><i class="fa fa-home"></i>{{pla.onLineRoom | wanNum}}</span>
                    </a>
                </div>
            </div>

            <div>
                <h2 class="show-params"><span><i class="fa fa-anchor"></i>分类</span></h2>
                <div class="t">
                    <a ng-repeat="t in $root.topData.types"
                       ui-sref="room({page:1, tag: t.name})"
                       ui-sref-opts="{reload: true, inherit: true}"
                       ng-class="{selected: t.name == $stateParams.tag}">
                        <span class="ellipsis">{{t.name}}</span>
                        <span class="min ellipsis"><i class="fa fa-child"></i>{{t.adSum | wanNum}}</span>
                        <span class="min ellipsis"><i class="fa fa-home"></i>{{t.roomCount | wanNum}}</span>
                    </a>
                </div>
            </div>
        </div>
    </script>

    %{-- 分页控件 --}%
    <script type="text/ng-template" id="paginate-tem">
        <div class="t-pagination" ng-if="config.pageCount" ng-init="tempCount = config.showCount; pCount = tempCount / 2">
            <a href="{{createHref(config.current - 1)}}" class="pre page" ng-class="{disable: config.current == 1}">上一页</a>
            <a href="{{createHref(1)}}" class="page" ng-class="{current: config.current == 1}">1</a>

            <a class="sh" ng-if="config.current - config.leftCount > 2">...</a>
            <a href="{{createHref(i)}}" class="page" ng-repeat="i in (config.current - config.leftCount | arr: config.leftCount)">{{i}}</a>
            <a href="{{createHref(i)}}" class="page current" ng-if="config.leftCount || config.current == 2">{{config.current}}</a>

            <a href="{{createHref(i)}}" class="page" ng-repeat="i in (config.current + 1 | arr: config.rightCount)" ng-if="i <= config.pageCount">{{i}}</a>
            <a class="sh" ng-if="config.pageCount - config.rightCount != config.current">...</a>
            <!--最后一页-->
            <a href="{{createHref(config.pageCount)}}" class="page" ng-if="config.current + config.rightCount < config.pageCount">{{config.pageCount}}</a>
            <a href="{{createHref(config.current + 1)}}" class="next page" ng-class="{disable: config.current == config.pageCount}">下一页</a>
        </div>
    </script>

    %{-- 弹出窗口 模板--}%
    <script type="text/ng-template" id="m-window-tem">
        <div class="t-window">
            <div class="show-window"
                 ng-repeat="curWindow in config.windows"
                 ng-show="!curWindow.hide"
                 index="{{curWindow.index}}">
                <i class="fa fa-close close" ng-show="curWindow.closeBtn!==false" ng-class="{insert: room.quoteUrl}" ng-click="config.close(curWindow)"></i>
                <div ng-include="curWindow.url" onload="config.resize(curWindow)">
                </div>
            </div>
        </div>
    </script>

    %{-- 房间显示模板 --}%
    <script type="text/ng-template" id="room-show-tem">
        <a class="room trans2"
           ng-click="$root.openRoom(room)"
           ng-href="{{$state.href('room.insetDetail', {roomId: room.id}, {inherit: true})}}"
           ng-class="{'off-line': !room.isOnLine}"
           ng-style="roomSize">
            <table cellspacing="0" cellpadding="0">
                <tr>
                    <td style="width:70%;"></td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="2" class="photo"  ng-style="{height: $root.roomSize.height - 50}">
                        <img ng-src="{{room.img}}"/>
                        <span class="pla-name">{{room.platform.name}}</span>
                        <i class="fa fa-play-circle play" ng-class="{insert: $root.isInsert(room.platform.flag)}"></i>
                    </td>
                </tr>
                <tr class="top">
                    <td class="ellipsis title">{{room.name}}</td>
                    <td class="t-r ellipsis tag">{{room.tag}}</td>
                </tr>
                <tr class="bottom">
                    <td class="ellipsis anchor">
                        <i title="关注" roomId="{{room.id}}"
                           ng-click="$root.changeCollect($event, room);$event.stopPropagation();"
                           ng-class="{'fa-heart': $root.collectMap[room.id], 'fa-heart-o': !$root.collectMap[room.id]}"
                           class="fa heart"></i>{{room.anchor}}
                    </td>
                    <td class="t-r ellipsis num"><i class="fa fa-child"></i>{{room.adNum | wanNum}}</td>
                </tr>
            </table>
        </a>
    </script>

    %{--我的关注页面--}%
    <script type="text/ng-template" id="collect-tem">
        <div class="collect">
            <h2 class="show-params">
                <span><i class="fa fa-heart"></i>我的关注</span>
            </h2>
            <a ng-repeat="coll in $root.collects" repeat-finish ng-init="room=coll.room" room-show></a>
        </div>
    </script>

    %{--推荐页面--}%
    <script type="text/ng-template" id="recommend-tem">
        <div class="collect">
            <h2 class="show-params">
                <span><i class="fa fa-rocket"></i>精彩推荐</span>
            </h2>
            <a ng-repeat="room in recommends" repeat-finish room-show></a>
            <div ng-if="rPaginate" t-paginate="rPaginate"></div>
        </div>
    </script>

    %{--修改密码页面--}%
    <script type="text/ng-template" id="update-pwd-tem">
        <div class="login" ng-controller="updatePwd">
            <h2>修改密码</h2>
            <div>
                <form ng-submit="submit($event)">
                    <input type="text" ng-model="sData.oldPwd" placeholder="请输入当前密码"/><br/>
                    <input type="password" ng-model="sData.password" placeholder="请输入密码"/><br/>
                    <input type="password" ng-model="sData.rePassword" placeholder="再输一次密码"/><br/>
                    <button type="submit">确认</button>
                </form>
            </div>
        </div>
    </script>

    %{--注册页面--}%
    <script type="text/ng-template" id="register-tem">
        <div class="login" ng-controller="register">
            <h2>注册</h2>
            <div>
                <form ng-submit="registerSubmit($event)">
                    <input type="text" ng-model="register.name" placeholder="请输入账户名称"/><br/>
                    <input type="password" ng-model="register.password" placeholder="请输入密码"/><br/>
                    <input type="password" ng-model="register.rePassword" placeholder="再输一次密码"/><br/>
                    <button type="submit">注册</button>
                </form>
            </div>
            <div style="text-align: right; margin-top:10px;"><a href="javascript:;" ng-click="$root.windows.close(curWindow);$root.login()">已有账号,点击登录</a></div>
        </div>
    </script>

    %{--登录页面--}%
    <script type="text/ng-template" id="login-tem">
        <!-- 登录页面 -->
        <div class="login" ng-controller="login">
            <h2>登录</h2>
            <div>
                <form ng-submit="loginSubmit($event)">
                    <input type="text" ng-model="login.name" placeholder="请输入账户名称"/><br/>
                    <input type="password" ng-model="login.password" placeholder="请输入密码"/><br/>
                    <label ng-init="login={keep: true}"><input type="checkbox" ng-model="login.keep"/>记住登录状态</label>
                    <button type="submit">登录</button>
                </form>
            </div>
            <div style="text-align: right; margin-top:10px;"><a href="javascript:;" ng-click="$root.windows.close(curWindow);$root.register()">还没有账号,点击注册</a></div>
        </div>
    </script>

    %{--情人节--}%
    <script type="text/ng-template" id="jieri-tem">
    <div class="jieri" ng-controller="jieri214">
        <h1>是不是感觉视线越来越模糊</h1>
        <h2>是不是感觉身体被掏空</h2>
        <h3>骚年,少lu点</h3>
        <h2>嘿嘿,祝大家{{jName}}快乐</h2>
        <h3 ng-if="fx">居然被你发现了, 嘿嘿, 送你个尊贵VIP(虽然并没有什么用)</h3>
        <h3 ng-if="fx && !$root.curUser">你还没登录，没法给你送，登录去吧..</h3>
        <button ng-mousedown="down()" ng-mouseup="up()">{{fx ? "关闭" : "确定"}}</button>
    </div>
    </script>

    %{--房间首页列表--}%
    <script type="text/ng-template" id="index-tem">
        <div class="body">
            <div ng-if="recommends.length">
                <h2 class="show-params">
                    <span><i class="fa fa-hand-o-right"></i>瞎推荐</span>
                    <a ui-sref="room.recommend({rPage:1})"
                       ui-sref-opts="{inherit: true}" class="more">更多></a>
                </h2>
                <div style="overflow: hidden; width: 100%;">
                    <div style="width: 1920px;">
                        <a ng-repeat="room in recommends" repeat-finish room-show></a>
                    </div>
                </div>
            </div>
            <h2 class="show-params">
                <span><i class="fa fa-youtube-play"></i>全部直播</span>
                <span ng-if="$stateParams.platformName">{{$stateParams.platformName}}</span>
                <span ng-if="$stateParams.tag">{{$stateParams.tag}}</span>
                <span ng-if="$stateParams.kw">包含'{{$stateParams.kw}}'</span>
            </h2>
            <h3 ng-if="rooms" class="show-tit" ng-show="!rooms.length">米有找到相应的房间...</h3>
            <a ng-repeat="room in rooms" repeat-finish room-show></a>
            <div ng-if="paginate" t-paginate="paginate"></div>
            <div class="icp" ng-show="rooms"><a href="mailto:ty_bt@live.cn">ty_bt@live.cn</a> &copy;版权所有&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a target="_blank" href="http://www.miitbeian.gov.cn/">皖ICP备16023346号-1</a></div>
        </div>
        <div ui-view class="detail-view"></div>

    </script>

    %{--房间详情页--}%
    <script type="text/ng-template" id="inset-detail-tem">
        <div class="r-detail">
            <!-- 背景 -->
            <img ng-src="{{curRoom.img}}" class="back-img filter-blur" />
            <!-- 内容 -->
            <div class="d-content" ng-show="curRoom.quoteUrl">
                <div class="top">
                    <i class="fa fa-close" title="关闭" ng-click="close()"></i>
                </div>
                <div class="cen">

                    <h3 class="ellipsis">{{curRoom.name}}<span><i class="fa fa-child"></i>{{curRoom.adNum | wanNum}}</span></h3>

                    <div class="embed-div">

                    </div>
                    <div class="bottom">
                        <a ><i title="关注" roomId="{{room.id}}"
                               ng-click="$root.changeCollect($event, curRoom);$event.stopPropagation();"
                               ng-class="{'fa-heart': $root.collectMap[curRoom.id], 'fa-heart-o': !$root.collectMap[curRoom.id]}"
                               class="fa heart"></i>{{curRoom.anchor}}</a>
                        <a class="right" target="_blank" href="{{curRoom.url}}"><i class="fa fa-level-up"></i>跳转到{{curRoom.platform.name}}观看</a>
                    </div>
                </div>

            </div>

            <div class="d-content-i" ng-show="!curRoom.quoteUrl">
                <i title="关注" roomId="{{room.id}}"
                   ng-click="$root.changeCollect($event, curRoom);$event.stopPropagation();"
                   ng-class="{'fa-heart': $root.collectMap[curRoom.id], 'fa-heart-o': !$root.collectMap[curRoom.id]}"
                   class="fa heart"></i>
                <i class="fa fa-close" title="关闭" ng-click="close()"></i>
                <a class="fa fa-level-up" target="_blank" title="新窗口打开{{curRoom.platform.name}}观看" href="{{curRoom.url}}"></a>

                <iframe style="width:100%; height:100%; border:none;"></iframe>
            </div>
        </div>

    </script>
</head>

<body style="overflow: hidden">
    <div class="main" ng-cloak>

        <div class="content table">
            <div class="left">
                <h1><span>zuo</span> TV</h1>
                %{--seo --}%
                <div class="m-search trans2">
                    <div class="condition search-input">
                        <input type="text" placeholder="输入房间名或主播名搜索" ng-keyup="search.submit($event)" ng-model="search.kw" />
                        <i class="fa fa-search" ng-click="$state.go('room', {page:1, kw: search.kw}, {inherit: true})"></i>
                    </div>
                    <div class="condition" ng-show="topData.platforms">
                        <h2>平台</h2>
                        <div class="checkeds"> 
                            <a href="#" ng-class="{selected: !$stateParams.platformName}"
                               ui-sref="room({page:1, platformName: ''})"
                               ui-sref-opts="{inherit: true, reload:true}">全部</a>
                            <a ng-repeat="pla in topData.platforms"
                               ui-sref="room({page:1, platformName: pla.name})"
                               ui-sref-opts="{inherit: true, reload:true}"
                               ng-class="{selected: pla.name == $stateParams.platformName}">{{pla.name}}</a>

                        </div>
                    </div>
                    <div class="condition" ng-show="topData.types">
                        <h2>分类<a ui-sref="room.type"
                                 ui-sref-opts="{inherit: true, reload:true}"
                                 ng-show="topData.types.length > 20">全部></a>
                        </h2>
                        <div class="checkeds">
                            <a ui-sref="room({page:1, tag: ''})"
                               ui-sref-opts="{inherit: true, reload:true}"
                               ng-class="{selected: !$stateParams.tag}">全部</a>
                            <a ng-repeat="t in topData.types"
                               ui-sref="room({page:1, tag: t.name})"
                               ui-sref-opts="{inherit: true, reload:true}"
                               ng-if="$index < 20 || t.name == $stateParams.tag"
                               ng-class="{selected: t.name == $stateParams.tag}">{{t.name}}</a>

                        </div>
                    </div>
                    <div class="condition">
                        <h2>我的关注&nbsp;<span style="font-size: 13px;" ng-show="$root.collects.length">{{$root.onLineCollects.length}}/{{$root.collects.length}}</span>
                            <a ui-sref="room.collect"
                               ui-sref-opts="{inherit: true, reload:true}" ng-show="$root.curUser && $root.collects.length">全部></a>
                        </h2>
                        %{-- 未登录 --}%
                        <div ng-if="!$root.curUser" class="collect-login">
                            <a ng-click="$root.login()">登录</a>
                            |
                            <a ng-click="$root.register()">注册</a>
                        </div>
                        %{-- 已登录 --}%
                        <div ng-if="$root.curUser">
                            <h3 class="show-tit" ng-show="!$root.onLineCollects.length">你关注的房间都没上线,哈哈...</h3>
                            <h3 class="show-tit" ng-show="!$root.collects.length">额,原来你一个房间都没关注 -.-</h3>
                            <a ng-repeat="coll in $root.onLineCollects"
                            %{--<a ng-repeat="coll in $root.collects"--}%
                               repeat-finish
                               ng-init="room = coll.room"
                               ng-click="openRoom(coll.room)"
                               class="room2 trans2"
                               ng-href="{{$state.href('room.insetDetail', {roomId: room.id}, {inherit: true})}}">
                                <img ng-src="{{room.img}}"/>
                                <span class="ellipsis top pla-name">{{room.platform.name}}</span>
                                <span class="ellipsis top anchor">
                                    <i title="关注" roomId="{{room.id}}"
                                       ng-click="$root.changeCollect($event, room);$event.stopPropagation();"
                                       ng-class="{'fa-heart': $root.collectMap[room.id], 'fa-heart-o': !$root.collectMap[room.id]}"
                                       class="fa heart"></i>{{room.anchor}}
                                </span>
                                <span class="ellipsis bottom room-name">{{room.name}}</span>
                                <span class="ellipsis bottom num">{{room.adNum | wanNum}}</span>
                                %{--<i class="fa fa-play-circle play" ng-class="{insert: room.quoteUrl}"></i>--}%
                            </a>
                        </div>
                    </div>
                    <div style="height: 20px;"></div>
                </div>
            </div>
            <div class="right">
                <div class="head trans2">
                    <div class="top-menu">
                        <a ui-sref="room({page:1, tag: '', platformName: '', kw: ''})"
                           ui-sref-opts="{reload:true}" href="${createLink(controller: 'room', action: 'list', params: [offset: 0, max: 120])}">首页</a>
                        <a ui-sref="room.type"
                           ui-sref-opts="{inherit: true, reload:true}">分类</a>
                        <a ui-sref="room.recommend({rPage:1})"
                           ui-sref-opts="{inherit: true, reload:true}">推荐</a>
                        <a ui-sref="room.collect"
                           ui-sref-opts="{inherit: true, reload:true}"
                           ng-show="$root.curUser">我的关注</a>
                    </div>
                    %{--<div style="float: right; height: 45px; overflow: hidden; text-align: right">--}%
                    <div class="right-o">

                        <div class="cur-user" ng-class="{'login-user': curUser}">

                            <div class="user-o">
                                <i style="cursor: default"><span class="fa fa-user"></span>&nbsp;{{curUser.name}}<span ng-if="curUser.isVip" class="vip" title="传说中尊贵的SVIP" ng-style="{color: curUser.color}">贵</span></i>
                                <i class="fa fa-key" ng-click="windows.add({url: 'update-pwd-tem'})" title="修改密码"></i>
                                <i class="fa fa-power-off" ng-click="$root.logout()" title="退出"></i>
                            </div>
                            <div>
                                <a class="login-btn" ng-click="$root.login()">登录</a>
                                <a class="register-btn" ng-click="$root.register()">注册</a>
                            </div>
                            <div class="clear"></div>
                        </div>
                    </div>
                </div>
                <div ui-view class="m-view">

                </div>
            </div>
            <div class="clear"></div>
        </div>


        %{-- 弹出窗口管理 --}%
        <div m-window="windows">

        </div>
    </div>
</body>
<script type="text/javascript">
    // google跟踪代码
    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

    ga('create', 'UA-88253768-1', 'auto');
    ga('send', 'pageview');

    // baidu跟踪代码
    var _hmt = _hmt || [];
    (function() {
        var hm = document.createElement("script");
        hm.src = "https://hm.baidu.com/hm.js?ef1feae8cc0e92928cabf9ef9c690893";
        var s = document.getElementsByTagName("script")[0];
        s.parentNode.insertBefore(hm, s);
    })();
</script>
</html>