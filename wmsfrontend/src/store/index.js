import vue from 'vue'
import Vuex from 'vuex'
import router,{resetRouter} from '../router'
import createPersistedState from 'vuex-persistedstate'
import order from './order'
import supplier from './supplier'
import customer from './customer'
import purchase from './purchase'
import sales from './sales'

vue.use(Vuex)

// 保存已添加的路由路径，避免重复添加
const addedRoutePaths = new Set();
// 初始化时添加首页路由路径，避免重复添加
addedRoutePaths.add('/Home');

function addNewRoute(menuList){
    console.log("menuList========="+menuList);
    console.log(menuList);
    let routes = router.options.routes
    // 循环路由
    routes.forEach(routeItem=>{
        if(routeItem.path=="/index"){
            // 循环数据 
            menuList.forEach(menu=>{
                // 检查路由是否已存在，避免重复添加
                const routePath = '/'+menu.menuClick;
                if (!addedRoutePaths.has(routePath)) {
                    let childRoute = {     
                        path: routePath,
                        name: menu.menuName,
                        meta:{
                            title: menu.menuName,
                        },
                        component:()=>import('../components/'+menu.menuComponent),
                    }
                    //添加子路由
                    routeItem.children.push(childRoute)
                    // 记录已添加的路由路径
                    addedRoutePaths.add(routePath);
                }
            })
        }
    })
    //原有路由会重复
    //添加路由之前调用路由清空方法
    resetRouter()
    router.addRoutes(routes)
}


export default new Vuex.Store({
    //数据，相当于data
    state: {
      menu:[]
    },
    getters: {
      getMenu(state){
        return state.menu
      }
    },
    //里面定义方法，操作state方发
    mutations: {
      setMenu(state,menuList){
        state.menu = menuList

        addNewRoute(menuList)
      }
    },
    // 操作异步操作mutation
    actions: {
      
    },
    modules: {
      order,
      supplier,
      customer,
      purchase,
      sales
    },
    //解决刷新动态路由丢失问题  存储menu
    plugins:[createPersistedState()]
  })