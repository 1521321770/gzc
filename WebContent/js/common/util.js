/**
 * util.js
 */

function HTMLEncode(A) {
	if (!A) {
		return '';
	}
	A = A.replace(/&/g, '&');
	A = A.replace(/</g, '<');
	A = A.replace(/>/g, '>');
	return A;   
}

/**
 * 国际化
 */
function translate() {
	var elems = $("[langkey]");// 实际上就是根据标签名取到这个标签的数据集合
	var elem, langKey, content;
	for (var i = 0; i < elems.length; i++) {
		elem = elems[i];
		langKey = elem.getAttribute("langkey");
		if(langKey){
			content = lang[langKey];
			if(content){
				content = HTMLEncode(content);
				if(elem.getAttribute("placeholder")){
//					alert(elem.getAttribute("placeholder")+"content:"+content);
					elem.setAttribute("placeholder",content);
				}else{
					try{
					    elem.innerHTML = content;
					}catch(e){
					    
					}
				}
				
			}
		}
	}
	setCollapsible();
}

/**
 * 设置小界面可折叠
 */
function setCollapsible(){
//	$(".tab_title").toggle(function(){
//		$(this).siblings().fadeOut(300);
//		$(this).find(":first").addClass("up");
//	}, function(){
//		$(this).siblings().fadeIn(300);
//		$(this).find(":first").removeClass("up");
//	});
//	
//	$(".chart_tit h3:first-child").toggle(function(){
//		$(this).siblings().fadeOut(300); // 隐藏title后面的内容
//		$(this).parent().siblings().fadeOut(300);
//	}, function(){
//		$(this).siblings().fadeIn(300);
//		$(this).parent().siblings().fadeIn(300);
//	});
}

/**
 * JavaScript日志打印函数
 */
function log(msg){
	var console = document.getElementById("LogConsole");
	if(console){
		console.innerHTML += (msg + "<br>");
	}
}

var msgQueue = new Array();// 消息队列
var showingMsg = false;// 是否有消息正在显示

/**
 * 消息入队
 * 
 * @param type 消息类型
 * @param msg 消息内容
 * @param callback 消息回调函数
 */
function queueMsg(type, msg, callback){
	log("queue msg:" + msg);
	msgQueue.push({type:type, msg:msg, callback:callback});
}

/**
 * 从消息队列取出消息并显示
 */
function dequeueMsg(){
	var obj = msgQueue.shift();
	if(obj){
		var type = obj.type;
		var msg = obj.msg;
		var callback = obj.callback;
		log("show msg:" + msg);
		switch(type){
			case 'confirm':
				showConfirmMsg(msg, callback);
				break;
			case 'info':
				showInfoMsg(msg, callback);
				break;
			case 'warn':
				showWarnMsg(msg, callback);
				break;
			case 'error':
				showErrorMsg(msg, callback);
				break;
		}
	}
}

/**
 * 显示信息类消息
 * @param message 消息内容
 * @param callback 点击<确定>按钮后执行的回调函数
 */
function showInfoMsg(message, callback){
	message = getMessage(message);
	if(!showingMsg){
		showingMsg = true;
//		$('#bottomRightMsg').jGrowl(message);
		showNotyMsg("success", message);
		showingMsg = false;
		if(callback && typeof callback == 'function'){
			callback(true);
		}
	}else{
		queueMsg('info', message, callback);
	}
}

/**
 * 显示确认类消息
 * @param message 消息内容
 * @param confirmCallback 点击<确定>按钮后执行的回调函数
 * @param cancelCallback 点击<取消>按钮后执行的回调函数
 */
function showConfirmMsg(message, confirmCallback, cancelCallback){
	message = getMessage(message);
	if(!showingMsg){
		showingMsg = true;
		jConfirm(message, lang["confirmMsg"], function(result){
			if(result){
				if(confirmCallback && typeof confirmCallback == 'function'){
					confirmCallback();
				}
			}else{
				if(cancelCallback && typeof cancelCallback == 'function'){
					cancelCallback();
				}
			}
			showingMsg = false;
			dequeueMsg();
		}, 'confirm');
	}else{
		queueMsg('confirm', message, callback);
	}
}

/**
 * 显示强确认类的消息
 * @param message 消息内容
 * @param confirmCallback 点击<确定>按钮后执行的回调函数
 * @param cancelCallback 点击<取消>按钮后执行的回调函数
 * 
 */
function showStrongConfirmMsg(message, confirmCallback, cancelCallback){
	if(serviceAddr['openStrongConfirm'] == "0"){
		showConfirmMsg(message, confirmCallback, cancelCallback);
	}else{
		message = getMessage(message);
		if(!showingMsg){
			showingMsg = true;
			var tip = lang['conOperConfirm'];
			jPrompt("<font color='red'>" + tip + "</font>" + "<br>" + message, 'NO', lang["confirmMsg"], function(result){
				if(result == "YES"){
					if(confirmCallback && typeof confirmCallback == 'function'){
						confirmCallback();
					}
				}else{
					if(cancelCallback && typeof cancelCallback == 'function'){
						cancelCallback();
					}
				}
				showingMsg = false;
				dequeueMsg();
			}, 'confirm');
		}else{
			queueMsg('confirm', message, callback);
		}
	}
}

