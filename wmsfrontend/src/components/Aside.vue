<template>
  <el-menu 
    text-color="#fff"
    active-text-color="#ffd04b"
    style="height:100vh;background-color: #062a50"
    default-active="/Home"
    router
  >
      <el-menu-item :index="'/'+item.menuClick" v-for="(item,i) in menu" :key="i">
        <i :class="item.menuIcon"></i>
        <span slot="title">{{item.menuName}}</span>
      </el-menu-item>
  </el-menu>
</template>

<script>
export default {
    name: "Aside",
    data(){
      return{

      }
    },
    computed:{
      "menu":{
        get(){
          return this.$store.state.menu
        }
      }
    }
}
</script>

<style>
.el-menu {
  border-right: none !important;
}

.el-menu-item {
  background-color: #062a50 !important;
}

.el-menu-item.is-active {
  background-color: #1e3a5f !important;
}
</style>