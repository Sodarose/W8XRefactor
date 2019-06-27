function requestData() {
    $.ajax({
        url: '/core/projectTree',
        type: 'get',
        dataType: 'json',
        async: false,
        success: function (res) {
            let list = []
            list.push(res)
            layui.use('tree', function () {
                layui.tree({
                    elem: '#demo' //传入元素选择器
                    , nodes: list,
                    click: function(node){
                        getJavaFileDetial(node.id)
                    }
                });
            });
            return res;
        },
        error: function (e) {

        }
    })
}
function getJavaFileDetial(fileName) {
    $.ajax({
        url: '/core/javaFileDetail',
        type: 'post',
        dataType: 'json',
        data:{'filePath':fileName},
        async: true,
        success:function (res) {
            if(res!=null){
                $("#BeforeRefactoring").html(res.originalCode);
            }
        },error:function (res) {
            
        }
    });
}