function loadPovperWin(){
	var result = $("#popup_prompt").val().toLocaleUpperCase();
	$("#popup_prompt").val(result);
}

/**
 * 显示警告类消息
 * @param message 消息内容
 * @param callback 点击<确定>或<取消>按钮后执行的回调函数
 */
function showWarnMsg(message, callback){
	message = getMessage(message);
	if(!showingMsg){
		showingMsg = true;
//		$('#bottomRightMsg').jGrowl(message);
		showNotyMsg("warning", message);
		showingMsg = false;
		if(callback && typeof callback == 'function'){
			callback(true);
		}
	}else{
		queueMsg('warn', message, callback);
	}
}

/**
 * 显示错误类消息
 * @param message 消息内容
 * @param callback 点击<确定>或<取消>按钮后执行的回调函数
 */
function showErrorMsg(message, callback){
	message = getMessage(message);
	if(!showingMsg){
		showingMsg = true;
//		$('#bottomRightMsg').jGrowl(message);
		showNotyMsg("error", message);
		showingMsg = false;
		if(callback && typeof callback == 'function'){
			callback(true);
		}
	}else{
		queueMsg('error', message, callback);
	}
}

/**
 * 显示Noty提示消息.
 * 
 * @param type 消息类型
 * @param message 消息内容
 */
function showNotyMsg(type, message){
	// 生成消息显示容器
	if($("#msgNoty").length <= 0){
		var sidebar = $("#sidebar");
		var lis = $(sidebar).find("li");
		var ulWidth = 0; // 各菜单项累加宽度
		for(var index = 0; index < lis.length; index++){
			var liWidth = $(lis[index]).width() + 26; // 26为菜单项填充宽度(12 + 12 + 2)
//			console.log("with:" + liWidth);
			ulWidth += liWidth;
		}
		// 二级导航菜单宽度
		var sidebarWidth = sidebar.width();
		// 提示消息宽度
		var divWidth = sidebarWidth - ulWidth - 30;
		if(divWidth >= 400){
			divWidth = 400;
		}
//		alert("sidebarWidth:" + sidebarWidth + " ul:" + ulWidth + " div:" + divWidth);
		sidebar.append('<div id="msgNoty" style="z-index:10001; position:fixed; top:60px; right:5px;width: ' + divWidth + 'px;"></div>');
	}
//	var scrollPos = $(document).scrollTop(); // 垂直滚动条位置
//	if(scrollPos > 61){
//		$("#msgNoty").css("top", "0px");
//	}else{
//		var h = 97 - scrollPos;
//		$("#msgNoty").css("top", h + "px");
//	}
//	var welcomeDiv = $("#welcomeDiv");
////	$('#mws-container').append();
//	var aa = $('<div id="msgNoty" style="z-index:10001; position:fixed; top:102px; right:13px;width: 300px;"></div>');
//	$('#mws-container').append(aa);
	// 显示消息
	$("#msgNoty").noty({
		layout: 'topRight',  // 默认布局
		text:message,
		type:type,
		dismissQueue:false,
		theme:"defaultTheme",
		maxVisible:1,
		killer:true,
		timeout:3000,
		callback:{
		    afterClose:function(){
		        $.noty.closeAll();
		    }
		}
	});
}
/**
 * 获取错误信息（如果有错误码定义则返回错误码对应的message，否则显示传入的错误码）
 *
 * @param message 错误码或错误信息
 * @returns
 */
function getMessage(message){
	// TODO token失效处理
	// "20008":"请求中没有token,请退出重新登录.",
	// "20009":"无效的token,请退出重新登录.",
	// "20004":"请求中没有授权令牌",
	if(message == "20008" || message == "20009" || message == "20004"){
		jAlert(msg["20004"],lang["errorMsg"], function(){
			$.ajax({
				dataType:"json",
		        url:"logout.do?timestamp=" + (new Date()).getTime(), 
		        success:function(response){
		        	$.cookie("auth-token", "");
		        	window.location = "/icm/login.jsp";
		        }
			});
		}, 'error');
	}
	// 组织被禁用
	if(message == "10212" || message == "20007"){
		showInfoMsg(lang["orgDisabledRelogin"], function(){
			$.ajax({
				dataType:"json",
		        url:"logout.do?timestamp=" + (new Date()).getTime(), 
		        success:function(response){
		        	$.cookie("auth-token", "");
		        	window.location = "/icm/login.jsp";
		        }
			});
		});
	}
	// 用户被禁用
	if(message == "10713" || message == "20006"){
		showInfoMsg(lang["userDisabledRelogin"], function(){
			$.ajax({
				dataType:"json",
		        url:"logout.do?timestamp=" + (new Date()).getTime(), 
		        success:function(response){
		        	$.cookie("auth-token", "");
		        	window.location = "/icm/login.jsp";
		        }
			});
		});
	}
	if(msg[message]){
		message = msg[message];
	} else {
		if(message){
			// 考虑返回非单纯错误码的情形 如：30219:ERROR_VDC_DELETE_USED_RESOURCE
			splits = message.split(":");
			if(splits && splits.length > 0){
				for(var index = 0; index < splits.length; index++){
					if(msg[splits[index]]){
						message = msg[splits[index]];
						break;
					}
				}
			}
		}
	}
	return message;
}

