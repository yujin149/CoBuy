$(function(){
    //인플루언서 정보 팝업
    $(".infoSeller").click(function(){
        $(".infoSellerModal").css("display","block");
    });

    //자동매칭 팝업
    $(".matchSeller").click(function(){
        $(".matchModal").css("display","block");
    });

    $(".closeBtn").click(function(){
        $(".modal").css("display","none");
    });
});