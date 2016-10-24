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
    <title>TV</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link href="${resource(file: '/css/font-awesome/css/font-awesome.min.css')}" rel="stylesheet" />
    <link href="${resource(file: '/css/normalize.css')}" rel="stylesheet" />
    <link href="${resource(file: '/css/base.css')}" rel="stylesheet" />
    <script type="text/javascript">window.ctx = "${createLink(uri:'/')}";</script>
    <script type="text/javascript" src="${resource(file: '/js/jquery-3.1.0.min.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/angular/angular.min.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/angular/angular-ui-router.min.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/self/tools.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/self/main.js')}"></script>
</head>

<body>
    <div class="main" ng-cloak>
        <div class="head trans2">
            TV
            <div style="float: right; height: 45px; overflow: hidden; text-align: right">
                <div class="cur-user" ng-class="{'login-user': curUser}">
                    <div>
                        <a><i class="fa fa-user"></i>{{curUser.name}}</a>
                    </div>
                    <div>
                        <a ng-click="windows.add({url: '${resource(file: 'html/user/login.html')}'})">登录</a>
                        <a ng-click="windows.add({url: '${resource(file: 'html/user/register.html')}'})">注册</a>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>
        <div class="table content">
            <div class="left">
                <div class="m-search">
                    <h1>TV</h1>
                    <div class="condition">
                        <input type="text" ng-keyup="search.submit($event)" ng-model="search.kw" />
                        <i class="fa fa-search" ng-click="$state.go('room', {page:0, kw: search.kw}, {reload: true, inherit: true})"></i>
                    </div>
                    <div class="condition">
                        <h2>平台</h2>
                        <div class="checkeds">
                            <a href="#" ng-class="{selected: !$stateParams.platformName}"
                               ui-sref="room({page:0, platformName: ''})"
                               ui-sref-opts="{reload: true, inherit: true}">全部</a>
                            <a ng-repeat="pla in topData.platforms"
                               ui-sref="room({page:0, platformName: pla.name})"
                               ui-sref-opts="{reload: true, inherit: true}"
                               ng-class="{selected: pla.name == $stateParams.platformName}">{{pla.name}}</a>

                        </div>
                    </div>
                    <div class="condition">
                        <h2>分类</h2>
                        <div class="checkeds">
                            <a ui-sref="room({page:0, tag: ''})"
                               ui-sref-opts="{reload: true, inherit: true}"
                               ng-class="{selected: !$stateParams.tag}">全部</a>
                            <a ng-repeat="t in topData.types.slice(0,20)"
                               ui-sref="room({page:0, tag: t.name})"
                               ui-sref-opts="{reload: true, inherit: true}"
                               ng-class="{selected: t.name == $stateParams.tag}">{{t.name}}</a>
                            <a href="#" ng-show="topData.types.length > 20">更多...</a>
                        </div>
                    </div>
                    <div class="condition">
                        <h2>我的关注</h2>
                        <div class="checkeds">

                        </div>
                    </div>
                </div>
            </div>
            <div class="right" ui-view>

            </div>
        </div>


        %{-- 弹出窗口管理 --}%
        <div m-window="windows">

        </div>
    </div>
</body>
</html>