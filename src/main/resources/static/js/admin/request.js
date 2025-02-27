// 전역 변수로 현재 페이지 상태 관리
let currentPage = 1;
const itemsPerPage = 5;

// fetch 요청에 CSRF 헤더 추가
const csrfToken = document.getElementById('csrf-token').value;
const csrfHeader = document.getElementById('csrf-header').value;

// 모달 닫기 함수
function closeModal() {
    $('.addModal').hide();
}

// 테이블 헤더 업데이트 함수
function updateTableHeader(userRole) {
    const partnerNameTh = document.querySelector('.requestModal .tableWrap table thead th.partnerName');
    const urlTh = document.querySelector('.requestModal .tableWrap table thead th.url');

    if (userRole === 'ADMIN') {
        partnerNameTh.textContent = '인플루언서명';
        urlTh.textContent = 'SNS 주소';
    } else {
        partnerNameTh.textContent = '쇼핑몰명';
        urlTh.textContent = '홈페이지 주소';
    }
}

// 요청 모달 데이터 로드 함수
function loadRequestModalData() {
    const currentUserId = document.getElementById('currentUserId').value;
    const currentUserRole = document.getElementById('currentUserRole').value;

    // 테이블 헤더 업데이트
    updateTableHeader(currentUserRole);

    // 받은 요청과 보낸 요청 모두 가져오기
    Promise.all([
        fetch(`/manage/received-requests?id=${currentUserId}&role=${currentUserRole}&page=0&size=1000`, {
            headers: {
                [csrfHeader]: csrfToken
            }
        }),
        fetch(`/manage/sent-requests?requester=${currentUserId}&page=0&size=1000`, {
            headers: {
                [csrfHeader]: csrfToken
            }
        })
    ])
        .then(responses => Promise.all(responses.map(res => res.json())))
        .then(([receivedRequests, sentRequests]) => {
            // 모든 요청을 합치고 날짜순으로 정렬
            const allRequests = [...receivedRequests.content, ...sentRequests.content]
                .sort((a, b) => new Date(b.regTime) - new Date(a.regTime));

            // 클라이언트 측에서 페이징 처리
            const totalElements = allRequests.length;
            const totalPages = Math.max(1, Math.ceil(totalElements / itemsPerPage));

            // 현재 페이지에 해당하는 요청만 선택
            const startIndex = (currentPage - 1) * itemsPerPage;
            const endIndex = Math.min(startIndex + itemsPerPage, totalElements);
            const pageRequests = allRequests.slice(startIndex, endIndex);

            displayRequests(pageRequests, totalPages);
        })
        .catch(error => {
            console.error('Error loading requests:', error);
            showNoResults();
        });
}

// 요청 목록 표시 함수
function displayRequests(requests, totalPages) {
    const tbody = document.querySelector('.requestModal .tableWrap table tbody');
    const currentUserId = document.getElementById('currentUserId').value;
    const currentUserRole = document.getElementById('currentUserRole').value;

    if (!requests || requests.length === 0) {
        showNoResults();
        return;
    }

    tbody.innerHTML = requests.map(request => {
        // 현재 사용자가 요청자인지 확인
        const isRequester = request.requester === currentUserId;

        // 표시할 정보 결정 (현재 사용자의 역할이 아닌, 상대방의 정보를 표시)
        const displayInfo = isRequester ?
            // 내가 요청자인 경우 상대방(수신자) 정보 표시
            (request.requesterRole === 'ADMIN' ?
                    {
                        name: request.sellerNickName,
                        url: request.sellerUrl,
                        categories: request.sellerCategories
                    } :
                    {
                        name: request.adminShopName,
                        url: request.adminUrl,
                        categories: request.adminCategories
                    }
            ) :
            // 내가 수신자인 경우 요청자 정보 표시
            (request.requesterRole === 'ADMIN' ?
                    {
                        name: request.adminShopName,
                        url: request.adminUrl,
                        categories: request.adminCategories
                    } :
                    {
                        name: request.sellerNickName,
                        url: request.sellerUrl,
                        categories: request.sellerCategories
                    }
            );

        return `
            <tr>
                <td>
                    <div class="info">
                        <p class="name">${displayInfo.name}</p>
                        <ul class="category">
                            ${(displayInfo.categories || [])
            .map(category => `<li>${category}</li>`).join('')}
                        </ul>
                    </div>
                </td>
                <td>
                    <a href="${displayInfo.url}" target="_blank" class="url">
                        ${displayInfo.url}
                    </a>
                </td>
                <td>
                    <div class="btnWrap">
                        ${getActionButton(request, isRequester)}
                    </div>
                </td>
            </tr>
        `;
    }).join('');

    updatePagination(totalPages);
}

// 상태 텍스트와 클래스 변환 함수
function getStatusText(status, isRequester) {
    const statusConfig = {
        'PENDING': {
            text: '요청대기중',
            class: 'status'
        },
        'ACCEPTED': {
            text: isRequester ? '수락됨' : '요청수락',
            class: 'status status01'
        },
        'REJECTED': {
            text: isRequester ? '거절됨' : '요청거절',
            class: 'status status02'
        },
        'CANCELED': {
            text: isRequester ? '취소됨' : '취소됨',
            class: 'status status02'
        }
    };
    return statusConfig[status] || { text: status, class: 'status' };
}