/**
 * 切换选项卡
 * @param {} elementID
 * @param {} listName
 * @param {} n
 */
function switchTab(elementID,listName,n) {
    var elem = document.getElementById(elementID);
    var elemlist = elem.getElementsByTagName("li");
    for (var i=0; i<elemlist.length; i++) {
        elemlist[i].className = "tabinfo_normal";
        var m = i+1;
        document.getElementById(listName+"_"+m).className = "tabinfo_normal";
    }
        elemlist[n-1].className = "tabinfo_current";
        document.getElementById(listName+"_"+n).className = "tabinfo_current";
}

/**
 * 重写javascript的toFixed方法
 */
Number.prototype.toFixed = function(d){
	var s = this + "";
	if(!d){
		d=0;
	} 
	if(s.indexOf(".") == -1){
		s+=".";
	}
	s += new Array(d+1).join("0"); 
	if(new RegExp("^(-|\\+)?(\\d+(\\.\\d{0,"+(d+1)+"})?)\\d*$").test(s)){
		var s = "0"+RegExp.$2, pm = RegExp.$1, a = RegExp.$3.length, b=true;
		if(a == d+2){ 
			a = s.match(/\d/g); 
			if(parseInt(a[a.length-1])>4){
				for(var i=a.length-2;i>=0;i--){
					a[i]=parseInt(a[i])+1;
					if(a[i]==10){
						a[i]=0; 
						b=i!=1; 
					}else{
						break;
					}
				}
			}
			s = a.join("").replace(new RegExp("(\\d+)(\\d{"+d+"})\\d$"),"$1.$2");
		}
		if(b){
			s=s.substr(1);
		}
		return (pm+s).replace(/\.$/,"");
	}
	return this + ""; 
};

/**
 * 文件大小格式化
 * 
 * @param value 文件大小
 * @param unit 单位
 * @returns {String} 文件大小格式化字符串
 */
function formatFileSize(value, unit){
	var str = "";
	switch(unit){
	  case "Byte":str = fileSize(value);break;
	  case "KB":str = fileSize(value*1024);break;
	  case "MB":str = fileSize(value*1024*1024);break;
	  case "GB":str = fileSize(value*1024*1024*1024);break;
	  case "TB":str = fileSize(value*1024*1024*1024*1024);break;
	}
	return str;
}

/**
 * 文件大小格式化
 * 
 * @param value 文件Byte大小
 * @returns {String} 文件大小格式化字符串
 */
function fileSize(value){
	if(value > 0 && value < 1024){
		return value + "Byte";
	}else if(value >= 1024 && value < 1024*1024){
		return Number(value/1024).toFixed(2) + "KB";
	}else if(value >= 1024*1024 && value < 1024*1024*1024){
		return Number(value/(1024*1024)).toFixed(2) + "MB";
	}else if(value >= 1024*1024*1024 && value < 1024*1024*1024*1024){
		return Number(value/(1024*1024*1024)).toFixed(2) + "GB";
	}else if(value >= 1024*1024*1024*1024 && value < 1024*1024*1024*1024*1024){
		return Number(value/(1024*1024*1024*1024)).toFixed(2) + "TB";
	}else if(value >= 1024*1024*1024*1024*1024 && value < 1024*1024*1024*1024*1024*1024){
		return Number(value/(1024*1024*1024*1024*1024)).toFixed(2) + "PB";
	}else{
		return value + "Byte";
	}	
}

/**
 * 计算文件大小单位
 * 
 * @param value 文件Byte大小
 * @returns {String} 文件大小格式化字符串
 */
function calcFileSizeUnit(value, unit){
	switch(unit){
	  case "Byte":break;
	  case "K":
	  case "KB":value = value*1024;break;
	  case "M":
	  case "MB":value = value*1024*1024;break;
	  case "G":
	  case "GB":value = value*1024*1024*1024;break;
	  case "T":
	  case "TB":value = value*1024*1024*1024*1024;break;
	}
	if(value > 0 && value < 1024){
		return "Byte";
	}else if(value >= 1024 && value < 1024*1024){
		return "KB";
	}else if(value >= 1024*1024 && value < 1024*1024*1024){
		return "MB";
	}else if(value >= 1024*1024*1024 && value < 1024*1024*1024*1024){
		return "GB";
	}else if(value >= 1024*1024*1024*1024 && value < 1024*1024*1024*1024*1024){
		return "TB";
	}else if(value >= 1024*1024*1024*1024*1024 && value < 1024*1024*1024*1024*1024*1024){
		return "PB";
	}else{
		return "Byte";
	}
}

/**
 * 将文件大小格式化到指定单位
 * 
 * @param value 文件Byte大小
 * @returns {String} 文件大小格式化字符串
 */
