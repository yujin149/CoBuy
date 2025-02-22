$(function(){
    //쇼핑몰 정보 팝업
    $(".infoAdmin").click(function(){
        $(".infoAdminModal").css("display","block");
    });

    //인플루언서 정보 팝업
    $(".infoSeller").click(function(){
        $(".infoSellerModal").css("display","block");
    });

    //자동매칭 팝업
    $(".matchAdmin").click(function(){
        $(".matchAdminModal").css("display","block");
    });

    $(".matchSeller").click(function(){
        $(".matchSellerModal").css("display","block");
    });


    $(".addAdminBtn").click(function(){
        $(".addAdminModal").css("display","block");
    });
    $(".addSellerBtn").click(function(){
        $(".addSellerModal").css("display","block");
    });


    /*체크박스*/
    $(".allChk").click(function(){
        if($(this).prop("checked")){
            $(".chk").prop("checked", true);
        } else {
            $(".chk").prop("checked", false);
        }
    });

    /*주문내역확인 모달*/
    $(".orderDetailBtn").click(function(){
        $(".orderDetailModal").css("display","block");
    });

    /*순위 모달*/
    $(".rankBtn").click(function(){
        $(".rankModal").css("display","block");
    });
    
    
    /*닫기*/
    $(".closeBtn").click(function(){
        $(".modal").css("display","none");
    });
    $(".bgB").click(function(){
        $(".modal").css("display","none");
    });
    
    
    

});
