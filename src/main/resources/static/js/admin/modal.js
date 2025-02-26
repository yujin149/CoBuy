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

    /*협업 요청 모달*/
    $(".requestBtn").click(function(){
        $(".requestModal").css("display","block");
    });

    /*닫기*/
    $(".closeBtn").click(function(){
        $(".modal").css("display","none");
    });
    $(".bgB").click(function(){
        $(".modal").css("display","none");
    });




});

// 협업요청 확인 모달에서 수락/거절 버튼 클릭 시 실행되는 함수
function respondToRequest(manageId, status) {
    fetch(`/manage/respond/${manageId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [document.getElementById("csrf-header").value]: document.getElementById("csrf-token").value
        },
        body: JSON.stringify({ status: status })
    })
        .then(response => {
            if (response.ok) {
                alert('요청이 처리되었습니다.');
                // 요청 처리 후 알림 숫자 업데이트
                updatePendingRequestCount();
                // 모달 닫기
                document.querySelector('.requestModal').style.display = 'none';
            } else {
                alert('요청 처리에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('요청 처리 중 오류가 발생했습니다.');
        });
}