function formatFileSizeToDestUnit(value, unit, destUnit){
	var fileSize = 0;
	switch(unit){
	case "Byte":break;
	  case "K":
	  case "KB":value = value*1024;break;
	  case "M":
	  case "MB":value = value*1024*1024;break;
	  case "G":
	  case "GB":value = value*1024*1024*1024;break;
	  case "T":
	  case "TB":value = value*1024*1024*1024*1024;break;
	}
	
	switch(destUnit){
	  case "Byte":fileSize = value;break;
	  case "KB":fileSize = Number(value/1024).toFixed(2);break;
	  case "MB":fileSize = Number(value/(1024*1024)).toFixed(2);break;
	  case "GB":fileSize = Number(value/(1024*1024*1024)).toFixed(2);break;
	  case "TB":fileSize = Number(value/(1024*1024*1024*1024)).toFixed(2);break;
	  case "PB":fileSize = Number(value/(1024*1024*1024*1024*1024)).toFixed(2);break;
	  default: fileSize = 0;break;
	}
	return fileSize;
}

/**
 * cpu频率格式化
 * 
 * @param value CPU频率
 * @param unit 单位
 * @returns {String} cpu频率格式化字符串
 */
function formatCpuHz(value, unit){
	var str = "";
	switch(unit){
	  case "Hz":
	  case "HZ":str = cpuHz(value);break;
	  case "KHz":
	  case "KHZ":str = cpuHz(value*1000);break;
	  case "MHz":
	  case "MHZ":str = cpuHz(value*1000*1000);break;
	  case "GHz":
	  case "GHZ":str = cpuHz(value*1000*1000*1000);break;
	  case "THz":
	  case "THZ":str = cpuHz(value*1000*1000*1000*1000);break;
	}
	return str;
}

/**
 * cpu频率格式化
 * 
 * @param value CPU频率HZ
 * @returns {String} cpu频率格式化字符串
 */
function cpuHz(value){
	if(value > 0 && value < 1000){
		return value + "Hz";
	}else if(value >= 1000 && value < 1000*1000){
		return Number(value/1000).toFixed(2) + "KHz";
	}else if(value >= 1000*1000 && value < 1000*1000*1000){
		return Number(value/(1000*1000)).toFixed(2) + "MHz";
	}else if(value >= 1000*1000*1000 && value < 1000*1000*1000*1000){
		return Number(value/(1000*1000*1000)).toFixed(2) + "GHz";
	}else if(value >= 1000*1000*1000*1000 && value < 1000*1000*1000*1000*1000){
		return Number(value/(1000*1000*1000*1000)).toFixed(2) + "THz";
	}else if(value >= 1000*1000*1000*1000*1000 && value < 1000*1000*1000*1000*1000*1000){
		return Number(value/(1000*1000*1000*1000*1000)).toFixed(2) + "PHz";
	}else{
		return value + "Hz";
	}	
}

/**
 * 计算CPU HZ单位
 * 
 * @param value CPU频率
 * @param unit 单位
 * @returns {String}
 */
function calcCpuHzUnit(value, unit){
	switch(unit){
	  case "Hz":
	  case "HZ":value = value;break;
	  case "KHz":
	  case "KHZ":value = value*1000;break;
	  case "MHz":
	  case "MHZ":value = value*1000*1000;break;
	  case "GHz":
	  case "GHZ":value = value*1000*1000*1000;break;
	  case "THz":
	  case "THZ":value = value*1000*1000*1000*1000;break;
	}
	if(value > 0 && value < 1000){
		return "Hz";
	}else if(value >= 1000 && value < 1000*1000){
		return "KHz";
	}else if(value >= 1000*1000 && value < 1000*1000*1000){
		return "MHz";
	}else if(value >= 1000*1000*1000 && value < 1000*1000*1000*1000){
		return "GHz";
	}else if(value >= 1000*1000*1000*1000 && value < 1000*1000*1000*1000*1000){
		return "THz";
	}else if(value >= 1000*1000*1000*1000*1000 && value < 1000*1000*1000*1000*1000*1000){
		return "PHz";
	}else{
		return "Hz";
	}
}

/**
 * 将CPU HZ格式化到指定单位
 * 
 * @param value
 * @param unit
 * @param destUnit
 * @returns
 */
function formatCpuHZToDestUnit(value, unit, destUnit){
	var cpuHz;
	switch(unit){
	  case "Hz":
	  case "HZ":value = value;break;
	  case "KHz":
	  case "KHZ":value = value*1000;break;
	  case "MHz":
	  case "MHZ":value = value*1000*1000;break;
	  case "GHz":
	  case "GHZ":value = value*1000*1000*1000;break;
	  case "THz":
	  case "THZ":value = value*1000*1000*1000*1000;break;
	}
	
	switch(destUnit){
	  case "Hz":
	  case "HZ":cpuHz = value;break;
	  case "KHz":
	  case "KHZ":cpuHz = Number(value/1000).toFixed(2);break;
	  case "MHz":
	  case "MHZ":cpuHz = Number(value/(1000*1000)).toFixed(2);break;
	  case "GHz":
	  case "GHZ":cpuHz = Number(value/(1000*1000*1000)).toFixed(2);break;
	  case "THz":
	  case "THZ":cpuHz = Number(value/(1000*1000*1000*1000)).toFixed(2);break;
	  case "PHz":
	  case "PHZ":cpuHz = Number(value/(1000*1000*1000*1000*1000)).toFixed(2);break;
	  default:cpuHz = 0;
	}
	return cpuHz;
}

/**
 * 获取第一个可用的模块
 *
 * @param menuId 模块菜单容器id
 * @returns
 */
