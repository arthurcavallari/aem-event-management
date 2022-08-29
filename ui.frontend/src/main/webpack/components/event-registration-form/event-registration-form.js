import {postEventRegistration} from './api/postEventRegistration';

const form = document.querySelector('#event-registration-form');
const errorMsg = document.createElement('div');
errorMsg.classList.add('cmp-registration-form__error');

let isSubmitting = false;

initForm();

export function initForm() {
    if (form) {
        // Insert errorContainer
        const buttonContainer = form.querySelector('.button');
        form.insertBefore(errorMsg, buttonContainer);

        // Derive placeholder attribute from labels and update form elements
        form.querySelectorAll('label[for]')
            .forEach((labelEl) => {
                const labelFor = labelEl.getAttribute('for');
                const labelText = labelEl.innerHTML.replace(':', '');
                const inputEl = document.getElementById(labelFor);
                inputEl.setAttribute('placeholder', labelText);
            });

        form.addEventListener('submit', handleFormSubmit);
    }
}

function handleFormSubmit(e) {
    e.preventDefault();

    !isSubmitting && handleApiRequest();

    return false;
}

function handleApiRequest() {
    const redirect = form.querySelector('[name=":redirect"]')?.value;
    const formData = getFormData();

    errorMsg.innerHTML = '';
    isSubmitting = true;

    postEventRegistration(formData)
        .then(() => {
            window.location.href = redirect;
        })
        .catch(() => {
            errorMsg.innerHTML = `<p>Oops, something wasn't right. Please try again later.</p>`;
        })
        .finally(() => {
            isSubmitting = false;
        });
}

function getFormData() {
    const eventName = document.getElementById('eventName')?.value;
    const organiserEmail = document.getElementById('organiserEmail')?.value;
    const eventNotes = document.getElementById('eventNotes')?.value;

    return {
        eventName,
        organiserEmail,
        eventNotes,
    };
}
