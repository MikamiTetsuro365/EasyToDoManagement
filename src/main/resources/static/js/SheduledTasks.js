//ここJquery
//Bootstrapのモーダルで発生するイベントを感知したい
$(function(){
　　StartTimer();

    //ここタイマー
    function StartTimer(){
        console.log("スタート");
        intervalMethod = setInterval(checkTaskList, 5000);
    };
    function checkOverdueTask(){
        window.location.href = '/index';
        //どこかのボタンを押して発火させれば良さそう
    };
    function StopTimer(){
       clearInterval(intervalMethod);
    };

//    $('.closeModal').on('click', function(){
//        StartTimer();
//    });

//    $('#registerModal').on('show.bs.modal', function () {
//        StopTimer();
//    });

//    $('#registerModal').on('hidden.bs.modal', function () {
//        //StartTimer();
//        //ここらAjaxでどうにかして方がええんやろうなぁ
//        //window.location.href = '/index';
//        updateTaskList();
//    })

    //Ajax
    function checkTaskList(){
                $.ajax({
                    url:'/index/ajax',
                    type:'GET',
                    dataType:"html"
                })
                // Ajaxリクエストが成功した時発動
                .done( (data) => {
                    console.log(data);
                    $('#tasklist').html(data);
                })
                // Ajaxリクエストが失敗した時発動
                .fail( (data) => {
                    console.log("ng");
                })
                // Ajaxリクエストが成功・失敗どちらでも発動
                .always( (data) => {
                    console.log("^v^");
                });
    }

    function test(){
        console.log("OK");
    }

});







