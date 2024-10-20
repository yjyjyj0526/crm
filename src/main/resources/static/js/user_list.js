document.addEventListener("DOMContentLoaded", () => {
    const registerButton = document.getElementById("registerButton");
    const registrationContainer = document.getElementById("registrationContainer");
    const content = document.getElementById("contentMain");
    const cancelButton = document.getElementById("cancelButton");

    function loadData(page = 1) {
        const url = `/user/list/json?page=${page}&categorySelect=&searchText=&order=&orderDirection=ASC&countPerPage=10&formOpen=`;

        fetch(url) // JSON 형식으로 데이터를 가져옴
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json(); // JSON으로 파싱
            })
            .then(data => {
                updateTable(data.list);
                updatePagination(data.navi);
            })
            .catch(error => console.error('Error fetching or parsing data:', error)); // 오류 처리
    }

    function updateTable(users) {
        const tableBody = document.getElementById('userTableBody');
        tableBody.innerHTML = ''; // 기존 내용을 지움

        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
        <td>${user.user_id}</td>
        <td>${user.user_name}</td>
        <td>${user.phone_number}</td>
        <td>${user.department}</td>
        <td>${user.authority}</td>
      `;
            tableBody.appendChild(row);
        });

        const emptyRows = 10 - users.length;
        for (let i = 0; i < emptyRows; i++) {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = `
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      `;
            tableBody.appendChild(emptyRow);
        }
    }

    function updatePagination(navi) {
        const paginationContainer = document.getElementById('paginationContainer');
        paginationContainer.innerHTML = '';

        if (navi.totalPageCount > 1) {
            const pagination = document.createElement('ul');
            pagination.classList.add('pagination');

            const firstPage = document.createElement('li');
            firstPage.classList.add('page-item', navi.currentPage == 1 ? 'disabled' : '');
            firstPage.innerHTML = `<a class="page-link" href="#" data-page="1">&laquo;</a>`;
            pagination.appendChild(firstPage);

            const prevPage = document.createElement('li');
            prevPage.classList.add('page-item', navi.currentPage == 1 ? 'disabled' : '');
            prevPage.innerHTML = `<a class="page-link" href="#" data-page="${navi.currentPage - 1}">&lsaquo;</a>`;
            pagination.appendChild(prevPage);

            for (let i = navi.startPageGroup; i <= navi.endPageGroup; i++) {
                const pageItem = document.createElement('li');
                pageItem.classList.add('page-item', navi.currentPage == i ? 'active' : '');
                pageItem.innerHTML = `<a class="page-link" href="#" data-page="${i}">${i}</a>`;
                pagination.appendChild(pageItem);
            }

            const nextPage = document.createElement('li');
            nextPage.classList.add('page-item', navi.currentPage == navi.totalPageCount ? 'disabled' : '');
            nextPage.innerHTML = `<a class="page-link" href="#" data-page="${navi.currentPage + 1}">&rsaquo;</a>`;
            pagination.appendChild(nextPage);

            const lastPage = document.createElement('li');
            lastPage.classList.add('page-item', navi.currentPage == navi.totalPageCount ? 'disabled' : '');
            lastPage.innerHTML = `<a class="page-link" href="#" data-page="${navi.totalPageCount}">&raquo;</a>`;
            pagination.appendChild(lastPage);

            paginationContainer.appendChild(pagination);
        }

        document.querySelectorAll('.page-link').forEach(link => {
            link.addEventListener('click', event => {
                event.preventDefault();
                const page = event.target.dataset.page;
                loadData(page);
            });
        });
    }

    loadData();

    registerButton.addEventListener("click", () => {
        registrationContainer.classList.add("visible");
        content.classList.add("shrink");
    });

    cancelButton.addEventListener("click", () => {
        registrationContainer.classList.remove("visible");
        content.classList.remove("shrink");
    });

    window.addEventListener("popstate", () => {
        loadData();
    });
});