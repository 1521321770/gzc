/**
 * REST调用帮助函数
 */
/**
 * REST调用封装函数
 */
function doRestCall(options){
    var defaults = {
    	//////////////////必需////////////////
        restUrl:"", // REST API地址(包含URL参数--通过程序动态构造)
        remoteIp:"", // 调用远程rest接口IP地址
        remotePort:"", // 调用远程rest接口端口
        method:"GET", // REST API方法类型 -- GET、POST、PUT、DELETE
        timeoutControl:true, // 是否进行超时控制 -- 默认进行控制
        polling:false, // 是否为轮询接口 -- 默认不是轮询接口
        /////////////////可选/////////////////
        data:{
        	param:{},
        	body:{}
        }, // REST请求参数(非URL参数部分)
        // data参数说明：如果为@RequestParam:直接使用key:value的形式将各个参数填写到param中
        //             如果为@RequestBody:使用javascript构造所需对象，然后把构造的对象赋值给body
        confirmMsg:"", // 执行操作前的确认消息
        confirmCallback:function(response){}, // 点击确认操作提示后处理 -- 如果确认提示框选择“确定”，则执行操作
        successMsg:"", // 操作执行成功的提示消息
        showErrMsg:true, // 是否显示错误消息
        callback:function(response){}, // 操作完成后的回调方法 -- 如有操作成功提示消息，则在操作提示消息框关闭后执行
        success:function(response){} // 操作执行成功后的回调
    };
    options = $.extend(defaults, options);
    if(options.confirmMsg){
    	showConfirmMsg(options.confirmMsg, function(result){
    	    options.confirmCallback();
    		doRealRestCall(options);
    	},
    	function(result){
    		if(options.cancelCallback && typeof options.cancelCallback == 'function'){
    			options.cancelCallback();
			}
    	}
    	);
    }else{
    	doRealRestCall(options);
    }
}

// 获取sessionID
var sessionID = $.cookie("JSESSIONID");

/**
 * REST调用实际执行函数
 */
function doRealRestCall(options){
	for(var key in options.data.param){
		options.data.param[key] =  encodeURIComponent(options.data.param[key]);
	}
	$.ajax({
		type:"post",
        dataType:"json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        async:true,
        data:{
            restUrl:options.restUrl,
            remoteIp:options.remoteIp,
            remotePort:options.remotePort,
            timeoutControl:options.timeoutControl,
            polling:options.polling,
            method:options.method,
            data:$.toJSON( options.data ),
            sessionID:sessionID
        },
        url:"/icm/rest.do?timestamp=" + (new Date()).getTime(),
        success:function(response){
        	if(response){
        		var flag = response["flag"];
            	var errCode = response["errCode"];
            	if(true == flag || "true" == flag){
            		if(options.successMsg){
            			showInfoMsg(options.successMsg, function(){
            				options.callback(response);
            				if(options.success){
            					try {
            						options.success(response);
            					} catch(ex) {
            						
            					}
                    		}
            			});
            		}else{
            			options.callback(response);
            			if(options.success){
            				try {
        						options.success(response);
        					} catch(ex) {
        						
        					}
                		}
            		}
            	}else if(errCode){
            		if(options.showErrMsg){
            			showErrorMsg(errCode, function(){
                			try {
        						options.callback(response);
        					} catch(ex) {
        						
        					}
                		});
            		} else {
            			try {
    						options.callback(response);
    					} catch(ex) {
    						
    					}
            		}
            	}else{
//            		if(options.showErrMsg){
//            			showErrorMsg("Flag is false, but no error code returned:" + options.restUrl);
//            		}
            		try {
						options.callback(response);
					} catch(ex) {
						
					}
            	}
        	}else{
        		try {
					options.callback(response);
				} catch(ex) {
					
				}
//        		showErrorMsg("Nothing responsed:" + options.restUrl, function(){
//        			options.callback(response);
//        		});
        	}
        }
	});
}

/**
 * REST批量调用封装函数 -- 典型应用场景--表格行数据的批量操作
 */
