jQuery().ready(function(){
	$("#uploadDiv").pluploadQueue({   
        // General settings   
        runtimes : 'html5,gears,browserplus,silverlight,flash,html4', 
        url : 'http://localhost:8080/gengzc/fileUploader',    //服务上传路径
//        browse_button : 'butselectfile', //触发文件选择对话框的按钮，为那个元素id
           
        max_file_count : 20,		   //指示用户可以上传文件的最大数量
//        max_file_size : '10mb',      //文件上传最大限制
        unique_names : true,           //名字是否唯一
        rename : true,                 //是否重命名
        chunk_size : '10mb',           //上传分块每块的大小，小于服务器最大上传值即可
        sortable : true,               //Sort files
        dragdrop : true,               //启用文件到小部件能够拖放（操作）
        views : {
        	list : false,
    		thumbs : false,            //show thumbs
    		active : "thumbs"
        },
        
        multipart : true,              //使用mutlipart格式，实现大附件分段上传的功能
        multipart_params: {
        	'time': new Date()
        },
        // 上传文件时限制的上传文件类型
        filters : [   
            {title: "Image files", extensions: "jpg,gif,png"}, 
            {title: "files", extensions: "*"},
            {title: "Zip files", extensions: "zip"}   
        ],
        
        resize: {
        	width: 640,
        	height: 480,
        	quality: 90
        },   
        
        //swf文件，当需要使用swf方式进行上传时需要配置该参数
        flash_swf_url : '../js/plupload/Moxie.swf',  
        
        //silverlight文件，当需要使用silverlight方式进行上传时需要配置该参数 
        silverlight_xap_url: '../js/plupload/Moxie.xap'     
    });  
	
	var fileNameList = [];
	var uploader = $('#uploadDiv').pluploadQueue();
	
//    在实例对象上调用init()方法进行初始化
//    uploader.init();

	/**
	 * 绑定各种事件，并在事件监听函数中做你想做的事
	 * 每个事件监听函数都会传入一些很有用的参数，
	 * 我们可以利用这些参数提供的信息来做比如更新UI，提示上传进度等操作
	 * 添加文件事件绑定
	 * up 包涵Uploader的全部信息　
	 * files 包涵这一次添加的所有文件的基本信息
	 */
    uploader.bind('FilesAdded',function(up,files){
    	var reg = new RegExp(/^[^\\\/<>*|:"?]+$/);
		for(var i=0;i<files.length;i++){
			var file = files[i];
			alert(file.name);
			fileNameList.push(file.name);
			
			if(!reg.test(file.name)){
				showErrorMsg(lang["fileNameCheck"]);
				loadIndex("/gengzc/propertyFilter/index.jsp", function(){
		      		alert("重新上传");
		      	})
				return;
			 }
		}
		
		if(fileNameList.length == 0){
			$(".plupload_start").addClass("disabled");
		}else{
			$(".plupload_start").addClass("disabled");
		}
    });
    
    /**
     * 删除已添加的文件所触发的事件
     */
    uploader.bind("FilesRemoved", function(up, fileArray) {
 		for(var i=0;i<fileNameList.length;i++){
 			if(fileNameList[i] == fileArray[0].name){
 				fileNameList.splice(i, 1);
 				break;
 			 }
 		}
 		
 		if(fileNameList.length > 0){
 			$(".plupload_start").removeClass("disabled");
 		}else{
 			$(".plupload_start").addClass("disabled");
 		}
     });
    
    /**
     * 点击上传提交后，上传前的事件
     * @param up
     * 		包涵Uploader的全部信息
     * @param file
     * 		准备上传的一个文件的基本信息
     */
    uploader.bind("BeforeUpload", function(up, file) {
     	up.settings.multipart_params.name = file.name;
     	up.settings.multipart_params.size = file.size;
     	up.settings.multipart_params.chunkSize = up.settings.chunk_size;
     	up.settings.multipart_params.parentFolderId = "";
     });
    
    uploader.bind('UploadProgress',function(uploader,file){
        //每个事件监听函数都会传入一些很有用的参数，
        //我们可以利用这些参数提供的信息来做比如更新UI，提示上传进度等操作
    	//点击上传提交后，上传前的事件完成后，执行正在上传事件
    	//up 包涵Uploader的全部信息　                 
    	//file 正在上传的一个文件的基本信息
    });
    
    uploader.bind("Error", function(up, err) {
    	//当上传失败时，所触发的事件
     }); 
    
     

     uploader.bind("FileUploaded", function(up, file, obj) {
    	 //完成一个文件上传
    	 //up 包涵Uploader的全部信息　                
    	 //file 刚刚上传成功的一个文件的基本信息
    	 	//obj 后台cs文件返回的基本信息
      });
     
     /**
      * 全部文件上传完毕之后所触发的事件
      * @param up
      * 		包涵Uploader的全部信息
      * @param files
      * 		所上传的全部文件的基本信息
      */
      uploader.bind("UploadComplete", function(up, files) {
    	  window.location.href="/gengzc/propertyFilter/index.jsp";
    	  window.open("上传成功");
      });	
});

function loadIndex(url, callback){
	alert(url)	
	$.ajax({
		url : url,
		type : 'get',
		async : true,
		success : function(data){
			if(callback){
				callback();
				}
			}
		});
};
