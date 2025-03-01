$(function(){
    //쇼핑몰 정보 팝업
    $(".infoAdmin").click(function(){
        $(".infoAdminModal").css("display","block");
    });

    // 기존 인플루언서 정보 팝업 이벤트 제거 (다른 페이지의 단순 모달용)
    $(document).on('click', '.infoSeller:not([data-seller-id])', function(){
        $(".infoSellerModal").css("display","block");
    });

    // 인플루언서 정보 팝업 (상품 등록 페이지용)
    $(document).on('click', '.infoSeller[data-seller-id]', function(){
        var sellerId = $(this).data('seller-id');

        // 인플루언서 정보 가져오기
        $.ajax({
            url: '/admin/manage/seller/' + sellerId,
            type: 'GET',
            success: function(seller) {
                // 디버깅을 위한 로그 추가
                console.log('Received seller data:', seller);
                console.log('Product Categories:', seller.productCategories);
                console.log('Type of productCategories:', typeof seller.productCategories);

                // 모달 내용 업데이트
                var $modal = $(".infoSellerModal");

                // 인플루언서 활동명 업데이트
                $modal.find('.modalCont .titWrap .tit').text(seller.sellerNickName);

                // 카테고리 업데이트
                var $categoryList = $modal.find('.modalCont .category');
                $categoryList.empty();

                // 카테고리 데이터 검사 로그
                if (seller.productCategories) {
                    console.log('Categories exist. Is array?', Array.isArray(seller.productCategories));
                    console.log('Categories length:', seller.productCategories.length);
                }

                if (seller.productCategories && Array.isArray(seller.productCategories) && seller.productCategories.length > 0) {
                    seller.productCategories.forEach(function(category) {
                        console.log('Processing category:', category);
                        $categoryList.append('<li>' + category + '</li>');
                    });
                } else {
                    console.log('No categories found or invalid format');
                    $categoryList.append('<li>등록된 카테고리가 없습니다.</li>');
                }

                // 소개글 업데이트
                $modal.find('.modalCont .infoBox').text(seller.sellerContents || '등록된 소개글이 없습니다.');

                // SNS 링크 업데이트
                var $snsLink = $modal.find('.modalCont .snsLink');
                if (seller.sellerUrl) {
                    $snsLink.attr('href', seller.sellerUrl).text(seller.sellerUrl);
                    $snsLink.show();
                } else {
                    $snsLink.attr('href', '#').text('등록된 SNS 링크가 없습니다.');
                    $snsLink.show();
                }

                // 모달 표시
                $modal.css("display","block");
            },
            error: function(xhr, status, error) {
                console.error('인플루언서 정보 조회 오류:', error);
                alert('인플루언서 정보를 가져오는 중 오류가 발생했습니다.');
            }
        });
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
