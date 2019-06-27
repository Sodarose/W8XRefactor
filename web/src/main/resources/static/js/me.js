function scanner(path) {
    $.ajax({
        url: '/core/analysis',
        type: 'post',
        async: false,
        data: {'fileName': path},
        dataType: 'json',
        beforeSend: function () {
            layer.msg('扫描中', {
                icon: 16
                ,shade: 0.01
            });
        },
        success: function (res) {
            if(res.code===200){
                layer.msg('扫描成功 即将跳转', {icon: 1});
                window.setTimeout(function(){location.href='/route/index.html'},2000);
            }else{
                layer.msg('扫描失败', {icon: 5});
            }
        }, error: function (e) {

        }
    })
}
function refactor() {
    $.ajax({
        url: '/core/analysis',
        type: 'get',
        async: true,
        dataType: 'json',
        beforeSend: function () {
            layer.msg('重构中', {
                icon: 16
                ,shade: 0.01
            });
        },
        success: function (res) {
            if(res.code===200){
                layer.msg('重构成功 即将刷新', {icon: 1});
                window.setTimeout(function(){location.href='/route/index.html'},2000);
            }else{
                layer.msg('重构失败', {icon: 5});
            }
        }, error: function (e) {

        }
    })
}