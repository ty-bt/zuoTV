<%--
  Created by IntelliJ IDEA.
  User: 勇
  Date: 2016/8/23 0023
  Time: 20:00
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE HTML>
<html ng-app="main">
<head>
    <title>Zuo TV</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    %{--<link href="${resource(file: '/css/font-awesome/css/font-awesome.min.css')}" rel="stylesheet" />--}%
    <link href="http://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    %{--<link href="${resource(file: '/css/normalize.css')}" rel="stylesheet" />--}%
    <link href="http://cdn.bootcss.com/normalize/5.0.0/normalize.css" rel="stylesheet">
    <link href="${resource(file: '/css/base.css')}" rel="stylesheet" />
    <script type="text/javascript">window.ctx = "${createLink(uri:'/')}";</script>
    %{--<script type="text/javascript" src="${resource(file: '/js/jquery-3.1.0.min.js')}"></script>--}%
    <script type="text/javascript" src="http://cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
    <script type="text/javascript" src="http://cdn.bootcss.com/angular.js/1.5.6/angular.min.js"></script>
    %{--<script type="text/javascript" src="${resource(file: '/js/angular/angular.min.js')}"></script>--}%
    %{--<script type="text/javascript" src="${resource(file: '/js/angular/angular-ui-router.min.js')}"></script>--}%
    <script type="text/javascript" src="http://cdn.bootcss.com/angular-ui-router/1.0.0-beta.3/angular-ui-router.min.js"></script>


    <script type="text/javascript" src="${resource(file: '/js/self/tools.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/self/main.js')}"></script>
</head>

<body style="overflow: hidden">
    <div class="main" ng-cloak>

        <div class="content table">
            <div class="left trans2">
                <h1>Zou TV</h1>
                <div class="m-search">
                    <div class="condition search-input">
                        <input type="text" placeholder="输入房间名或主播名搜索" ng-keyup="search.submit($event)" ng-model="search.kw" />
                        <i class="fa fa-search" ng-click="$state.go('room', {page:1, kw: search.kw}, {inherit: true})"></i>
                    </div>
                    <div class="condition">
                        <h2>平台</h2>
                        <div class="checkeds"> 
                            <a href="#" ng-class="{selected: !$stateParams.platformName}"
                               ui-sref="room({page:1, platformName: ''})"
                               ui-sref-opts="{inherit: true}">全部</a>
                            <a ng-repeat="pla in topData.platforms"
                               ui-sref="room({page:1, platformName: pla.name})"
                               ui-sref-opts="{inherit: true}"
                               ng-class="{selected: pla.name == $stateParams.platformName}">{{pla.name}}</a>

                        </div>
                    </div>
                    <div class="condition">
                        <h2>分类<a ui-sref="room.type"
                                 ui-sref-opts="{inherit: true}"
                                 ng-show="topData.types.length > 20">全部></a>
                        </h2>
                        <div class="checkeds">
                            <a ui-sref="room({page:1, tag: ''})"
                               ui-sref-opts="{inherit: true}"
                               ng-class="{selected: !$stateParams.tag}">全部</a>
                            <a ng-repeat="t in topData.types.slice(0,20)"
                               ui-sref="room({page:1, tag: t.name})"
                               ui-sref-opts="{inherit: true}"
                               ng-class="{selected: t.name == $stateParams.tag}">{{t.name}}</a>

                        </div>
                    </div>
                    <div class="condition">
                        <h2>我的关注
                            <a ui-sref="room.collect"
                               ui-sref-opts="{inherit: true}" ng-show="$root.curUser && $root.collects.length">全部></a>
                        </h2>
                        %{-- 未登录 --}%
                        <div ng-if="!$root.curUser" class="collect-login">
                            <a ng-click="windows.add({url: '${resource(file: 'html/user/login.html')}'})">登录</a>
                            |
                            <a ng-click="windows.add({url: '${resource(file: 'html/user/register.html')}'})">注册</a>
                        </div>
                        %{-- 已登录 --}%
                        <div ng-if="$root.curUser">
                            <h3 class="show-tit" ng-show="!$root.onLineCollects.length">你关注的房间都没上线,哈哈...</h3>
                            <h3 class="show-tit" ng-show="!$root.collects.length">额,原来你一个房间都没关注 -.-</h3>
                            <a ng-repeat="coll in $root.onLineCollects"
                               repeat-finish
                               ng-init="room = coll.room"
                               class="room2 trans2"
                               target="{{room.quoteUrl ? '_self' : '_blank'}}"
                               ng-href="{{room.quoteUrl ? $state.href('room.insetDetail', {roomId: room.id}, {inherit: true}) : room.url;}}">
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
                </div>
            </div>
            <div class="right">
                <div class="head trans2">
                    <div class="top-menu">
                        <a ui-sref="room({page:1, tag: '', platformName: '', kw: ''})"
                           ui-sref-opts="{roload:true}">首页</a>
                        <a ui-sref="room.type"
                           ui-sref-opts="{inherit: true}">分类</a>
                        <a ui-sref="room.collect"
                           ui-sref-opts="{inherit: true}"
                           ng-show="$root.curUser">我的关注</a>
                    </div>
                    %{--<div style="float: right; height: 45px; overflow: hidden; text-align: right">--}%
                    <div class="right-o">

                        <div class="cur-user" ng-class="{'login-user': curUser}">

                            <div class="user-o">
                                <i class="fa fa-user" style="cursor: default">&nbsp;{{curUser.name}}</i>
                                <i class="fa fa-key" ng-click="windows.add({url: '${resource(file: 'html/user/update-pwd.html')}'})" title="修改密码"></i>
                                <i class="fa fa-power-off" ng-click="$root.logout()" title="退出"></i>
                            </div>
                            <div>
                                <a ng-click="windows.add({url: '${resource(file: 'html/user/login.html')}'})">登录</a>
                                <a ng-click="windows.add({url: '${resource(file: 'html/user/register.html')}'})">注册</a>
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
</html>