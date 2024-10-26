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
    const searchForm = document.getElementById("searchForm");

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

    // 검색 폼 제출 이벤트 설정
    searchForm.addEventListener("submit", (event) => {
        event.preventDefault();
        loadData(1); // 검색 시 첫 페이지부터 시작
    });

    // 이벤트 위임을 사용하여 상세 버튼 및 수정 버튼 클릭 이벤트 처리
    document.addEventListener("click", (event) => {
        if (event.target.classList.contains("detailButton")) {
            const user_id = event.target.getAttribute("data-id");
            fetchUserDetails(user_id);
        } else if (event.target.classList.contains("editButton")) {
            const user_id = event.target.getAttribute("data-id");
            fetchUserEditForm(user_id);
        }
    });

    function showForm(container) {
        container.classList.add("visible");
        content.classList.add("shrink");
    }

    function hideForm(container) {
        container.classList.remove("visible");
        content.classList.remove("shrink");
    }

    function hideAllForms() {
        hideForm(registrationContainer);
        hideForm(detailContainer);
        hideForm(editContainer);
    }

    function fetchUserDetails(user_id) {
        if (!user_id) {
            console.error('User ID is missing');
            return;
        }

        fetch(`/user/details/${user_id}`)
            .then(response => response.json())
            .then(data => {
                fillDetailForm(data);
                hideAllForms();
                showForm(detailContainer);
            })
            .catch(error => console.error('Error fetching user details:', error));
    }

    function fetchUserEditForm(user_id) {
        if (!user_id) {
            console.error('User ID is missing');
            return;
        }

        fetch(`/user/edit/${user_id}`)
            .then(response => response.json())
            .then(data => {
                fillEditForm(data);
                hideAllForms();
                showForm(editContainer);
            })
            .catch(error => console.error('Error fetching user edit form:', error));
    }

    function fillDetailForm(data) {
        document.getElementById("detail_user_id").value = data.user_id;
        document.getElementById("detail_user_name").value = data.user_name;
        document.getElementById("detail_phone_number").value = data.phone_number;
        document.getElementById("detail_department").value = data.department;
        document.getElementById("detail_authority").value = data.authority;
    }

    function fillEditForm(data) {
        document.getElementById("edit_user_id").value = data.user_id;
        document.getElementById("edit_user_name").value = data.user_name;
        document.getElementById("edit_phone_number").value = data.phone_number;
        document.getElementById("edit_department").value = data.department;
        document.getElementById("edit_authority").value = data.authority;
    }

    function loadData(page = 1) {
        if (!loadingSpinner || !userTable) {
            console.error("Loading spinner or user table element not found");
            return;
        }

        loadingSpinner.style.display = 'block';
        userTable.style.display = 'none';

        const categorySelect = document.getElementById('categorySelect').value;
        const searchTextElement = document.getElementById('searchText');
        const searchText = searchTextElement ? searchTextElement.value.trim() : ""; // 검색어가 없는 경우 빈 문자열로 설정

        const url = `/user/list/json?page=${page}&categorySelect=${categorySelect}&searchText=${encodeURIComponent(searchText)}&order=&orderDirection=ASC&countPerPage=10`;

        fetch(url)
            .then(response => response.json())
            .then(data => {
                console.log("data received", data); // 이 부분에 로그를 추가하여 데이터 확인
                if (data.list) {
                    updateTable(data.list);
                } else {
                    console.warn("User list is missing");
                }
                if (data.navi) {
                    updatePagination(data.navi);
                } else {
                    console.warn("Pagination data is missing");
                }
                loadingSpinner.style.display = 'none';
                userTable.style.display = 'table';
            })
            .catch(error => {
                console.error('Error fetching or parsing data:', error);
                loadingSpinner.style.display = 'none';
            });
    }

    function updateTable(users) {
        const tableBody = document.getElementById('userTableBody');
        tableBody.innerHTML = '';

        if (users.length === 0) {
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
          <td>
            <button class="btn btn-info detailButton" data-id="${user.user_id}">상세</button>
            <button class="btn btn-warning editButton" data-id="${user.user_id}">수정</button>
          </td>
        `;
                tableBody.appendChild(row);
            });

            const emptyRowsCount = 10 - users.length;
            for (let i = 0; i < emptyRowsCount; i++) {
                const emptyRow = document.createElement('tr');
                emptyRow.innerHTML = `<td colspan="6">&nbsp;</td>`;
                tableBody.appendChild(emptyRow);
            }
        }
    }

    function updatePagination(navi) {
        const paginationContainer = document.querySelector('.pagination');
        paginationContainer.innerHTML = '';

        if (!navi.pages) {
            // navi.pages가 없을 경우 직접 생성
            const totalPages = navi.totalPageCount;
            navi.pages = Array.from({ length: totalPages }, (_, i) => i + 1);
        }

        const firstPageItem = document.createElement('li');
        firstPageItem.classList.add('page-item');
        const firstPageLink = document.createElement('a');
        firstPageLink.classList.add('page-link');
        firstPageLink.href = '#';
        firstPageLink.textContent = '<<';
        firstPageLink.addEventListener('click', (event) => {
            event.preventDefault();
            loadData(1);
        });
        firstPageItem.appendChild(firstPageLink);
        paginationContainer.appendChild(firstPageItem);

        const prevPageItem = document.createElement('li');
        prevPageItem.classList.add('page-item');
        const prevPageLink = document.createElement('a');
        prevPageLink.classList.add('page-link');
        prevPageLink.href = '#';
        prevPageLink.textContent = '<';
        prevPageLink.addEventListener('click', (event) => {
            event.preventDefault();
            if (navi.currentPage > 1) {
                loadData(navi.currentPage - 1);
            }
        });
        prevPageItem.appendChild(prevPageLink);
        paginationContainer.appendChild(prevPageItem);

        navi.pages.forEach(page => {
            const pageItem = document.createElement('li');
            pageItem.classList.add('page-item');
            if (page === navi.currentPage) {
                pageItem.classList.add('active');
            }

            const pageLink = document.createElement('a');
            pageLink.classList.add('page-link');
            pageLink.href = '#';
            pageLink.textContent = page;
            pageLink.addEventListener('click', (event) => {
                event.preventDefault();
                loadData(page);
            });

            pageItem.appendChild(pageLink);
            paginationContainer.appendChild(pageItem);
        });

        const nextPageItem = document.createElement('li');
        nextPageItem.classList.add('page-item');
        const nextPageLink = document.createElement('a');
        nextPageLink.classList.add('page-link');
        nextPageLink.href = '#';
        nextPageLink.textContent = '>';
        nextPageLink.addEventListener('click', (event) => {
            event.preventDefault();
            if (navi.currentPage < navi.totalPageCount) {
                loadData(navi.currentPage + 1);
            }
        });
        nextPageItem.appendChild(nextPageLink);
        paginationContainer.appendChild(nextPageItem);

        const lastPageItem = document.createElement('li');
        lastPageItem.classList.add('page-item');
        const lastPageLink = document.createElement('a');
        lastPageLink.classList.add('page-link');
        lastPageLink.href = '#';
        lastPageLink.textContent = '>>';
        lastPageLink.addEventListener('click', (event) => {
            event.preventDefault();
            loadData(navi.totalPageCount);
        });
        lastPageItem.appendChild(lastPageLink);
        paginationContainer.appendChild(lastPageItem);
    }

    loadData();
});