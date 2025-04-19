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
                            "mandatory": false,
                            "idx": 10,
                            "defaultValue": false,
                            "serviceTime": 30,
                            "price": 16.0,
                            "visible": true,
                            "use": true
                        },
                        {
                            "serviceId": 2,
                            "name": "남자 Root Colour (커트포함)",
                            "mandatory": false,
                            "idx": 20,
                            "defaultValue": false,
                            "serviceTime": 60,
                            "price": 65.0,
                            "visible": true,
                            "use": true
                        },
                        {
                            "serviceId": 3,
                            "name": "남자 다운펌 (커트포함)",
                            "mandatory": false,
                            "idx": 30,
                            "defaultValue": false,
                            "serviceTime": 60,
                            "price": 40.0,
                            "visible": true,
                            "use": true
                        },
                        {
                            "serviceId": 4,
                            "name": "남자 Perm (커트포함)",
                            "mandatory": false,
                            "idx": 40,
                            "defaultValue": false,
                            "serviceTime": 90,
                            "price": 65.0,
                            "visible": true,
                            "use": true
                        },
                        {
                            "serviceId": 7,
                            "name": "여자 헤어컷",
                            "mandatory": false,
                            "idx": 50,
                            "defaultValue": false,
                            "serviceTime": 60,
                            "price": 25.0,
                            "visible": true,
                            "use": true
                        },
                        {
                            "serviceId": 8,
                            "name": "여자 Root Colour (커트포함)",
                            "mandatory": false,
                            "idx": 60,
                            "defaultValue": false,
                            "serviceTime": 90,
                            "price": 65.0,
                            "visible": true,
                            "use": true
                        },
                        {
                            "serviceId": 9,
                            "name": "칼 갈이",
                            "mandatory": false,
                            "idx": 999,
                            "defaultValue": false,
                            "serviceTime": 30,
                            "price": 5.0,
                            "visible": true,
                            "use": true
                        }
                    ]
                }
            )
        }
    ),
    http.get('/api/user/:dateStr/designers',( {params}) => {
            const {dateStr} = params;
            const today = new Date();
            let targetDate = moment(dateStr,"YYYYMMDD").toDate();


        if( targetDate.getTime() > today.getTime()+ 86400000*4 ){
                return HttpResponse.json(
                    {
                        "msg": "OK",
                        "code": 200,
                        "payload": []
                    }
                )
            }else {
                return HttpResponse.json(
                    {
                        "msg": "OK",
                        "code": 200,
                        "payload": [
                            {
                                "phone": null,
                                "password": null,
                                "email": null,
                                "name": "Jay",
                                "id": 1,
                                "rootUser": false
                            }
                        ]
                    }
                )
            }
        }
    ),

