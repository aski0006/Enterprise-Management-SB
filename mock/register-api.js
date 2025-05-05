export default [
    {
        url: '/api/register',
        method: 'post',
        response: ({body}) => {
            const {username, password, confirmPassword, email} = body;
            if (password === confirmPassword && username !== 'exist') {
                return {
                    code:200,
                    message: '注册成功'
                }
            }else if(username === 'exist'){
                return {
                    code:400,
                    message: '用户名已存在'
                }
            } else if(password !== confirmPassword){
                return {
                    code:400,
                    message: '两次输入的密码不一致'
                }
            }
        }
    }
]