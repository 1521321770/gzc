<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" 
			href="../js/plupload/jquery.plupload.queue/css/jquery.plupload.queue.css">
<script type="text/javascript" src="../js/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="../js/plupload/plupload.full.js"></script>   
<script type="text/javascript" src="../js/plupload/jquery.plupload.queue/jquery.plupload.queue.js"></script> 

<script type="text/javascript">   
$(function(){   
    function plupload(){   
        $("#uploader").pluploadQueue({   
            // General settings   
            runtimes : 'html5,gears,browserplus,silverlight,flash,html4',   
            url : 'http://localhost:8080/gengzc/gzc/fileUpload.do',   
            max_file_size : '10mb',   
            unique_names: true,   
            chunk_size: '2mb',   
            // Specify what files to browse for   
            filters : [   
                {title: "Image files", extensions: "jpg,gif,png"},   
                {title: "Zip files", extensions: "zip"}   
            ],   
            resize: {width: 640, height: 480, quality: 90},   
            // Flash settings   
            flash_swf_url: 'plupload/plupload.flash.swf',   
            // Silverlight settings   
            silverlight_xap_url: 'plupload/plupload.silverlight.xap',   
            // 参数   
            multipart_params: {'user': 'Rocky', 'time': '2012-06-12'}   
        });   
    }   
    plupload();   
    $('#Reload').click(function(){   
        plupload();   
    });   
});   
</script>   
</head>
<body>

<div style="width:750px; margin:0 auto">   
    <form id="formId" action="Submit.action" method="post">   
        <div id="uploader">   
            <p>您的浏览器未安装 Flash, Silverlight, Gears, BrowserPlus 或者支持 HTML5 .</p>   
        </div>   
        <input value="重新上传" id="Reload" type="button">   
    </form>   
</div>    

</body>
</html>