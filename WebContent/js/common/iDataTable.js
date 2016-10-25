/**
 * jQuery iDataTable plugin
 * 
 * iDataTable是一个轻量级的jQuery表格插件
 * 
 * @name iDataTable -- inspur data table
 * @author gao.fei
 * @version 0.1
 * @copyright (c) 2012 gao.fei
 * @example TODO
 */
(function($){
  $.fn.iDataTable = function(options){
    var defaults = {
      autoLoad   : true, // 是否自动加载数据
      loadingMsg : lang["defaultLoadingMsg"] || "loading...", // 加载数据的场合, 显示的信息
      url        : "",   // 数据获取URL
      remoteIp:"", // 调用远程rest接口IP地址
      remotePort:"", // 调用远程rest接口端口
      defaultLoadParam  : {}, // 默认URL参数
      rowDataId  : "id", // 行数据的唯一标识

      sort       : "", // 默认排序字段, datakey
      dir        : "asc", // 排序顺序 asc, desc

      orderType  : "num",

      showInlineMenu: false, // 是否显示行内嵌菜单

      autoReload:false, // 是否自动刷新
      needReload:function(rowData){return false;}, // 是否需要reload的判断函数
      reloadTime:5000, // 刷新间隔ms
      timeoutControl:true, // 是否进行超时控制 -- 默认进行控制

      rowStyle:function(rowData){return null;}, // 行样式决策器 -- 参数行数据 返回值tr要应用的样式

      paging     : true, // 是否分页
      showPageSizeChange:false, // 是否显示条数选择
      showSelectedNum : true, // 是否显示选中条数
      showTotalNum  : true, // 是否显示总条数
      showRefreshBtn : true, // 是否显示刷新按钮
      showColSelBtn : true, // 是否显示列选择按钮
      pageSize   : 10,   // 一页显示的记录数
      ignoreEmptyRow : false, // 是否不自动补充空行

      selectable : true, // 是否可选中 -- 无复选框、单选框的表格有效
      keepSelection : true, // 保持翻页场合的选中状态
      filterDiv  : '',   // 表格数据过滤条件DIV的id
      operateDiv : '',   // 表格行数据操作DIV的id
      operate:[],
      preventContinuousClick : false, // 防止操作连续点击
      onRowDblClick : function(rowData){}, // 表格行双击事件处理
      onRowClick : function(rowData){}, // 表格行单击事件处理
      onPageDataLoaded : function(pageDataList){}, // 表格数据加载完成后的处理 -- 参数为表格当前页数据
      onPageDataLoadedResData :function(resData){} // 表格数据加载完成后的处理 -- 参数为resData里的数据（不止当前页）
    }, _this = this;
    this.opt = $.extend(defaults, options); // 插件参数
    if(!this.opt.paging){
      this.opt.showColSelBtn = false;
    }
    this.mulitSel = false; // 是否需要多行选中 -- 有复选框
    this.singleSel = false; // 是否为单选 -- 有单选框
    // 排序信息
    this.sortInfo = {
      sort:_this.opt.sort || "",
      dir:_this.opt.dir || "asc"
    };
    this.headerInfo = []; // 数据列信息 - 绑定到具体数据的列
    this.colSpanHeaderInfo = []; // 列合并信息 - 合并列表头
    this.secondRowColNum = 0;
    this.dataAlignInfo = {}; // 数据对齐方式信息
    this.dataColCount = 0; // 数据列数目
    this.checkboxColWidth = 25;
    this.colSelDiv = {};

    /**
     * 计算表格布局
     */
    this.calcDisplay = function(){
      if(_this.colSpanHeaderInfo.length > 0){
        var colSpanInfoMap = {}; // 列合并信息
        // 初始化
        for(var index = 0; index < _this.colSpanHeaderInfo.length; index++){
          var header = $(_this.colSpanHeaderInfo[index]);
          var colNO = header.attr("colNO");
          colSpanInfoMap[colNO] = 0;
          if(!header.attr("_width")){ // 保存width的最初值 -- html中配置
            header.attr("_width", header.attr("width"));
          }
        }
        // 提取列合并信息
        for(var index = 0; index < _this.headerInfo.length; index++){
          var header = $(_this.headerInfo[index]);
          var colSpanNO = header.attr("colSpanNO");
          var hide = header.attr("hide");
          if(colSpanNO && hide != "true"){
            colSpanInfoMap[colSpanNO] = colSpanInfoMap[colSpanNO] + 1;
          }
        }
        //设置colspan属性
        for(var index = 0; index < _this.colSpanHeaderInfo.length; index++){
          var header = $(_this.colSpanHeaderInfo[index]);
          var colNO = header.attr("colNO");
          var colSpan = colSpanInfoMap[colNO];
          if(colSpan == 0){
            // 合并列的所有数据列都已经隐藏 - 隐藏合并列
            if(!header.attr("__style")){
              header.attr("__style", header.attr("style")); // 如果未保存过样式，保存列样式
            }
            if(!header.is(":hidden")){
              header.fadeOut("slow");
              header.attr("style", "display:none;");
            }
            header.attr("colspan", 0);
          }else{
            if(header.is(":hidden")){
              header.fadeIn("slow");
              header.attr("style", header.attr("__style")); // 恢复列样式
            }
            header.attr("colspan", colSpan);
          }
          header.removeAttr("width");
        }
      }

      var secondRowColNum = _this.secondRowColNum;
      var hide, configWidth = 0, configTotalWidth = 0;
      for(var index = 0; index < _this.headerInfo.length; index++){
        var header = $(_this.headerInfo[index]);
        hide = header.attr("hide");
        if(hide && hide == "true"){
          _this.dataColCount--;
          if(!header.attr("__style")){
            header.attr("__style", header.attr("style")); // 如果未保存过样式，保存列样式
          }
          if(!header.is(":hidden")){
            header.fadeOut("slow");
            header.attr("style", "display:none;");
            header.attr("_style", "display:none;"); // 修改隐藏列不能隐藏的问题
          }
          header.attr("resizable", false);
        }else{
          if(header.is(":hidden")){
            header.fadeIn("slow");
            header.attr("style", header.attr("__style")); // 恢复列样式
            header.attr("_style", header.attr("__style"));
          }
          configWidth = parseInt(header.attr("width") || header.attr("_width"));
          if(configWidth){
            configTotalWidth = configTotalWidth + configWidth;
            header.width(0); // 重置列style中的width属性，否则会出现_this.width()计算错误的情况
          }
        }
      }
      var totalWidth = _this.width();
      _this.totalWidth = totalWidth;
      if(_this.multiSel || _this.singleSel){
        totalWidth = totalWidth - 25;
        if(secondRowColNum > 0){
          secondRowColNum = secondRowColNum + 1;
        }else{
          secondRowColNum = -1;
        }
        _this.totalWidth = totalWidth - secondRowColNum;
      }

      var newWidth = 0, newWidthSum = 0;
      for(var index = 0; index < _this.headerInfo.length; index++){
        header = $(_this.headerInfo[index]);
        hide = header.attr("hide");
        type = header.attr("type");
        if((!hide || hide != "true") && (type != "checkbox" && type != "radio")){
          // 此处假设最后一列为非隐藏列
          if(!header.attr("_width")){ // 保存width的最初值 -- html中配置
            header.attr("_width", header.attr("width"));
          }
          if(index != _this.headerInfo.length - 1){
            // 设置表头绝对宽度
            var width = header.attr("_width");
            newWidth = Math.floor(parseInt(width) * totalWidth / configTotalWidth);
            newWidthSum = newWidthSum + newWidth;
            header.width(newWidth - 5);
          }else{
            header.width(totalWidth - newWidthSum - 5);
          }
          header.removeAttr("width");
        }
      }
    };
    /**
     * 进行排序相关设置 - 点击表头排序.
     *
     * @param header 表头
     */
    this.doSortSetting = function(header){
      var langkey = header.attr("langkey");
      if(langkey && lang[langkey]){
        header.attr("langkey", null);
        header.html("<span langkey='" + langkey + "'>" + lang[langkey] + "</span>");
      }
      header.addClass("sorting");
      header.append('<span class="column-sorter"></span>');
      var datakey = header.attr("datakey");
      if(datakey == _this.opt.sort){
        // IE8兼容性修改
        header.removeClass("sorting");
        // 默认排序字段
        if(_this.opt.dir == "desc"){
          header.addClass("sorting_desc");
        }else{
          header.addClass("sorting_asc");
        }
      }

      // 添加鼠标点击排序操作
      $(header).click(function(){
        var row = $(this).parent();
        var isCurColASC = $(this).hasClass("sorting_asc");
        // IE8兼容性修改
        $(this).removeClass("sorting");
        row.find("[sortable]").removeClass("sorting_asc").removeClass("sorting_desc").addClass("sorting");
        if(isCurColASC){
          $(this).removeClass("sorting").removeClass("sorting_asc").addClass("sorting_desc");
          _this.sortInfo.dir = "desc";
        }else{
          $(this).removeClass("sorting").removeClass("sorting_desc").addClass("sorting_asc");
          _this.sortInfo.dir = "asc";
        }
        _this.sortInfo.sort = $(this).attr("datakey");
        _this.loadTableData("init");
      });
    };

    /**
     * 隐藏列选择面板
     */
    this.hideColSel = function(evt){
      if($(evt.target).parents("#colSelDiv").length > 0){
        return;
      }
      // 点击面板以外的区域 隐藏显示
      $("body").unbind("mousedown", _this.hideColSel);
      // 隐藏列选择DIV
      _this.colSelDiv.fadeOut("slow");
    };
    /**
     * 显示列选择面板
     */
    this.showColSel = function(topPos, leftPos){
      var colSelDiv = $(_this).parent().find("#colSelDiv");
      if(colSelDiv.length <= 0){
        colSelDiv = $('<div id="colSelDiv" class="selectcolumn" style="border:1px solid #E9E9E9; background-color:white; max-width:300px; z-index:10001; position:absolute;">'
                   +    '<div style="max-height:300px; min-height:80px; overflow:auto; display:block;">'
                   +      '<ul id="colItems" style="padding:0px;"></ul>'
                   +    '</div>'
                   +    '<div id="colSelButtons" style="margin-top:5px; margin-left:5px; margin-bottom:5px;"></div>'
                   +  '</div>');
        var btnOK = $('<a id="btn_ok_col_sel" style="margin-left:5px; color:#ffffff; background-color:#0A8DFF; border-color:#1463E7;" class="btn btn-xs">' + lang["ok"] + '</a>');
        var btnCancel = $('<a id="btn_cancel_col_sel" style="margin-left:5px;color: #444444;background-color:#EBEBEB;border-color:#CDCDCD;" class="btn btn-xs">' + lang["cancel"] + '</a>');
        btnOK.click(function(){
          var colStateMap = {}; // 列显示状态Map
          // 获取列选中状态
          var colItems = _this.colSelDiv.find("#colItems").find("input");
          for(var i=0; i < colItems.length; i++){
            var colItem = colItems[i];
            var colNO = $(colItem).attr("id");
            var showCol = $(colItem).attr("checked")?true:false;
            colStateMap[colNO] = showCol;
          }
          // 更新列显示
          for(var i=0; i < _this.headerInfo.length; i++){
            var colNO = $(_this.headerInfo[i]).attr("colNO");
            var colType = $(_this.headerInfo[i]).attr("type");
            if(colType == "checkbox" || colType == "radio"){
              // 复选框和单选框始终显示
              continue;
            }
            var hide = colStateMap[colNO]?false:true;
            $(_this.headerInfo[i]).attr("hide", hide);
          }
          // 重新计算表格布局
          _this.calcDisplay();
          // 重新加载表格数据
          _this.loadTableData("reload");
          // 隐藏列选择DIV
          _this.colSelDiv.fadeOut("slow");
        });
        btnCancel.click(function(){
          // 隐藏列选择DIV
          _this.colSelDiv.fadeOut("slow");
        });
        colSelDiv.find("#colSelButtons").append(btnOK);
        colSelDiv.find("#colSelButtons").append(btnCancel);
        $(_this).parent().append(colSelDiv);
      }
      _this.colSelDiv = colSelDiv;
      var colItems = _this.colSelDiv.find("#colItems");
      colItems.html("");
      for(var i=0; i < _this.headerInfo.length; i++){
        var colNO = $(_this.headerInfo[i]).attr("colNO");
        var colName = $(_this.headerInfo[i]).attr("dispColName");
        var hide = $(_this.headerInfo[i]).attr("hide");
        var colType = $(_this.headerInfo[i]).attr("type");
        if(colType == "checkbox" || colType == "radio" || colName == ""){
          // 复选框和单选框不在选择里面 列名为空的也不在显示范围
          continue;
        }
        var colItem = $('<li class="dropdownitem" style="list-style:none; padding-left:5px; font-size:12px;"></li>');
        var colItemChk = $('<input type="checkbox" id="' + colNO + '"style="vertical-align:middle; margin:0 2px 0 0; cursor:pointer;">');
        var checked = (hide == "true")?false:true;
        $(colItemChk).attr("checked", checked);
        var colItemLabel = $('<label style="cursor:pointer;font-size:12px;">' + colName + '</label>');
        colItem.click(function(evt){
          var checkbox = $(this).find("input");
          if(evt.target.localName != "input"){
            //点击标签或li的场合反选checkbox -- 点击checkbox的场合无需处理选中状态
            var checked = $(checkbox).attr("checked")?true:false;
            checkbox.attr("checked", !checked);
          }
        });
        colItem.append(colItemChk);
        colItem.append(colItemLabel);
        colItems.append(colItem);
      }
      var colSelDivWidth = 140; // 列选择面板宽度
      // 根据显示内容计算面板应该显示的宽度
      var ruler = $('<span id="ruler" style="white-space:nowrap; font-size:12px;"></span>');
      $(_this).parent().append(ruler);
        for(var i=0; i < _this.headerInfo.length; i++){
          var colName = $(_this.headerInfo[i]).attr("dispColName");
          // 计算文字占用宽度
          ruler.text(colName);
          var colNameWidth = ruler[0].offsetWidth + 55;
          if(colNameWidth > colSelDivWidth){
            colSelDivWidth = colNameWidth;
          }
        }
        $(_this).parent().find("#ruler").remove();
        _this.colSelDiv.css("width", colSelDivWidth);

        _this.colSelDiv.css("top", topPos - _this.colSelDiv.height()/2);
        _this.colSelDiv.css("left", leftPos + 1);
        _this.colSelDiv.fadeIn("slow");
        $("body").bind("mousedown", _this.hideColSel);
    };
    /**
     * 初始化表格
     */
    this.initTable = function(){
      // 修改列选择面板显示位置问题 -- 检查widget widget-table样式
      var _parent = _this.parent();
//      if(!_parent.hasClass("widget")){
//        _parent.addClass("widget");
//      }
//      if(!_parent.hasClass("widget-table")){
//        _parent.addClass("widget-table");
//      }
      _this.removeClass("table-striped"); // 删除bootstrap样式 -- 隔行换色和行选中问题
      _this.addClass("idatatable");
      var content = _this.html().replace(/\<td/g, "<th").replace(/\/td>/g, "/th>");
      // IE8兼容性修改
      content = content.replace(/\<TD/g, "<th").replace(/\/TD>/g, "/th>");
      _this.html(content);
      // 保存URL参数 -- lazy load方式问题修改
      _this.loadParam = this.loadParam || this.opt.defaultLoadParam || {};
      // 生成表头
      var header, headers = _this.find("thead th");
      var type, sortable;
      var datakey, dataColCount = 0;
      for(var i = 0; i < headers.length; i++){
        header = $(headers[i]);
        header.attr("colNO", i);
        header.attr("dispColName", header.html()); // 显示列名
        header.attr("resizable", true);
        datakey = header.attr("datakey");
        type = header.attr("type");
        if(datakey || (type && (type == "checkbox" || type == "radio"))){
          dataColCount++;
          // 保存数据对齐信息
          _this.dataAlignInfo[datakey] = header.attr("align") || "center";
        }
        // 备份样式信息 style class
        header.attr("_style", header.attr("style"));
        header.attr("_class", header.attr("class"));
        // 所有表头居中显示
        header.attr("style", "text-align:center; vertical-align:middle;");

        if(type && type == "checkbox"){
          header.html("");
          var checkbox = $("<input type='checkbox' id='iDataTableChk' style='margin-top:0px;'/>");
          checkbox.click(_this.selectAllRow);
          header.append(checkbox);
          _this.multiSel = true;
          // 固定宽度 不可拖动
          header.width(_this.checkboxColWidth);
          header.attr("resizable", false);
        }

        if(type && type == "radio"){
          header.html("");
          _this.singleSel = true;
          // 固定宽度 不可拖动
          header.width(_this.checkboxColWidth);
          header.attr("resizable", false);
        }

        sortable = header.attr("sortable");
        if(sortable && (sortable == "true" || sortable == true)){
          _this.doSortSetting(header);
        }
      }

      // headers顺序整合 -- 解决双行表头数据混乱问题
      _this.headerInfo = headers;
      var headerRows = _this.find("thead tr");
      if(headerRows.length == 2){
        var firstRow = $(headerRows[0]).find("th");
        var secondRow = $(headerRows[1]).find("th");
        _this.secondRowColNum = secondRow.length;
        if(firstRow.length > 0 && secondRow.length > 0){
          _this.headerInfo = [];
          for(var i = 0, k = 0; i < firstRow.length; i++){
            var td = $(firstRow[i]);
            var colSpan = td.attr("colSpan");
            if(colSpan){
              colSpan = parseInt(colSpan);
              // IE8兼容性问题--未定义colSpan的场合IE8默认为1
              if(colSpan > 1){
                _this.colSpanHeaderInfo.push(td);
                var colSpanNO = td.attr("colNO");
                var colSpanName = td.attr("dispColName");
                for(var j = 0; j < colSpan && k < secondRow.length; j++){
                  td = $(secondRow[k]);
                  // 设置数据列的colSpanNO - 用于隐藏数据列后修改colspan值
                  td.attr("colSpanNO", colSpanNO);
                  // 设置存在列合并的数据列在选择场合的名称 - 如：CPU配置 - 超量分配百分比
                  td.attr("dispColName", colSpanName + " - " + td.attr("dispColName"));
                  k++;
                  _this.headerInfo.push(td);
                }
              }else{
                _this.headerInfo.push(td);
              }
            }else{
              _this.headerInfo.push(td);
            }
          }
        }
      }
      // 数据列数目
      _this.dataColCount = dataColCount;
      _this.calcDisplay();

      // 鼠标滑过
      _this.delegate("tr", "hover", function(event){
        var hasData = $(this).attr("hasData");
        if(hasData && hasData == "true"){
          $(this).toggleClass("mouseover");
          if(_this.opt.showInlineMenu){
            // 显示行数据操作菜单
            var operateDiv = $(this).find("#inlineOpe");
            var rowDataId = $(this).attr("rowDataId");
            var isDataValid = $(this).attr("isDataValid");
            var rowData = _this.tableData[rowDataId];
            // 获取表格下拉框数据
            var select = $(this).find("select");
            if(select){
              datakey = select.attr("datakey");
              value = select.attr("value");
              rowData[datakey] = value;
            }

            var rowDataArray = new Array();
            rowDataArray.push(rowData);
            // 行数据操作菜单状态控制 -- 操作按钮状态的制御
            var operate, btn, canEnable;
            for(var i = 0; i < _this.opt.operate.length; i++){
              operate = _this.opt.operate[i];
              if(operate && operate.id){
                btn = operateDiv.find("#" + operate.id);
                if(btn){
                  if(isDataValid == undefined || isDataValid == true || isDataValid == "true"){
                    if(operate.enable){// 有操作启用条件
                      canEnable = operate.enable(rowData || {});
                      if(canEnable){
//                        btn.addClass("btn-operat");
                        btn.removeClass("disabled");
                      }else{
//                        btn.removeClass("btn-operat");
                        btn.addClass("disabled");
                      }
                    }else{// 无操作启用条件 -- 直接启用
//                      btn.addClass("btn-operat");
                      btn.removeClass("disabled");
                    }
                  }else{ // 数据校验无效的场合，按钮不可操作
//                    btn.removeClass("btn-operat");
                    btn.addClass("disabled");
                  }

                  // 绑定按钮事件
                  if(!btn.hasClass("disabled")){
                    if(operate.action){
                      btn.unbind("click");
                      btn.bind("click", rowDataArray, operate.action);
                      // 如果需要防止连续点击，则触发点击事件后：将按钮置为不可用，并且去除事件绑定
                      if(_this.opt.preventContinuousClick == true){
                        btn.bind("click", function(){
//                          btn.removeClass("btn-operat");
                          btn.addClass("disabled");
                          btn.unbind("click");
                        });
                      }
                    }
                  }else{
                    btn.unbind("click");
                  }
                }
              }
            }// end of for
            var curRow = $(this);
            operateDiv.unbind("click"); // 解除click时间的绑定 -- 解决点击触发多个事件的问题
            operateDiv.click(function(event){
              if(_this.multiSel){
                // 复选框多选
                // 获取当前行的复选框 ID
                var curRowChk = curRow.find("[iDataTableChkId]");
                var curRowChkId = curRowChk.attr("iDataTableChkId");
                // 改变当前行的选中状态
                var curRowChecked = true;
                // 获取所有复选框
                var allChk = _this.find("[iDataTableChkId]");
                // 清空所有选择项
                _this.selectedRecords = new Array();
                var rowChkId;
                var index = 0, chk;
                for(index = 0; index < allChk.length; index++){
                  chk = $(allChk[index]);
                  rowChkId = chk.attr("iDataTableChkId");
                  _this.recordRowSelect(rowChkId, false);
                }
                // 设置当前行复选框状态
                _this.recordRowSelect(curRowChkId, curRowChecked, true);
              }else if(_this.singleSel){
                // 单选框单选处理
                _this.clearAllSelection();
                _this.recordRowSelect($(this).parent().attr("rowDataId"), true, true);
              }else{
                // 非选复选、单选列表场合的处理
                if(_this.opt.selectable){
                  _this.clearAllSelection();
                  _this.recordRowSelect($(this).parent().attr("rowDataId"), true, true);
                }
              }
              if(_this.opt.onRowClick && (typeof _this.opt.onRowClick == "function")){
                var rowDataId = $(curRow).attr("rowDataId");
                var rowData = _this.tableData[rowDataId];
                _this.opt.onRowClick(rowData);
              }
              event.preventDefault();
            });
            if(event.type === "mouseenter"){
              operateDiv.fadeIn(0);
            }else{
              operateDiv.fadeOut(0);  
            }
          }
        }
      });

      //鼠标单击
      if(!_this.multiSel && _this.opt.selectable){
        _this.delegate("tr", "click", function(){
          var hasData = $(this).attr("hasData");
          if(hasData && hasData == "true"){
            _this.find("tr").removeClass("selectedrow");
            $(this).toggleClass("selectedrow");
          }
        });
      }

      // 生成空数据
      _this.find("tbody").remove();
      var tbody = $("<tbody></tbody>");
      _this.append(tbody);
      for(var k = 0; k < _this.opt.pageSize; k++){
        tr = $("<tr></tr>");
        tbody.append(tr);
        for(var l = 0; l < _this.headerInfo.length; l++){
          header = $(_this.headerInfo[l]);
          td = $("<td>&nbsp;</td>");
          var hide = header.attr("hide");
          if(hide && hide == "true"){
            td.attr("style", "display:none;");
          }
          tr.append(td);
        }
      }
      _this.find("tr:even").addClass("even");//偶数行的背景色
      _this.find("tr:odd").addClass("odd");//奇数行的背景色
      
      // 生成表尾
      _this.addFooter({});
      _this.updateDisplay();
    };

    /**
     * 获取要显示的页码信息
     */
    this.getPageNumsToDisplay = function(){
      var pageNums = []; // 要显示页码, -1表示省略号
      if(_this.pageInfo.totalPage < 11){
        // 无省略号
        for(var i = 1; i <= _this.pageInfo.totalPage; i++){
          pageNums.push(i);
        }
      } else {
        if(_this.pageInfo.curPage < 6) {
          // 显示后省略号
          for(var i = 1; i <= 7; i++){
            pageNums.push(i);
          }
          pageNums.push(-1); // 后省略号
          pageNums.push(_this.pageInfo.totalPage - 1);
          pageNums.push(_this.pageInfo.totalPage);
        }else{
          if(_this.pageInfo.totalPage - _this.pageInfo.curPage < 5){
            // 显示前省略号
            pageNums.push(1);
            pageNums.push(2);
            pageNums.push(-1); // 前省略号
            for(var i = _this.pageInfo.totalPage - 6; i <= _this.pageInfo.totalPage; i++){
              pageNums.push(i);
            }
          }else{
            // 显示前、后省略号
            pageNums.push(1);
            pageNums.push(2);
            pageNums.push(-1); // 前省略号
            pageNums.push(_this.pageInfo.curPage - 1);
            pageNums.push(_this.pageInfo.curPage);
            pageNums.push(Number(_this.pageInfo.curPage) + 1);
            pageNums.push(-1); // 后省略号
            pageNums.push(_this.pageInfo.totalPage - 1);
            pageNums.push(_this.pageInfo.totalPage);
          }
        }
      }
      return pageNums;
    };
    /**
     * 生成表尾
     * <div id="tfoot" class="widget-footer">
     *   <div class="btn-group paging_full_numbers" style="margin-right:15px;">
     *     <a class="btn btn-default disabled btn-boo">&lt;&lt;</a>
     *     <a class="previous btn btn-default disabled btn-boo">&lt;</a>
     *     <a class="btn btn-default btn-boo">1</a>
     *     <a class="btn btn-default btn-sky">2</a>
     *     <a class="btn btn-default btn-boo">3</a>
     *     <a class="btn btn-default btn-boo">4</a>
     *     <a class="btn btn-default btn-boo">&gt;</a>
     *     <a class="btn btn-default btn-boo">&gt;&gt;</a>
     *   </div>
     * </div>
     */
    this.addFooter = function(loadParam){
      // 清空表尾
      _this.parent().find("#tfoot").remove();
      var footer = $('<div id="tfoot" class="widget-footer clearfix"></div>');

      /**
       * <div style="margin-top: 13px;margin-left: 10px;">
       *   <a href="javascript:;"><img title="刷新" src="images/refresh.png"></a>
       *   <span id="info">共 4 条记录，当前选中 1 条</span>
       * </div>
       */
      var info = $('<div style=" float:left; padding:10px 0 0 5px">');
      if(_this.opt.showRefreshBtn){
        var refresh = $("<a href='javascript:;'><img title='" + lang["refresh"] + "' src='images/refresh.png'/></a>");
        refresh.click(function(){
          _this.reload();
        });
        info.append(refresh);
      }
      var i = $("<span id='info'></span>");
      info.append(i);
      
      if(_this.opt.showColSelBtn){
        var btnSelCol = $('<a class="btn btn-xs btn-default btn-primary" style="margin:0 0 0 5px;">' + lang["selectColumn"] + '</a>');
        btnSelCol.click(function(){
          var top = $(this).offset().top - $(_this).offset().top + $(this)[0].offsetHeight;
          var left = $(this).offset().left - $(_this).offset().left +  + $(this)[0].offsetWidth;
          _this.showColSel(top, left);
        });
        info.append(btnSelCol);
      }
      
//      if(_this.opt.showPageSizeChange){
//    	  var select = $("<select><option value='5'>5</option><option value='20'>20</option><option value='100'>100</option></select>");
//          select.change(function(){
//        	  _this.opt.pageSize = this.value;
//        	  _this.reload();
//          });
//          select.attr("value", _this.opt.pageSize);
//          info.append(select);
//      }
      var info2 = $('<div style=" float:right;">');
      var info3 = $('<div style="float:left;padding:8px 10px 0 0;"></div>');
      var info4 = $("<span style='padding-right:10px;font-size:14px;color:#666666' >"+lang['pageinfo4']+"</span>");
      var info5 = $("<span style='padding-left:10px;font-size:14px;color:#666666'>"+lang['pageinfo5']+"</span>");
      var select = $('<select value="5" style="width:60px; height:26px; line-height:26px; background:#E5E5E5; border:1px solid #D4D4D4;"><option selected="selected" value="5">5</option><option value="10">10</option><option value="20">20</option><option value="50">50</option></select>');
      select.change(function(){
        _this.opt.pageSize = this.value;
        _this.reload();
      });
      select.attr("value", _this.opt.pageSize);
      info3.append(info4);
      info3.append(select);
      info3.append(info5);
      if(_this.opt.showPageSizeChange){
        info2.append(info3);
      }
      //      _this.opt.pageSize

      footer.append(info);
//      footer.append(info2);
      if(_this.opt.paging){
        var from = _this.pageInfo.from || 0;
        var btnGroup = $('<div class="btn-group paging_full_numbers" style=" float:right"></div>');
        // 第一页
        var firstPage = $('<a class="btn btn-default disabled btn-boo">&lt;&lt;</a>');
        if(_this.pageInfo.curPage > 1){
          firstPage.removeClass("disabled");
          firstPage.click(function(){
            _this.loadTableData('first', loadParam);
          });
        }
        btnGroup.append(firstPage);

        // 前一页
        var prePage = $('<a class="previous btn btn-default disabled btn-boo">&lt;</a>');
        if(_this.pageInfo.curPage > 1){
          prePage.removeClass("disabled");
          prePage.click(function(){
            _this.loadTableData('pre', loadParam);
          });
        }
        btnGroup.append(prePage);

        if(_this.pageInfo.totalCount > 0){
          var pageNums = _this.getPageNumsToDisplay();
          for(var i = 0; i < pageNums.length; i++){
            var pageNum = pageNums[i];
            if(pageNum != -1){
              var page = $('<a class="btn btn-default btn-boo" page="' + pageNum + '">' + pageNum + '</a>');
              if((_this.pageInfo.curPage) == pageNum){
                page.removeClass("btn-boo").addClass("btn-sky"); // 选中当前的页
              }
              page.click(function(){
                _this.pageInfo.curPage = $(this).attr("page");
                $(this).removeClass("btn-boo").addClass("btn-sky");
                _this.loadTableData('goto', loadParam);
              });
              btnGroup.append(page);
            }else{
              // 省略号
              var span = $('<a class="btn btn-defualt btn-boo disabled">...</a>');
              btnGroup.append(span);
            }
          }
        }

        // 下一页
        var nextPage = $('<a class="btn btn-default disabled btn-boo">&gt;</a>');
        if(_this.pageInfo.curPage < _this.pageInfo.totalPage){
          nextPage.removeClass("disabled");
          nextPage.click(function(){
            _this.loadTableData('next', loadParam);
          });
        }
        btnGroup.append(nextPage);

       // 最后一页
       var lastPage = $('<a class="btn btn-default disabled btn-boo">&gt;&gt;</a>');
       if(_this.pageInfo.curPage < _this.pageInfo.totalPage){
         lastPage.removeClass("disabled");
         lastPage.click(function(){
           _this.loadTableData('last', loadParam);
         });
       }
       btnGroup.append(lastPage);
//         // GO TO
//         var goTo = $('<a class="btn btn-default btn-boo"></a>');
//         var goToBtn = $('<input type="button" value="Go" style="font-size:14px;margin-left:3px;width:40px;"/>');
//         goToBtn.click(function(){
//           var gotoPage = goToInput.attr("value");
//           if(gotoPage > 0 && gotoPage <= _this.pageInfo.totalPage){
//             _this.pageInfo.curPage = gotoPage;
//           }else{
//             if(gotoPage <= 0){
//               _this.pageInfo.curPage = 1;
//             }
//             if(gotoPage > _this.pageInfo.totalPage){
//               _this.pageInfo.curPage = _this.pageInfo.totalPage;
//             }
//           }
//           _this.loadTableData('goto', loadParam);
//         });
//         //var goToInput = $('<input type="text" style="width:30px;text-align:center;" value="' + _this.pageInfo.curPage + '"/>');
//         var goToInput = $('<input type="text"  onfocus="this.style.imeMode=\'disabled\'" onkeyup="value=value.replace(/[^\\d]/g,\'\')" onafterpaste="this.value=this.value.replace(/\\d/g,\'\')" style="width:30px;text-align:center;" value="' + _this.pageInfo.curPage + '"/>');
//         goToInput.bind("keypress", function(event){
//         	var keyCode = event.which;  
//         	if ((keyCode >= 48 && keyCode <=57)|| keyCode == 8){
//         		return true;
//         	} 
//         	else{
//         		return false;  
//         	}
//           //return _this.isValidPage(event);
//         });
//         goToInput.bind("keyup", function(event){
//         	this.value = this.value.replace(/s/g,'');
//         	return _this.isValidPage(event);
//         });
//         goToInput.bind("keydown", function(event){
//           var keyCode = event.which || event.keyCode;
//           if(keyCode == 13){
//             $(goToBtn).click();
//           }
//         });
//         goTo.append('<span>' + lang["iDataTableGo"] + '</span>');
//         goTo.append(goToInput);
//         goTo.append('<span>' + lang["iDataTablePage"] + '</span>');
//         goTo.append(goToBtn);
       info2.append(btnGroup);
        footer.append(info2);
      }
      _this.parent().append(footer);
    };

    //分页信息
    this.pageInfo = {
      from:0,
      to:0,
      totalCount:0,
      curPage:1,
      totalPage:1
    };

    this.loadParam = null;
    this.isLoading = false; // 是否正在加载 -- 防止重复load
    this.isAutoLoading = false; // 是否正在自动重新加载

    this.tableData = {}; // 表格数据
    this.loaded = false;
    this.loadTableData = function(mode, loadParam){
      if(_this.isLoading){
        return;
      }
      _this.loaded = true;
      _this.isLoading = true;
      if(_this.parent()){
        _this.parent().mask(_this.opt.loadingMsg, 300);
      }else{
        _this.mask(_this.opt.loadingMsg, 300);
      }
      var start = 0, limit = 99999;
      var pageSize = _this.opt.pageSize;
      var curPage = (_this.pageInfo.curPage || 1) * 1; // 当前页数
      var totalPage = (_this.pageInfo.totalPage || 1) * 1;;// 总页数
      switch(mode){
        case "init":curPage = 1;break;
        case "reload":break;
        case "first":curPage = 1;break;
        case "pre":curPage = ((curPage-1 >= 1)?(curPage-1):curPage);break;
        case "next":curPage = ((curPage+1 <= totalPage)?(curPage+1):curPage);break;
        case "last":curPage = totalPage;break;
        case "goto":break;
        default:curPage = 1;break;
      }
      start = pageSize * (curPage - 1);
      limit = pageSize;

      loadParam = loadParam || _this.loadParam || this.opt.defaultLoadParam || {};
      _this.loadParam = loadParam;
      if(loadParam.restUrl){
          _this.opt.url = "rest.do";
          if(!loadParam.method){
            loadParam.method = "GET";
          }
          if(loadParam.originalRestUrl){
            loadParam.restUrl = loadParam.originalRestUrl;
          }else{
            loadParam.originalRestUrl  = loadParam.restUrl;
          }
//          if(loadParam.restUrl.indexOf("{start}") != -1
//            || loadParam.restUrl.indexOf("{limit}") != -1
//            || loadParam.restUrl.indexOf("{page}") != -1
//            || loadParam.restUrl.indexOf("{pageSize}") != -1
//            || loadParam.restUrl.indexOf("{field}") != -1
//            || loadParam.restUrl.indexOf("{dir}") != -1)
//          {
//            loadParam.restUrl = loadParam.restUrl.replace("{start}", start);
//            loadParam.restUrl = loadParam.restUrl.replace("{limit}", limit);
//            loadParam.restUrl = loadParam.restUrl.replace("{page}", (start/limit) + 1);
//            loadParam.restUrl = loadParam.restUrl.replace("{pageSize}", limit);
//            // 计费集成兼容
//            loadParam.restUrl = loadParam.restUrl.replace("{field}", _this.sortInfo.sort);
//            loadParam.restUrl = loadParam.restUrl.replace("{dir}", _this.sortInfo.dir);
//          }
      }
      
//      loadParam.start = start;
//      loadParam.limit = limit;
//      loadParam.sort = _this.sortInfo.sort;
//      loadParam.dir = _this.sortInfo.dir;
      
      loadParam.remoteIp = "", // 调用远程rest接口IP地址
      loadParam.remotePort = "", // 调用远程rest接口端口

      loadParam.data = loadParam.data || {};
      loadParam.data.param = loadParam.data.param || {};
      loadParam.data.param.page = (start/limit) + 1;
      loadParam.data.param.pageSize = limit;
      loadParam.data.param.dir = _this.sortInfo.dir;
      loadParam.data.param.field = _this.sortInfo.sort;
      //设置超时控制
      loadParam.timeoutControl = _this.opt.timeoutControl;

      // TODO del
      if(loadParam.restUrl && loadParam.restUrl.indexOf("imonitor") != -1){
        loadParam.data.param.sort = _this.sortInfo.sort;
      }
      var reg = /^[\u4E00-\u9FA5]+$/; 
      for(var key in loadParam.data.param){
        if(reg.test(loadParam.data.param[key])){ 
          loadParam.data.param[key] =  encodeURIComponent(loadParam.data.param[key]);
      } 
    }
//      loadParam.data.body = loadParam.data.body || {};
//      loadParam.data.body.reqarg = loadParam.data.body.reqarg || {};
//      loadParam.data.body.reqarg.page = loadParam.data.param.page;
//      loadParam.data.body.reqarg.pageSize = loadParam.data.param.pageSize;
//      loadParam.data.body.reqarg.field = loadParam.data.param.field;
//      loadParam.data.body.reqarg.dir = loadParam.data.param.dir;

      // 设置sessionID
      var sessionID = $.cookie("JSESSIONID");
      loadParam.sessionID = sessionID;

      var tempData = loadParam.data;
      var jsonData = $.toJSON(loadParam.data);
      loadParam.data = jsonData;
      $.ajax({
        type:"post",
        data:loadParam,
        dataType:"json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        async: true,
        url:_this.opt.url,
        success:function(response){
          if(_this.parent()){
            _this.parent().unmask();
          }else{
            _this.unmask();
          }

          _this.isLoading = false;

          var result = response["result"];
          var datas = response["data"] || [];
          var total = response["total"] || 0;

          var flag = response["flag"];
          if(flag == undefined){
            flag = true;
          }
          var errCode = response["errCode"];
          var restData = response["resData"];// REST数据接口集成

          if(restData){
            datas = restData["Info"] || restData["list"] || restData["data"] || restData || [];
            total = restData["Size"] || restData["totalResults"] || restData["total"] || 0;
          }
          if(result == "failed" || !flag || flag == false || flag == "false"){
            if((!flag || flag == false || flag == "false") && !errCode){
              errCode = "Flag is false but no error code returned in errCode param";
            }
            showErrorMsg(errCode);
            _this.opt.onPageDataLoaded(datas); // 加载完表格数据后的回调 -- 当前页数据
          }else{
            if(datas){
              // 当前页请求无数据，获取前一页数据
              if(total > 0 && datas.length <= 0 && curPage > 1){
            	  if(curPage>Math.ceil(total/pageSize)){
            		  _this.pageInfo.curPage = Math.ceil(total/pageSize);
            		  _this.loadTableData('goto', loadParam);
            	  }else{
            		  _this.loadTableData('pre', loadParam);
            	  }
                return;
              }
              _this.updateTableData(datas, total);
            }else{
              _this.updateTableData([], total);
            }
          }
          _this.opt.onPageDataLoadedResData(restData); // 加载完表格数据后的回调 -- resData里的所有数据
          _this.updateDisplay();

          // 自动重新加载
          /*
           * reloadDataKey:"", // 刷新监测的datakey
           * reloadValue:"", // 刷新监测的datakey对应的取值
           * reloadTime:5000, // 刷新间隔ms
           */
          if(_this.opt.autoReload 
             && _this.opt.needReload
             && !_this.isAutoLoading){
            var isNeedReload = false;
            if(datas){
              for(var index in datas){
                var rowData = datas[index];
                if(rowData && rowData != "null"){
                  if(_this.opt.needReload(rowData)){
                    isNeedReload = true;
                    break;
                  }
                }
              }
            }
            // 需要重新加载并且表格可见的场合，重新请求数据 20131023 gaofei 修改Cathy性能问题
            if(isNeedReload && _this.is(":visible")){
              // 重新加载表格数据
              _this.isAutoLoading = true;
              setTimeout(function(){
                // 20131023 gaofei 修改Cathy性能问题
                if(!_this.stopReq && _this.is(":visible")){
                  _this.reload();
                  _this.isAutoLoading=false;
                }
              },
              _this.opt.reloadTime);
            }
          }
        },
        error:function(){
          if(_this.parent()){
            $.unmaskElement(_this.parent());
          }else{
            $.unmaskElement(_this);
          }
          _this.updateDisplay();
          _this.isLoading = false;
        }
      });
      loadParam.data = tempData;
    };

    /**
     * 更新表格数据
     *
     * @param datas 当前页数据
     * @param total 总记录数
     */
    this.updateTableData = function(datas, total){
      if(!_this.opt.keepSelection){
        _this.clearAllSelection();
      }
      var from = 0, to = 0;
      var start = _this.loadParam.start;
      from = (!datas.length || datas.length <= 0)?0:(start + 1);
      to = start + (datas.length || 0);
      if(total <= 0){
        total = to = from = 0;
      }
      var header, headers = _this.headerInfo;
      var datakey = null, render = null, align = null, style = null, cls, value = null, ignoreRowClick;
      var tr = null, td = null;
      
      // 清空表数据
      _this.find("tbody").remove();
      _this.currentPageData = []; // 清空表格当前页数据记录
      var tbody = $("<tbody></tbody>");
      _this.append(tbody);
      var count = 0;
      // 修正引入jtopo-0.3.6-min.js后引起的冲突问题
      // jtopo定义了Array.prototype.del函数
      // 导致下面循环使用in的时候，会把该函数也当成一条数据来处理 for(var index in datas){
      // 所以会出现多一行的现象，修改为使用length进行循环解决了该问题
      for(var index = 0; index < datas.length; index++){
        var data = datas[index];
        if(data && data != "null"){
          _this.tableData[data[_this.opt.rowDataId]] = data;
          _this.currentPageData.push(data); // 保存表格当前页数据
          tr = $("<tr rowDataId='" + data[_this.opt.rowDataId] + "'></tr>");
          // 设置行背景色
          if (count % 2 == 0) {
              tr.addClass("odd");
          } else {
              tr.addClass("even");
          }
          count++;

          var rowStyle = _this.opt.rowStyle(data);
          if(rowStyle != null){
            tr.addClass(rowStyle);
          }
          // 设置行数据标号
          tr.attr("index", index);
          // 行存在数据的标志
          tr.attr("hasData", true);
          tbody.append(tr);
          if(_this.opt.onRowDblClick && (typeof _this.opt.onRowDblClick == "function")){
            tr.dblclick(function(){
              var data = datas[$(this).attr("index")];
              _this.opt.onRowDblClick(data);
            });
          }
          for(var j = 0; j < headers.length; j++){
            td = $("<td></td>");
            header = $(headers[j]);
            value = "";
            type = header.attr("type");
            if(type && type == "checkbox"){
              tr.append(td);
              td.attr("align", "center");
              var checkbox = $("<input type='checkbox' iDataTableChkId='" + data[_this.opt.rowDataId] + "' style='margin-top:0px;'/>");
              checkbox.click(_this.selectRow);
              td.append(checkbox);
            }else if(type && type == "radio"){
              tr.append(td);
              td.attr("align", "center");
              td.click(function(){
                // 单选框单选处理
                _this.clearAllSelection();
                _this.recordRowSelect($(this).parent().attr("rowDataId"), true, true);
                if(_this.opt.onRowClick && (typeof _this.opt.onRowClick == "function")){
                  var rowData = datas[$(this).parent().attr("index")];
                  _this.opt.onRowClick(rowData);
                }
              });
              var radio = $("<input type='radio' iDataTableRadioId='" + data[_this.opt.rowDataId] + "'/>");
              radio.click(function(){
                // $(this).parent().click(); // 触发TD的点击事件
              });
              td.append(radio);
            }else{
              datakey = header.attr("datakey");
              if(datakey){
                tr.append(td);
                value = data[datakey];
                render = header.attr("render");
                td.attr("align", _this.dataAlignInfo[datakey]);
                
                style = header.attr("_style");
                if(style){
                  td.attr("style", style);
                  td.width(0);
                }
                cls = header.attr("_class");
                if(cls){
                  td.attr("class", cls);
                }
                var ignoreRowClick = header.attr("ignoreRowClick");
                if(!ignoreRowClick){
                  td.click(function(){
                    var curRow = $(this).parent();
                    var hasData = $(curRow).attr("hasData");
                    if(hasData && hasData == "true" && (_this.isRowSelectable(curRow))){
                      if(_this.multiSel){
                        // 复选框多选
                        // 获取当前行的复选框 ID
                        var curRowChk = curRow.find("[iDataTableChkId]");
                        var curRowChkId = curRowChk.attr("iDataTableChkId");
                        // 改变当前行的选中状态
                        var curRowChecked = (curRowChk.attr("checked")?false:true);
                       // 获取当前选中的记录数
                        var curSelectedRowCount = _this.getSelectedRecords().length;
                        if(curSelectedRowCount > 1){
                          curRowChecked = true;
                        }
                        // 获取所有复选框
                        var allChk = _this.find("[iDataTableChkId]");
                        // 清空所有选择项
                        _this.selectedRecords = new Array();
                        var rowChkId;
                        var index = 0, chk;
                        for(index = 0; index < allChk.length; index++){
                          chk = $(allChk[index]);
                          rowChkId = chk.attr("iDataTableChkId");
                          _this.recordRowSelect(rowChkId, false);
                        }
                        // 设置当前行复选框状态
                        _this.recordRowSelect(curRowChkId, curRowChecked, true);
                        if(_this.opt.onRowClick && (typeof _this.opt.onRowClick == "function")){
                          var rowData = datas[$(this).parent().attr("index")];
                          _this.opt.onRowClick(rowData);
                        }
                      }else if(_this.singleSel){
                        // 单选框单选处理
                        _this.clearAllSelection();
                        _this.recordRowSelect($(this).parent().attr("rowDataId"), true, true);
                        if(_this.opt.onRowClick && (typeof _this.opt.onRowClick == "function")){
                          var rowData = datas[$(this).parent().attr("index")];
                          _this.opt.onRowClick(rowData);
                        }
                      }else{
                        // 非选复选、单选列表场合的处理
                        if(_this.opt.selectable){
                          _this.clearAllSelection();
                          _this.recordRowSelect($(this).parent().attr("rowDataId"), true, true);
                          // 响应行单击事件
                          if(_this.opt.onRowClick && (typeof _this.opt.onRowClick == "function")){
                            var rowData = datas[$(this).parent().attr("index")];
                            _this.opt.onRowClick(rowData);
                          }
                        }
                      }
                    }
                  });
                }
                if(type && (type == "text" || type == "password")){
                  var validation = header.attr("validation");
                  var promptPosition = header.attr("promptPosition");
                  var form = $("<form></form>");
                  var input = $("<input type='" + type + "' validation='" + validation + "' datakey='" + datakey + "' value='" + value + "'>");
                  form.append(input);
                  form.validationEngine({
//                    validationEventTrigger:"keyup",
                    promptPosition:promptPosition || 'bottomLeft',
                    scroll:false
                  });
                  input.bind("keyup", function(){
                    var isValid = $(this).parent().validationEngine('validate');
                    var tr = $(this).parent().parent().parent();
                    if(isValid != tr.attr("isDataValid")){
                      // 设置数据是否有效的标识
                      tr.attr("isDataValid", isValid);

                      tr.trigger("mouseleave");
                      tr.trigger("mouseenter");
                      _this.updateDisplay();
                    }

                    if(isValid){
                      var rowDataId = tr.attr("rowDataId");
                      var datakey = $(this).attr("datakey");
                      var value = $(this).attr("value");
                      var rowData = _this.tableData[rowDataId];
                      rowData[datakey] = value;
                    }
                  });
                  td.append(form);
                  if(render){
                    value = eval(render + "(value, data, tr, td)");
                    input.attr("value", value);
                  }
                }else{
                  if(render){
                    value = eval(render + "(value, data, tr, td)");
                  }
                  td.html(value);
                }
              }
            }
          }
          if(_this.opt.showInlineMenu && _this.opt.operateDiv){
            var opeDiv = $("#" + _this.opt.operateDiv);
            var width = opeDiv.width();
            var height = tr.height() - 2;
            // 获取第一列之后的长度
            var firstColWidth = 0, marginWidth = 0;
            for(var index = 0; index < _this.headerInfo.length; index++){
              header = $(_this.headerInfo[index]);
              hide = header.attr("hide");
              type = header.attr("type");
              if((!hide || hide != "true") && (!type && type != "checkbox" && type != "radio")){
                if(firstColWidth == 0){
                  firstColWidth = header.width();
                  continue;
                }
                marginWidth += Math.floor(header.width() + 6);
              }
            }
            // marginWidth = Math.floor((_this.totalWidth + width) / 2);
            var inlineMenuContainer = $('<div></div>');
            inlineMenuContainer.attr("style",
                                     "margin-top:1px; "
                                   + "margin-left:" + (-_this.totalWidth) + "px; "
                                   + "width:" + (width + 40) + "px; "
                                   + "opacity:1; "
                                   + "height:" + height + "px; "
                                   );
            var inlineMenuDiv = $('<div id="inlineOpe" class="operate filter inline-commands" style="display: none;"></div>');

            var ul = opeDiv.find("ul");
            var li = ul.find("li");
            if(li && li.length > 0){ // 实际上有菜单的场合才显示
              var ulClone = $(ul).clone();
              var maginTop = (height - 20) / 2;
              ulClone.css("margin-top", (maginTop > 0 ? maginTop : 0) + "px");
              ulClone.css("height", (height - maginTop) + "px");
              inlineMenuDiv.append(ulClone);
              inlineMenuContainer.append(inlineMenuDiv);
              // 判断浏览器版本 -- 对于IE10以下版本添加clear-both
              var documentMode = document.documentMode || 1;
              if (documentMode < 10) {
                tr.append('<div class="clear-both">&nbsp;</div>');
              }
              tr.append(inlineMenuContainer);
            }
          }
        }
      }

      var ignoreEmptyRow = _this.opt.ignoreEmptyRow;
      if(!ignoreEmptyRow){
        var datasLen = datas.length || 0;
         // 数据不够一页时，补全空白行
        if(datasLen < _this.opt.pageSize){
          for(var k = 0; k < _this.opt.pageSize - datasLen; k++){
            tr = $("<tr></tr>");
            // 设置行背景色
            if (count % 2 == 0) {
              tr.addClass("odd");
            } else {
              tr.addClass("even");
            }
            count++;
            tbody.append(tr);
            for(var l = 0; l < _this.headerInfo.length; l++){
              header = $(_this.headerInfo[l]);
              td = $("<td>&nbsp;</td>");
              var hide = header.attr("hide");
              if(hide && hide == "true"){
                td.attr("style", "display:none;");
              }
              tr.append(td);
            }
          }
        }
      }
      var totalPage = Math.ceil(total / _this.opt.pageSize);// 总页数
      if(totalPage <= 0){
        totalPage = 1;
      }
      _this.pageInfo.curPage = _this.loadParam.data.param.page || _this.pageInfo.curPage || 1;
      _this.pageInfo.totalPage = totalPage;
      // 刷新后页数变更的处理
      if(_this.pageInfo.curPage > _this.pageInfo.totalPage){
        _this.pageInfo.curPage = _this.pageInfo.totalPage;
      }
      _this.pageInfo.totalCount = total;
      _this.pageInfo.from = from;
      _this.pageInfo.to = to;

      // 生成表尾
      _this.addFooter(_this.loadParam);

//      _this.find("tr:even").addClass("even");//偶数行的背景色
//      _this.find("tr:odd").addClass("odd");//奇数行的背景色
      _this.opt.onPageDataLoaded(datas); // 加载完表格数据后的回调 -- 当前页数据
    };


    this.selectedRecords = new Array(); // 选中的记录

    /**
     * 记录行选择状态
     */
    this.recordRowSelect = function(rowDataId, checked, updateDisplay){
      _this.selectedRecords[rowDataId] = checked;
      if(updateDisplay == true){
        _this.updateDisplay();
      }
    };

    /**
     * 清空所有选中记录
     */
    this.clearAllSelection = function(){
      _this.selectedRecords = new Array();
      _this.updateDisplay();
    };

    /**
     * 选择所有行
     */
    this.selectAllRow = function(){
      var checked = $(this).attr("checked")?true:false;
      // 复选框选中状态
      var allChk = _this.find("[iDataTableChkId]");
      var rowChkId, chk;
      var row;
      for(index = 0; index < allChk.length; index++){
        chk = $(allChk[index]);
        rowChkId = chk.attr("iDataTableChkId");
        row = $(chk).parent().parent();
        if(row){
          if(_this.isRowSelectable(row)){
            _this.recordRowSelect(rowChkId, checked);
          }
        }else{
          _this.recordRowSelect(rowChkId, checked);
        }
      }
      _this.updateDisplay();
    };

    /**
     * 选中某一行
     */
    this.selectRow = function(){
      var checked = $(this).attr("checked");
      var rowChkId = $(this).attr("iDataTableChkId");
      _this.recordRowSelect(rowChkId, checked, true);
    };

    /**
     * 收集表格数据 -- 文本框等
     */
    this.collectTableData = function(){
//      var allRow = _this.find("tbody tr");
//      var index = 0, row, rowDataId, rowData;
//      var inputs, input;
//      var datakey, value;
//      for(var index = 0; index < allRow.length; index++){
//        row = $(allRow[index]);
//        if(row.attr("hasData")){
//          // 获取表格行数据引用
//          rowDataId = row.attr("rowDataId");
//          rowData = _this.tableData[rowDataId];
//          // 查找行内的文本框
//          inputs = row.find("[type=text]");
//          if(inputs && inputs.length > 0){
//            // 保存文本框数据
//            for(var i = 0; i < inputs.length; i++){
//              input = inputs[i];
//              datakey = input.attr("datakey");
//              value = input.attr("value");
//              rowData[datakey] = value;
//            }
//          }
//        }
//      }
    };

    /**
     * 判断指定行是否可选择(selectable属性)
     * 未设置是否可选 -- 默认可选
     * 设置是否可选 -- 值为true
     * 
     * @param row
     * @returns {Boolean}
     */
    _this.isRowSelectable = function(row){
      var selectable = row.attr("selectable");
      return (!selectable || selectable == "true");
    };

    /**
     * 更新表格操作显示 -- 过滤条件与行数据操作间的切换
     */
    this.updateDisplay = function(){
      // 更新选择状态的显示
      // 行选中样式
      var allRow = _this.find("tbody tr");
      var index = 0, row, rowChk, checked;
      var hasNotCheckedRowInCurPage = false;
      var hasCheckedRowInCurPage = false; // 存在选中的有效数据行
      var isAllSelectRowValid = true; // 是否所有选中的行数据都有效
      for(var index = 0; index < allRow.length; index++){
        row = $(allRow[index]);
        if(row.attr("hasData") && (_this.isRowSelectable(row))){
          rowDataId = row.attr("rowDataId");
          checked = _this.selectedRecords[rowDataId]?true:false;
          
          // 复选列表
          rowChk = row.find("[iDataTableChkId]");
          if(rowChk.length > 0){
            rowChk.attr("checked", checked);
            if(!checked){
              hasNotCheckedRowInCurPage = true;
            }else{
              hasCheckedRowInCurPage = true;
            }
          }

          // 单选列表
          rowRadio = row.find("[iDataTableRadioId]");
          if(rowRadio.length > 0){
            rowRadio.attr("checked", checked);
          }

          if(checked){
            row.addClass("selectedrow");
            // 是否所有选中的行数据都有效
            if(isAllSelectRowValid){
              var isDataValid = row.attr("isDataValid");
              if(isDataValid == false || isDataValid == "false"){
                isAllSelectRowValid = false;
              }
            }
          }else{
            row.removeClass("selectedrow");
          }
        }
      }
      if(_this.multiSel){
        // 更新表头复选框状态
        // 有数据的场合才把复选框置为选中状态 -- 解决没有数据场合复选框默认选中的问题
        _this.find("#iDataTableChk").attr("checked", _this.hasSelectedRecords() && !hasNotCheckedRowInCurPage && hasCheckedRowInCurPage);
      }

      // 更新表格操作的显示
      var filterDiv, operateDiv;
      if(_this.opt.filterDiv){
        filterDiv = $("#" + _this.opt.filterDiv);
      }
      if(_this.opt.operateDiv){
        operateDiv = $("#" + _this.opt.operateDiv);
      }
      if(_this.hasSelectedRecords()){
        if(filterDiv && operateDiv){
          // 有过滤条件、操作的场合
          filterDiv.attr("style", "display:none;");
          operateDiv.attr("style", "display:block;");
          if(!($(operateDiv).find("li").length > 0)){
            // 操作列表为空
            filterDiv.attr("style", "display:block;");
          }
        }
        if(!filterDiv && operateDiv){
          // 无过滤条件的场合
          operateDiv.attr("style", "display:block;");
        }

        var records = _this.getSelectedRecords();
        var recordsCount = records.length;
        // 操作按钮状态的制御
        var operate, btn, canEnable, rowData;
        for(var i = 0; i < _this.opt.operate.length; i++){
          operate = _this.opt.operate[i];
          if(operateDiv && operate && operate.id){
            btn = operateDiv.find("#" + operate.id);
            if(btn){
              if(isAllSelectRowValid){
                if(operate.multi){// 支持批量操作
                  if(operate.enable){ // 有操作启用条件
                    canEnable = true;
                    for(var j =0; j < recordsCount; j++){
                      rowData = _this.tableData[records[j]];
                      if(!operate.enable(rowData || {})){
                        canEnable = false;
                        break;
                      }
                    }
                    if(canEnable){
//                      btn.addClass("btn-operat");
                      btn.removeClass("disabled");
                    }else{
//                      btn.removeClass("btn-operat");
                      btn.addClass("disabled");
                    }
                  }else{// 无操作启用条件 -- 直接启用
//                    btn.addClass("btn-operat");
                    btn.removeClass("disabled");
                  }
                }else{// 不支持批量操作
                  if(recordsCount > 1){ // 选中多条记录
//                    btn.removeClass("btn-operat");
                    btn.addClass("disabled");
                  }else{
                    if(operate.enable){// 有操作启用条件
                      canEnable = true;
                      for(var j = 0; j < recordsCount; j++){
                        rowData = _this.tableData[records[j]];
                        if(!operate.enable(rowData || {})){
                          canEnable = false;
                          break;
                        }
                      }
                      if(canEnable){
//                        btn.addClass("btn-operat");
                        btn.removeClass("disabled");
                      }else{
//                        btn.removeClass("btn-operat");
                        btn.addClass("disabled");
                      }
                    }else{// 无操作启用条件 -- 直接启用
//                      btn.addClass("btn-operat");
                      btn.removeClass("disabled");
                    }
                  }
                }
              }else{ // 如果不是所有选中的行数据都有效，则按钮不可操作
//                btn.removeClass("btn-operat");
                btn.addClass("disabled");
              }
              // 绑定按钮事件
              if(!btn.hasClass("disabled")){
                if(operate.action){
                  btn.unbind("click");
                  btn.bind("click", operate.action, function(event){
                    var selectedRowData = _this.getSelectedRowData();
                    var action = event.data;
                    action({
                      data:selectedRowData
                    });
                  });
                  // 如果需要防止连续点击，则触发点击事件后：将按钮置为不可用，并且去除事件绑定
                  if(_this.opt.preventContinuousClick == true){
                    btn.bind("click", function(){
//                      btn.removeClass("btn-operat");
                      btn.addClass("disabled");
                      btn.unbind("click");
                    });
                  }
                }
              }else{
                btn.unbind("click");
              }
            }
          }
        }// end of for
      }else{
        if(filterDiv && operateDiv){
          filterDiv.attr("style", "display:block;");
          operateDiv.attr("style", "display:none;");
        }
        if(!filterDiv && operateDiv){
          operateDiv.attr("style", "display:block;");
          // 操作按钮状态的制御
          var operate, btn, canEnable, rowData;
          for(var i = 0; i < _this.opt.operate.length; i++){
            operate = _this.opt.operate[i];
            if(operate && operate.id){
              btn = operateDiv.find("#" + operate.id);
              if(btn){
//                btn.removeClass("btn-operat");
                btn.addClass("disabled");
                btn.unbind("click");
              }
            }
          }
        }
      }

      // 更新表格尾部信息的显示
      var info = "";
      if(_this.multiSel){
        if(_this.opt.showSelectedNum){
          if(_this.opt.showTotalNum){
            info = lang["iDataTableTotal"] + " "  + _this.pageInfo.totalCount + " " + lang["iDataTableRecords"];
          }
          if(info){
            info = info + "，" + lang["iDataTableCurrentSelect"] + " " + _this.getSelectedRecords().length + " " + lang["iDataTableRecord"];
          }else{
            info = lang["iDataTableCurrentSelect"] + " " + _this.getSelectedRecords().length + " " + lang["iDataTableRecord"];
          }
        } else {
          if(_this.opt.showTotalNum){
            info = lang["iDataTableTotal"] + " "  + _this.pageInfo.totalCount + " " + lang["iDataTableRecords"];
          }
        }
      }else if(_this.singleSel){
        if(_this.opt.showTotalNum){
          info = lang["iDataTableTotal"] + " "  + _this.pageInfo.totalCount + " " + lang["iDataTableRecords"];
        }
      }else{
        if(_this.opt.showTotalNum){
          info = lang["iDataTableTotal"] + " " + _this.pageInfo.totalCount + " " + lang["iDataTableRecords"];
        }
      }
      _this.parent().find("#info").html(info);
    };
    this.copyParam = function(oldParam, newParam){
      oldParam = oldParam || {};
      newParam = newParam || {};
      if(newParam.restUrl){
        oldParam.originalRestUrl = null;
      }
      for(var key in newParam){
        oldParam[key] = newParam[key];
      }
      return oldParam;
    };
    /**
     * 加载空的数据表格
     */
    this.load = function(loadParam, forceLoad){
      if(_this.loaded && !forceLoad){
        // _this.reload(); do nothing for wizard use only
      }else{
        this.loadTableData('init', _this.copyParam(_this.loadParam, loadParam) || {});
      }
    };

    this.reload = function(loadParam){
      this.loadTableData('reload', _this.copyParam(_this.loadParam, loadParam) || {});
    };

    /**
     * 添加行数据
     */
    this.appendRow = function(rowData){
      // TODO
    };

    /**
     * 删除行
     */
    this.delRow = function(rowDataId){
      // TODO
    };

    /**
     * 设置选中的行 -- 参数为rowDataId的数组
     */
    this.setSelectedRows = function(rowDataIds){
      if(rowDataIds && rowDataIds.length > 0){
        var rowDataId;
        for(var index = 0; index < rowDataIds.length; index++){
          rowDataId = rowDataIds[index];
          _this.selectedRecords[rowDataId] = true;
        }
        _this.updateDisplay();
      }
    };

    /**
     * 是否存在选中的记录
     */
    this.hasSelectedRecords = function(){
      for(var record in _this.selectedRecords){
        if(_this.selectedRecords[record]){
          return true;
        }
      }
      return false;
    };

    /**
     * 获取当前表格所有选中的记录 -- id列表
     */
    this.getSelectedRecords = function(){
      var records = new Array();
      for(var record in _this.selectedRecords){
        if(_this.selectedRecords[record]){
          records.push(record);
        }
      }
      return records;
    };

    /**
     * 获取当前表格所有选中的记录 -- 所有行数据
     */
    this.getSelectedRowData = function(){
      var selectedRowData = [];

      var records = _this.getSelectedRecords();
      var recordsCount = records.length;
      var tbody = _this.find("tbody");
      var rowData, row, rowDataId, select, datakey, value;
      for(var r = 0; r < recordsCount; r++){
        rowDataId = records[r];
        rowData = _this.tableData[rowDataId];
        if(rowData){
         // 获取下拉框的值
            row = tbody.find("[rowDataId='" + rowDataId + "']");
            selectList = row.find("select");
            if(selectList.length > 0){
              for(var index = 0; index < selectList.length; index++){
                var select = $(selectList[index]);
                if(select){
                  datakey = select.attr("datakey");
                  value = select.attr("value");
                  rowData[datakey] = value;
                }
              }
            }

            selectedRowData.push(rowData);
        }
      }
      return selectedRowData;
    };

    _this.stopReq = false; // 自动刷新标识
    /**
     * 手动停止自动刷新
     */
    this.stopRequest = function(){
      _this.stopReq = true;
    };
    _this.currentPageData = [];
    /**
     * 获取表格当前页的记录 -- 行数据
     */
    this.getCurrentPageData = function(){
      return _this.currentPageData;
    };
    //////////////////////////////////////////////////////////////////////////
    this.initTable();
    if(this.opt.autoLoad){
      this.load();
    }
    $(this).colResizer();

    return this;
  };
})(jQuery);
