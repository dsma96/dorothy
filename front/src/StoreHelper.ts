import {useDispatch, useSelector} from "react-redux";
import {HairService} from './typedef'
import {setConfig} from './redux/store';

const getServices = async (dispatch):Promise<HairService[]> => {
    const services: HairService[]  = useSelector( state => state.date);

    return new Promise((resolve, reject)=> {
        if (services.length > 0) {
            resolve(services);
        } else {
            fetch('/api/getServices')
                .then((response) => response.json())
                .then((data) => {
                    dispatch(setConfig(data));
                    resolve(data);
                })
                .catch((error) => {
                    console.error('Error fetching services:', error);
                    reject(error);
            });
        }
    });
}

export {getServices};