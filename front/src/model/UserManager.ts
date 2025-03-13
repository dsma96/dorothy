import type {Member} from 'type'
import { useSelector, useDispatch } from 'react-redux';

const loginUser: Member = useSelector( state => state.user.loginUser);


