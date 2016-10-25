var $ = jQuery.noConflict();

jQuery().ready(function(){
	$("#but_sub_property").click(function(event){
		sendProperty();
	});

    $("#but_select_file").click(function(event){
    	window.location.href="/gengzc/propertyFilter/upload.jsp" ;
    });
	
});

/**
 * 发送propertyFilter语句
 */
function sendProperty(){
	var propertyValue = document.getElementById('text_property').value;
	$.ajax({
		url : "http://localhost:8080/gengzc/gzc/property.do",
		data : {'propertySQL' : propertyValue},
		type : 'post',
		success : function(data){
			property_callback(data)
			},
		error : function(data){
			alert(data.responseText);
			}
			});
}

/**
 * “发送propertyFilter语句” 的回调函数
 * @param data
 */
function property_callback(data) {   
	document.getElementById('textarea_property').value = data.resData
}


