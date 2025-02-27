// 현재 매칭된 파트너 ID를 저장할 변수
let currentMatchedPartnerId = null;

// 이미 표시된 파트너의 ID를 저장할 배열
let shownPartnerIds = [];

// 매칭 모달 초기화 및 데이터 로드
function initializeMatchModal(role) {
    const currentUserId = document.getElementById('currentUserId').value;
    const endpoint = role === 'ADMIN' ? '/seller/search' : '/admin/search';

    // CSRF 토큰 가져오기
    const token = document.getElementById("csrf-token").value;
    const header = document.getElementById("csrf-header").value;

    // 검색 파라미터 설정
    const params = new URLSearchParams();

    if (role === 'ADMIN') {
        params.append('searchType', '인플루언서명');
        params.append('sellerNickName', ''); // 빈 검색어로 전체 검색
    } else {
        params.append('searchType', '쇼핑몰명');
        params.append('adminShopName', ''); // 빈 검색어로 전체 검색
    }
    params.append('currentUserId', currentUserId);

    console.log('Fetching from endpoint:', endpoint, 'with params:', params.toString());

    // API 호출
    fetch(`${endpoint}?${params.toString()}`, {
        headers: {
            [header]: token,
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Received data:', data);
            if (data && data.length > 0) {
                // 아직 보여주지 않은 파트너만 필터링
                const remainingPartners = data.filter(partner =>
                    !shownPartnerIds.includes(role === 'ADMIN' ? partner.sellerId : partner.adminId)
                );

                if (remainingPartners.length > 0) {
                    displayRandomPartner(remainingPartners, role);
                } else {
                    // 모든 파트너를 다 보여줬을 경우
                    alert('더 이상 매칭할 파트너가 없습니다.');
                    $('.matchModal').hide(); // 모달 닫기
                    // shownPartnerIds 초기화 (다음 매칭 시도를 위해)
                    shownPartnerIds = [];
                }
            } else {
                showNoMatchMessage(role);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showErrorMessage(role);
        });
}

// 현재 사용자의 카테고리 가져오기
function getCurrentUserCategories() {
    // 현재 페이지의 카테고리 정보를 다양한 선택자로 시도
    let categoriesElements = document.querySelectorAll('.myInfo .category li');
    if (!categoriesElements.length) {
        categoriesElements = document.querySelectorAll('.category li');
    }
    if (!categoriesElements.length) {
        categoriesElements = document.querySelectorAll('[class*="category"] li');
    }

    console.log('Found category elements:', categoriesElements);

    if (!categoriesElements.length) {
        console.error('카테고리 요소를 찾을 수 없습니다.');
        return [];
    }

    const categories = Array.from(categoriesElements)
        .map(li => li.textContent.trim())
        .filter(category => category); // 빈 문자열 제거

    console.log('Extracted categories:', categories);
    return categories;
}

// 카테고리가 일치하는 파트너 필터링
function filterMatchingPartners(partners) {
    const currentUserCategories = getCurrentUserCategories();
    console.log('Current user categories:', currentUserCategories);

    if (!currentUserCategories || currentUserCategories.length === 0) {
        console.warn('No categories found for current user');
        return partners; // 카테고리가 없으면 모든 파트너 반환
    }

    const matchedPartners = partners.filter(partner => {
        console.log('Checking partner:', partner);
        console.log('Partner categories:', partner.productCategories);

        // 파트너의 카테고리가 현재 사용자의 카테고리와 하나라도 일치하는지 확인
        return partner.productCategories.some(partnerCategory =>
            currentUserCategories.includes(partnerCategory)
        );
    });

    console.log('Matched partners after filtering:', matchedPartners);
    return matchedPartners;
}

// 랜덤 파트너 표시
function displayRandomPartner(partners, role) {
    const randomIndex = Math.floor(Math.random() * partners.length);
    const partner = partners[randomIndex];
    const modalClass = role === 'ADMIN' ? '.matchSellerModal' : '.matchAdminModal';
    const modal = document.querySelector(modalClass);

    console.log('Displaying partner:', partner);
    console.log('Using modal:', modalClass);

    if (!modal) {
        console.error('모달을 찾을 수 없습니다.');
        return;
    }

    currentMatchedPartnerId = role === 'ADMIN' ? partner.sellerId : partner.adminId;

    // 표시된 파트너 ID 저장
    shownPartnerIds.push(currentMatchedPartnerId);

    // 모달 내용 업데이트
    const titleElement = modal.querySelector('.titWrap .tit');
    const categoryElement = modal.querySelector('.category');
    const infoBoxElement = modal.querySelector('.infoBox');
    const snsLinkElement = modal.querySelector('.snsLink');

    if (titleElement) {
        const name = role === 'ADMIN' ? partner.sellerNickName : partner.adminShopName;
        titleElement.textContent = name || '이름 없음';
        console.log('Updated title:', name);
    }

    if (categoryElement) {
        const categoryHtml = partner.productCategories
            .map(cat => `<li>${cat}</li>`).join('');
        categoryElement.innerHTML = categoryHtml;
        console.log('Updated categories:', categoryHtml);
    }

    if (infoBoxElement) {
        console.log('Partner data:', partner);

        let contents;
        if (role === 'ADMIN') {
            contents = partner.sellerContents;
        } else {
            contents = partner.adminContents;
        }

        // 빈 값 체크를 더 엄격하게 수정
        infoBoxElement.textContent = (contents !== undefined && contents !== null && contents !== '')
            ? contents
            : '소개글이 없습니다.';

        console.log('Role:', role);
        console.log('Contents field:', contents);
    }

    if (snsLinkElement) {
        const url = role === 'ADMIN' ? partner.sellerUrl : partner.adminUrl;
        if (url) {
            // URL에 프로토콜이 있는지 확인하고 없으면 추가
            const formattedUrl = url.startsWith('http://') || url.startsWith('https://')
                ? url
                : `https://${url}`;

            snsLinkElement.href = formattedUrl;
            snsLinkElement.textContent = url; // 표시되는 텍스트는 원래 URL 유지
            snsLinkElement.style.pointerEvents = 'auto';
            snsLinkElement.style.cursor = 'pointer';
            snsLinkElement.target = '_blank'; // 새 탭에서 열기
        } else {
            snsLinkElement.removeAttribute('href');
            snsLinkElement.textContent = '등록된 URL이 없습니다.';
            snsLinkElement.style.pointerEvents = 'none';
            snsLinkElement.style.cursor = 'default';
        }
    }

    // 모달 표시
    $(modalClass).show();
}

// 매칭 없음 메시지 표시
function showNoMatchMessage(role) {
    const modalClass = role === 'ADMIN' ? '.matchSellerModal' : '.matchAdminModal';
    const modal = document.querySelector(modalClass);
    if (modal) {
        modal.querySelector('.modalCont').innerHTML =
            '<p class="none">매칭 가능한 파트너가 없습니다.</p>';
        $(modalClass).show();
    }
}

// 에러 메시지 표시
function showErrorMessage(role) {
    const modalClass = role === 'ADMIN' ? '.matchSellerModal' : '.matchAdminModal';
    const modal = document.querySelector(modalClass);
    if (modal) {
        modal.querySelector('.modalCont').innerHTML =
            '<p class="none">매칭 중 오류가 발생했습니다.</p>';
        $(modalClass).show();
    }
}

// 이벤트 리스너 등록
$(document).ready(function() {
    // 매칭 버튼 클릭 이벤트
    $('.matchAdmin, .matchSeller').click(function() {
        const role = $(this).hasClass('matchAdmin') ? 'SELLER' : 'ADMIN';
        // 새로운 매칭 시작 시 shownPartnerIds 초기화
        shownPartnerIds = [];
        initializeMatchModal(role);
    });

    // 다시 매칭하기 버튼 클릭 이벤트
    $('.rematchBtn').click(function() {
        const role = $(this).closest('.matchAdminModal').length > 0 ? 'SELLER' : 'ADMIN';
        initializeMatchModal(role);
    });

    // 협업 요청하기 버튼 클릭 이벤트
    $('.requestMatchBtn').click(function() {
        if (!currentMatchedPartnerId) {
            alert('매칭된 파트너가 없습니다.');
            return;
        }

        const currentUserId = $('#currentUserId').val();
        const role = $(this).closest('.matchAdminModal').length > 0 ? 'SELLER' : 'ADMIN';

        // 협업 요청 API 호출
        fetch('/manage/request', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [document.getElementById("csrf-header").value]: document.getElementById("csrf-token").value
            },
            body: `requesterId=${currentUserId}&targetId=${currentMatchedPartnerId}&role=${role}`
        })
            .then(response => {
                if (response.ok) {
                    alert('협업 요청이 전송되었습니다.');
                    // 매칭 모달 닫고 요청 모달 열기
                    $('.matchModal').hide();
                    $('.requestModal').show();
                    // 요청 목록 새로고침
                    loadRequestModalData();
                } else {
                    alert('협업 요청 전송에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('협업 요청 처리 중 오류가 발생했습니다.');
            });
    });

    // 닫기 버튼 클릭 이벤트
    $('.closeBtn').click(function() {
        $(this).closest('.modal').hide();
    });
});
