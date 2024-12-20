// Allow drop event function
function allowDrop(event) {
    event.preventDefault();
}

// Drop event function
function drop(event) {
    event.preventDefault();
    const draggingItem = document.querySelector('.dragging');
    const targetList = event.target.closest('.sortable-list');

    if (!targetList.contains(draggingItem)) {
        targetList.appendChild(draggingItem);
    } else {
        const siblings = [...targetList.querySelectorAll('.item:not(.dragging)')];
        const nextSibling = siblings.find(sibling => {
            return event.clientY <= sibling.offsetTop + sibling.offsetHeight / 2;
        });
        targetList.insertBefore(draggingItem, nextSibling);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    // ID가 직접 주입된 변수를 사용하여 설정됩니다.
    console.log('Opportunity ID:', opportunityId);

    fetch(`/sales/details/${opportunityId}/json`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            console.log(data); // 데이터 출력: 서버로부터 반환된 JSON 확인

            const contactList = data.contacts;

            contactList.forEach(contact => {
                console.log(contact); // 개별 연락처 데이터 출력: 올바른 속성 확인

                const stepId = `#step-${contact.step} .sortable-list`;
                const listElement = document.querySelector(stepId);
                if (listElement) {
                    const item = document.createElement('li');
                    item.classList.add('item');
                    item.setAttribute('draggable', true);

                    const detailsDiv = document.createElement('div');
                    detailsDiv.classList.add('details');
                    detailsDiv.innerHTML = `
                        <span>${contact.contact_id}</span>
                        <p>${contact.manager_id}</p>
                    `;
                    item.appendChild(detailsDiv);
                    item.innerHTML += '<i class="bi bi-grip-vertical"></i>';
                    listElement.appendChild(item);
                }
            });

            // 드래그 앤 드롭 이벤트 처리
            const items = document.querySelectorAll('.item');
            const handles = document.querySelectorAll('.bi-grip-vertical');

            handles.forEach(handle => {
                handle.addEventListener('mousedown', function(event) {
                    const item = handle.closest('.item');
                    item.setAttribute('draggable', true);
                    item.addEventListener('dragstart', dragStart);
                    item.addEventListener('dragend', dragEnd);
                });
            });

            items.forEach(item => {
                item.addEventListener('dragend', function() {
                    item.setAttribute('draggable', false);
                });
            });
        })
        .catch(error => console.error('There was a problem with your fetch operation:', error));

    function dragStart(event) {
        const item = this.closest('.item');
        item.classList.add('dragging');
        event.dataTransfer.setData('text/plain', item.id);
    }

    function dragEnd() {
        const item = this.closest('.item');
        item.classList.remove('dragging');
        document.querySelectorAll('.item').forEach(item => {
            item.classList.remove('over');
        });
    }

    document.querySelectorAll('.sortable-list .item').forEach(item => {
        item.addEventListener('dragenter', function(event) {
            event.preventDefault();
            if (!this.classList.contains('dragging')) {
                this.classList.add('over');
            }
        });
        item.addEventListener('dragleave', function() {
            this.classList.remove('over');
        });
    });

    // 포함된 폼 코드
    console.log('DOMContentLoaded 이벤트 발생'); // DOMContentLoaded 이벤트 확인

    const registerForm = document.getElementById('registerForm');

    if (!registerForm) {
        console.error('registerForm 요소를 찾을 수 없습니다.');
    }

    console.log('registerForm 요소 찾음', registerForm); // 폼 요소 확인 로그

    registerForm.addEventListener('submit', function(event) {
        event.preventDefault();
        console.log('폼 제출 이벤트 발생'); // 폼 제출 이벤트 로그

        const formData = new FormData(registerForm);

        console.log("Sending data:", Array.from(formData.entries())); // 전송 데이터 로그

        fetch('/contact/register', {
            method: 'POST',
            body: formData,
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                console.log('Success:', data);

                // 모달 닫기 및 폼 초기화
                const registerModal = document.getElementById('registerModal');
                const modal = bootstrap.Modal.getInstance(registerModal);
                modal.hide();
                document.getElementById('registerForm').reset();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('콘텍트를 등록하지 못했습니다. 관리자에게 문의해주세요.');
            });
    });
});