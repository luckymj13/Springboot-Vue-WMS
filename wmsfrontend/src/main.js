import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/global.css';
import axios from 'axios';
import VueRouter from 'vue-router';
import router from './router';
import store from './store';
import * as echarts from "echarts";

Vue.prototype.$axios=axios;
Vue.prototype.$httpUrl='http://localhost:8008'

// 添加请求拦截器
axios.interceptors.request.use(
  config => {
    // 从sessionStorage中获取token
    const token = sessionStorage.getItem('token');
    
    // 如果token存在，则添加到请求头中
    if (token) {
      config.headers.token = token;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

Vue.use(ElementUI);
Vue.config.productionTip = false
Vue.use(VueRouter)
Vue.prototype.$echarts = echarts;

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')
