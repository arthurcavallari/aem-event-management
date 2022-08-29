import {getCsrfToken} from './getCsrfToken';
const EVENT_REGISTRATION_ENDPOINT = '/bin/eventRegistration';

export const postEventRegistration = async (data) => {
    const csrfToken = await getCsrfToken();
    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'CSRF-Token': csrfToken
        },
        body: JSON.stringify(data),
    };

    const response = await fetch(EVENT_REGISTRATION_ENDPOINT, options).catch(error => console.error('Error:', error));

    if (!response?.ok) {
        throw new Error('Event creation failed =(');
    }

    return response;
};
