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

    const { showForm, hideForm, hideAllForms, fetchData, postData } = window.myApp;

    const registerButton = document.getElementById('registerButton');
    const searchForm = document.getElementById('searchForm');
    const registrationContainer = document.getElementById('registrationContainer');
    const editContainer = document.getElementById('editContainer');
    const detailContainer = document.getElementById('detailContainer');
    const formContainers = [registrationContainer, detailContainer, editContainer];

    const sortableHeaders = document.querySelectorAll('.sortable');
    let currentOrder = 'company_id';
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
            const manager_id = event.target.getAttribute('data-id');
            fetchmanagerDetails(manager_id);
        } else if (event.target.classList.contains('editButton')) {
            const manager_id = event.target.getAttribute('data-id');
            fetchmanagerEditForm(manager_id);
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

    function loadData(page = 1) {
        if (!searchForm) {
            console.error('searchForm not found');
            return;
        }

        const params = new URLSearchParams(new FormData(searchForm));
        params.append('page', page);
        params.append('order', currentOrder);
        params.append('orderDirection', orderDirection);

        fetchData(`/manager/list/json?${params}`)
            .then(data => {
                if (!data || !Array.isArray(data.list)) throw new Error('Invalid managers data');
                populateTable(data.list, data.countPerPage || 10);
                setupPagination(data.totalPages, page);
            })
            .catch(error => console.error('Error loading manager list:', error));
    }

    function populateTable(managers, countPerPage = 10) {
        const managerTableBody = document.getElementById('managerTableBody');
        if (!managerTableBody) {
            console.error('managerTableBody not found');
            return;
        }

        managerTableBody.innerHTML = '';

        managers.forEach(manager => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${manager.company_id}</td>
                <td>${manager.manager_id}</td>
                <td>${manager.manager_mail}</td>
                <td>${manager.department}</td>
                <td>${manager.position}</td>
                <td>
                    <button class="btn btn-info detailButton" data-id="${manager.manager_id}">상세</button>
                    <button class="btn btn-warning editButton" data-id="${manager.manager_id}">수정</button>
                </td>
            `;
            managerTableBody.appendChild(row);
        });

        const remainingRows = countPerPage - managers.length;
        for (let i = 0; i < remainingRows; i++) {
            const row = document.createElement('tr');
            row.innerHTML = '<td colspan="6">&nbsp;</td>';
            managerTableBody.appendChild(row);
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

    function fetchmanagerDetails(manager_id) {
        fetchData(`/manager/details/${manager_id}`)
            .then(data => {
                if (data) {
                    hideAllForms(formContainers);
                    fillDetailForm(data);
                    showForm(detailContainer);
                } else {
                    console.error('No data received for manager details');
                }
            })
            .catch(error => console.error('Error fetching manager details:', error));
    }

    function fetchmanagerEditForm(manager_id) {
        fetchData(`/manager/edit/${manager_id}`)
            .then(data => {
                if (data) {
                    document.getElementById('edit_manager_id').value = data.manager_id;
                    document.getElementById('edit_company_id').value = data.company_id;
                    document.getElementById('edit_manager_name').value = data.manager_name;
                    document.getElementById('edit_phone_number_1').value = data.phone_number_1;
                    document.getElementById('edit_phone_number_2').value = data.phone_number_2;
                    document.getElementById('edit_manager_age').value = data.manager_age;
                    document.getElementById('edit_manager_mail').value = data.manager_mail;
                    document.getElementById('edit_department').value = data.department;
                    document.getElementById('edit_position').value = data.position;
                    hideAllForms(formContainers);
                    showForm(editContainer);
                } else {
                    console.error('No data received for manager edit form');
                }
            })
            .catch(error => console.error('Error fetching manager edit form:', error));
    }

    function fillDetailForm(data) {
        if (!data || !data.manager) {
            console.error('Invalid data provided for filling detail form:', data);
            return;
        }

        const manager = data.manager;
        document.getElementById('detail_manager_id').value = manager.manager_id;
        document.getElementById('detail_manager_name').value = manager.manager_name;
        document.getElementById('detail_company_id').value = manager.company_id;
        document.getElementById('detail_manager_name').value = manager.manager_name;
        document.getElementById('detail_phone_number_1').value = manager.phone_number_1;
        document.getElementById('detail_phone_number_2').value = manager.phone_number_2;
        document.getElementById('detail_manager_age').value = manager.manager_age;
        document.getElementById('detail_manager_mail').value = manager.manager_mail;
        document.getElementById('detail_department').value = manager.department;
        document.getElementById('detail_position').value = manager.position;
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

        fetch('/manager/register', {
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
                } else {
                    if (registrationFailModal) {
                        registrationFailModal.show();
                    }
                    console.error('Error registering manager:', response);
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

        if (!formData.get('manager_name')) {
            console.error('manager_name is missing in the form data');
            return;
        }

        loadingSpinner.style.display = 'block';

        fetch('/manager/update', {
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
                    console.error('Error updating manager:', response);
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