import { http, HttpResponse    } from 'msw'
import moment from 'moment'

export const handlers = [
    http.get('/api/reserve/services', () => {
            // ...and respond to them using this JSON response.
            return HttpResponse.json(
                {
                    "msg": "OK",
                    "code": 200,
                    "payload": [
                        {
                            "serviceId": 1,
                            "name": "남자 헤어컷",
                            "mandatory": true,
                            "idx": 0,
                            "defaultValue": true,
                            "visible": false,
                            "use": true
                        },
                        {
                            "serviceId": 6,
                            "name": "조용히 시술받고 싶어요",
                            "mandatory": false,
                            "idx": 999,
                            "defaultValue": false,
                            "visible": true,
                            "use": true
                        }
                    ]
                }
            )
        }
    ),

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
    http.put('/api/user/:id',()=> {
        return HttpResponse.json(
            {
                msg: "OK",
                code: 200,
                payload: {
                    phone: "6474060362",
                    email: "silverwing@gmail.com",
                    name: "Lemmy",
                    id: 99
                }
            }
            )
        }
    )
    ,
    http.post('/api/login/logout', async ( ) => {
        // ...and respond to them using this JSON response.
        return HttpResponse.json(
            {
                msg: "OK",
                code: 200,
            }
        )
    }
    ),
    http.post('/api/login/login', async ( {request, params, cookies}) => {
            // ...and respond to them using this JSON response.
            const data = await request.json();
            if (data.phone == '6474060362') {
                return HttpResponse.json(
                    {
                        msg: "OK",
                        code: 200,
                        payload: {
                            phone: "6474060362",
                            password: null,
                            email: "silverwing@gmail.com",
                            name: "Lemmy",
                            id: 2
                        }
                    }
                )
            }
            else {
                return HttpResponse.json(
                    {"msg":"Invalid password :6474060362 merong2w3","code":500,"payload":null}
                );
            }
        }
    ),
    http.get('/api/login/relogin', () => {
            // ...and respond to them using this JSON response.
            return HttpResponse.json(
                {
                    msg: "OK",
                    code: 500,
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
                        "editable": true,
                        "requireSilence":true
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
                                    serviceId: 6,
                                }
                            ],
                            status: 'CREATED',
                            editable:true,
                            memo:'hello World',
                            requireSilence:true
                        },
                        {
                            reservationId: 2,
                            userName: "Johne Doe",
                            phone: "416-000-1234",
                            startDate: today+"T13:00",
                            createDate: "20250309T13:30",
                            services: [],
                            status: 'CREATED',
                            editable: false,
                            requireSilence:false
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
                    "msg": "OK",
                    "code": 200,
                    "payload": {
                        "reservationId": 28,
                        "userName": "동석",
                        "phone": "6474060362",
                        "startDate": "20250326T14:00",
                        "createDate": "20250319T01:09",
                        "status": "CREATED",
                        "services": [
                            {
                                "serviceId": 1,
                                "name": "남자 헤어컷",
                                "mandatory": true,
                                "idx": 0,
                                "defaultValue": true,
                                "visible": false,
                                "use": true
                            }
                        ],
                        "memo": "앞 머리는 빡빡 밀고 \n옆 머리만 남겨주세요.\n저 심각하니까 말걸지 마세요",
                        "editable": true,
                        "requireSilence": true
                    }
                }
            )
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
                            "editable": false,
                            "requireSilence": true
                        }
                });

        }
    }),
    http.post('/api/verify/request', async ( {request, params, cookies}) => {
        // ...and respond to them using this JSON response.
        const data = await request.json();
        if( data.phoneNo == '6474060362') {
            return HttpResponse.json(
                {
                    "msg": "OK",
                    "code": 200,
                    "payload": {
                        "phoneNo": data.phoneNo,
                        "channel": "SMS",
                        "type": "SIGN_UP",
                        "maxTry": 15,
                        "failCnt": 0,
                        "verifyCode": null,
                        "state": "CREATED",
                        "createDate": moment(new Date()).format("YYYYMMDDTHH:mm:ss"),
                        "expireDate": moment(new Date(new Date().getTime() + 1 * 60 * 1000)).format("YYYYMMDDTHH:mm:ss")
                    }
                }
            );
        }else{
            return HttpResponse.json(
                {
                    "msg": "Phone number already in use ",
                    "code": 500,
                    "payload": null
                }
            );
        }



    }),
    http.post('/api/verify/match', async ( {request, params, cookies}) => {
        // ...and respond to them using this JSON response.
        const data = await request.json();
        if( data.verifyCode == '1234') {
            return HttpResponse.json(
                {
                    "msg": "OK", "code": 200,
                    "payload": {
                        "phoneNo": data.phoneNo,
                        "channel": "SMS",
                        "type": "SIGN_UP",
                        "maxTry": 15, "failCnt": 0,
                        "verifyCode": null, "state": "VERIFIED",
                        "createDate": moment(new Date()).format("YYYYMMDDTHH:mm:ss"),
                        "expireDate": moment(new Date(new Date().getTime() + 1 * 60 * 1000)).format("YYYYMMDDTHH:mm:ss")
                    }
                }
            );
        }else{
            return HttpResponse.json(
                {
                    "msg": "OK", "code": 200,
                    "payload": {
                        "phoneNo": data.phoneNo,
                        "channel": "SMS",
                        "type": "SIGN_UP",
                        "maxTry": 15,
                        "failCnt": 7,
                        "verifyCode": null,
                        "state": "CREATED",
                        "createDate": moment(new Date()).format("YYYYMMDDTHH:mm:ss"),
                        "expireDate": moment(new Date(new Date().getTime() + 1 * 60 * 1000)).format("YYYYMMDDTHH:mm:ss")
                    }
                }
            );
        }
    }),
]