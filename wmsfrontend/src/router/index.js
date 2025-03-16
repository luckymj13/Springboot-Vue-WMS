import VueRouter from "vue-router";

const routes = [
    {
        path:'/',
        name:'login',
        component:()=>import('../components/Login')
    },
    {
        path:'/index',
        name:'index',
        component:()=>import('../components/index'),
        children:[
            // 保留首页路由，确保登录后有内容显示
            {
                path:'/Home',
                name:'Home',
                meta:{
                    title:'首页'
                },
                component:()=>import('../components/Home'),
            }
            // 其他路由将通过动态路由机制添加
        ]
    }
]

const router = new VueRouter({
    mode:'history',
    routes
})

//错误处理
export function resetRouter(){
    router.matcher = new VueRouter({
        mode:'history',
        routes:[]
    }).matcher
}

const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(to){
    return VueRouterPush.call(this, to).catch(err => err)
}

export default router;