function doBatchRestCall(options){
	var defaults = {
        //////////////////必需////////////////
	    restUrl:"", // REST API地址(包含URL参数,使用{key}的形式占位 -- 如：iworkflow/v1/flows/{flowId}/enableFlow)
	    remoteIp:"", // 调用远程rest接口IP地址
        remotePort:"", // 调用远程rest接口端口
	    method:"GET", // REST API方法类型 -- GET、POST、PUT、DELETE
	    params:[], // URL参数数据列表 -- 如表格行数据组成的数组
	    paramKeys:[], // URL参数占位符名称列表 -- 如：["flowId"]
	    bodys:[], // body数据列表
	    
	    /////////////////可选/////////////////
	    data:{
        	param:{},
        	body:{}
        }, // REST请求参数(非URL参数部分)
        // data参数说明：如果为@RequestParam:直接使用key:value的形式将各个参数填写到param中
        //             如果为@RequestBody:使用javascript构造所需对象，然后把构造的对象赋值给body
        confirmMsg:"", // 执行操作前的确认消息
        successMsg:"", // 操作执行成功的提示消息
        showErrMsg:true, // 是否显示错误消息
        isStrongConfig:false,
        callback:function(response){} // 操作执行成功后的回调方法 -- 如有操作成功提示消息，则在操作提示消息框关闭后执行
	};
	options = $.extend(defaults, options);
	// 拷贝params, bodys参数
	options.params = options.params.slice(0);
	options.bodys = options.bodys.slice(0);

    if(options.confirmMsg){
    	if(serviceAddr['openStrongConfirm'] == "1"){ //执行强确认配置操作
    		if(options.isStrongConfig){
        		showStrongConfirmMsg(options.confirmMsg, function(result){
            		doRealBatchRestCall(options);
            	});
        	}else{
        		showConfirmMsg(options.confirmMsg, function(result){
            		doRealBatchRestCall(options);
            	});
        	}
    	}else{
    		showConfirmMsg(options.confirmMsg, function(result){
        		doRealBatchRestCall(options);
        	});
    	}
    }else{
    	doRealBatchRestCall(options);
    }
}

/**
 * REST批量调用实际执行函数
 */
function doRealBatchRestCall(options){
	var params = options.params;
	var paramKeys = options.paramKeys;
	var bodys = options.bodys;
	if((params && params.length > 0) || (bodys && bodys.length > 0)){
		var restUrl = options.restUrl;
		if(params && params.length > 0){
			// 构造单个restUrl
			var param = params.shift();
			for(var index = 0; index < paramKeys.length; index++){
				var paramKey = paramKeys[index];
				restUrl = restUrl.replace("{" + paramKey + "}", param[paramKey] || "");
			}
		}
		if(bodys && bodys.length > 0){
			// 传递每个body
			var body = bodys.shift();
			options.data.body = body;
		}
		for(var key in options.data.param){
			options.data.param[key] = encodeURIComponent(options.data.param[key]);
		}
		$.ajax({
			type:"post",
	        dataType:"json",
	        contentType: "application/x-www-form-urlencoded; charset=utf-8",
	        async:true,
	        data:{
	            restUrl:restUrl, 
	            method:options.method,
	            remoteIp:options.remoteIp,
	            remotePort:options.remotePort,
	            data:$.toJSON(options.data),
	            sessionID:sessionID
	        },
	        url:"/icm/rest.do?timestamp=" + (new Date()).getTime(),
	        success:function(response){
	        	if(response){
	        		var flag = response["flag"];
		        	var errCode = response["errCode"];
		        	if(true == flag || "true" == flag){
		        		doRealBatchRestCall(options);
		        	}else if(errCode){
		        		if(options.showErrMsg){
		        			showErrorMsg(errCode);
		        		}
		        	}else{
//		        		if(options.showErrMsg){
//		        			showErrorMsg("Flag is false, but no error code returned:" + restUrl);
//		        		}
		        	}
	        	}else{
	        		// showErrorMsg("Noting responsed:" + restUrl);
	        	}
	        }
		});
	}else{
		if(options.successMsg){
			showInfoMsg(options.successMsg, function(){
				try {
					options.callback();
				} catch(ex) {
					
				}
			});
		} else {
			try {
				options.callback();
			} catch(ex) {
				
			}
		}
	}
}