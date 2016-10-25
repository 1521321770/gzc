/**
 * 系统表格操作共通
 */

/**
 * 加载空的数据表
 */
function loadEmptyTable(tableId){
	var table = $("#" + tableId);
	if(table){
		var headers = $("#" + tableId + " thead td");
		var pageSize = (table.attr("pageSize") || 10) * 1;// 每页记录数
		generateEmptyTable(table, headers, pageSize);
	}
}

var tableLoadOptions = [];
/**
 * 加载表格数据
 * 
 * @param tableId 表格id
 * @param callback 加载完成后的回调
 * @param onRowDblClick 双击表格行时的动作
 * @param onClick 点击表格行时的动作
 */
function loadTableData(tableId, options){
	options = options || {};
	options.callback = options.callback || function(){};
	options.onRowDblClick = options.onRowDblClick || function(){};
	options.onClick = options.onClick || function(){};
	options.params = options.params || {};
	
	var table = $("#" + tableId);
	
	var url = table.attr("url");
	var pageSize = table.attr("pageSize");
	table.iDataTable({
		pageSize:pageSize,
		url:url
	});
	
//	if(table){
//		//鼠标滑过
//		table.delegate("tr", "hover", function(){
//			$(this).toggleClass("mouseover");
//		});
//		//鼠标单击
//		table.delegate("tr", "click", function(){
//			var hasData = $(this).attr("hasData");
//			if(hasData && hasData == "true"){
//				table.find("tr").removeClass("selectedrow");
//				$(this).toggleClass("selectedrow");
//			}
//		});
//		
//		// 保存表格加载配置
//		tableLoadOptions[tableId] = options;
//		var callback = options.callback;
//		var onRowDblClick = options.onRowDblClick;
//		var onClick = options.onClick;
//		var loadParam = options.params;
//		
//		realLoadData(tableId, callback, "init", onRowDblClick, onClick, loadParam);
//	}
}

/**
 * 显示表格操作
 */
function showTableOprate(){
	
}

/**
 * 重新加载表格数据
 * 
 * @param tableId
 */
function reloadTableData(tableId, options){
	options = options || tableLoadOptions[tableId] || {};
	options = options || {};
	options.callback = options.callback || function(){};
	options.onRowDblClick = options.onRowDblClick || function(){};
	options.onClick = options.onClick || function(){};
	options.params = options.params || {};
	
	var table = $("#" + tableId);
	if(table){
		//鼠标滑过
		table.delegate("tr", "hover", function(){
			$(this).toggleClass("mouseover");
		});
		//鼠标单击
		table.delegate("tr", "click", function(){
			var hasData = $(this).attr("hasData");
			if(hasData && hasData == "true"){
				table.find("tr").removeClass("selectedrow");
				$(this).toggleClass("selectedrow");
			}
		});
		
		// 保存表格加载配置
		tableLoadOptions[tableId] = options;
		var callback = options.callback;
		var onRowDblClick = options.onRowDblClick;
		var onClick = options.onClick;
		var loadParam = options.params;
		
		realLoadData(tableId, callback, "reload", onRowDblClick, onClick, loadParam);
	}
}

/**
 * 真正加载表格数据
 * 
 * @param tableId 表格id
 * @param callback 加载完成后的回调
 * @param ope 加载方式
 * @param onRowDblClick 双击表格行时的动作
 * @param onClick 点击表格行时的动作
 */
function realLoadData(tableId, callback, ope, onRowDblClick, onClick, loadParam){
	var table = $("#" + tableId);
	if(table){
		var url = table.attr("url");
		var headers = $("#" + tableId + " thead td");
		
		var pageSize = (table.attr("pageSize") || 10) * 1;// 每页记录数
		var page = (table.attr("page") || 1) * 1;// 当前页数
		var totalCount = (table.attr("totalCount") || 0) * 1;// 总记录数
		var totalPage = Math.ceil(totalCount / pageSize);// 总页数
		switch(ope){
		  case "init":page = 1;break;
		  case "reload":break;
		  case "first":page = 1;break;
		  case "pre":page = ((page-1 >= 1)?(page-1):page);break;
		  case "next":page = ((page+1 <= totalPage)?(page+1):page);break;
		  case "last":page = totalPage;break;
		  default:page = 1;break;
		}
		
		if(headers){
			if(url){
				if($("#" + table.attr("id") + " tbody").length < 1){
					generateEmptyTable(table, headers, pageSize);
				}
				ajaxLoad(table, headers, url, pageSize, page, callback, onRowDblClick, onClick, loadParam);
			}else{
				generateEmptyTable(table, headers, pageSize);
			}
		}
	}
}

/**
 * AJAX加载表格数据
 * 
 * @param table 表格对象
 * @param headers 表头对象
 * @param url 数据加载url
 * @param pageSize 每页记录数
 * @param page 当前页数
 * @param callback 加载完成后的回调
 * @param onRowDblClick 双击表格行时的动作
 * @param onClick 点击表格行时的动作
 */
