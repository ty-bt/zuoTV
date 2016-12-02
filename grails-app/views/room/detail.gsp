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
    <title>${room.name}-Zuo TV</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="keywords" content="${room.name},${room.anchor},直播导航,聚合直播,Zuo,Zuo TV"/>
    <script type="text/javascript">
        location.href = "${g.createLink(uri:'/')}#/1////inset-detail/${room.id}";
    </script>
</head>

<body>
<div>
    <h2>${room.name}</h2>
    <p>
        <img src="${room.img}"/>
    </p>
    <p>平台:${room.platform.name}</p>
    <p>主播:${room.anchor}</p>
    <p>观众:${room.adNum}</p>
    <p><a href="${g.createLink(uri:'/')}#/1////inset-detail/${room.id}">观看</a></p>
</div>
</body>
</html>