,
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
    http.put('/api/user/:id/memo',()=> {
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
                            "rootUser":true,
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
    http.get('/api/login/relogin', async () => {
            return HttpResponse.json(
                {
                    msg: "OK",
                    code: 200,
                    payload: {
                        phone: "6474060362",
                        password: null,
                        email: "silverwing@gmail.com",
                        name: "Lemmy",
                        id:2,
                        rootUser:true
                    }
                }
            )
        }
    ),
    http.get('/api/user/:userId', async () => {
            return HttpResponse.json(
                {
                    "msg": "OK",
                    "code": 200,
                    "payload": {
                        "phone": "6474060362",
                        "password": null,
                        "email": null,
                        "name": "Lemmy Ma",
                        "memo": "머리 잘못 파마했음",
                        "id": 2,
                        "rootUser": false
                    }
                }
            )
        }
    ),
    http.get('api/reserve/history', async () => {
        return HttpResponse.json(
            {
                "msg": "OK",
                "code": 200,
                "payload": {
                    "content": [
                        {
                            "reservationId": 86,
                            "userName": "Lemmy Ma",
                            "phone": "6474060362",
                            "startDate": "20250412T14:30",
                            "endDate": "20250412T16:00",
                            "createDate": "20250411T15:44",
                            "status": "CREATED",
                            "services": [
                                {
                                    "serviceId": 8,
                                    "name": "여자 Root Colour (커트포함)",
                                    "mandatory": false,
                                    "idx": 60,
                                    "defaultValue": false,
                                    "serviceTime": 90,
                                    "price": 65.0,
                                    "visible": true,
                                    "use": true
                                }
                            ],
                            "memo": "",
                            "files": [],
                            "requireSilence": false,
                            "editable": false,
                            "userId":2
                        },
                        {
                            "reservationId": 2,
                            "userName": "Lemmy Ma",
                            "phone": "6474060362",
                            "startDate": "20250411T16:00",
                            "endDate": "20250411T16:30",
                            "createDate": "20250306T00:00",
                            "status": "CREATED",
                            "services": [
                                {
                                    "serviceId": 1,
                                    "name": "남자 헤어컷",
                                    "mandatory": false,
                                    "idx": 10,
                                    "defaultValue": false,
                                    "serviceTime": 30,
                                    "price": 16.0,
                                    "visible": true,
                                    "use": true
                                }
                            ],
                            "memo": "첫예약",
                            "files": [],
                            "requireSilence": false,
                            "editable": false
                        },
                        {
                            "reservationId": 20,
                            "userName": "Lemmy Ma",
                            "phone": "6474060362",
                            "startDate": "20250313T10:30",
                            "endDate": "20250313T11:00",
                            "createDate": "20250312T22:46",
                            "status": "CREATED",
                            "services": [
                                {
                                    "serviceId": 1,
                                    "name": "남자 헤어컷",
                                    "mandatory": false,
                                    "idx": 10,
                                    "defaultValue": false,
                                    "serviceTime": 30,
                                    "price": 16.0,
                                    "visible": true,
                                    "use": true
                                }
                            ],
                            "memo": "lemmy's",
                            "files": [],
                            "requireSilence": false,
                            "editable": false
                        },
                        {
                            "reservationId": 17,
                            "userName": "Lemmy Ma",
                            "phone": "6474060362",
                            "startDate": "20250312T10:30",
                            "endDate": "20250312T11:00",
                            "createDate": "20250312T00:30",
                            "status": "CREATED",
                            "services": [
                                {
                                    "serviceId": 1,
                                    "name": "남자 헤어컷",
                                    "mandatory": false,
                                    "idx": 10,
                                    "defaultValue": false,
                                    "serviceTime": 30,
                                    "price": 16.0,
                                    "visible": true,
                                    "use": true
                                }
                            ],
                            "memo": "남자 커트 예약합니다!",
                            "files": [],
                            "requireSilence": false,
                            "editable": false
                        },
                        {
                            "reservationId": 14,
                            "userName": "Lemmy Ma",
                            "phone": "6474060362",
                            "startDate": "20250308T14:00",
                            "endDate": "20250308T14:30",
                            "createDate": "20250308T03:46",
                            "status": "CREATED",
                            "services": [
                                {
                                    "serviceId": 1,
                                    "name": "남자 헤어컷",
                                    "mandatory": false,
                                    "idx": 10,
                                    "defaultValue": false,
                                    "serviceTime": 30,
                                    "price": 16.0,
                                    "visible": true,
                                    "use": true
                                }
                            ],
                            "memo": "안녕하세요? 잘 부탁드려요",
                            "files": [],
                            "requireSilence": false,
                            "editable": false
                        },
                        {
                            "reservationId": 13,
                            "userName": "Lemmy Ma",
                            "phone": "6474060362",
                            "startDate": "20250308T13:00",
                            "endDate": "20250308T13:30",
                            "createDate": "20250308T03:42",
                            "status": "CREATED",
                            "services": [
                                {
                                    "serviceId": 1,
                                    "name": "남자 헤어컷",
                                    "mandatory": false,
                                    "idx": 10,
                                    "defaultValue": false,
                                    "serviceTime": 30,
                                    "price": 16.0,
                                    "visible": true,
                                    "use": true
                                }
                            ],
                            "memo": "3번째 예약",
                            "files": [],
                            "requireSilence": false,
                            "editable": false
                        },
                        {
                            "reservationId": 12,
                            "userName": "Lemmy Ma",
                            "phone": "6474060362",
                            "startDate": "20250308T11:30",
                            "endDate": "20250308T12:00",
                            "createDate": "20250308T03:40",
                            "status": "CREATED",
                            "services": [
                                {
                                    "serviceId": 1,
                                    "name": "남자 헤어컷",
                                    "mandatory": false,
                                    "idx": 10,
                                    "defaultValue": false,
                                    "serviceTime": 30,
                                    "price": 16.0,
                                    "visible": true,
                                    "use": true
                                }
                            ],
                            "memo": "두번째 예약입니다.",
                            "files": [],
                            "requireSilence": false,
                            "editable": false
                        },
                        {
                            "reservationId": 11,
                            "userName": "Lemmy Ma",
                            "phone": "6474060362",
                            "startDate": "20250308T10:00",
                            "endDate": "20250308T10:30",
                            "createDate": "20250308T02:20",
                            "status": "CREATED",
                            "services": [
                                {
                                    "serviceId": 1,
                                    "name": "남자 헤어컷",
                                    "mandatory": false,
                                    "idx": 10,
                                    "defaultValue": false,
                                    "serviceTime": 30,
                                    "price": 16.0,
                                    "visible": true,
                                    "use": true
                                }
                            ],
                            "memo": "처음 예약입니다. 잘 부탁드려요",
                            "files": [],
                            "requireSilence": false,
                            "editable": false
                        }
                    ],
                    "pageable": {
                        "pageNumber": 0,
                        "pageSize": 100,
                        "sort": [
                            {
                                "direction": "DESC",
                                "property": "startDate",
                                "ignoreCase": false,
                                "nullHandling": "NATIVE",
                                "ascending": false,
                                "descending": true
                            }
                        ],
                        "offset": 0,
                        "paged": true,
                        "unpaged": false
                    },
                    "last": true,
                    "totalElements": 8,
                    "totalPages": 1,
                    "first": true,
                    "size": 100,
                    "number": 0,
                    "sort": [
                        {
                            "direction": "DESC",
                            "property": "startDate",
                            "ignoreCase": false,
                            "nullHandling": "NATIVE",
                            "ascending": false,
                            "descending": true
                        }
                    ],
                    "numberOfElements": 8,
                    "empty": false
                }
            }
        )
    }),
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
                        "requireSilence":true,
                        "userId":2
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
                    "msg": "OK",
                    "code": 200,
                    "payload": [
                        {
                            "reservationId": 72,
                            "userName": "Occupied",
                            "phone": "000-000-0000",
                            "startDate": today+"T11:00",
                            "endDate": today+"T11:30",
                            "createDate": "20250409T20:10",
                            "status": "CREATED",
                            "services": [],
                            "memo": "",
                            "files": [],
                            "editable": false,
                            "requireSilence": false
                        },
                        {
                            "reservationId": 86,
                            "userName": "Lemmy Ma",
                            "phone": "6474060362",
                            "startDate": today+"T13:00",
                            "endDate": today+"T14:00",
                            "createDate": "20250411T15:44",
                            "status": "CREATED",
                            "userId":2,
                            "services": [
                                {
                                    "serviceId": 2,
                                    "name": "남자 Root Colour (커트포함)",
                                    "mandatory": false,
                                    "idx": 20,
                                    "defaultValue": false,
                                    "serviceTime": 60,
                                    "price": 65.0,
                                    "visible": true,
                                    "use": true
                                }
                            ],
                            "memo": "",
                            "files": [],
                            "editable": true,
                            "requireSilence": false
                        },
                        {
                            "reservationId": 78,
                            "userName": "Occupied",
                            "phone": "000-000-0000",
                            "startDate": today+"T14:00",
                            "endDate": today+"T14:30",
                            "createDate": "20250411T02:03",
                            "status": "CREATED",
                            "services": [],
                            "memo": "",
                            "files": [],
                            "editable": false,
                            "requireSilence": false
                        }
                    ]
                }
            )
        }
    ),
    http.get('/api/reserve/:id', ({params})=>{
        const{id} = params;
        let today = moment(new Date()).format("YYYYMMDD");
        if( id == '86'){
            return HttpResponse.json(
                {
                    "msg": "OK",
                    "code": 200,
                    "payload": {
                        "reservationId": 28,
                        "userName": "동석",
                        "phone": "6474060364",
                        "startDate": "20250326T13:00",
                        "endDate": "20250326T14:00",
                        "createDate": "20250319T01:09",
                        "status": "CREATED",
                        "userId":2,
                        "services": [
                            {
                                "serviceId": 2,
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
                        "requireSilence": true,
                        "files": [
                            {
                                "id": 1,
                                "userFileName": "deny.jpg",
                                "url": "/1.jpg"
                            },
                            {
                                "id": 2,
                                "userFileName": "gaza.png",
                                "url": "/2.gif"
                            }
                        ],
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
                            "requireSilence": true,
                            "userId":3,
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
    http.get('/api/user/offday/:year/:month', () => {
            // ...and respond to them using this JSON response.
            return HttpResponse.json(
                {
                    "msg": "OK",
                    "code": 200,
                    "payload": [
                        {
                            "offDay": "20250407",
                            "designer": 1
                        },
                        {
                            "offDay": "20250408",
                            "designer": 1
                        },
                        {
                            "offDay": "20250409",
                            "designer": 1
                        },
                        {
                            "offDay": "20250410",
                            "designer": 1
                        },
                        {
                            "offDay": "20250414",
                            "designer": 1
                        },
                        {
                            "offDay": "20250415",
                            "designer": 1
                        },
                        {
                            "offDay": "20250416",
                            "designer": 1
                        },
                        {
                            "offDay": "20250417",
                            "designer": 1
                        },
                        {
                            "offDay": "20250421",
                            "designer": 1
                        },
                        {
                            "offDay": "20250422",
                            "designer": 1
                        },
                        {
                            "offDay": "20250423",
                            "designer": 1
                        },
                        {
                            "offDay": "20250424",
                            "designer": 1
                        },
                        {
                            "offDay": "20250428",
                            "designer": 1
                        },
                        {
                            "offDay": "20250429",
                            "designer": 1
                        },
                        {
                            "offDay": "20250430",
                            "designer": 1
                        }
                    ]
                }
            )
        }
    ),
    http.put('/api/reserve/:op/:id',() => {
    // ...and respond to them using this JSON response.
    return HttpResponse.json(
        {
            "msg": "OK",
            "code": 200,
            "payload": {
                "reservationId": 1,
                "userName": "Aiden",
                "phone": "2892440503",
                "startDate": "20250411T15:00",
                "endDate": "20250411T15:30",
                "createDate": "20250418T16:15",
                "status": "CREATED",
                "services": [
                    {
                        "serviceId": 1,
                        "name": "남자 헤어컷",
                        "mandatory": false,
                        "idx": 10,
                        "defaultValue": false,
                        "serviceTime": 30,
                        "price": 16.0,
                        "visible": true,
                        "use": true
                    },
                    {
                        "serviceId": 3,
                        "name": "남자 다운펌 (커트포함)",
                        "mandatory": false,
                        "idx": 30,
                        "defaultValue": false,
                        "serviceTime": 60,
                        "price": 40.0,
                        "visible": true,
                        "use": true
                    }
                ],
                "memo": "첫예약",
                "files": [],
                "userId": 3,
                "editable": false,
                "requireSilence": false
            }
        }
    )
})
]