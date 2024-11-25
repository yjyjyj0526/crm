document.addEventListener('DOMContentLoaded', () => {
    if (!window.myApp) {
        window.myApp = {};
    }

    if (!window.myApp.fetchData) {
        window.myApp.fetchData = async function (url) {
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
        window.myApp.postData = async function (url, data) {
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
        window.myApp.showForm = function (container) {
            requestAnimationFrame(() => {
                container.classList.add('visible');
            });
            document.getElementById('contentMain').classList.add('shrink');
        };
    }

    if (!window.myApp.hideForm) {
        window.myApp.hideForm = function (container) {
            container.classList.remove('visible');
            document.getElementById('contentMain').classList.remove('shrink');
        };
    }

    if (!window.myApp.hideAllForms) {
        window.myApp.hideAllForms = function (formContainers) {
            formContainers.forEach(container => window.myApp.hideForm(container));
        };
    }

    const {
        showForm,
        hideForm,
        hideAllForms,
        fetchData,
        postData
    } = window.myApp;

    const registerButton = document.getElementById('registerButton');
    const searchForm = document.getElementById('searchForm');
    const registrationContainer = document.getElementById('registrationContainer');
    const editContainer = document.getElementById('editContainer');
    const detailContainer = document.getElementById('detailContainer');
    const formContainers = [registrationContainer, detailContainer, editContainer];

    const sortableHeaders = document.querySelectorAll('.sortable');
    let currentOrder = 'company_name';
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

    document.getElementById('searchAddressButton').addEventListener('click', fetchAddress);

    async function fetchAddress() {
        const postNumber1 = document.getElementById('post_number1').value.trim();
        const postNumber2 = document.getElementById('post_number2').value.trim();
        const postNumber = postNumber1 + postNumber2;

        if (postNumber) {
            const url = `http://localhost:8080/api/zipcode?zipcode=${postNumber}`;
            try {
                const data = await fetchData(url);
                if (data && data.results && data.results.length > 0) {
                    const result = data.results[0];
                    const address = `${result.address1} ${result.address2} ${result.address3}`;
                    document.getElementById('address').value = address;
                } else {
                    alert("住所が見つかりませんでした。");
                }
            } catch (error) {
                console.error('Error fetching address:', error);
                alert('住所を取得する際にエラーが発生しました。');
            }
        } else {
            alert('郵便番号を入力してください。');
        }
    }

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

    // phone_number 필드를 처리하는 이벤트 리스너 추가
    const phoneNumberParts = [
        document.getElementById('phone_number_part1'),
        document.getElementById('phone_number_part2'),
        document.getElementById('phone_number_part3')
    ];
    phoneNumberParts.forEach(part => {
        part.addEventListener('input', () => {
            document.getElementById('phone_number').value = phoneNumberParts.map(el => el.value.trim()).join('-');
        });
    });

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
            const company_id = event.target.getAttribute('data-id');
            fetchCompanyDetails(company_id);
        } else if (event.target.classList.contains('editButton')) {
            const company_id = event.target.getAttribute('data-id');
            fetchCompanyEditForm(company_id);
        }
    });

    const registrationForm = document.getElementById('registrationForm');
    if (registrationForm) {
        registrationForm.addEventListener('submit', event => {
            event.preventDefault();
            submitRegistrationForm(registrationForm);
        });
    } else {
        console.error('registrationForm not found');
    }

    const editForm = document.getElementById('editForm');
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

    async function loadData(page = 1) {
        if (!searchForm) {
            console.error('searchForm not found');
            return;
        }

        const params = new URLSearchParams(new FormData(searchForm));
        params.append('page', page);
        params.append('order', currentOrder);
        params.append('orderDirection', orderDirection);

        try {
            const data = await fetchData(`/company/list/json?${params}`);
            if (!data || !Array.isArray(data.list)) throw new Error('Invalid company data');
            populateTable(data.list, data.countPerPage || 10);
            setupPagination(data.totalPages, page);
        } catch (error) {
            console.error('Error loading company list:', error);
        }
    }

    function populateTable(companies, countPerPage = 10) {
        const companyTableBody = document.getElementById('companyTableBody');
        if (!companyTableBody) {
            console.error('companyTableBody not found');
            return;
        }

        companyTableBody.innerHTML = '';

        companies.forEach(company => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${company.company_name}</td>
                <td>${company.ceo_name}</td>
                <td>${company.phone_number}</td>
                <td>${company.business_type}</td>
                <td>${company.contract_type}</td>
                <td>
                    <button class="btn btn-info detailButton" data-id="${company.company_id}">詳細</button>
                    <button class="btn btn-warning editButton" data-id="${company.company_id}">編集</button>
                </td>
            `;
            companyTableBody.appendChild(row);
        });

        const remainingRows = countPerPage - companies.length;
        for (let i = 0; i < remainingRows; i++) {
            const row = document.createElement('tr');
            row.innerHTML = '<td colspan="6">&nbsp;</td>';
            companyTableBody.appendChild(row);
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

        if (totalPages === 0) totalPages = 1;

        paginationContainer.appendChild(createPageItem(1, '<<', currentPage === 1));
        paginationContainer.appendChild(createPageItem(currentPage - 1, '<', currentPage === 1));

        for (let i = 1; i <= totalPages; i++) {
            paginationContainer.appendChild(createPageItem(i, i, currentPage === i));
        }

        paginationContainer.appendChild(createPageItem(currentPage + 1, '>', currentPage === totalPages));
        paginationContainer.appendChild(createPageItem(totalPages, '>>', currentPage === totalPages));
    }

    async function fetchCompanyDetails(company_id) {
        try {
            const data = await fetchData(`/company/details/${company_id}`);
            console.log(data);
            if (data) {
                document.getElementById('detail_company_name').value = data.company_name;
                document.getElementById('detail_ceo_name').value = data.ceo_name;
                document.getElementById('detail_phone_number').value = data.phone_number;
                document.getElementById('detail_post_number').value = data.post_number;
                document.getElementById('detail_address2').value = data.address;
                document.getElementById('detail_detail_address').value = data.detail_address;
                document.getElementById('detail_homepage').value = data.homepage;
                document.getElementById('detail_business_type').value = data.business_type;
                document.getElementById('detail_contract_type').value = data.contract_type;
                hideAllForms(formContainers);
                showForm(detailContainer);
            } else {
                console.error('No data received for company detail form');
            }
        } catch (error) {
            console.error('Error fetching company detail form:', error);
        }
    }

    async function fetchCompanyEditForm(company_id) {
        try {
            const data = await fetchData(`/company/edit/${company_id}`);
            if (data) {
                document.getElementById('edit_company_id').value = data.company_id;
                document.getElementById('edit_company_name').value = data.company_name;
                document.getElementById('edit_ceo_name').value = data.ceo_name;
                document.getElementById('edit_phone_number').value = data.phone_number;
                document.getElementById('edit_post_number').value = data.post_number;
                document.getElementById('edit_address').value = data.address;
                document.getElementById('edit_detail_address').value = data.detail_address;
                document.getElementById('edit_homepage').value = data.homepage;
                document.getElementById('edit_business_type').value = data.business_type;
                document.getElementById('edit_contract_type').value = data.contract_type;
                hideAllForms(formContainers);
                showForm(editContainer);
            } else {
                console.error('No data received for company edit form');
            }
        } catch (error) {
            console.error('Error fetching company edit form:', error);
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

        // `post_number1`과 `post_number2`를 결합하여 하나의 `post_number`로 만듭니다.
        const postNumber1 = formData.get('post_number1').trim();
        const postNumber2 = formData.get('post_number2').trim();
        formData.set('post_number', postNumber1 + postNumber2);

        // `phone_number` 필드를 결합
        const phoneNumberPart1 = formData.get('phone_number_part1').trim();
        const phoneNumberPart2 = formData.get('phone_number_part2').trim();
        const phoneNumberPart3 = formData.get('phone_number_part3').trim();
        formData.set('phone_number', `${phoneNumberPart1}-${phoneNumberPart2}-${phoneNumberPart3}`);

        // 원래는 `post_number1`, `post_number2`, `phone_number_part1`, `phone_number_part2`, `phone_number_part3`를 삭제하지 않고 남겨두기로 합니다.
        // 어차피 서버가 필요한 파라미터를 적절히 추출할 수 있게 합니다.
        formData.delete('post_number1');
        formData.delete('post_number2');
        formData.delete('phone_number_part1');
        formData.delete('phone_number_part2');
        formData.delete('phone_number_part3');

        console.log('Form Data to be sent:', Array.from(formData.entries()));

        loadingSpinner.style.display = 'block';

        fetch('/company/register', {
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
                } else {
                    if (registrationFailModal) {
                        registrationFailModal.show();
                    }
                    console.error('Error registering company:', response);
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

    async function submitEditForm(form) {
        const loadingSpinner = document.getElementById('loadingSpinner');
        if (!loadingSpinner) {
            console.error('loadingSpinner not found');
            return;
        }

        const formData = new FormData(form);

        if (!formData.get('company_id')) {
            console.error('company_id is missing in the form data');
            return;
        }

        // `phone_number` 필드를 결합
        const phoneNumberPart1 = formData.get('phone_number_part1').trim();
        const phoneNumberPart2 = formData.get('phone_number_part2').trim();
        const phoneNumberPart3 = formData.get('phone_number_part3').trim();
        formData.set('phone_number', `${phoneNumberPart1}-${phoneNumberPart2}-${phoneNumberPart3}`);

        // 원래는 `phone_number_part1`, `phone_number_part2`, `phone_number_part3`를 삭제하지 않고 남겨두기로 합니다.
        formData.delete('phone_number_part1');
        formData.delete('phone_number_part2');
        formData.delete('phone_number_part3');

        loadingSpinner.style.display = 'block';

        try {
            const response = await postData('/company/update', Object.fromEntries(formData));
            loadingSpinner.style.display = 'none';
            if (response && response.success) {
                hideAllForms(formContainers);
                loadData();
                if (editSuccessModal) {
                    editSuccessModal.show();
                }
                editForm.reset(); // 폼 초기화
            } else {
                if (editFailModal) {
                    editFailModal.show();
                }
                console.error('Error updating company:', response);
            }
        } catch (error) {
            loadingSpinner.style.display = 'none';
            if (editFailModal) {
                editFailModal.show();
            }
            console.error('Error submitting edit form:', error);
        }
    }
});