function getFirstModule(menuId){
	var modules = $("#" + menuId).find("li");
	if(modules && modules.length > 0) {
		return $(modules[0]).attr("id");
	}
	return "";
}

/**
 * 加载某模块的最后访问子模块
 * 
 * @param key 存储模块访问信息时定义的key值
 * @param defaultIfNoHistory 不存在访问历史的场合加载的子模块菜单id
 */
function loadLastAccessModule(key, defaultIfNoHistory){
	var last = $.cookie(key);
	if(last && $("#" + last).length > 0){
		$("#" + last).click();
	}else{
		$("#" + defaultIfNoHistory).click();
	}
}

/**
 * 获取某模块的最后访问子模块的菜单id
 * 
 * @param key 存储模块访问信息时定义的key值
 */
function getLastAccessModule(key){
	return $.cookie(key);
}

/**
 * 更新某模块的最后访问子模块的菜单id
 * 
 * @param key 存储模块访问信息的key值
 * @param value 子模块菜单id
 */
function updateLastAccessModule(key, lastAccess){
	$.cookie(key, lastAccess);
	updateNavi();
}

/**
 * 更新导航条显示
 */
function updateNavi(){
	var posDiv = $.find("[class=position]");
	// 更新当前显示模块
	if(posDiv && posDiv != "null"){
		$(posDiv).html("");
		var homeDiv = $('<div class="home"></div>');
		var endDiv = $('<div class="end"></div>');
		var naviUl = $('<ul style="display:"></ul>');
		moduleList = new Array();
		getModuleLevel("indexLastModule");
		if(moduleList && moduleList.length > 0){
			var moduleNum = moduleList.length;
			for(var index = 0; index < moduleNum; index++){
				var module = moduleList[index];
				var levelLi = $('<li class="cloudStack-elem cloudBrowser"><span>' + lang[module] + '</span></li>');
				var levelDiv = $('<div class="end cloudStack-elem cloudBrowser"></div>');
				if(index == moduleNum - 1){
					levelLi.addClass("active");
					levelDiv.addClass("active");
				}
				naviUl.append(levelLi);
				naviUl.append(levelDiv);
			}
		}
		$(posDiv).append(homeDiv);
		$(posDiv).append(endDiv);
		$(posDiv).append(naviUl);
	}
}

var moduleList = null; // 当前模块层次列表
/**
 * 获取当前模块层次
 */
function getModuleLevel(key){
	var moduleKey = getLastAccessModule(key);
	if(moduleKey){
		moduleList.push(moduleKey);
		getModuleLevel(moduleKey);
	}
}
/**
 * 时间渲染
 * @param value
 * @param rowData
 * @returns {String}
 */
function timeRender(value,rowData){
    if("0" == value || "" == value){
        return "-";
    }else{
        return formatTimeStamp(value,"yyyy-MM-dd HH:mm:ss");
    }
}
/**
 * 监控管理时间渲染
 * @param value
 * @param rowData
 * @returns {String}
 */
function monitorTimeRender(value,rowData){
	if("0" == value || "" == value){
		return "-";
	}else{
		return formatTimeStamp(Number(value)*1000,"yyyy-MM-dd HH:mm:ss");
	}
}
/**
 * 将时间戳格式化的方法
 * @param timeStamp时间戳值
 * @param fmt 要生成的格式，如yyyy-MM-dd HH:mm:ss
 * @returns
 */
function formatTimeStamp(timeStamp,fmt) {
	var date = new Date();
	date.setTime(timeStamp);
    var o = {         
    "M+" : date.getMonth()+1, //月份         
    "d+" : date.getDate(), //日         
    "h+" : date.getHours()%12 == 0 ? 12 : date.getHours()%12, //小时         
    "H+" : date.getHours(), //小时         
    "m+" : date.getMinutes(), //分         
    "s+" : date.getSeconds(), //秒         
    "q+" : Math.floor((date.getMonth()+3)/3), //季度         
    "S" : date.getMilliseconds() //毫秒         
    };         
    if(/(y+)/.test(fmt)){         
        fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));         
    }         
    if(/(E+)/.test(fmt)){         
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "\u661f\u671f" : "\u5468") : ""));         
    }         
    for(var k in o){         
        if(new RegExp("("+ k +")").test(fmt)){         
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));         
        }         
    }         
    return fmt;         
} 


/**
 * 获取下拉框选中选项指定属性的值
 */
function getSelAttr(id, attr){
	if(id && attr){
		if(id.indexOf("#") == -1){
			id = "#" + id;
		}
		var selValue = $(id).attr("value");
		if(selValue){
			var selOption = $(id + " option[value='" + selValue + "']");
			if(selOption){
				return selOption.attr(attr);
			}
		}
	}
	return "";
}

/**
 * 获取某年某月的最后一天的日期
 * @param year
 * @param month
 * @returns
 */
function getLastDay(year,month){
	var new_year = year;    //取当前的年份
	var new_month = month++;//取下一个月的第一天，方便计算（最后一天不固定）
	if(month>12)            //如果当前大于12月，则年份转到下一年
	{
	new_month -=12;        //月份减
	new_year++;            //年份增
	}
	var new_date = new Date(new_year,new_month,1);                //取当年当月中的第一天
	return (new Date(new_date.getTime()-1000*60*60*24)).getDate();//获取当月最后一天日期
}

