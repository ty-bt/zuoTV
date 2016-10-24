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
    <div class="main">
        <div class="head">
            TV
            <div style="float: right">
                <span ng-click="windows.add({url: '${resource(file: 'html/user/login.html')}'})">登录</span>
                <span ng-click="windows.add({url: '${resource(file: 'html/user/register.html')}'})">注册</span>
            </div>
            <div style="float: right">
                <span><i class="fa fa-user"></i>{{curUser.name}}</span>
            </div>
        </div>
        <div ui-view>
        </div>
        %{--<div class="show-window" ng-include="'${resource(file: 'html/user/login.html')}'">--}%
        %{--</div>--}%
        <div m-window="windows">

        </div>
    </div>
</body>
</html>