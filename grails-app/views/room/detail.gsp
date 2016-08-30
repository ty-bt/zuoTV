<%--
  Created by IntelliJ IDEA.
  User: 勇
  Date: 2016/8/29 0029
  Time: 15:31
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>TV</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link href="${resource(file: '/css/font-awesome/css/font-awesome.min.css')}" rel="stylesheet" />
    <link href="${resource(file: '/css/normalize.css')}" rel="stylesheet" />
    <link href="${resource(file: '/css/base.css')}" rel="stylesheet" />
    <script type="text/javascript">window.ctx = "${createLink(uri:'/')}";</script>
    %{--<script type="text/javascript" src="${resource(file: '/js/jquery-3.1.0.min.js')}"></script>--}%
    <script type="text/javascript" src="${resource(file: '/js/angular/angular.min.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/angular/angular-ui-router.min.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/self/tools.js')}"></script>
    <script type="text/javascript" src="${resource(file: '/js/self/main.js')}"></script>
</head>

<body>
    <embed width="640" height="360" allownetworking="all" allowscriptaccess="always" src="${room.quoteUrl}" quality="high" bgcolor="#000" wmode="window" allowfullscreen="true" allowFullScreenInteractive="true" type="application/x-shockwave-flash">
    <button class="add">增加</button>
    <button class="jian">减少</button>
    <button class="jin">静音</button>
 音量:<span></span>
</body>

<script type="text/javascript">
//    var embed1 = $("embed").get(0);
//    $(".add").click(function(){
//        change(+1)
//    });
//
//    $(".add").click(function(){
//        change(-1)
//    });
//
//    $(".jin").click(function(){
//        embed1.mute  = !embed1.mute;
//    });
//
//    function change(dz){
//        console.log(embed1)
//        embed1.volume += dz;
//        $("span").text(embed1.volume)
//    }
//    change(0)
</script>
</html>