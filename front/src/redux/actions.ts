export const SET_USER = 'SET_USER';
import type {Member} from '../type'

export const setUser = (user: Member) => ({
    type: SET_USER,
    payload: user,
});
