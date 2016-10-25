//////////////////////////公用表格列渲染函数////////////////////

/**
 * 日期列渲染
 */
function chkBoxColRender(value, data){
	return value;
}

/**
 * 日期列渲染
 */
function dateColRender(value, data){
	return value;
}

/**
 * 浮点数列渲染
 */
function floatColRender(value, data){
	return Number(value).toFixed(2);
	
}

/**
 * 钱数列渲染
 */
function moneyColRender(value, data){
	return Number(value).toFixed(2) + "元";
}

/**
 * 使用时长列渲染
 */
function useTimeColRender(value, data){
	return Number(value).toFixed(2) + "小时";
}

/**
 * 利用率列渲染
 * 
 * @param value
 * @param rowData
 */
function percentColRender(value, rowData){
	var color = "star_y";
	if(value >= 80){
		color = "star_r";
	}
	// TODO 添加百分比的显示
	return '<div class="star_g"><div style="width: ' + value + '%;" class="' + color + '"></div></div>';
}

function genUsageMutiCapAppend(usage, cap){
	if(usage){
		usage = usage.replace("%", "");
	}else{
		usage = 0;
	}
	if(!cap){
		cap = "";
	}
	var append = "-";
	if(cap && cap != ""){
		append = usage + "% * " + cap;
	}else{
		append = usage + "%";
	}
	return append;
}

/**
 * 渲染使用量列（使用量/总量）
 * 
 * @param value
 * @param rowData
 */
function renderUsageCol(total, used, unit){
	var usage = parseInt(used)/parseInt(total);
	usage = usage * 100;
	if(unit){
		if((unit.indexOf("B") != -1)){
			var destUnit = calcFileSizeUnit(total, unit);
			if(total<used){
				destUnit = calcFileSizeUnit(used, unit);
			}
			total = formatFileSizeToDestUnit(total, unit, destUnit);
			used = formatFileSizeToDestUnit(used, unit, destUnit);
			unit = destUnit;
		} else if (unit.indexOf("Hz") != -1){
			var destUnit = calcCpuHzUnit(total, unit);
			if(total<used){
				destUnit = calcCpuHzUnit(used, unit);
			}
			total = formatCpuHZToDestUnit(total, unit, destUnit);
			used = formatCpuHZToDestUnit(used, unit, destUnit);
			unit = destUnit;
		}
	}
	var appendText = used + "/" + total + unit;
	var color = "star_y";
	if(usage >= 80){
		color = "star_r";
	}
	var progress = '<div class="star_g"><div style="width: ' + usage + '%;" class="' + color + '"></div></div>';
	progress += "<div style='float:right'>" + appendText + "</div>";
	return progress;
}

/**
 * 渲染利用率列
 * 
 * @param value
 * @param rowData
 */
function renderPercentCol(value, appendText){
	if(value){
		value = value.replace("%", "");
	}else{
		value = 0;
	}
	var color = "star_y";
	if(value >= 80){
		color = "star_r";
	}
	var progress = '<div class="star_g"><div style="width: ' + value + '%;" class="' + color + '"></div></div>';
	progress += "<div style='float:right'>" + appendText + "</div>";
	return progress;
}

/**
 * 状态列渲染
 */
function statusColRender(value, rowData){
	if("1" == value){
		return "<span><img src='images/ico/image 268 .png' align='absmiddle'/><font>已启用</font></font>";
	}else if("2" == value){
		return "<span><img src='images/ico/image 368 .png' align='absmiddle'/><font>已禁用</font></font>";
	}
	return "-";
}