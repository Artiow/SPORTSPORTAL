import apiUrl from '../boot/constants';
import axios from 'axios';

/**
 * @param successCallback {function(Object)}
 * @param failureCallback {function(Object)}
 */
export default function verify(successCallback, failureCallback) {
    const accessToken = localStorage.getItem('token');
    const call = (callback, object) => {
        if (typeof callback === 'function') callback(object);
    };
    if (accessToken !== null) {
        axios
            .get(apiUrl('/auth/verify'), {params: {accessToken: accessToken}})
            .then(function (response) {
                login(response.data);
                console.debug('verification:', response);
                call(successCallback, response.data);
            })
            .catch(function (error) {
                logout();
                console.warn('verification:', ((error.response != null) ? error.response : error));
                call(failureCallback, error);
            })
    } else call(failureCallback, undefined);
}

export function login(token) {
    localStorage.setItem('token', (token.tokenType + ' ' + token.tokenHash));
    localStorage.setItem('login', JSON.stringify(token.login));
}

export function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('login');
}