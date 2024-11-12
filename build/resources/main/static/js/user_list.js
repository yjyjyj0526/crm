const previewProfileImage2 = (event) => {
    const reader = new FileReader();
    reader.onload = () => {
        const output = document.getElementById('profileImagePreview');
        output.src = reader.result;
    };
    reader.readAsDataURL(event.target.files[0]);
};

function previewProfileImage(event) {
    const input = event.target;
    const file = input.files[0];

    if (file) {
        const reader = new FileReader();

        reader.onload = function(e) {
            const previewElement = document.getElementById('edit_profile_image');
            if (previewElement) {
                previewElement.src = e.target.result;
            } else {
                console.error('Profile image preview element not found');
            }
        };

        reader.readAsDataURL(file);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (!window.myApp) {
        window.myApp = {};
    }

    if (!window.myApp.fetchData) {
        window.myApp.fetchData = async function(url) {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        };
    }

    if (!window.myApp.postData) {
        window.myApp.postData = async function(url, data) {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        };
    }

    if (!window.myApp.showForm) {
        window.myApp.showForm = function(container) {
            requestAnimationFrame(() => {
                container.classList.add('visible');
            });
            document.getElementById('contentMain').classList.add('shrink');
        };
    }

    if (!window.myApp.hideForm) {
        window.myApp.hideForm = function(container) {
            container.classList.remove('visible');
            document.getElementById('contentMain').classList.remove('shrink');
        };
    }

    if (!window.myApp.hideAllForms) {
        window.myApp.hideAllForms = function(formContainers) {
            formContainers.forEach(container => window.myApp.hideForm(container));
        };
    }

    const editProfileImageInput = document.getElementById('edit_profile_image_input');

    if (editProfileImageInput) {
        editProfileImageInput.addEventListener('change', previewProfileImage);
    } else {
        console.error('edit_profile_image_input not found');
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

    const initializeForm = (formId, prefix) => {
        const form = document.getElementById(formId);
        if (!form) {
            console.error(`${formId} not found`);
            return;
        }

        const phoneticInput = document.getElementById(`${prefix}user_name_phonetic`);
        const phoneInputs = document.querySelectorAll(`[id^=${prefix}phone_number_part]`);
        const emailInput = document.getElementById(`${prefix}email`);
        const departmentSelect = document.getElementById(`${prefix}department`);
        const authoritySelect = document.getElementById(`${prefix}authority`);

        form.addEventListener('submit', event => {
            const part1 = document.getElementById(`${prefix}phone_number_part1`).value;
            const part2 = document.getElementById(`${prefix}phone_number_part2`).value;
            const part3 = document.getElementById(`${prefix}phone_number_part3`).value;

            document.getElementById(`${prefix}phone_number`).value = `${part1}-${part2}-${part3}`;
            if (!validateAllInputs()) {
                event.preventDefault();
                return;
            }

            event.preventDefault();
            if (prefix === "edit_") {
                submitEditForm(form);
            } else {
                submitRegistrationForm(form);
            }
        });

        phoneticInput.addEventListener('blur', () => validateInput(phoneticInput, `${prefix}phoneticMessage`));
        phoneInputs.forEach(input => input.addEventListener('blur', () => validateInput(input, `${prefix}phoneNumberMessage`)));
        emailInput.addEventListener('blur', () => validateInput(emailInput, `${prefix}emailMessage`));
        departmentSelect.addEventListener('blur', () => validateSelect(departmentSelect, `${prefix}departmentMessage`));
        authoritySelect.addEventListener('blur', () => validateSelect(authoritySelect, `${prefix}authorityMessage`));

        function validateInput(input, messageId) {
            const messageElement = document.getElementById(messageId);
            if (!input.checkValidity()) {
                messageElement.textContent = input.title;
            } else {
                messageElement.textContent = '';
            }
        }

        function validateSelect(select, messageId) {
            const messageElement = document.getElementById(messageId);
            if (select.value === '') {
                messageElement.textContent = 'このフィールドは必須です。';
                return false;
            } else {
                messageElement.textContent = '';
                return true;
            }
        }

        function validateAllInputs() {
            let isValid = true;

            if (!phoneticInput.checkValidity()) {
                validateInput(phoneticInput, `${prefix}phoneticMessage`);
                isValid = false;
            }

            phoneInputs.forEach(input => {
                if (!input.checkValidity()) {
                    validateInput(input, `${prefix}phoneNumberMessage`);
                    isValid = false;
                }
            });

            if (!emailInput.checkValidity()) {
                validateInput(emailInput, `${prefix}emailMessage`);
                isValid = false;
            }

            if (!validateSelect(departmentSelect, `${prefix}departmentMessage`)) {
                isValid = false;
            }

            if (!validateSelect(authoritySelect, `${prefix}authorityMessage`)) {
                isValid = false;
            }

            return isValid;
        }
    };

    initializeForm('registrationForm', '');
    initializeForm('editForm', 'edit_');

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
            registrationForm.reset(); // 폼 초기화
            document.getElementById('profileImagePreview').src = '/images/default-profile.png'; // 이미지 초기화
        });
    } else {
        console.error('cancelButton not found');
    }

    loadData();

    function loadData(page = 1) {
        if (!searchForm) {
            console.error('searchForm not found');
            return;
        }

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

    function populateTable(users, countPerPage = 10) {
        const userTableBody = document.getElementById('userTableBody');
        if (!userTableBody) {
            console.error('userTableBody not found');
            return;
        }

        userTableBody.innerHTML = '';

        users.forEach(user => {
            const row = document.createElement('tr');

            // authority 값을 변환
            let authorityLabel = '';
            switch (user.authority.toString()) {
                case '1':
                    authorityLabel = '管理者';
                    break;
                case '2':
                    authorityLabel = '部署長';
                    break;
                case '3':
                    authorityLabel = '社員';
                    break;
                default:
                    authorityLabel = '未知';
            }

            row.innerHTML = `
        <td>${user.user_id}</td>
        <td>${user.user_name}</td>
        <td>${user.phone_number}</td>
        <td>${user.department}</td>
        <td>${authorityLabel}</td>
        <td>
            <button class="btn btn-info detailButton" data-id="${user.user_id}">詳細</button>
            <button class="btn btn-warning editButton" data-id="${user.user_id}">修正</button>
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

    function setupPagination(totalPages, currentPage = 1) {
        const paginationContainer = document.querySelector('.pagination');
        if (!paginationContainer) {
            console.error('paginationContainer not found');
            return;
        }

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

    function fetchUserEditForm(user_id) {
        fetchData(`/user/edit/${user_id}`)
            .then(response => {
                if (!response) {
                    console.error('No data received for user edit form');
                    return;
                }

                // 콘솔 로그로 전체 응답을 출력
                console.log(response);

                const data = response.data; // `data` 키가 맞는지 확인
                const profileImageBase64 = response.profile_image_base64;

                if (!data) {
                    console.error('No user data received');
                    return;
                }

                const profileImageElement = document.getElementById('edit_profile_image');
                if (profileImageElement) {
                    if (profileImageBase64) {
                        profileImageElement.src = `data:image/png;base64,${profileImageBase64}`;
                    } else {
                        profileImageElement.src = '/images/default-profile.png';
                    }
                } else {
                    console.error('Profile image element not found');
                }

                document.getElementById('edit_user_id').value = data.user_id || '';
                document.getElementById('edit_user_name').value = data.user_name || '';
                document.getElementById('edit_user_name_phonetic').value = data.user_name_phonetic || '';
                document.getElementById('edit_email').value = data.email || '';
                // 콘솔 로그로 특정 데이터 출력
                console.log("User Name Phonetic: ", data.user_name_phonetic);

                // 전화번호 처리
                const phoneNumberParts = data.phone_number ? data.phone_number.split('-') : ['', '', ''];
                document.getElementById('edit_phone_number_part1').value = phoneNumberParts[0] || '';
                document.getElementById('edit_phone_number_part2').value = phoneNumberParts[1] || '';
                document.getElementById('edit_phone_number_part3').value = phoneNumberParts[2] || '';

                document.getElementById('edit_department').value = data.department || '';
                document.getElementById('edit_authority').value = data.authority || '';

                // Convert date strings to yyyy-MM-dd format before setting values
                document.getElementById('edit_joining_date').value = data.joining_date ? formatDate(data.joining_date) : '';
                document.getElementById('edit_date_of_birth').value = data.date_of_birth ? formatDate(data.date_of_birth) : '';

                hideAllForms(formContainers);
                showForm(editContainer);
            })
            .catch(error => console.error('Error fetching user edit form:', error));
    }

    // Helper function to format date as yyyy-MM-dd
    function formatDate(dateStr) {
        const date = new Date(dateStr);
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);
        return `${year}-${month}-${day}`;
    }

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

    function initializeModal(elementId, modalName) {
        const element = document.getElementById(elementId);
        if (!element) {
            console.error(`${modalName} not found with ID ${elementId}`);
            return null;
        }
        return new bootstrap.Modal(element);
    }

    const registrationSuccessModal = initializeModal('registrationSuccessModal', 'registrationSuccessModal');
    const registrationFailModal = initializeModal('registrationFailModal', 'registrationFailModal');
    const editSuccessModal = initializeModal('editSuccessModal', 'editSuccessModal');
    const editFailModal = initializeModal('editFailModal', 'editFailModal');

    function submitRegistrationForm(form) {
        const loadingSpinner = document.getElementById('loadingSpinner');
        if (!loadingSpinner) {
            console.error('loadingSpinner not found');
            return;
        }

        const formData = new FormData(form);

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
                    if (registrationSuccessModal) {
                        registrationSuccessModal.show();
                    }
                    registrationForm.reset(); // 폼 초기화
                    document.getElementById('profileImagePreview').src = '/images/default-profile.png'; // 이미지 초기화
                } else {
                    if (registrationFailModal) {
                        registrationFailModal.show();
                    }
                    console.error('Error registering user:', response);
                }
            })
            .catch(error => {
                loadingSpinner.style.display = 'none';
                if (registrationFailModal) {
                    registrationFailModal.show();
                }
                console.error('Error submitting registration form:', error);
            });
    }

    function submitEditForm(form) {
        const loadingSpinner = document.getElementById('loadingSpinner');
        if (!loadingSpinner) {
            console.error('loadingSpinner not found');
            return;
        }

        const formData = new FormData(form);

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
                    if (editSuccessModal) {
                        editSuccessModal.show();
                    }
                } else {
                    if (editFailModal) {
                        editFailModal.show();
                    }
                    console.error('Error updating user:', response);
                }
            })
            .catch(error => {
                loadingSpinner.style.display = 'none';
                if (editFailModal) {
                    editFailModal.show();
                }
                console.error('Error submitting edit form:', error);
            });
    }
});