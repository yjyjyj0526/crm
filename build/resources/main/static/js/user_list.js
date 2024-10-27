document.addEventListener('DOMContentLoaded', () => {
    if (!window.myApp) {
        window.myApp = {};
    }

    // fetchData 함수 정의
    if (!window.myApp.fetchData) {
        window.myApp.fetchData = async function(url) {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            return response.json();
        };
    }

    // postData 함수 정의
    if (!window.myApp.postData) {
        window.myApp.postData = async function(url, data) {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json', // JSON 데이터를 전송하려면 이 헤더가 필요합니다
                },
                body: data,
            });
            return response.json();
        };
    }

    // 폼 표시 함수 정의
    if (!window.myApp.showForm) {
        window.myApp.showForm = function(container) {
            requestAnimationFrame(() => {
                container.classList.add('visible');
            });
            document.getElementById('contentMain').classList.add('shrink');
        };
    }

    // 폼 숨기기 함수 정의
    if (!window.myApp.hideForm) {
        window.myApp.hideForm = function(container) {
            container.classList.remove('visible');
            document.getElementById('contentMain').classList.remove('shrink');
        };
    }

    // 모든 폼 숨기기 함수 정의
    if (!window.myApp.hideAllForms) {
        window.myApp.hideAllForms = function(formContainers) {
            formContainers.forEach(container => window.myApp.hideForm(container));
        };
    }

    const { showForm, hideForm, hideAllForms, fetchData, postData } = window.myApp;

    const registerButton = document.getElementById('registerButton');
    const searchForm = document.getElementById('searchForm');
    const registrationContainer = document.getElementById('registrationContainer');
    const editContainer = document.getElementById('editContainer');
    const detailContainer = document.getElementById('detailContainer');
    const formContainers = [registrationContainer, detailContainer, editContainer];

    const sortableHeaders = document.querySelectorAll('.sortable');
    let currentOrder = 'user_name';
    let orderDirection = 'ASC';

    sortableHeaders.forEach(header => {
        header.addEventListener('click', () => {
            const order = header.getAttribute('data-order');
            if (!order) return;

            if (currentOrder === order) {
                orderDirection = orderDirection === 'ASC' ? 'DESC' : 'ASC';
            } else {
                currentOrder = order;
                orderDirection = 'ASC';
            }
            setSortingArrow();
            loadData(1);
        });
    });

    function setSortingArrow() {
        sortableHeaders.forEach(header => {
            header.classList.remove('active');
            const arrowIcons = header.querySelector('.sort-icons');
            if (arrowIcons) {
                arrowIcons.querySelector('.icon-asc').style.color = '#ccc';
                arrowIcons.querySelector('.icon-desc').style.color = '#ccc';
            }
        });

        const activeHeader = document.querySelector(`.sortable[data-order="${currentOrder}"]`);
        if (activeHeader) {
            activeHeader.classList.add('active');
            const activeArrowIcons = activeHeader.querySelector('.sort-icons');
            if (activeArrowIcons) {
                activeArrowIcons.querySelector(`.icon-${orderDirection.toLowerCase()}`).style.color = '#fff';
            }
        }
    }

    if (registerButton) {
        registerButton.addEventListener('click', () => {
            hideAllForms(formContainers);
            showForm(registrationContainer);
        });
    } else {
        console.error('registerButton not found');
    }

    if (searchForm) {
        searchForm.addEventListener('submit', event => {
            event.preventDefault();
            loadData(1);
        });
    } else {
        console.error('searchForm not found');
    }

    document.addEventListener('click', event => {
        if (event.target.classList.contains('detailButton')) {
            const user_id = event.target.getAttribute('data-id');
            fetchUserDetails(user_id);
        } else if (event.target.classList.contains('editButton')) {
            const user_id = event.target.getAttribute('data-id');
            fetchUserEditForm(user_id);
        }
    });

    const registrationForm = document.getElementById('form'); // 변경된 ID 사용
    console.log('registrationForm:', registrationForm); // 콘솔 로그 추가
    if (registrationForm) {
        registrationForm.addEventListener('submit', event => {
            event.preventDefault();
            submitRegistrationForm(registrationForm);
        });
    } else {
        console.error('registrationForm not found');
    }

    const editForm = document.getElementById('editForm');
    console.log('editForm:', editForm); // 콘솔 로그 추가
    if (editForm) {
        editForm.addEventListener('submit', event => {
            event.preventDefault();
            submitEditForm(editForm);
        });
    } else {
        console.error('editForm not found');
    }

    const editCancelButton = document.getElementById('editCancelButton');
    if (editCancelButton) {
        editCancelButton.addEventListener('click', () => {
            hideForm(editContainer);
        });
    } else {
        console.error('editCancelButton not found');
    }

    const detailCancelButton = document.getElementById('detailCancelButton');
    if (detailCancelButton) {
        detailCancelButton.addEventListener('click', () => {
            hideForm(detailContainer);
        });
    } else {
        console.error('detailCancelButton not found');
    }

    const cancelButton = document.getElementById('cancelButton');
    if (cancelButton) {
        cancelButton.addEventListener('click', () => {
            hideForm(registrationContainer);
        });
    } else {
        console.error('cancelButton not found');
    }

    loadData();

    // 데이터 로드 함수 정의
    function loadData(page = 1) {
        const params = new URLSearchParams(new FormData(searchForm));
        params.append('page', page);
        params.append('order', currentOrder);
        params.append('orderDirection', orderDirection);

        fetchData(`/user/list/json?${params}`)
            .then(data => {
                if (!data || !Array.isArray(data.list)) throw new Error('Invalid users data');
                populateTable(data.list, data.countPerPage || 10);
                setupPagination(data.totalPages, page);
            })
            .catch(error => console.error('Error loading user list:', error));
    }

    // 테이블 채우기 함수 정의
    function populateTable(users, countPerPage = 10) {
        const userTableBody = document.getElementById('userTableBody');
        userTableBody.innerHTML = '';

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
            userTableBody.appendChild(row);
        });

        const remainingRows = countPerPage - users.length;
        for (let i = 0; i < remainingRows; i++) {
            const row = document.createElement('tr');
            row.innerHTML = '<td colspan="6">&nbsp;</td>';
            userTableBody.appendChild(row);
        }
    }

    // 페이지네이션 설정 함수 정의
    function setupPagination(totalPages, currentPage = 1) {
        const paginationContainer = document.querySelector('.pagination');
        paginationContainer.innerHTML = '';

        const createPageItem = (page, label, disabled = false) => {
            const pageItem = document.createElement('li');
            pageItem.classList.add('page-item');
            if (disabled) pageItem.classList.add('disabled');
            pageItem.innerHTML = `<a class="page-link" href="#">${label}</a>`;
            if (!disabled) {
                pageItem.addEventListener('click', event => {
                    event.preventDefault();
                    loadData(page);
                });
            }
            return pageItem;
        };

        paginationContainer.appendChild(createPageItem(1, '<<', currentPage === 1));
        paginationContainer.appendChild(createPageItem(currentPage - 1, '<', currentPage === 1));

        for (let i = 1; i <= totalPages; i++) {
            paginationContainer.appendChild(createPageItem(i, i, currentPage === i));
        }

        paginationContainer.appendChild(createPageItem(currentPage + 1, '>', currentPage === totalPages));
        paginationContainer.appendChild(createPageItem(totalPages, '>>', currentPage === totalPages));
    }

    // 사용자 상세 정보 가져오기 함수 정의
    function fetchUserDetails(user_id) {
        fetchData(`/user/details/${user_id}`)
            .then(data => {
                if (data) {
                    hideAllForms(formContainers);
                    fillDetailForm(data);
                    showForm(detailContainer);
                } else {
                    console.error('No data received for user details');
                }
            })
            .catch(error => console.error('Error fetching user details:', error));
    }

    // 사용자 편집 폼 가져오기 함수 정의
    function fetchUserEditForm(user_id) {
        fetchData(`/user/edit/${user_id}`)
            .then(data => {
                if (data) {
                    document.getElementById('edit_user_id').value = data.user_id;
                    document.getElementById('edit_user_name').value = data.user_name;
                    document.getElementById('edit_phone_number').value = data.phone_number;
                    document.getElementById('edit_department').value = data.department;
                    document.getElementById('edit_authority').value = data.authority;
                    document.getElementById('edit_profile_image').value = '';
                    hideAllForms(formContainers);
                    showForm(editContainer);
                } else {
                    console.error('No data received for user edit form');
                }
            })
            .catch(error => console.error('Error fetching user edit form:', error));
    }

    // 상세 폼 채우기 함수 정의
    function fillDetailForm(data) {
        if (!data || !data.user) {
            console.error('Invalid data provided for filling detail form:', data);
            return;
        }

        const user = data.user;
        document.getElementById('detail_user_id').value = user.user_id;
        document.getElementById('detail_user_name').value = user.user_name;
        document.getElementById('detail_phone_number').value = user.phone_number;
        document.getElementById('detail_department').value = user.department;
        document.getElementById('detail_authority').value = user.authority;
        document.getElementById('detail_profile_image').value = user.profile_image;

        const profileImageElement = document.getElementById('detail_profile_image');
        if (data.profile_image_base64) {
            profileImageElement.src = `data:image/png;base64,${data.profile_image_base64}`;
        } else {
            profileImageElement.src = '/images/default-profile.png';
        }
    }

    // 등록 폼 제출 함수 정의
    function submitRegistrationForm(form) {
        const loadingSpinner = document.getElementById('loadingSpinner');

        const formData = new FormData(form);

        console.log('Submit Registration Form Data:', Array.from(formData.entries())); // 제출할 데이터 로그 출력

        loadingSpinner.style.display = 'block';

        fetch('/user/register', {
            method: 'POST',
            body: formData,
        })
            .then(response => response.json())
            .then(response => {
                loadingSpinner.style.display = 'none';
                if (response && response.success) {
                    hideAllForms(formContainers);
                    loadData();
                } else {
                    console.error('Error registering user:', response);
                }
            })
            .catch(error => {
                loadingSpinner.style.display = 'none';
                console.error('Error submitting registration form:', error);
            });
    }

    // 편집 폼 제출 함수 정의
    function submitEditForm(form) {
        const loadingSpinner = document.getElementById('loadingSpinner');

        const formData = new FormData(form);

        console.log('Submit Edit Form Data:', Array.from(formData.entries())); // 제출할 데이터 로그 출력

        if (!formData.get('user_id')) {
            console.error('user_id is missing in the form data');
            return;
        }

        loadingSpinner.style.display = 'block';

        fetch('/user/update', {
            method: 'POST',
            body: formData,
        })
            .then(response => response.json())
            .then(response => {
                loadingSpinner.style.display = 'none';
                if (response && response.success) {
                    hideAllForms(formContainers);
                    loadData();
                } else {
                    console.error('Error updating user:', response);
                }
            })
            .catch(error => {
                loadingSpinner.style.display = 'none';
                console.error('Error submitting edit form:', error);
            });
    }
});