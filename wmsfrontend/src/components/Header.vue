<template>
  <div style="display: flex;line-height: 60px">
    <div style="flex: 1;display: flex; font-size: 30px;justify-items: center">
      <img src="../assets/warehouse-icon.jpeg" alt="" width="40px" height="40px" style="padding: 10px 10px 10px 10px">
      <span style="color: #ffffff;font-size: 30px">社区团购仓库管理系统</span>
    </div>
    <el-dropdown style="flex: 1; text-align: right;color: #ffffff;padding-right: 20px">
      <span>{{user.name}}</span>
        <i class="el-icon-arrow-down" ></i>
        <el-dropdown-menu>
          <el-dropdown-item @click.native="toUser">个人中心</el-dropdown-item>
          <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
    </el-dropdown>

    

  </div>
  
</template>

<script>
export default {
    name: "Header",
    data(){     
      return{
        user: JSON.parse(sessionStorage.getItem('CurUser')),
        
      }
    },
    methods:{
      toUser(){
        console.log("to_User")
        this.$router.push("/Home")
      },
      logout(){
        console.log("logout")

        this.$confirm('确定退出登录？','提示',{
          confirmButtonText: '确定',
          type: 'warning',
          center: true,
        }).then(()=>{
          this.$message({
            type: 'success',
            message: '退出登录成功'
          })
          this.$router.push("/")
          sessionStorage.clear()
        }).catch(()=>{
          this.$message({
            type: 'info',
            message: '已取消'
          })
        }) 
      },
      
    },
    
    created(){
      this.$router.push("/Home")
    }
}
</script>

<style>
.el-header{
  padding: 0px;
  background-color: #2782e7;
}
</style>