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
    <title>在线直播-Zuo TV</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="Zuo TV,一站聚合六个直播平台50万主播,不用一个一个平台去找喜爱的主播.." />
    <meta name="keywords" content="zuotv,聚合直播,作TV,nozuonodie,直播人数统计,直播平台统计,Zuo,Zuo TV,直播导航,直播推荐,妹纸直播,星秀,一起看电影"/>
    <script type="text/javascript">
        location.href = "${g.createLink(uri:'/')}#/${params.int("offset", 0) / 120 + 1}///";
    </script>
</head>

<body>
    <g:each in="${rooms}" var="r">
        <div>
            <h2>${r.name}</h2>
            <p>
                <img src="${r.img}"/>
            </p>
            <p>平台:${r.platform.name}</p>
            <p>主播:${r.anchor}</p>
            <p>观众:${r.adNum}</p>
            <p><a href="${g.createLink(uri:'/')}#/1////inset-detail/${r.id}">观看</a></p>
            <p><a href="${g.createLink(action: 'detail', id: r.id)}">详情</a></p>
        </div>
    </g:each>
    <g:paginate total="${rooms.totalCount}" max="120"></g:paginate>
</body>

</html>