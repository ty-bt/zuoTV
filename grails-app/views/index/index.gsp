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
    <title>meituo TV</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link href="${resource(file: '/css/normalize.css')}" rel="stylesheet" />
    <link href="${resource(file: '/css/base.css')}" rel="stylesheet" />
    <script type="text/javascript">window.ctx = "${createLink(uri:'/')}";</script>
    <script type="text/javascript" src="${resource(file: '/js/jquery-3.1.0.min.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/angular/angular.min.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/self/tools.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/self/main.js')}"></script>
</head>

<body ng-controller="main.index">
    <div class="main">
        <div class="head">
            meituo TV
        </div>
        <div class="body">
            <a ng-repeat="room in rooms" repeat-finish class="room trans2" ng-href="{{room.url}}" target="_blank" ng-style="roomSize">
                <table cellspacing="0" cellpadding="0">
                    <tr>
                        <td style="width:70%;"></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td colspan="2" class="photo" ng-style="{height: roomSize.height - 50}"><img ng-src="{{room.img}}"/></td>
                    </tr>
                    <tr class="top">
                        <td class="ellipsis title">{{room.name}}</td>
                        <td class="t-r ellipsis tag">{{room.tag}}</td>
                    </tr>
                    <tr class="bottom">
                        <td class="ellipsis anchor">{{room.anchor}}</td>
                        <td class="t-r ellipsis num">{{room.adNum}}</td>
                    </tr>
                </table>
            </a>
        </div>
    </div>
</body>
</html>