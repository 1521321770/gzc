/**
 * jQuery ColResizer Date 13/11/2012
 * 
 * @author Victor Gao <gaoht1987@gmail.com>
 * 
 * A lightweight plugin that creates resizable tables
 * 
 */

(function($) {
	$.fn.colResizer = function(options) {
		$.fn.colResizer.defaults = {
			// the border style of the two reference lines
			resize_line_border : {
				"border-style" : "solid",
				"border-color" : "#0066FF",
				"border-width" : "0 1px 0 1px"
			},
			// set the min width of the resize column
			minWidth : 20,
			// the range you can resize column around the column border
			range : 4,
			// whether wrap the target table into a div, for resize better.
			bContainer : false,
			// whether change the column width immediately when dragging the
			// reference line
			// false means to change the column width when mouseup
			// strongly recommend set this false, to better performance and
			// interaction.
			bDynamic : false,
			// the two reference lines are global, each document only two lines:
			// the left one and the right one.
			// so give an identity to both lines, when the two lines have
			// already appended to body, should not
			// create the new lines.
			sLineIdPrefix : "table_resize_reference"
		}, _this = this;
		// default options used on initialisation
		// and arguments used on later calls
		var opts = $.extend({}, $.fn.colResizer.defaults, options);
		var args = arguments;

		// global virable
		var resizing = false; // mark whether is resizing
		var resizable = false; // mark whether is the resize state
		var resizeHeader; // store the resize column header
		var leftLine; // the left reference line
		var rightLine; // the right reference line

		_this.rightBorder = _this.width() + _this.offset().left;
		// headers顺序整合 -- 解决双行表头数据混乱问题
		_this.headerInfo = _this.find("thead th");
		var headerRows = _this.find("thead tr");
		if (headerRows.length == 2) {
			var firstRow = $(headerRows[0]).find("th");
			var secondRow = $(headerRows[1]).find("th");
			if (firstRow.length > 0 && secondRow.length > 0) {
				_this.headerInfo = [];
				for ( var i = 0, k = 0; i < firstRow.length; i++) {
					var td = $(firstRow[i]);
					var type = td.attr("type");
					if(!type && type != "checkbox" && type != "radio"){
						var colSpan = td.attr("colSpan");
						if (colSpan) {
							colSpan = parseInt(colSpan);
							if(colSpan > 1){
								for ( var j = 0; j < colSpan && k < secondRow.length; j++) {
									td = $(secondRow[k]);
									k++;
									_this.headerInfo.push(td);
								}
							}else{
								_this.headerInfo.push(td);
							}
						} else {
							_this.headerInfo.push(td);
						}
					}
				}
			}
		}

		var colResizer = function(container) {
			// get the target table
			var tbl = opts.bContainer ? container.children("table") : container;
			// get the first row
//			var tr = tbl.find("tr:first");
			var headers = tbl.find("thead th");
//			headers = headers.length ? headers : tr.find("td");
			// mousemove
			headers.mousemove(function(e) {
				var target = $(e.target);
				if (resizing) {
					// when user is dragging the line to resize
					onDraging(e);
				} else if (fnIsLeftEdge(e)) {
					// mouse always over the right border, so when mouse is over
					// the left border,
					// set the resizeHeader to the left one column, so mouse is
					// actually over the left column's
					// right border
					resizeHeader = target.prev();
					// if mouse is over the first cell disable resizing
					if (resizeHeader.length == 0)
						return;
					if (resizeHeader.attr("resizable") != "true")
						return;
					// enable resize
					resizable = true;
					// change the cursor is resize style
					target.css("cursor", "col-resize");
				} else if (fnIsRightEdge(e)) {
					// when cursor is over the right border
					resizeHeader = target;
					if (resizeHeader.attr("resizable") != "true")
						return;
					resizable = true;
					target.css("cursor", "col-resize");
				} else {
					// disable the resize feature when mouse is over the other
					// place not the border
					resizable = false;
					target.css("cursor", "default");
				}
			});
			// mouse down and resize enabled that means resize is starting
			headers.mousedown(function(e) {
				onDragingStart(e);
			});
		};

		/**
		 * caculate whether mouse near the border, and can resize
		 * 
		 * @param event
		 *            e
		 * @side boolean true near the left border,false near the right border
		 * @return boolean true yes,false no
		 */
		function _fnIsColEdge(e, side) {
			var target = $(e.target);
			var x = e.pageX;
			var offset = target.offset();
			var left = offset.left;
			var right = left + target.outerWidth();
			return side ? x <= left + opts.range : x >= right - opts.range;
		}
		/**
		 * caculate whether mouse near the left border, and can resize
		 * 
		 * @param event
		 *            e
		 * @return boolean true yes,false no
		 */
		function fnIsLeftEdge(e) {
			return _fnIsColEdge(e, true);
		}
		/**
		 * caculate whether mouse near the right border, and can resize
		 * 
		 * @param event
		 *            e
		 * @return boolean true yes,false no
		 */
		function fnIsRightEdge(e) {
			return _fnIsColEdge(e, false);
		}
		/**
		 * called when resize start
		 * 
		 * @param event
		 *            e
		 * @return void
		 */
		function onDragingStart(e) {
			if (resizable) {
				var target = $(e.target);
				// disable text selection, if not when resizing it will select
				// text in the table.
				if (!$.browser.mozilla) {
					$(document).bind("selectstart", function() {
						return false;
					});
				} else {
					// mozilla is different from the chrom and ie
					$("body").css("-moz-user-select", "none");
				}
				// if the reference line not created, create one
				leftLine = $("#" + opts.sLineIdPrefix + "_left");
				rightLine = $("#" + opts.sLineIdPrefix + "_right");
				if (leftLine.length === 0) {
					leftLine = $("<div></div>");
					leftLine.css(opts.resize_line_border);
					leftLine.css("position", "absolute");
					leftLine.css("width", "0px");
					leftLine.appendTo("body");
					leftLine.attr("id", opts.sLineIdPrefix + "_left");
					rightLine = leftLine.clone();
					rightLine.appendTo("body");
					rightLine.attr("id", opts.sLineIdPrefix + "_right");
				}
				// set leftLine
				leftLine.css({
					"top" : resizeHeader.offset().top,
					"left" : resizeHeader.offset().left,
					"height" : _this.innerHeight(),
					"z-index" : 99999
				});
				// set rightLine
				rightLine.css({
					"top" : resizeHeader.offset().top,
					"left" : (e.pageX < _this.rightBorder) ? e.pageX : _this.rightBorder,
					"css" : "col-resize",
					"height" : _this.innerHeight(),
					"z-index" : 99999
				});
				// add documment listener for user will drag the line out of the
				// table
				$(document).bind("mousemove", onDraging);
				// add documment listener when mouseup do resize the column
				$(document).bind("mouseup", onDragingEnd);
				leftLine.show();
				rightLine.show();
				// set resize is starting
				resizing = true;
			}
		}
		/**
		 * called when dragging the line
		 * 
		 * @param event
		 *            e
		 * @return void
		 */
		function onDraging(e) {
			// if resize is start
			if (resizing) {
				// caculate whether the new width is less than the minWidth
				if (e.pageX - resizeHeader.offset().left > opts.minWidth) {
					rightLine.css("left", e.pageX);
					if (opts.bDynamic) {
						doResize();
					}
				}
			}
		}
		/**
		 * called when resize is end
		 * 
		 * @param event
		 *            e
		 * @return void
		 */
		function onDragingEnd(e) {
			// if resize is start
			if (resizing) {
				resizing = false;
				// hide the two reference line
				rightLine.hide();
				leftLine.hide();
				// enable text selection
				if (!$.browser.mozilla) {
					$(document).unbind("selectstart");
				} else {
					$("body").css("-moz-user-select", "");
				}
				// unbind the document listener, for better performance
				$(document).unbind("mousemove", onDraging);
				$(document).unbind("mouseup", onDragingEnd);
				// caculate the new width, and set the column width to the new
				// one.
				doResize();
			}
		}
		/**
		 * caculate the new width, and set the column width to the new one.
		 * 
		 * @param event
		 *            e
		 * @return void
		 */
		function doResize() {
			// caculate the new width
			var newWidth = parseInt(rightLine.css("left"), 10) - resizeHeader.offset().left - resizeHeader.width();
			// alert("rightLine:" + parseInt(rightLine.css("left"), 10) + ",
			// headerLeft:" + resizeHeader.offset().left + ", headerWidth:" +
			// resizeHeader.width());
			// set the new width
			resizeHeader.width(resizeHeader.width() + newWidth);
			resizeHeader.attr("tag", true);
//			var headers = _this.headerInfo;
//			for ( var i = 0; i < headers.length; i++) {
//				var header = $(headers[i]);
//				if (header.attr("tag") != "true") {
//					if (header.attr("resizable") == "true")
//						header.width(header.width() - (newWidth / (headers.length - 1.0)));
//				} else {
//					header.attr("tag", null);
//				}
//			}
		}
		/**
		 * Entry point
		 */
		return this.each(function() {
			// make sure each element is table
			if (!$.nodeName(this, "table"))
				return;
			if (opts.bContainer) {
				var root = $(this).wrap("<div/>").parent();
				colResizer(root);
			} else {
				colResizer($(this));
			}
		});
	};
}(jQuery));