import { http, HttpResponse    } from 'msw'


export const handlers = [
    // Intercept "GET https://example.com/user" requests...
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
                        name: "Lemmy"
                    }
                }
            )
        }
    ),
]