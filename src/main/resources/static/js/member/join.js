document.addEventListener('DOMContentLoaded', function() {
    const checkIdButton = document.querySelector('.checkId');
    const form = document.querySelector('form');
    let isIdChecked = false;
    
    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    };
    
    // CSRF 토큰이 있는 경우에만 헤더에 추가
    const csrfMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    if (csrfMeta && csrfHeaderMeta) {
        const token = csrfMeta.getAttribute('content');
        const header = csrfHeaderMeta.getAttribute('content');
        headers[header] = token;
    }
    
    checkIdButton.addEventListener('click', function(e) {
        e.preventDefault();
        const userId = document.querySelector('input[name="userId"]').value;

        // 아이디 유효성 검사
        if (!userId) {
            alert('아이디를 입력해주세요.');
            return;
        }
        
        if (userId.length < 6 || userId.length > 20) {
            alert('아이디는 6자에서 20자 사이로 입력해주세요.');
            return;
        }
        
        if (!/^[a-zA-Z0-9]+$/.test(userId)) {
            alert('아이디는 영문과 숫자만 사용 가능합니다.');
            return;
        }

        fetch('/member/checkId', {
            method: 'POST',
            headers: headers,
            body: 'userId=' + encodeURIComponent(userId)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.isDuplicate) {
                alert('이미 사용중인 아이디입니다.');
                isIdChecked = false;
            } else {
                alert('사용 가능한 아이디입니다.');
                isIdChecked = true;
                document.querySelector('input[name="idChecked"]').value = "true";
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('중복 확인 중 오류가 발생했습니다.');
            isIdChecked = false;
        });
    });

    // 아이디 입력 필드 값이 변경되면 중복 확인 초기화
    document.querySelector('input[name="userId"]').addEventListener('input', function() {
        isIdChecked = false;
        document.querySelector('input[name="idChecked"]').value = "false";
    });

    // 폼 제출 시 중복 확인 여부 체크
    form.addEventListener('submit', function(e) {
        if (!isIdChecked) {
            e.preventDefault();
            alert('아이디 중복 확인을 해주세요.');
        }
    });

    // 주소 입력 필드들의 값이 변경될 때마다 전체 주소 업데이트
    document.querySelectorAll('#sample6_postcode, #sample6_address, #sample6_detailAddress, #sample6_extraAddress')
        .forEach(function(element) {
            element.addEventListener('change', function() {
                updateFullAddress();
            });
        });

    function updateFullAddress() {
        const postcode = document.querySelector('#sample6_postcode').value;
        const baseAddress = document.querySelector('#sample6_address').value;
        const detailAddress = document.querySelector('#sample6_detailAddress').value;
        const extraAddress = document.querySelector('#sample6_extraAddress').value;

        let fullAddress = '';
        if (postcode) fullAddress += `(${postcode}) `;
        if (baseAddress) fullAddress += baseAddress;
        if (detailAddress) fullAddress += ` ${detailAddress}`;
        if (extraAddress) fullAddress += ` (${extraAddress})`;

        document.querySelector('input[name="userAddress"]').value = fullAddress.trim();
    }
}); 