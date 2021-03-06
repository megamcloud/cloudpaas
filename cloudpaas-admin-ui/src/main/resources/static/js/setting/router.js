layui.config({
	base : $config.context+"/static/js/extends/",
	version : true
}).extend({
	"tool" : "tool",
	"api" : "api"
}).use([ 'element', 'layer' ,'api',"table","form","tool"], function() {
	var $ = layui.jquery, element = layui.element, layer = layui.layer,
	    api = layui.api,
	    table = layui.table,
        form = layui.form,
        tool = layui.tool;
	/*表格*/
	table.render({
        elem: '#oTable',
        url: api.getRouterPageUrl,
        cols: [[
            {type: "checkbox", width: 50, fixed: "left"},
            {field: 'id', width: 80, title: 'ID', sort: true},
            {field: 'routerId', width: 180, title: '网关ID'},
            {field: 'uri', title: '服务URI', width: 150},
            {field: 'routerOrder', minWidth: 50, title: '路由顺序' },
            {field: 'path', minWidth: 50, title: '路径标识' },
            {field: 'stripPrefix', minWidth: 50, title: '忽略前缀数' },
            {field: 'enabled', minWidth: 50, title: '状态',templet: function (d) {
            	if(d.enabled == 1) {
                    return '<span class="layui-badge layui-bg-blue">开启</span>';
                } else{
                    return '<span class="layui-badge layui-bg-gray">关闭</span>';
                }
    		
            }
            },
            {title: '操作', minWidth: 50, templet: '#oBar', fixed: "right", align: "center"}
        ]],
        limits: [5,10, 15, 20, 25, 50, 100],
        limit: 15,
        page: true
    });
	
	function layuiresize(index) {
        $(window).resize(function(){
            layer.full(index);
        });
        layer.full(index);
    };

    //监听表格操作列
    table.on('tool(oList)', function (obj) {
        var data = obj.data;
        if (obj.event === 'edit') {
        	var editIndex = layer.open({
                title : "编辑角色",
                type : 2,
                content : $config.context+"/router/edit.html?id="+data.id,
                area: ['80%', '80%'],
                maxmin: true,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }
            });
            //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
        	//layuiresize(editIndex);
        } else if (obj.event === 'del') {
        	layer.confirm("你确定要删除该数据么？",{btn:['是的,我确定','我再想想']},
                function(){
	        		$.ajax({
	                    type:"DELETE",
	                    url:api.deleteGateRouterDelUrl+"?id="+data.id+"&routerId="+data.routerId,
	                    dataType:"json",
	                    contentType:"application/json",
	                    data:JSON.stringify(data.field),
	                    success:function(res){
	                        if(res.code==0){
	                            parent.layer.msg("删除成功！",{time:1000},function(){
	                                location.reload();
	                            });
	                        }else{
	                            layer.msg(res.message,{time:1000},function(){
	                                //刷新本页面
	                                location.reload();
	                            });
	
	                        }
	                    }
	                });
                }
            )
        }
    });
    
    /*工具栏对象*/
    var active={
            add : function(){
                addIndex = layer.open({
                    title : "添加",
                    type : 2,
                    content : $config.context+"/router/add.html",
                    area: ['80%', '80%'],
                    maxmin: true,
                    success : function(layero, addIndex){
                        setTimeout(function(){
                            layer.tips('点击此处返回角色列表', '.layui-layer-setwin .layui-layer-close', {
                                tips: 3
                            });
                        },500);
                    }
                });
                //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
                //layuiresize(addIndex);
            },
            //批量删除
            delBatch : function(){
                var checkStatus = table.checkStatus('oTable'),
                    data = checkStatus.data;
                if(data.length > 0){
                    console.log(JSON.stringify(data));
                    //layer.msg(JSON.stringify(data));
                    
                    layer.confirm("你确定要删除这些数据么？",{btn:['是的,我确定','我再想想']},
                        function(){
                            var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                            $.ajax({
                                type:"DELETE",
                                url:api.deleteBatchRouterUrl,
                                dataType:"json",
                                contentType:"application/json",
                                data:JSON.stringify(data),
                                success:function(res){
                                    layer.close(deleteindex);
                                    if(res.code==0){
                                        layer.msg("删除成功",{time: 1000},function(){
                                        	location.reload();
                                        });
                                    }else{
                                        layer.msg(res.message);
                                    }
                                }
                            });
                        }
                    )
                }else{
                    layer.msg("请选择需要删除的行",{time:1000});
                }
            }
        };
    
    //监听工具拦的操作按钮
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    // 监听搜索操作
    form.on('submit(data-search-btn)', function (data) {

        //执行搜索重载
        table.reload('oTable', {
            page: {
                curr: 1
            }
            , where: data.field
        }, 'data');

        return false;
    });

    //监听表格复选框选择
    table.on('checkbox(oList)', function (obj) {
        console.log(obj)
    });

});