document.addEventListener('DOMContentLoaded', function() {
    console.log('Seller Join Script Loaded');
    
    // seller 전용 변수명 사용
    const sellerCheckIdButton = document.querySelector('.checkId');
    const sellerForm = document.querySelector('form');
    const sellerIdInput = document.querySelector('input[name="sellerId"]');
    const sellerIdCheckedInput = document.querySelector('input[name="idChecked"]');
    
    // 각 요소의 존재 여부 확인
    console.log('Seller Elements found:', {
        checkIdButton: !!sellerCheckIdButton,
        form: !!sellerForm,
        sellerIdInput: !!sellerIdInput,
        idCheckedInput: !!sellerIdCheckedInput
    });

    if (sellerCheckIdButton && sellerForm && sellerIdInput && sellerIdCheckedInput) {
        console.log('All seller elements found, adding event listeners');
        
        // 중복 확인 버튼 클릭 이벤트
        sellerCheckIdButton.addEventListener('click', function(e) {
            e.preventDefault();
            const sellerId = sellerIdInput.value;

            if (!sellerId) {
                alert('아이디를 입력해주세요.');
                return;
            }

            fetch('/seller/checkId', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'sellerId=' + encodeURIComponent(sellerId)
            })
            .then(response => response.json())
            .then(data => {
                if (data.isDuplicate) {
                    alert('이미 사용중인 아이디입니다.');
                    sellerIdCheckedInput.value = "false";
                } else {
                    alert('사용 가능한 아이디입니다.');
                    sellerIdCheckedInput.value = "true";
                }
            });
        });

        // 아이디 입력 시 중복 확인 초기화
        sellerIdInput.addEventListener('input', function() {
            sellerIdCheckedInput.value = "false";
        });

        // form 제출 시 중복 확인 여부 체크
        sellerForm.addEventListener('submit', function(e) {
            if (sellerIdCheckedInput.value === "false") {
                e.preventDefault();
                alert('아이디 중복 확인을 해주세요.');
            }
        });
    } else {
        console.error('Some seller elements are missing');
    }
});