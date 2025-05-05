export default [
    {
        url: '/api/login',
        method: 'post',
        response: ({ body }) => {
            const { username, password, remember } = body
            if (username === 'admin' && password === 'admin') {
                return {
                    code: 200,
                    data: {
                        token: 'admin',
                        user: {
                            name: 'admin',
                            role: 'common',
                            remember: remember
                        }
                    },
                    message: '登录成功'
                }
            } else {
                return {
                    code: 400,
                    message: '用户名或密码错误'
                }
            }
        }
    }
]