import { http, HttpResponse    } from 'msw'
import moment from 'moment'

export const handlers = [
    http.put('/api/reserve/cancel/:id', ( {params}) => {
        const{id} = params;
            // ...and respond to them using this JSON response.
            return HttpResponse.json(
                {
                    msg: "OK",
                    code: 200,
                    "payload": {
                        "reservationId": id,
                        "userName": "Aiden",
                        "phone": "2892440503",
                        "startDate": "20250309T13:30",
                        "createDate": "20250309T13:30",
                        "status": "CANCELED",
                        "services": [null],
                        "memo": "reserve1",
                        "editable": true
                    }
                }
            )
        }
    ),
    http.post('/api/user/signup',()=> {
        return HttpResponse.json(
            {
                msg: "OK",
                code: 200,
                payload: {
                    phone: "6474060362",
                    password: null,
                    email: "silverwing@gmail.com",
                    name: "Lemmy",
                    id: 99
                }
            }
        )
     }
    )
    ,
    http.post('/api/login/login', () => {
        // ...and respond to them using this JSON response.
        return HttpResponse.json(
                {
                    msg: "OK",
                    code: 200,
                    payload: {
                        phone: "6474060362",
                        password: null,
                        email: "silverwing@gmail.com",
                        name: "Lemmy",
                        id:2
                    }
                }
            )
        }
    ),
    http.get('/api/login/relogin', () => {
            // ...and respond to them using this JSON response.
            return HttpResponse.json(
                {
                    msg: "OK",
                    code: 200,
                    payload: {
                        phone: "6474060362",
                        password: null,
                        email: "silverwing@gmail.com",
                        name: "Lemmy",
                        id:2
                    }
                }
            )
        }
    ),
    http.post(
        '/api/reserve/reservation', ({request})=> {
            return HttpResponse.json(
                {
                    msg: "OK",
                    code: 200,
                    "payload": {
                        "reservationId": 10,
                        "userName": "Aiden",
                        "phone": "2892440503",
                        "startDate": "20250309T13:30",
                        "createDate": "20250309T13:30",
                        "status": "CREATED",
                        "services": [null],
                        "memo": "reserve1",
                        "editable": true
                    }
                }
            )
        }
    )
,
    http.get('/api/reserve/reservations', ({request})=>{
            const url = new URL(request.url);
            const startDate = url.searchParams.get('startDate');

            let today = moment(startDate,"YYYYMMDD'T'HH:mm").format("YYYYMMDD");

            return HttpResponse.json(
                {
                    msg: "OK",
                    code: 200,
                    payload:[
                        {
                            reservationId: 1,
                            userName: "Aiden",
                            phone: "2892440503",
                            startDate: today+"T10:00",
                            createDate: null,
                            services: [
                                {serviceI: 1,
                                    name: "남자 헤어컷",
                                    mandatory: true,
                                    idx: 0,
                                    use: true
                                },
                                {
                                    serviceId: 3,
                                    name:"다운펌",
                                    mandatory: false,
                                    idx: 2,
                                    use: true
                                }
                            ],
                            status: 'CREATED',
                            editable:true,
                            memo:'hello'
                        },
                        {
                            reservationId: 2,
                            userName: "John Doe",
                            phone: "416-000-1234",
                            startDate: today+"T13:00",
                            createDate: "20250309T13:30",
                            services: [],
                            status: 'CREATED',
                            editable: false
                        }
                    ]
                }
            )
        }
    ),
    http.get('/api/reserve/:id', ({params})=>{
        const{id} = params;
        let today = moment(new Date()).format("YYYYMMDD");

        if( id == '1'){
            return HttpResponse.json(
                {
                    msg: "OK",
                    code: 200,
                    payload:
                        {
                            "reservationId": 1,
                            "userName": "Aiden",
                            "phone": "2892440503",
                            "startDate": today+"T10:00",
                            "createDate": "20250309T13:30",
                            "status": "CREATED",
                            "memo":"memo1",
                            "services": [
                                {
                                    "serviceId": 1,
                                    "name": "남자 헤어컷",
                                    "mandatory": true,
                                    "idx": 0,
                                    "use": true
                                },
                                {
                                    "serviceId": 3,
                                    "name": "다운펌",
                                    "mandatory": false,
                                    "idx": 2,
                                    "use": true
                                }
                            ],
                            "editable": true
                        }
                });
        }else{
            return HttpResponse.json(
                {
                    msg: "OK",
                    code: 200,
                    payload:
                        {
                            "reservationId": parseInt(id),
                            "userName": "John Doe",
                            "phone": "416-000-1234",
                            "startDate": today+"T13:00",
                            "createDate": null,
                            "status": "CREATED",
                            "services": [],
                            "editable": false
                        }
                });

        }
    })
]