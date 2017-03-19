<%--
  Created by IntelliJ IDEA.
  User: 勇
  Date: 2017/3/19 0019
  Time: 17:30
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>不支持IE10以下浏览器 - zuoTV</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <style type="text/css">
        body,html{padding: 0; margin: 0}
        body{background-size: 100% 100%; font-family: '微软雅黑'}
        a{color: #3498db}
        .main{
            width: 600px;
            position: absolute;
            padding: 0 10px 10px 10px;
            top: 50%;
            margin-top: -176px;
            left: 50%;
            margin-left: -300px;
            text-align: center;
            color: #fff;
            background: #000;
            filter:alpha(opacity=80);
            opacity: 0.8;
        }
    </style>
    <link href="http://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="http://cdn.bootcss.com/jquery/1.12.0/jquery.min.js"></script>
    <script src="http://cdn.bootcss.com/d3/4.5.0/d3.min.js"></script>

    <script src="http://cdn.bootcss.com/trianglify/1.0.1/trianglify.min.js"></script>
</head>

<body>
    <div class="main">
        <div>
            <h1>珍爱生活，换个浏览器</h1>
            <p>本网站不支持IE10以下浏览器</p>
            <p>推荐使用：<a href="https://www.baidu.com/s?wd=chrome">chrome(google浏览器)</a> 或 <a href="http://www.firefox.com.cn/" target="_blank">火狐</a></p>
            <p>如果你一定要使用IE，请<a href="https://support.microsoft.com/zh-cn/help/17621/internet-explorer-downloads" target="_blank">下载最新版本</a></p>
            <p>如果你使用的国内的浏览器，请切换至极速模式或者闪电模式（其实就是chrome）</p>
            <p><a href="mailto:ty_bt@live.cn" style="text-decoration: none">站务与建议:ty_bt@live.cn</a></p>
        </div>
    </div>
</body>
<script type="text/javascript">
    $(window).resize(function(){
        $("body").css({
            "background-image": "url(" + Trianglify({
                width: $(window).width(),
                height: $(window).height(),
                cell_size: 45,
                x_colors: "random"}).png() + ")",
            "height": $(window).height()
        });
    }).resize();


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