$(function(){
    $('.headerWrap .menuBtn').click(function(){
       $('.menuWrap').css("display","block");
    });

    $(".closeBtn").click(function(){
        $('.menuWrap').css("display","none");
    });
});