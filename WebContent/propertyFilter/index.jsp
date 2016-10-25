<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>发票倒卖之家</title>
<%
	String id = (String)request.getParameter("id");
	String username = (String)request.getParameter("username");
%>
<script type="text/javascript" src="../js/jquery-2.1.1.min.js"></script>

<script type="text/javascript" src="../propertyFilter/index.js"></script>

</head>
<body>

	<div>
		<input type="text" id="id" name="id" value="<%=id %>" readonly />
		<input type="text" id="username" name="username" value="<%=username %>" disabled />
	</div>
	<div>
	<form>
		<ul>
			<li><label>查询语句：</label>
				<input type="text" id="text_property"  name="filter" value=""/>
			</li>
				
			<li><input type="button" id="but_sub_property" value="提交" >
			</li>
		</ul>
	</form>
	</div>
	
	<div>
		<textarea name="textarea_property" id="textarea_property"
		 rows="5" cols="20" readonly></textarea>	
	</div>
	
	<div>
	<!-- 这里我们只使用最基本的html结构：一个选择文件的按钮，一个开始上传文件的按钮(甚至该按钮也可以不要) -->
    <p>
        <button id="but_select_file">上传</button>
    </p>	
	</div>	
	
</body>
</html>