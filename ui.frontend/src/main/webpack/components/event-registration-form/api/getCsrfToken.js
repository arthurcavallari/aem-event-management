const CSRF_TOKEN_ENDPOINT = '/libs/granite/csrf/token.json';

export const getCsrfToken = async () => {
    const options = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = await fetch(CSRF_TOKEN_ENDPOINT, options).catch((err) => console.error(err));

    if (response?.ok) {
        const {token} = await response.json();
        return token;
    }
};
