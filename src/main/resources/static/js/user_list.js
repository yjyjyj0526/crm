document.addEventListener("DOMContentLoaded", () => {
    const registerButton = document.getElementById("registerButton");
    const registrationContainer = document.getElementById("registrationContainer");
    const detailContainer = document.getElementById("detailContainer");
    const editContainer = document.getElementById("editContainer");
    const content = document.getElementById("contentMain");
    const cancelButton = document.getElementById("cancelButton");
    const detailCancelButton = document.getElementById("detailCancelButton");
    const editCancelButton = document.getElementById("editCancelButton");
    const loadingSpinner = document.getElementById("loadingSpinner");
    const userTable = document.getElementById("userTable");

    // 폼 보이기 및 숨기기 로직
    registerButton.addEventListener("click", () => {
        hideAllForms();
        showForm(registrationContainer);
    });

    cancelButton.addEventListener("click", () => {
        hideForm(registrationContainer);
    });

    detailCancelButton.addEventListener("click", () => {
        hideForm(detailContainer);
    });

    editCancelButton.addEventListener("click", () => {
        hideForm(editContainer);
    });

    // 이벤트 위임을 사용하여 상세 버튼 및 수정 버튼 클릭 이벤트 처리
    document.addEventListener("click", (event) => {
        if (event.target.classList.contains("detailButton")) {
            const userId = event.target.getAttribute("data-id");
            fetchUserDetails(userId);
        } else if (event.target.classList.contains("editButton")) {
            const userId = event.target.getAttribute("data-id");
            fetchUserEditForm(userId);
        }
    });

    // 폼 보이기
    function showForm(container) {
        container.classList.add("visible");
        content.classList.add("shrink");
    }

    // 폼 숨기기
    function hideForm(container) {
        container.classList.remove("visible");
        content.classList.remove("shrink");
    }

    // 모든 폼 숨기기
    function hideAllForms() {
        hideForm(registrationContainer);
        hideForm(detailContainer);
        hideForm(editContainer);
    }

    // 사용자 상세 정보 요청
    function fetchUserDetails(userId) {
        fetch(`/user/details/${userId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                fillDetailForm(data);
                hideAllForms();
                showForm(detailContainer);
            })
            .catch(error => console.error('Error fetching user details:', error));
    }

    // 사용자 수정 폼 요청
    function fetchUserEditForm(userId) {
        fetch(`/user/edit/${userId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                fillEditForm(data);
                hideAllForms();
                showForm(editContainer);
            })
            .catch(error => console.error('Error fetching user edit form:', error));
    }

    // 상세 정보 폼 채우기
    function fillDetailForm(data) {
        document.getElementById("detail_user_id").value = data.user_id;
        document.getElementById("detail_user_name").value = data.user_name;
        document.getElementById("detail_phone_number").value = data.phone_number;
        document.getElementById("detail_department").value = data.department;
        document.getElementById("detail_authority").value = data.authority;
    }

    // 수정 폼 채우기
    function fillEditForm(data) {
        document.getElementById("edit_user_id").value = data.user_id;
        document.getElementById("edit_user_name").value = data.user_name;
        document.getElementById("edit_phone_number").value = data.phone_number;
        document.getElementById("edit_department").value = data.department;
        document.getElementById("edit_authority").value = data.authority;
    }

    // 사용자 리스트 및 페이지네이션 업데이트
    function loadData(page = 1) {
        if (!loadingSpinner || !userTable) {
            console.error("Loading spinner or user table element not found");
            return;
        }

        // 로딩 스피너 표시 및 테이블 숨기기
        loadingSpinner.style.display = 'block';
        userTable.style.display = 'none';

        const categorySelect = document.getElementById('categorySelect').value;
        const searchText = document.getElementById('searchText').value;
        const url = `/user/list/json?page=${page}&categorySelect=${categorySelect}&searchText=${searchText}&order=&orderDirection=ASC&countPerPage=10`;

        fetch(url)
            .then(response => response.json())
            .then(data => {
                console.log('Data fetched:', data);
                updateTable(data.list);
                updatePagination(data.navi);

                // 로딩 스피너 숨기기 및 테이블 표시
                loadingSpinner.style.display = 'none';
                userTable.style.display = 'table';
            })
            .catch(error => {
                console.error('Error fetching or parsing data:', error);
                // 에러 발생 시 로딩 스피너 숨기기
                loadingSpinner.style.display = 'none';
                // 필요한 경우 에러 메시지 표시
            });
    }

    // 사용자 테이블 업데이트
    function updateTable(users) {
        const tableBody = document.getElementById('userTableBody');
        tableBody.innerHTML = '';

        if (users.length === 0) {
            // 유저 리스트가 비어있는 경우 처리
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = `<td colspan="6">No users found.</td>`;
            tableBody.appendChild(emptyRow);
        } else {
            users.forEach(user => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${user.user_id}</td>
                    <td>${user.user_name}</td>
                    <td>${user.phone_number}</td>
                    <td>${user.department}</td>
                    <td>${user.authority}</td>
                    <td class="align-middle text-center">
                        <button class="btn btn-info btn-sm me-2 detailButton" data-id="${user.user_id}">상세</button>
                        <button class="btn btn-warning btn-sm editButton" data-id="${user.user_id}">수정</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        }
    }

    // 페이지네이션 업데이트
    function updatePagination(navi) {
        const paginationContainer = document.getElementById('paginationContainer');
        if (!paginationContainer) {
            console.error('Pagination container not found');
            return;
        }

        const paginationList = paginationContainer.querySelector('ul.pagination');
        if (!paginationList) {
            console.error('Pagination list not found');
            return;
        }
        paginationList.innerHTML = '';

        if (navi.currentPage > 1) {
            const prev = document.createElement('li');
            prev.innerHTML = `<a class="btn btn-secondary" href="#" data-page="${navi.currentPage - 1}"> < </a>`;
            paginationList.appendChild(prev);
            prev.querySelector('a').addEventListener('click', (e) => {
                e.preventDefault();
                loadData(navi.currentPage - 1);
            });
        }

        for (let i = navi.startPageGroup; i <= navi.endPageGroup; i++) {
            const pageItem = document.createElement('li');
            pageItem.innerHTML = `<a class="btn btn-secondary ${i === navi.currentPage ? 'active' : ''}" href="#" data-page="${i}">${i}</a>`;
            paginationList.appendChild(pageItem);
            pageItem.querySelector('a').addEventListener('click', (e) => {
                e.preventDefault();
                loadData(i);
            });
        }

        if (navi.currentPage < navi.totalPageCount) {
            const next = document.createElement('li');
            next.innerHTML = `<a class="btn btn-secondary" href="#" data-page="${navi.currentPage + 1}"> > </a>`;
            paginationList.appendChild(next);
            next.querySelector('a').addEventListener('click', (e) => {
                e.preventDefault();
                loadData(navi.currentPage + 1);
            });
        }
    }

    // 초기 데이터 로딩
    loadData();
});