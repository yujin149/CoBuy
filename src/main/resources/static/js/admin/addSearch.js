// 쇼핑몰 검색 (모달용)
function searchAdmins() {
    const searchType = document.getElementById('adminSearchType').value;
    const searchInput = document.getElementById('adminSearchInput').value;
    const currentUserId = document.getElementById('currentUserId').value;

    const tbody = document.getElementById('adminSearchResults');
    if (!tbody) return; // 모달이 없는 경우 종료

    tbody.innerHTML = '';

    // 검색어가 비어있는지 체크
    if (!searchInput.trim()) {
        tbody.innerHTML = `
            <tr>
                <td colspan="3"><p class="none">검색어를 입력해주세요.</p></td>
            </tr>`;
        return;
    }

    // CSRF 토큰 가져오기
    const token = document.getElementById("csrf-token").value;
    const header = document.getElementById("csrf-header").value;

    // 검색 타입에 따라 파라미터 설정
    let params = new URLSearchParams({
        searchType: searchType,
        adminId: searchInput,        // 전체 검색일 때도 검색어를 adminId로 전달
        adminShopName: searchInput,   // 전체 검색일 때도 검색어를 adminShopName으로 전달
        currentUserId: currentUserId
    });

    fetch(`/admin/search?${params.toString()}`, {
        headers: {
            [header]: token
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(response => {
            tbody.innerHTML = ''; // 기존 결과 초기화
            const data = response.partners; // 변경된 부분: partners 배열 사용

            if (!data || data.length === 0) {
                tbody.innerHTML = `
                <tr>
                    <td colspan="3"><p class="none">검색 결과가 없습니다.</p></td>
                </tr>`;
                return;
            }

            data.forEach(admin => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                <td>
                    <div class="chkBox">
                        <input type="checkbox" class="chk" value="${admin.adminId}">
                    </div>
                </td>
                <td>
                    <div class="info">
                        <p class="name">${admin.adminShopName}</p>
                        <ul class="category">
                            ${admin.productCategories.map(category => `<li>${category}</li>`).join('')}
                        </ul>
                    </div>
                </td>
                <td>
                    <a href="${admin.adminUrl}" target="_blank" class="url">${admin.adminUrl}</a>
                </td>
            `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Error:', error);
            tbody.innerHTML = `
            <tr>
                <td colspan="3"><p class="none">검색 중 오류가 발생했습니다.</p></td>
            </tr>`;
        });
}

// 인플루언서 검색 (모달용)
function searchSellers() {
    const searchType = document.getElementById('sellerSearchType').value;
    const searchInput = document.getElementById('sellerSearchInput').value;
    const currentUserId = document.getElementById('currentUserId').value;

    const tbody = document.getElementById('sellerSearchResults');
    if (!tbody) return; // 모달이 없는 경우 종료

    tbody.innerHTML = '';

    // 검색어가 비어있는지 체크
    if (!searchInput.trim()) {
        tbody.innerHTML = `
            <tr>
                <td colspan="3"><p class="none">검색어를 입력해주세요.</p></td>
            </tr>`;
        return;
    }

    // CSRF 토큰 가져오기
    const token = document.getElementById("csrf-token").value;
    const header = document.getElementById("csrf-header").value;

    // 검색 타입에 따라 파라미터 설정
    let params = new URLSearchParams({
        searchType: searchType,
        sellerId: searchInput,      // 전체 검색일 때도 검색어를 sellerId로 전달
        sellerNickName: searchInput,  // 전체 검색일 때도 검색어를 sellerNickName으로 전달
        currentUserId: currentUserId
    });

    fetch(`/seller/search?${params.toString()}`, {
        headers: {
            [header]: token
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(response => {
            tbody.innerHTML = '';
            const data = response.partners; // 변경된 부분: partners 배열 사용

            if (!data || data.length === 0) {
                tbody.innerHTML = `
                <tr>
                    <td colspan="3"><p class="none">검색 결과가 없습니다.</p></td>
                </tr>`;
                return;
            }

            data.forEach(seller => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                <td>
                    <div class="chkBox">
                        <input type="checkbox" class="chk" value="${seller.sellerId}">
                    </div>
                </td>
                <td>
                    <div class="info">
                        <p class="name">${seller.sellerNickName}</p>
                        <ul class="category">
                            ${seller.productCategories.map(category => `<li>${category}</li>`).join('')}
                        </ul>
                    </div>
                </td>
                <td>
                    <a href="${seller.sellerUrl}" target="_blank" class="url">${seller.sellerUrl}</a>
                </td>
            `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Error:', error);
            tbody.innerHTML = `
            <tr>
                <td colspan="3"><p class="none">검색 중 오류가 발생했습니다.</p></td>
            </tr>`;
        });
}

// 추가 버튼 클릭 시 실행될 함수
function sendManageRequest(role) {
    const modal = role === 'ADMIN' ? 'adminSearchResults' : 'sellerSearchResults';
    const selectedCheckboxes = document.querySelectorAll(`#${modal} input[type="checkbox"]:checked`);

    selectedCheckboxes.forEach(checkbox => {
        const targetId = checkbox.value;
        const requesterId = document.getElementById('currentUserId').value;

        fetch('/manage/request', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                requesterId: requesterId,
                targetId: targetId,
                role: role
            })
        })
            .then(response => {
                if (response.ok) {
                    alert('협업 요청이 성공적으로 전송되었습니다.');
                    // 모달 닫기
                    closeModal();
                } else {
                    alert('협업 요청 전송에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('협업 요청 전송 중 오류가 발생했습니다.');
            });
    });
}

// 검색 이벤트 설정 함수 추가
function setupModalSearch() {
    // 쇼핑몰 검색 모달
    const adminSearchInput = document.getElementById('adminSearchInput');
    if (adminSearchInput) {
        adminSearchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault(); // 폼 제출 방지
                searchAdmins();
            }
        });
    }

    // 인플루언서 검색 모달
    const sellerSearchInput = document.getElementById('sellerSearchInput');
    if (sellerSearchInput) {
        sellerSearchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault(); // 폼 제출 방지
                searchSellers();
            }
        });
    }
}

// 페이지 로드 시 검색 이벤트 설정
document.addEventListener('DOMContentLoaded', function() {
    setupModalSearch();
});