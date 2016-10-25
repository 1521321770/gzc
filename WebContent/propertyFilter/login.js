var $ = jQuery.noConflict();

jQuery().ready(function(){
	$("#login-button").click(function(event){
		event.preventDefault();
		$("form").fadeOut(500);
		$(".wrapper").addClass('form-success');
		sendUrl_login();
	});
});

/**
 * 发送用户的输入信息
 */
function sendUrl_login(){
	var username = document.getElementById('login_user').value;
	var password = document.getElementById('login_passwd').value;
	$.ajax({
		url : "http://localhost:8080/gengzc/gzc/login.do",
		data : {'username' : username,
				'password' : password},
		type : 'post',
		success : function(data){
			login_callback(data)
			},
		error : function(data){
			alert("登陆失败");
			}
		});
}

/**
 * “发送用户的输入信息” 的回调函数
 * @param data
 */
function login_callback(data) {   
	var flag = data.flag
	var id = data.resData.id
	var username = data.resData.username
	if(flag){ 
		window.location.href="/gengzc/propertyFilter/index.jsp?" +
				"id=" + id + "&username=" + username;
	}else{
		alert("用户名或密码错误")
	}
} 























//$.post("http://localhost:8080/gengzc/gzc/property.do",
//{
//	propertySQL:propertyValue
//},
//function(data,status){
//	alert(data)
//	document.getElementById('textarea_property').value = result	
//});



//doRestCall({
//restUrl : "/geng/property.do",
//method : "post",
//data : {
//	param:{
//		propertySQL:propertyValue
//	}
//},
//callback : function(response) {
//	var resData = response["propertySQL"];		
//	document.getElementById('textarea_property').value = resData			
//},	
//success : function(response) {
//
//}
//});