// 액션 버튼 생성 함수
function getActionButton(request, isRequester) {
    if (request.status === 'PENDING') {
        if (isRequester) {
            return '<span class="status">요청대기중</span>';
        } else {
            return `
                <button class="acceptBtn" onclick="respondToRequest(${request.id}, 'ACCEPTED')">수락</button>
                <button class="rejectBtn" onclick="respondToRequest(${request.id}, 'REJECTED')">거절</button>
            `;
        }
    } else {
        const status = getStatusText(request.status, isRequester);
        return `<span class="${status.class}">${status.text}</span>`;
    }
}

// 페이지네이션 업데이트 함수
function updatePagination(totalPages) {
    const paginationContainer = document.querySelector('.requestModal .pagination');
    if (!paginationContainer) return;

    const maxVisiblePages = 5;

    // 시작 페이지와 끝 페이지 계산
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    // startPage 재조정
    if (endPage - startPage + 1 < maxVisiblePages) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    // 전체 페이지네이션 HTML 생성
    let html = `
        <li class="page-item page-arrow page-prev ${currentPage <= 1 ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" 
               ${currentPage > 1 ? `onclick="changePage(${currentPage - 1})"` : ''}
               style="cursor: ${currentPage <= 1 ? 'auto' : 'pointer'}">이전 페이지</a>
        </li>
        <li class="page-item page-num">
    `;

    // 페이지 번호 생성
    for (let i = startPage; i <= endPage; i++) {
        html += `
            <a class="page-link ${i === currentPage ? 'active' : ''}" 
               href="javascript:void(0)" 
               onclick="changePage(${i})">${i}</a>
        `;
    }

    html += `
        </li>
        <li class="page-item page-arrow page-next ${currentPage >= totalPages ? 'disabled' : ''}">
            <a class="page-link" href="javascript:void(0)" 
               ${currentPage < totalPages ? `onclick="changePage(${currentPage + 1})"` : ''}
               style="cursor: ${currentPage >= totalPages ? 'auto' : 'pointer'}">다음 페이지</a>
        </li>
    `;

    paginationContainer.innerHTML = html;
}

// 페이지 변경 함수
function changePage(page) {
    currentPage = page;
    loadRequestModalData();
}

// 결과 없음 표시 함수
function showNoResults() {
    const tbody = document.querySelector('.requestModal .tableWrap table tbody');
    tbody.innerHTML = `
        <tr>
            <td colspan="3"><p class="none">요청 내역이 없습니다.</p></td>
        </tr>
    `;
}

// 요청 응답 처리 함수
function respondToRequest(requestId, status) {
    // 상태에 따른 확인 메시지
    const confirmMessage = status === 'ACCEPTED' ?
        '요청을 수락하시겠습니까?' :
        '요청을 거절하시겠습니까?';

    // 사용자 확인
    if (!confirm(confirmMessage)) {
        return; // 취소를 누르면 함수 종료
    }

    fetch(`/manage/respond/${requestId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            [csrfHeader]: csrfToken
        },
        body: `status=${status}`
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Request failed');
            }
            loadRequestModalData();  // 목록 새로고침
            updatePendingRequestCount(); // 알림 숫자 업데이트 추가
        })
        .catch(error => {
            console.error('Error:', error);
            alert('처리 중 오류가 발생했습니다.');
        });
}

// 모달 열기 이벤트 핸들러
$(document).ready(function() {
    // 요청 모달 열기
    $('.requestBtn').click(function() {
        loadRequestModalData();
    });

    // Add 버튼 클릭 시 처리
    $('.addModal .addBtn').click(function() {
        const modal = $(this).closest('.addModal');
        const role = modal.hasClass('addAdminModal') ? 'SELLER' : 'ADMIN';
        const checkedBoxes = modal.find('input.chk:checked');

        if (checkedBoxes.length === 0) {
            alert('선택된 항목이 없습니다.');
            return;
        }

        const promises = Array.from(checkedBoxes).map(checkbox => {
            const targetId = checkbox.value;
            const requesterId = document.getElementById('currentUserId').value;

            console.log('Request Data:', {
                requesterId: requesterId,
                targetId: targetId,
                role: role
            });

            return fetch('/manage/request', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    [csrfHeader]: csrfToken
                },
                body: `requesterId=${encodeURIComponent(requesterId)}&targetId=${encodeURIComponent(targetId)}&role=${encodeURIComponent(role)}`
            });
        });

        Promise.all(promises)
            .then(responses => {
                return Promise.all(responses.map(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            throw new Error(text || 'Request failed');
                        });
                    }
                    return response;
                }));
            })
            .then(() => {
                alert('협업 요청이 전송되었습니다.');
                modal.hide();
                $('.requestModal').show();
                loadRequestModalData();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('요청 처리 중 오류가 발생했습니다: ' + error.message);
            });
    });
});