/**
 * 验证指定IP是否合法
 *
 * @param strIP
 * @returns {Boolean}
 */
function isIP(strIP) {
	if (!strIP){
		return false;
	}
	var re = /^(\d+)\.(\d+)\.(\d+)\.(\d+)$/g; // 匹配IP地址的正则表达式
	if (re.test(strIP)) {
		if (RegExp.$1 < 256 && RegExp.$2 < 256 && RegExp.$3 < 256
				&& RegExp.$4 < 256){
			return true;
		}
	}
	return false;
}

/**
 * 导出文件.
 */
function exportFile(restUrl, params){
	var paramStr = "restUrl=" + restUrl;
	if(!params.method){
		params.method = "GET";
	}
	for(var param in params){
		paramStr = (paramStr + "&" + param + "=" + params[param]);
	}
	paramStr = paramStr + "&timestamp=" + (new Date()).getTime();
	document.location = "export.do?" + paramStr;
}

/**
 * 加载跳转页面
 * @param id
 * @param url
 * @param callback
 */
function loadPageContent(id, url,callback){
    $.ajax({
        type:"post",       
        url:url, 
        success:function(data){
            $("#" + id).html("");
            $("#" + id).append(data);
            translate();
            if(callback){
                callback();
            }
        }
     });
}

/**
 * 表格数据过长，增加Title提示
 * @param value
 * @param rowData
 */
function contentTitleRender(value, rowData){
    var content = value;
    if(value && value.length > 100){
        content = value.substr(0, 100) + "...";
    }
    return "<font title='" + value + "'>" + content + "</font>";
}

/**
 * JavaScript容错
 * @returns {Boolean}
 */
function killErrors(){
	NProgress.remove();
	return true;
}
/**
 * 为避免bootstrap和ztree样式冲突，解决ztree在chrome和IE下不出现横向滚动条的问题添加该方法
 * @param event
 * @param treeId
 * @param node
 */
function deleteOverFlow(event, treeId, node){
	$("#"+treeId).find("ul").each(function(){
		if(this.style.overflow&&this.style.overflow=="hidden")
			this.style.overflow = "";
	});
}
/**
 * 硬盘格式化
 * @param value
 * @param i
 * @returns
 */
function hardDiskCap(value,i){
	if(typeof(i)=="undefined"){
		i = 1;                 //默认精确位数
	}else{
		i = Number(i);
	}
	if(value <= 0){
		return 0;
	}else if(value > 0 && value < 1024){
		return value;
	}else if(value >= 1024 && value < 1024*1024){
		return Number(value/1024).toFixed(i);//统一.
	}else if(value >= 1024*1024 && value < 1024*1024*1024){
		return Number(value/(1024*1024.0)).toFixed(i);
	}else if(value >= 1024*1024*1024 && value < 1024*1024*1024*1024){
		return Number(value/(1024*1024*1024.0)).toFixed(i);
	}else if(value >= 1024*1024*1024*1024 && value < 1024*1024*1024*1024*1024){
		return Number(value/(1024*1024*1024*1024.0)).toFixed(i);
	}else if(value >= 1024*1024*1024*1024*1024 && value < 1024*1024*1024*1024*1024*1024){
		return Number(value/(1024*1024*1024*1024*1024.0)).toFixed(i);
	}else{
		return value;
	}
}

/**
 * 硬盘单位
 */
function hardDiskCapUnit(value){
	if(value <= 0){
		return "Byte";
	}else if(value > 0 && value < 1024){
		return "Byte";
	}else if(value >= 1024 && value < 1024*1024){
		return "KB";
	}else if(value >= 1024*1024 && value < 1024*1024*1024){
		return "MB";
	}else if(value >= 1024*1024*1024 && value < 1024*1024*1024*1024){
		return "GB";
	}else if(value >= 1024*1024*1024*1024 && value < 1024*1024*1024*1024*1024){
		return "TB";
	}else if(value >= 1024*1024*1024*1024*1024 && value < 1024*1024*1024*1024*1024*1024){
		return "PB";
	}else{
		return "Byte";
	}
}

/**
 * 组织网络的加载
 */
function loadOrgNetPageDataList(clusterId){
	doRestCall({
		restUrl:"inetwork/v1/vnet/list",
	    method:"GET",
	    data:{
			param:{
				type:2,
				orgid:organId,
				page:1,
				pageSize:9999,
				clusterid:clusterId
			}
        },
	    callback:function(response){
	    	var flag = response['flag'];
	    	if(flag){
	    		var result =  response['resData'].data;
	    		orgNetList = result;
	    	}else{
	    		orgNetList = null;
	    	}
	    }
	});
}

/**
 * 基础网络列表加载
 * 
 */
function loadBasisNetPageDataList(clusterId){
	doRestCall({
		restUrl:"inetwork/v1/vnet/list",
	    method:"GET",
	    data:{
			param:{
				type:1,
				page:1,
				pageSize:9999,
				clusterid:clusterId
			}
        },
	    callback:function(response){
	    	var flag = response['flag'];
	    	if(flag){
	    		var result = response['resData'].data;
	    		basisNetList = result;
	    	}else{
	    		basisNetList = null;
	    	}
	    }
	});
}


