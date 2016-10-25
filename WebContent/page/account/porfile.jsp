<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/page/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户信息</title>
<style type="text/css">
@CHARSET "UTF-8";

/*全局控制*/
* {
	margin: 0;
	padding: 0;
}

body {
	font-size: 12px;
	font-family: "宋体";
	text-align: center;
}

a {
	color: #e3e3e3;
	text-decoration: none;
	padding: 2px;
}

a:hover {
	color: #e3e3e3;
	background-color: #606870;
}

ul {
	list-style: none;
	margin: 10px auto;
}

li {
	margin: 3px 1px;
	line-height: 24px;
	height: 24px;
}

li:hover {
	background-color: #eee;
}

button {
	line-height: 24px;
	height: 26px;
}

fieldset {
	height: 120px;
	width: 250px;
	padding: 5px;
}

label {
	width: 80px;
	float: left;
	text-align: right;
}

input {
	width: 150px;
}
</style>
</head>
<body>
<fieldset><legend>用户信息</legend>
<ul>
	<li><label>用户名:</label><c:out value="${account.username}" /></li>
</ul>
</fieldset>
</body>
</html>