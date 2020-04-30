//ここJquery
//Bootstrapのモーダルで発生するイベントを感知したい
$(function(){
　　StartTimer();

//    $('.closeModal').on('click', function(){
//        StartTimer();
//    });

    $('#registerModal').on('show.bs.modal', function () {
        StopTimer();
    });

    $('#registerModal').on('hidden.bs.modal', function () {
        //StartTimer();
        //ここらAjaxでどうにかして方がええんやろうなぁ
        window.location.href = '/index';
    })
});

//ここタイマー
function StartTimer(){
    intervalMethod = setInterval(checkOverdueTask, 5000);
};
function checkOverdueTask(){
    window.location.href = '/index';
};
function StopTimer(){
   clearInterval(intervalMethod);
};





