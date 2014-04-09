var videolist = {
	name:"video",
	list:[
		"http://google.com",
		"http://baidu.com",
		"http://sina.com",
		"http://hello.com.us",
		"http://.xiaonei.com"
	]
};

exports.list = function(req, res){
	res.json(videolist);
};
