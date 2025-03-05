$(function() {
    // CSRF 토큰 설정
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    // 현재 도메인 가져오기
    var currentDomain = window.location.origin;

    // URL 입력 필드에 도메인 추가
    $('.prodListWrap .urlInput').each(function() {
        var path = $(this).val();
        if (path && !path.startsWith('http')) {
            $(this).val(currentDomain + path);
        }
    });

    // Ajax 요청 전에 CSRF 토큰을 헤더에 추가
    $.ajaxSetup({
        beforeSend: function(xhr) {
            if(token && header) {
                xhr.setRequestHeader(header, token);
            }
        }
    });

    // 인플루언서 선택 이벤트 처리
    $("#sellerSelect").change(function() {
        var sellerId = $(this).val();
        if (sellerId) {
            // 이미 추가된 인플루언서인지 확인
            if ($("#seller_" + sellerId).length > 0) {
                alert("이미 추가된 인플루언서입니다.");
                $(this).val(""); // select 초기화
                return;
            }

            // 인플루언서 정보 가져오기
            $.ajax({
                url: '/admin/manage/seller/' + sellerId,
                type: 'GET',
                success: function(seller) {
                    addSellerToList(seller);
                    $("#sellerSelect").val(""); // select 초기화
                },
                error: function(xhr, status, error) {
                    console.error('인플루언서 정보 조회 오류:', error);
                    alert('인플루언서 정보를 가져오는 중 오류가 발생했습니다.');
                }
            });
        }
    });

    // 인플루언서 삭제 버튼 클릭 이벤트
    $(document).on('click', '.deleteBtn', function() {
        $(this).closest('li').remove();
        reindexSellerList();
    });

    // 인플루언서 정보 버튼 클릭 이벤트
    $(document).on('click', '.infoSeller', function() {
        var sellerId = $(this).data('seller-id');

        // 상품 등록 페이지인 경우에만 AJAX 호출
        if ($('.prodWriteWrap').length > 0) {
            if (!sellerId) {
                sellerId = $(this).closest('li').find('input[name$=".manageId"]').val();
            }

            // 인플루언서 정보 가져오기
            $.ajax({
                url: '/admin/manage/seller/' + sellerId,
                type: 'GET',
                success: function(seller) {
                    // 모달 내용 업데이트
                    var $modal = $(".infoSellerModal");
                    $modal.find('.tit:eq(1)').text(seller.sellerNickName);

                    // 카테고리 업데이트
                    var $categoryList = $modal.find('.category');
                    $categoryList.empty();
                    if (seller.categories) {
                        seller.categories.forEach(function(category) {
                            $categoryList.append('<li>' + category + '</li>');
                        });
                    }

                    // 소개글 업데이트
                    $modal.find('.infoBox').text(seller.introduction || '소개글이 없습니다.');

                    // SNS 링크 업데이트
                    $modal.find('.snsLink').attr('href', seller.snsUrl || '#').text(seller.snsUrl || '');

                    // 모달 표시
                    $modal.css("display","block");
                },
                error: function(xhr, status, error) {
                    console.error('인플루언서 정보 조회 오류:', error);
                    alert('인플루언서 정보를 가져오는 중 오류가 발생했습니다.');
                }
            });
        } else {
            // 관리 페이지에서는 기존 모달 표시 로직 사용
            $(".infoSellerModal").css("display","block");
        }
    });

    // 날짜 형식 변환 함수 수정
    function formatDate(date) {
        if (!date) return '';

        // "25. 3. 1." 형식의 날짜를 처리
        if (typeof date === 'string') {
            // 점(.)이 포함된 형식 처리 (예: "25. 3. 1.")
            if (date.includes('.')) {
                const parts = date.split('.').map(part => part.trim()).filter(part => part !== '');
                if (parts.length >= 3) {
                    let year = parts[0];
                    // 2자리 연도를 4자리로 변환 (예: 25 -> 2025)
                    year = year.length <= 2 ? '20' + year : year;
                    // 월과 일을 2자리 숫자로 변환
                    const month = String(parseInt(parts[1])).padStart(2, '0');
                    const day = String(parseInt(parts[2])).padStart(2, '0');
                    return `${year}-${month}-${day}`;
                }
            }

            // 이미 yyyy-MM-dd 형식인 경우 그대로 반환
            if (date.match(/^\d{4}-\d{2}-\d{2}$/)) {
                return date;
            }
        }

        // Date 객체인 경우
        try {
            const dateObj = new Date(date);
            if (!isNaN(dateObj.getTime())) {
                const year = dateObj.getFullYear();
                const month = String(dateObj.getMonth() + 1).padStart(2, '0');
                const day = String(dateObj.getDate()).padStart(2, '0');
                return `${year}-${month}-${day}`;
            }
        } catch (e) {
            console.error('날짜 변환 오류:', e);
        }

        return '';
    }

    // 날짜 입력 필드 이벤트 처리
    $(document).on('input change blur', 'input[type="date"]', function() {
        const dateValue = $(this).val();
        if (dateValue) {
            // 이미 yyyy-MM-dd 형식인 경우 그대로 사용
            if (dateValue.match(/^\d{4}-\d{2}-\d{2}$/)) {
                return;
            }

            const formattedDate = formatDate(dateValue);
            if (formattedDate) {
                $(this).val(formattedDate);
            }
        }
    });

    // 폼 제출 전 날짜 형식 검증
    $('form').on('submit', function(e) {
        let isValid = true;

        $('input[type="date"]').each(function() {
            const dateValue = $(this).val();
            if (dateValue && !dateValue.match(/^\d{4}-\d{2}-\d{2}$/)) {
                isValid = false;
                alert('날짜 형식이 올바르지 않습니다. (YYYY-MM-DD)');
                e.preventDefault();
                return false;
            }
        });

        return isValid;
    });

    // 인플루언서 목록에 추가하는 함수 수정
    function addSellerToList(seller) {
        var $sellerList = $('#sellerList');
        var newIndex = $sellerList.children('li').length;

        // 오늘 날짜 계산 및 형식 지정
        let today = new Date();
        let formattedDate = formatDate(today);

        var newSeller = `
            <li id="seller_${seller.id}">
                <input type="hidden" name="productSellers[${newIndex}].manageId" value="${seller.id}">
                <div class="nameBox">
                    <input type="text" name="productSellers[${newIndex}].sellerNickName" value="${seller.sellerNickName}" readonly>
                    <a href="javascript:void(0)" class="infoSeller btn" data-seller-id="${seller.id}">인플루언서정보</a>
                    <a href="javascript:void(0)" class="deleteBtn btn">삭제</a>
                </div>
                <div class="priceBox textBox">
                    <p class="title">판매가</p>
                    <p class="text">
                        <input type="number" name="productSellers[${newIndex}].salePrice" value="0">
                        <span>원</span>
                    </p>
                    <p class="title">수량</p>
                    <p class="text">
                        <input type="number" name="productSellers[${newIndex}].saleQuantity" value="0">
                        <span>개</span>
                    </p>
                </div>
                <div class="dateBox textBox textBox02">
                    <p class="title">판매기간</p>
                    <p class="text date">
                        <input type="date" name="productSellers[${newIndex}].saleStartDate" value="${formattedDate}" pattern="\\d{4}-\\d{2}-\\d{2}" required>
                        <span>~</span>
                        <input type="date" name="productSellers[${newIndex}].saleEndDate" min="${formattedDate}" pattern="\\d{4}-\\d{2}-\\d{2}" required>
                    </p>
                </div>
            </li>
        `;
        $sellerList.append(newSeller);

        // 새로 추가된 날짜 입력 필드에 이벤트 핸들러 연결
        const $newDateInputs = $sellerList.find(`#seller_${seller.id} input[type="date"]`);
        $newDateInputs.on('change', function() {
            const dateValue = $(this).val();
            if (dateValue) {
                const formattedDate = formatDate(dateValue);
                if (formattedDate) {
                    $(this).val(formattedDate);
                } else {
                    alert('올바른 날짜 형식이 아닙니다. (YYYY-MM-DD)');
                    $(this).val('');
                }
            }
        });
    }

    // 인플루언서 목록 인덱스 재정렬
    function reindexSellerList() {
        $('#sellerList li').each(function(index) {
            $(this).find('input, textarea').each(function() {
                var name = $(this).attr('name');
                if (name) {
                    $(this).attr('name', name.replace(/\[\d+\]/, '[' + index + ']'));
                }
            });
        });
    }

    // 상품 코드 자동 생성 함수
    function generateProductCode() {
        $.ajax({
            url: '/admin/product/generateCode',
            type: 'GET',
            success: function(code) {
                if(code) {
                    $("input[name='productCode']").val(code);
                    $("input[name='productCode']").addClass("active").show();
                    $("input[name='inputCode']").hide();
                    $(".codeBtn").text("직접입력");
                    $(".codeChk").hide(); // Hide duplicate check button
                } else {
                    console.error('상품 코드가 생성되지 않았습니다.');
                    alert('상품 코드 생성 중 오류가 발생했습니다.');
                }
            },
            error: function(xhr, status, error) {
                console.error('상품 코드 생성 오류:', error);
                alert('상품 코드 생성 중 오류가 발생했습니다.');
            }
        });
    }

    // "직접입력" 버튼 클릭 시
    $(".prodWriteWrap .codeBtn").off('click').on('click', function() {
        console.log("자동 생성 버튼 클릭됨.");
        var $productCode = $("input[name='productCode']");
        var $inputCode = $("input[name='inputCode']");
        var $codeBtn = $(".prodWriteWrap .codeBtn");
        var $codeChk = $(".prodWriteWrap .codeChk");

        if ($productCode.hasClass("active")) {
            // 직접입력 모드로 전환
            $productCode.removeClass("active").hide();
            $inputCode.addClass("active").show().val("");
            $codeBtn.text("자동생성");
            $codeChk.show();
        } else {
            // Call the centralized function to generate product code
            generateProductCode();
        }
    });

    // 상품 코드 중복 체크
    $(".codeChk").click(function() {
        var productCode = $("input.active").val();
        if(!productCode) {
            alert('상품코드를 입력해주세요.');
            return;
        }

        $.ajax({
            url: '/admin/product/checkProductCode',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ productCode: productCode }),
            success: function(exists) {
                if(exists) {
                    alert('중복된 상품코드입니다.');
                } else {
                    alert('사용 가능한 상품코드입니다.');
                    // 중복 확인 성공 시 실제 입력 필드에 값 설정
                    $("input[name='productCode']").val(productCode);
                }
            },
            error: function(xhr, status, error) {
                console.error('상품코드 중복 확인 오류:', error);
                alert('상품코드 중복 확인 중 오류가 발생했습니다.');
            }
        });
    });

    /*옵션선택*/
    $("input[name='productOptionStatus']").change(function() {
        if ($(this).val() === 'OPTION_ON') {
            $(".setOpt").addClass("active").show();
            // 옵션명과 옵션값 필드를 필수 입력으로 설정
            $(".setOpt input[type='text']").prop('required', true);
            $(".setOpt textarea").prop('required', true);
        } else {
            $(".setOpt").removeClass("active").hide();
            // 옵션 미사용 시 필수 입력 해제
            $(".setOpt input[type='text']").prop('required', false);
            $(".setOpt textarea").prop('required', false);
        }
    });

    // 페이지 로드 시 초기 상태 설정
    if ($("input[name='productOptionStatus']:checked").val() === 'OPTION_ON') {
        $(".setOpt").addClass("active").show();
        $(".setOpt input[type='text']").prop('required', true);
        $(".setOpt textarea").prop('required', true);
    } else {
        $(".setOpt").removeClass("active").hide();
        $(".setOpt input[type='text']").prop('required', false);
        $(".setOpt textarea").prop('required', false);
    }

    // 옵션 추가 버튼 클릭 이벤트
    $(document).on('click', '.plusBtn', function() {
        var $optionList = $('.optionList');
        var newIndex = $optionList.find('.text').length;
        var newOption = `
            <li class="text">
                <div class="optTit">
                    <input type="text" name="productOptions[${newIndex}].optionName" required placeholder="예시) 사이즈">
                    <a href="javascript:void(0)" class="plusBtn optBtn">추가</a>
                    <a href="javascript:void(0)" class="minusBtn optBtn">제거</a>
                </div>
                <div class="optTxt">
                    <textarea name="productOptions[${newIndex}].optionValues" required placeholder="옵션값을 입력해주세요."></textarea>
                </div>
            </li>
        `;
        $optionList.append(newOption);
    });

    // 옵션 제거 버튼 클릭 이벤트
    $(document).on('click', '.minusBtn', function() {
        var $optionList = $('.optionList');
        if ($optionList.find('.text').length > 1) {
            $(this).closest('.text').remove();
            // 인덱스 재정렬
            $optionList.find('.text').each(function(index) {
                $(this).find('input[type="text"]').attr('name', `productOptions[${index}].optionName`);
                $(this).find('textarea').attr('name', `productOptions[${index}].optionValues`);
            });
        } else {
            alert('최소 1개의 옵션은 필요합니다.');
        }
    });
});
