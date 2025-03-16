<template>


  <div class="container">
    <div style="width: 30%; display: flex;background-color: white; ">
      <div style="flex: 1;width: 50%;padding: 40px;display: flex;  flex-direction: column;justify-content: center;" >
        <div style="text-align: center; font-size: 30px; color: #131313;margin-bottom: 15px;  ">社区团购仓库管理系统</div>
        <el-form ref="loginForm" :model="loginForm"  class="login-form">
          <el-form-item  prop="num">
            <el-input v-model="loginForm.num" placeholder="请输入账号" style="width: 100%;"></el-input>
          </el-form-item>
          <el-form-item  prop="password">
            <el-input type="password" v-model="loginForm.password" placeholder="请输入密码" style="width: 100%;" @keyup.enter.native="login(loginForm)"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="login(loginForm)" style="width: 100%;margin-top: 30px">登录</el-button>
          </el-form-item>
        </el-form>
      </div>

    </div>

  </div>


</template>

<script>

export default {
  name:"Login",
  data() {
    return {
      loginForm: {
        num: '',
        password: ''
      }
    };
  },

  methods: {
    login(loginForm) {
      this.$axios.get(this.$httpUrl+'/user/login?num='+loginForm.num+"&password="+loginForm.password).then(res=>res.data).then(res=>{
            console.log(res)
            if(res.code==200){
                // 保存用户信息和token
                sessionStorage.setItem("CurUser",JSON.stringify(res.data.user))
                // 单独保存token，确保拦截器可以获取到
                sessionStorage.setItem("token", res.data.token)
                
                // 过滤掉不需要的菜单项
                const filteredBackendMenu = res.data.menu.filter(item => {
                  // 过滤掉以下菜单项
                  const unwantedMenus = [
                    'ORDER', 'ORDER_C', 'ORDER_L', 'ORDER_R',
                    'PURCH', 'PURCH_P', 'PURCH_S',
                    'SALES', 'SALES_C', 'SALES_A'
                  ];
                  return !unwantedMenus.includes(item.menuCode);
                });
                
                // 确保首页菜单项存在
                const hasHomeMenu = filteredBackendMenu.some(item => item.menuClick === 'Home');
                if (!hasHomeMenu) {
                  // 如果后端返回的菜单中没有首页，手动添加一个
                  filteredBackendMenu.unshift({
                    id: 0,
                    menuCode: '000',
                    menuName: '首页',
                    menuLevel: '1',
                    menuParentCode: null,
                    menuClick: 'Home',
                    menuRight: '0,1,2', // 所有用户都可访问
                    menuComponent: 'Home',
                    menuIcon: 'el-icon-s-home'
                  });
                }
                
                // 更新菜单状态
                this.$store.commit("setMenu", filteredBackendMenu)
                
                this.$router.replace('/index');
            }else{
              this.$message.error('账号或密码错误');
            } 
        })
    },
  },
};
</script>

<style>

.container {
  height: 100vh;
  overflow: hidden;
  /*background-color: #f3e7ce;*/
  background-image: url("../assets/bg1.jpg");
  background-size: 100% 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}


.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  /*background: #f5f7fa;*/
  background-image: url("../assets/bg5.png");
  background-size: 100% 100%;
}

.login-card {
  width: 380px;
  padding: 40px;
  background-color: rgba(255,255,255,0.9);
}

.login-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.login-form {
  margin-top: 20px;
}
</style>