function ajaxLoad(table, headers, url, pageSize, page, callback, onRowDblClick, onClick, loadParam){
	var start = pageSize * (page - 1);
	if(table.parent()){
		table.parent().mask(lang["defaultLoadingMsg"], 300);
	}else{
		table.mask(lang["defaultLoadingMsg"], 300);
	}
	
	loadParam = loadParam || {};
	loadParam.start = start;
	loadParam.limit = pageSize;
	$.ajax({
        type:"get",
        data:loadParam,
        dataType:"json",
        url:url,
        success:function(response){
        	if(table.parent()){
        		table.parent().unmask();
        	}else{
        		table.unmask();
        	}
        	var from = 0, to = 0;
        	var result = response["result"];
        	var total = response["total"];
    		var datas = response["data"];
    		if(result == "failed"){
    			showInfoMsg(datas);
    		}else{
    			if(datas){
    				// 当前页请求无数据，获取前一页数据
    				if(total > 0 && datas.length <= 0){
    					var tableId = table.attr("id");
    					realLoadData(tableId, callback, 'pre', onRowDblClick, onClick, loadParam);
    					return;
    				}
            		from = (datas.length <= 0)?0:(start + 1);
            		to = start + datas.length;
            		var header;
            		var datakey = null, render = null, align = null, style = null, value = null, ignoreRowClick;
            		var tr = null, td = null;
            		// 清空表数据
            		$("#" + table.attr("id") + " tbody").remove();
            		var tbody = $("<tbody></tbody>");
            		table.append(tbody);
            		for(var index in datas){
            			var data = datas[index];
            			if(data && data != "null"){
            				tr = $("<tr></tr>");
                			// 设置行数据标号
                			tr.attr("index", index);
                			// 行存在数据的标志
                			tr.attr("hasData", true);
                			tbody.append(tr);
                			if(onRowDblClick && (typeof onRowDblClick == "function")){
                				tr.dblclick(function(){
                					var data = datas[$(this).attr("index")];
                					onRowDblClick(data);
                				});
                			}
                			
                    		for(var j = 0; j < headers.length; j++){
                    			td = $("<td></td>");
                    			tr.append(td);
                    			header = $(headers[j]);
                    			value = "";
                    			type = header.attr("type");
                				if(type){
                					td.attr("align", "center");
                					td.append("<input type='" + type + "' name='" + header.attr("name") + "'/>");
                				}
                    			datakey = header.attr("datakey");
                    			if(datakey){
                    				value = data[datakey];
                    				render = header.attr("render");
                    				if(render){
                    					value = eval(render + "(value, data)");
                    				}
                    				align = header.attr("align");
                    				if(align){
                    					td.attr("align", align);
                    				}
                    				style = header.attr("style");
                    				if(style){
                    					td.attr("style", style);
                    				}
                    				ignoreRowClick = header.attr("ignoreRowClick");
                    				if(!(ignoreRowClick)){
                    					if(onClick && (typeof onClick == "function")){
                            				td.click(function(){
                            					var rowData = datas[$(this).parent().attr("index")];
                            					table.attr("selectedRow", rowData);
                            					onClick(rowData);
                            				});
                            			}
                    				}
                    				td.html(value);
                    			}
                    		}
            			}
                	}
            		var ignoreEmptyRow = table.attr("ignoreEmptyRow");
            		if(!ignoreEmptyRow){
            			// 数据不够一页时，补全空白行
                		if(datas.length < pageSize){
                			for(var k = 0; k < pageSize - datas.length; k++){
                				tr = $("<tr></tr>");
                				tbody.append(tr);
                				for(var l = 0; l < headers.length; l++){
                					header = $(headers[l]);
                					td = $("<td></td>");
                					tr.append(td);
                					style = header.attr("style");
                    				if(style){
                    					td.attr("style", style);
                    				}
                				}
                			}
                		}
            		}
            	}
    		}
        	
        	table.attr("page", page);
        	table.attr("totalCount", total);
        	table.find("tr:even").addClass("even");//偶数行的背景色
        	table.find("tr:odd").addClass("odd");//奇数行的背景色
        	addFooter(table, headers.length, pageSize, from, to, total, callback, onRowDblClick, onClick, loadParam);
        	// 回调
        	if(callback){
        		callback(response);
        	}
        },
        error:function(){
        	generateEmptyTable(table, headers, pageSize);
        	if(table.parent()){
        		$.unmaskElement(table.parent());
        	}else{
        		$.unmaskElement(table);
        	}
        }
     });
}

/**
 * 生成空的表格
 * 
 * @param table
 * @param headers
 * @param pageSize
 */
