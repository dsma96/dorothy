import { http, HttpResponse  } from 'msw'
const posts = ["게시글1", "게시글2", "게시글3"];

export const handlers = [
    // Intercept "GET https://example.com/user" requests...
    http.get('https://example.com/user', () => {
        // ...and respond to them using this JSON response.
        return HttpResponse.json({
            id: 'c7b3d8e0-5e0b-4b0f-8b3a-3b9f4b3d3b3d',
            firstName: 'John',
            lastName: 'Maverick',
        })
    }),

    // 포스트 목록
    http.get("/posts", (req, res, ctx) => {
        return HttpResponse.json(posts);
    }),

]