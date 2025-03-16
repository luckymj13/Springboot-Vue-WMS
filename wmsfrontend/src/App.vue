<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>

export default {
  name: 'App',
  components: {
    
  },
  data(){
    return{
      user: JSON.parse(sessionStorage.getItem('CurUser')),
    }
  },
  //监视动态路由，刷新重新设置动态路由
  watch:{
    '$store.state.menu':{
        handler(val,old){
          if(!old&&this.user&&this.user.num){
              this.$store.commit("setMenu",val)
          }
        },
        immediate :true
    }
  }
}
</script>

<style>
  #app {
    height: 100%;
  }

  .el-menu-item:focus, .el-menu-item:hover {
    background-color: #062a50 !important;
  }

</style>