function generateEmptyTable(table, headers, pageSize){
	// 生成表头
	addHeader(table, headers);
	// 清空表数据
	$("#" + table.attr("id") + " tbody").remove();
	var tbody = $("<tbody></tbody>");
	table.append(tbody);
	for(var k = 0; k < pageSize; k++){
		tr = $("<tr></tr>");
		tbody.append(tr);
		for(var l = 0; l < headers.length; l++){
			header = $(headers[l]);
			td = $("<td></td>");
			tr.append(td);
			style = header.attr("style");
			if(style){
				td.attr("style", style);
			}
		}
	}
	table.find("tr:even").addClass("even");//偶数行的背景色
	table.find("tr:odd").addClass("odd");//奇数行的背景色
	addFooter(table, headers.length, 0, 0, 0, 0);
}

/**
 * 生成表头
 * 
 * @param table
 * @param headers
 */
function addHeader(table, headers){
	for(var i = 0; i < headers.length; i++){
		header = $(headers[i]);
		type = header.attr("type");
		if(type){
			header.html("");
			header.attr("align", "center");
			header.append("<input type='" + type + "' name='" + header.attr("name") + "'/>");
		}
	}
}

/**
 * <tfoot>
<tr>
<td colspan="7">
<div class="page">
<ul>
  <li class="tofirst" id=""><a id="firstPage" href="#" target="_self" onclick="return false;"></a></li><!-- 第一页 -->
  <li class="topre" id=""><a id="prePage" href="#" target="_self" onclick="return false;"></a></li><!-- 前一页 -->
  <li class="pagenumber"><span id="fromIndx">1</span>-<span id="toIndex">3</span>/<span id="total">25</span></li>
  <li class="tonext" id=""><a id="nextPage" href="#" target="_self" onclick="return false;"></a></li><!-- 下一页 -->
  <li class="tolast" id=""><a id="lastPage" href="#" target="_self" onclick="return false;"></a></li><!-- 最后一页 -->
</ul>
</div>
</td>
</tr>
</tfoot>
 */
function addFooter(table, colCount, pageSize, from, to, total, callback, onRowDblClick, onClick, loadParam){
	if(!loadParam || (loadParam && !loadParam.noFooter)){
		var tableId = table.attr("id");
		$("#" + tableId + " tfoot").remove();
		var footer = $("<tfoot></tfoot>");
		var footerTR = $("<tr></tr>");
		var footerTD = $("<td colspan='" + colCount + "'></td>");
		var page = $("<div class='page'></div>");
		var pageItems = $("<ul></ul>");
		// 第一页
		var firstPage = $('<li class="tofirstdisable"></li>');
		var firstPageA = $('<a></a>');
		if(from > 1){
			firstPage = $('<li class="tofirst"></li>');
			firstPageA = $('<a id="firstPage"></a>');
			firstPageA.click(function(){
				realLoadData(tableId, callback, 'first', onRowDblClick, onClick, loadParam);
			});
		}
		
		// 前一页
		var prePage = $('<li class="topredisable"></li>');
		var prePageA = $('<a></a>');
		if(from > 1){
			prePage = $('<li class="topre"></li>');
			prePageA = $('<a id="prePage"></a>');
			prePageA.click(function(){
				realLoadData(tableId, callback, 'pre', onRowDblClick, onClick, loadParam);
			});
		}
		
		// 当前页
		var currentPage = $('<li class="pagenumber"><span id="fromIndx">' + from + '</span>-<span id="toIndex">' + to + '</span>/<span id="total">' + total + '</span></li>');
		// 下一页
		var nextPage = $('<li class="tonextdisable"></li>');
		var nextPageA = $('<a></a>');
		if(to < total){
			nextPage = $('<li class="tonext"></li>');
			nextPageA = $('<a id="nextPage"></a>');
			nextPageA.click(function(){
				realLoadData(tableId, callback, 'next', onRowDblClick, onClick, loadParam);
			});
		}
		
		// 最后一页
		var lastPage = $('<li class="tolastdisable"></li>');
		var lastPageA = $('<a></a>');
		if(to < total){
			lastPage = $('<li class="tolast"></li>');
			lastPageA = $('<a id="lastPage"></a>');
			lastPageA.click(function(){
				realLoadData(tableId, callback, 'last', onRowDblClick, onClick, loadParam);
			});
		}
		
		firstPage.append(firstPageA);
		pageItems.append(firstPage);
		
		prePage.append(prePageA);
		pageItems.append(prePage);
		
		pageItems.append(currentPage);
		
		nextPage.append(nextPageA);
		pageItems.append(nextPage);
		
		lastPage.append(lastPageA);
		pageItems.append(lastPage);
		
		page.append(pageItems);
		footerTD.append(page);
		footerTR.append(footerTD);
		footer.append(footerTR);
		table.append(footer);
	}
}