/**
 * 基础/组织网络 选项及参数渲染
 * 
 */
function loadNetWorkDataList(list){
	var options = "";
	for(var l in list){
		options += "<option  value=\"" + list[l].uuid + "\" distribute=\"" + list[l].isDistributed  + "\"" +
							  " portGroupName=\"" + list[l].portGroup + "\" portGroupMor=\"" + list[l].networkMor + "\">" + 
							  list[l].name + 
					"</option>";
	}
	return options;
}


/**
 * 组织网络的下拉列表及监控
 * 
 */
function loadOrgNet(id){
	var options = loadNetWorkDataList(orgNetList);
	$("#" + id).html(options);
	$("#" + id + "extranet").val("UnChecked");
	$("input[name='\" + id + \"'][value='intranet']").attr("checked",true);
	//ip分配方式下的对应ip列表（依据网络类型）
	var ipAllocation = $("#" + id +"ipAllocation").val();
	if("dhcp" == ipAllocation){ //DHCP动态分配
		$("#" + id + "txt").html("");
	}else{//静态分配
		$("#" + id + "txt").html("");
		var netId = $("#" + id).val();
		if(null != netId && typeof(netId)!= "undefined"){
			loadSelectIpList(netId, 1, id);
		}
	}
	$("#" + id + "ipAllocation").show();
	$("#" + id + "txt").show();
}

/**
 *  基础网络的下拉列表及其监控
 *
 */
function loadBasisNet(id){
	var options = loadNetWorkDataList(basisNetList);
	$("#" + id).html(options);
	$("#" + id + "extranet").val("Checked");
	$("input[name='\" + id + \"'][value='extranet']").attr("checked",true); 
	//ip配方式下的对应ip列表（依据网络类型）
	var ipAllocation = $("#" + id +"ipAllocation").val();
	if("dhcp" == ipAllocation){ //动态DHCP分配
		$("#" + id + "txt").html("");
	}else{
		$("#" + id + "txt").html("");
		var netId = $("#" + id).val();
		if(null != netId && typeof(netId)!= "undefined"){
			loadSelectIpList(netId, 2, id);
		}
	}
	$("#" + id + "ipAllocation").show();
	$("#" + id + "txt").show();
}

/**
 * ip分配模式的选择及其监控
 * 
 */
function loadIpAllocation(id){
	var allocationType = $("#" + id + "ipAllocation").val();
	if("dhcp" == allocationType){//自动分配
		$("#" + id + "ipAllocation").removeAttr("name");
		$("#" + id + "txt").html("");
		$("#" + id + "txt").attr("disabled","disabled");
		$("#" + id + "txt").removeAttr("validation");
	}else{//手动分配
		$("#" + id + "txt").removeAttr("disabled");
		$("#" + id + "txt").attr("validation","validate[required,custom[ipv4]]");
		var netId = $("#" + id).val();
		if(null != netId && typeof(netId)!= "undefined"){
			var isNetType = $('input:radio[name='+ id + ']:checked').val();
			var netType;
			if(null != isNetType && "Checked" == isNetType){
				netType = 2;
			}else{
				netType = 1;
			}
			//ip配方式下的对应ip列表（依据IP分配方式选择）
			loadSelectIpList(netId, netType, id);
		}
		var ipType = $("#" + id + "ipAllocation").val();
		$("#" + id + "ipAllocation").attr("name",ipType);
	}
}

/**
 * ip列表加载
 * 
 */
function loadSelectIpList(netId, netType, id){
	doRestCall({
		restUrl:"inetwork/v1/vnet/" + netId + "/ip",
	    method:"GET",
	    data:{
	    	param:{
	    		page:"0",
	    		pageSize:"9999",
	    		type:2 //未使用的ip
	    	}
        },
	    callback:function(response){
	    	var flag = response['flag'];
	    	if(flag){
	    		var datas = response['resData'].data;
	    		if(datas && datas.length > 0){
	    			var selectIpList = $("#" + id + "txt");
	    			var index, data, ip;
	    			for(index = 0; index < datas.length; index++){
		    			data = datas[index];
		    			ip = $("<option>" + data["ip"] + "</option>");
		    			ip.attr("value", data["ip"]);
		    			selectIpList.append(ip);
		    		}
	    		}
	    	}else{
	    		$("#" + id + "txt").html("");
	    	}
	    }
	});
}

/**
 * ip列表的加载（依据网络id字段筛选）
 * 
 */
function loadNetIpList(id){
	var ipAllocation = $("#" + id +"ipAllocation").val();
	if("dhcp" == ipAllocation){ //DHCP动态分配
		$("#" + id + "txt").html("");
	}else{ //静态下拉框分配
		$("#" + id + "txt").html("");
		var netId = $("#" + id).val();
		if(null != netId && typeof(netId)!= "undefined"){
			var isNetType = $('input:radio[name=' + id +  ']:checked').val();
			var netType;
			if(null != isNetType && "Checked" == isNetType){
				netType = 2;
			}else{
				netType = 1;
			}
			loadSelectIpList(netId, netType, id);
		}
	}
}

function navi(text, action){
	var href = "<li><a href='#' onclick='" + action + "'>" + text + "</a></li>";
	return href;
}
/**
 * 数据库加载os列表
 */
