<template>

  <el-container style="height: 100%; border: 1px solid #eee">
    <el-header style=" font-size: 12px; height: 100%; border-bottom: rgba(168,168,168,0.3) 1px solid;">
      <Header></Header>
    </el-header>

    <el-container style="height: 100%;">
      <el-aside width="200px" style="background-color: rgb(238, 241, 246); height: 100%; margin-left: -1px;">
        <Aside></Aside>
      </el-aside>
      <el-main style="height: 100%">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>


</template>

<script>
import Aside from "./Aside";
import Header from "./Header"

  export default {
    name: "index",
    components:{
      // eslint-disable-next-line vue/no-reserved-component-names
      Aside,
      // eslint-disable-next-line vue/no-reserved-component-names
      Header,
      // eslint-disable-next-line vue/no-reserved-component-names
    },
    
  };
</script>

<style scoped>
  .el-header {
    /* background-color: #B3C0D1; */
    color: #333;
    line-height: 60px;
  }

  .el-main {
    padding: 5px;
  }
  
  .el-aside {
    color: #333;
  }

</style>