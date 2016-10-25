/*
  作者：gao.fei
  参考自tablegrid和tableresizer
  功能
      1.奇偶行不同色, 鼠标滑过颜色效果, 点击高亮
      2.列宽可拖动
      3.双击事件，在每行第一列取a的href值
 */
(function($) {
  $.fn.iTable = function(options){
    $.fn.iTable.defaults = {
      oddColor   : '#C4E1FF',
      evenColor  : '#F2F9FD',
      overColor  : '#C7C7E2',
      selColor   : '#336666',
      useClick   : true,
      useDblClick:false,
      col_border : "solid 1px #B9B4A1"
    };
    
    var opts = $.extend({}, $.fn.iTable.defaults, options); 
    //拖动列宽
    var resize_columns = function(root) {
      var tbl = root.children("table"); //找到table
      var tr = tbl.find("tr:first"); //找到第一行
      var header, nWidth;
      var resize = false;
      
      root.width(tbl.width()); //table的宽度
      tr.children("th").css("border-right", opts.col_border);  //给第一行的th加上一个css
      
//      tr.find("th").bind("mousemove", function(event) {
//        var th = $(this);
//        //不给第一列和最后一列添加效果
//        if(th.prevAll().length <= 1 || th.nextAll().length < 1){
//          return;
//        }
//        var left = th.offset().left;
//        //距离表头边框线左右4像素才触发效果
//        if (event.clientX - left < 4 || (th.width() - (event.clientX - left)) < 4) {
//          th.css({ 'cursor': 'col-resize' });
//          //修改为你的鼠标图标路径
//        }else{
//          th.css({ 'cursor': 'default' });
//        }
//      });
//      
//      tr.find("th").bind("mousedown", function(event){
//        var th = $(this);
//        //与mousemove函数中同样的判断
//        if (th.prevAll().length < 1 | th.nextAll().length < 1) {
//          return;
//        }
//        var pos = th.offset();
//        if (event.clientX - pos.left < 4 || (th.width() - (event.clientX - pos.left)) < 4) {
//          var height = th.parent().parent().height();
//          var top = pos.top;
////          $("#line").css({ "height": height, "top": top,"left":event .clientX,"display":"" });
//          //全局变量，代表当前是否处于调整列宽状态
//          lineMove = true;
//          //总是取前一个TH对象
//          if (event.clientX - pos.left < th.width() / 2) {
//            currTh = th.prev();
//          }else{
//            currTh = th;
//          }
//        }
//      });
      var left_pos = root.offset().left;
      endresize = function(){
    	if(resize == true && header != null){
          document.onselectstart = new Function ("return true");
          resize = false;
          root.children("table").css("cursor", "");
          header = null;
        }
      };
      
      tbl.mousemove(function(e){
    	if(resize){
          var width = e.clientX - header.offset().left;
          if(width > 1){
            var current_width = header.width();
            if(width > current_width){
//              var total = root.width() + ((width - header.width()));
//              root.width(total);
              header.width(width);
            } else {
              header.width(width);
//              if(header.width() == width){
//                var total = root.width() + ((width - current_width));
//                root.width(total);
//              }
            }
            nWidth = width;
          }
        } else {
          if(e.target.nodeName == "TH"){
            var tgt = $(e.target);
            var dosize = (left- (tgt.offset().left - left_pos) > tgt.width()-4);
            $(this).css("cursor", dosize ? "col-resize" : "");
          }
        }
      });
      
      tbl.mouseup(function(e){
        endresize();
      });
      
      tbl.bind("mouseleave", function(e){
        endresize();
        return false;
      });
      
      tr.mousedown(function(e){
        if(e.target.nodeName == "TH" && $(this).css("cursor") ==  "col-resize"){
          header = $(e.target);
          resize = true;
          document.onselectstart = new Function ("return false");
        }
        return false;
      });
      
      tr.bind('mouseleave',function(e){
        if(!resize){
          root.children("table").css("cursor","");
        }
      });
    };
    
    return this.each(function(){
      var root = $(this).wrap("<div class='roottbl' />").parent();
      resize_columns(root);
      
      //奇偶行上色
      $(this).find('tr:odd > td').css('backgroundColor', opts.oddColor);
      $(this).find('tr:even > td').css('backgroundColor', opts.evenColor);        
      $(this).find('tr').each(function(){
        //--------------------------------------this为tr------------------------------------------
        this.origColor = $(this).find('td').css('backgroundColor');    //未点击时的颜色
        this.clicked = false;   //初始状态,设置bool变量clicked,以click事件触发此变量的true or false
        if(opts.useClick){
          $(this).click(function(){  //此处的this是tr
            if(this.clicked){
              $(this).find('td').css('backgroundColor', this.origColor);
              this.clicked = false;
            }else{
              $(this).find('td').css('backgroundColor', opts.selColor);
              this.clicked = true;
            }
            //$(this).find('td > input[@type=checkbox]').attr('checked', this.clicked);
          });
        }
        
        //鼠标滑过及滑出事件
        $(this).mouseover(function(){
          $(this).find('td').css('backgroundColor', opts.overColor);
        });
        
        $(this).mouseout(function(){
          if(this.clicked){
            $(this).find('td').css('backgroundColor', opts.selColor);
          }else{
            $(this).find('td').css('backgroundColor', this.origColor);
          }
        });
        
        //双击事件
        if(opts.useDblClick){
          var urls=$(this).children("td:first-child").find("a").attr("href");
          $(this).dblclick(function(){
            // window.open (urls,'详细情况','height=540,width=1020,top=10,left=10,toolbar=no,menubar=no,scrollbars=yes, resizable=yes,location=no, status=no')
            
          });
        }
        //--------------------------------------this为tr------------------------------------------
      });
    });
  };
})(jQuery);