function loadVmOperateSysList(vdcId, osType){
	doRestCall({
		restUrl:"icompute/v1/vm/action/get-os-list",
	    method:"GET",
	    data:{
			param:{
				vdcId: vdcId,
				osType:osType
			}
        },
	    callback:function(response){
//	    	$("#newVappForm").unmask();
        },
        success:function(response){
        	$("#newVmOs").html("");
        	var datas = response["resData"];
	    	if(datas && datas.length > 0){
	    		var selectOsList = $("#newVmOs");
	    		var index, data, os;
	    		for(index = 0; index < datas.length; index++){
	    			data = datas[index];
	    			os = $("<option>" + data["osDesc"] + "</option>");
	    			os.attr("value", data["osId"]);
	    			selectOsList.append(os);
	    		}
	    	}
        }
	});
}
/**
 * 数据库加载os列表
 * @param virtualType
 * @param osType
 */
function loadVmOperateSysListApply(virtualType, osType,osId){
	doRestCall({
		restUrl:"icompute/v1/vm/action/get-os-list",
	    method:"GET",
	    data:{
			param:{
				hyperv:parseInt(virtualType),
				osType:osType
			}
        },
	    callback:function(response){
//	    	$("#newVappForm").unmask();
        },
        success:function(response){
        	$("#newVmOs").html("");
        	var datas = response["resData"];
	    	if(datas && datas.length > 0){
	    		var selectOsList = $("#newVmOs");
	    		var index, data, os;
	    		for(index = 0; index < datas.length; index++){
	    			data = datas[index];
	    			os = $("<option>" + data["osDesc"] + "</option>");
	    			os.attr("value", data["osId"]);
	    			selectOsList.append(os);
	    		}
	    		if(osId!=null&&typeof osId!="undefined"){
	    			$("#newVmOs").val(osId);
	    		}
	    	}
        }
	});
}
/**
 * 根据虚拟化类型编码返回虚拟化类型名称
 * @param hypervType
 * @returns {String}
 */
function getHypervTypeName(hypervType){
	var name = "--";
	if (hypervType == "1") {
		name = lang["vmwareVCenter"];
	} else if (hypervType == "2") {
		name = lang["inspurIVirtual"];
	} else if(hypervType == "3"){
		name = lang["zteVirtual"];
	} else if(hypervType == "4"){
		name = lang["IBMHMC"];
	} else if(hypervType == "5"){
		name = lang["hw"];
	} else if(hypervType == "6"){
		name = lang["XenServer"];
	} else if(hypervType == "7"){
		name = lang["OpenStack"];
	}
	return name;
}


/**
 * 加载新的 告警统一页面
 * 
 */
function loadResourceAlarmPage(id, callback){
	var url = "cloudRes/monitor/monitorBasicList.jsp";
	$.ajax({
        type:"post",       
        url:url, 
        success:function(data){
        	$("#" + id).html("");
            $("#" + id).append(data);
            translate();
            if(callback){
            	callback();
            }
        }
     });
}

/**
 * 加载新的 告警历史信息统一页面
 * 
 */
function loadResHistoryAlarmPage(id, callback){
	var url = "cloudRes/monitor/monitorHistoryList.jsp";
	$.ajax({
        type:"post",       
        url:url, 
        success:function(data){
        	$("#" + id).html("");
            $("#" + id).append(data);
            translate();
            if(callback){
            	callback();
            }
        }
     });
} 

/**
 * 统一告警级别的显示页面渲染
 * 
 */
function loadAlarmLevelRender(value, rowData){
	if(value=="1"){
        return "<img src=\"images/analarm.png\" align=\"absmiddle\"><font>"+lang["faultLevel3"]+"</font>";
    }else if(value=="2"){
        return "<img src=\"images/twoalarm.png\" align=\"absmiddle\"><font>"+lang["faultLevel2"]+"</font>";
    }else if(value=="3"){
        return "<img src=\"images/wrong.png\" align=\"absmiddle\"><font>"+lang["faultLevel1"]+"</font>";
    }else if(value=="66"){
        return "<img src=\"images/normal.png\" align=\"absmiddle\"><font>"+lang["regular"]+"</font>";
    }else{
    	 return "<img src=\"images/normal.png\" align=\"absmiddle\"><font>"+lang["regular"]+"</font>";
    }
    return value;
}


/**
 * 为解决FireFox浏览器不能直接new Date("2015-06-19 23:59:59")的兼容性问题
 * @param date 例如：2015-06-19
 * @returns
 */
function newDate_DayEnd(date){
	if(date == "" || date == null || typeof date == "undefined"){
		return new Date();
	}else{
		var str = date.split("-");
		return new Date(str[0],Number(str[1])-1,str[2],"23","59","59");
	}
}
/**
 * 为解决FireFox浏览器不能直接new Date("2015-06-19 00:00:00")的兼容性问题
 * @param date 例如：2015-06-19
 * @returns
 */
function newDate_DayStart(date){
	if(date == "" || date == null || typeof date == "undefined"){
		return new Date();
	}else{
		var str = date.split("-");
		return new Date(str[0],Number(str[1])-1,str[2],"","","");
	}
}