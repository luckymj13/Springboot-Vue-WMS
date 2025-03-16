<template>
<div >
  <h1 style="font-size:40px;margin-top: 50px" align="center">{{'你好!'+user.name}}</h1>
  <el-descriptions title='' :column="2" size="500" border style="padding: 100px 200px">
        <el-descriptions-item>
          <template slot="label">
              <i class="el-icon-s-custom"></i>
              账号
          </template>
          {{user.num}}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
              <i class="el-icon-mobile-phone"></i>
              电话
          </template>
          {{user.phone}}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
              <i class="el-icon-location-outline"></i>
              年龄
          </template>
          {{user.age}}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
              <i class="el-icon-location-outline"></i>
              性别
          </template>
          <el-tag :type="user.sex == '1' ? 'primary' : 'danger'" desable-transitions>
              <i :class="user.sex==1?'el-icon-male':'el-icon-female'"></i>
              {{user.sex==1?"男":"女"}}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
              <i class="el-icon-tickets"></i>
              角色
          </template>
          <el-tag type="success" desable-transitions>{{user.grade==0?"超级管理员":(user.grade==1?"管理员":"用户")}}</el-tag>
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
              <i class="el-icon-mobile-phone"></i>
              操作
          </template>
          <el-button type="primary" @click="modify" style="width: 150px; margin-top: 5px; margin-left: 4px">修改信息</el-button>
        </el-descriptions-item>

    </el-descriptions>

      
    

    <el-dialog
      title="提示"
      :visible.sync="centerDialogVisible"
      width="30%"
      center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="账号" prop="num">
          <el-col :span="19">
            <el-input v-model="form.num" readonly></el-input>
          </el-col>  
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-col :span="19">
            <el-input v-model="form.password"></el-input>
          </el-col>  
        </el-form-item>

        <el-form-item label="名字" prop="name">
          <el-col :span="19">
            <el-input v-model="form.name"></el-input>
          </el-col>  
        </el-form-item>

        <el-form-item label="年龄" prop="age">
          <el-col :span="19">
            <el-input v-model="form.age"></el-input>
          </el-col>  
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="电话" prop="phone">
          <el-col :span="19">
            <el-input v-model="form.phone"></el-input>
          </el-col>  
        </el-form-item>
      </el-form>  
      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doMod">确 定</el-button>
      </span>
    </el-dialog>

</div>

</template>

<script>
export default {
    name:'Home',
  data() {
    let checkAge = (rule, value, callback) => {
        if(value>150){
          callback(new Error('年龄输入过大'));
        }else{
          callback();
        }
      };
    return {
      user: {},
      centerDialogVisible: false,
        form:{
          id:'',
          num:'',
          name:'',
          password:'',
          age:'',
          phone:'',
          sex:0,
          grade:'2'
        },
        rules: {
          name: [
            { required: true, message: '请输入名字', trigger: 'blur' },
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            { min: 3, max: 8, message: '长度在 3 到 8 个字符', trigger: 'blur' }
          ],
          age: [
            { required: true, message: '请输入年龄', trigger: 'blur' },
            { min: 1, max: 3, message: '长度在 1 到 3 个字符', trigger: 'blur' },
            {pattern: /^([1-9][0-9]*){1,3}$/,message: '年龄必须为正整数字', trigger: "blur"},
            {validator: checkAge,trigger: 'blur'}
          ],
          phone: [
            { required: true, message: '手机号不能为空', trigger: 'blur' },
            { pattern:/^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码', trigger: 'blur' }
          ],
        }
    };
  },
  computed:{

  },
  methods:{
    init(){
      this.user = JSON.parse(sessionStorage.getItem('CurUser'))
    },
    modify(){
        this.centerDialogVisible = true;
        this.$nextTick(()=>{
          this.form.id = this.user.id;
          this.form.num = this.user.num;
          this.form.name = this.user.name;
          this.form.password = this.user.password;
          this.form.age = this.user.age+'';
          this.form.sex = this.user.sex+'';
          this.form.phone = this.user.phone;
          this.form.grade = this.user.grade;
        })
      },
      doMod(){

        this.$refs.form.validate((valid) => {
          if (valid) {
            this.$axios.post(this.$httpUrl+'/user/mod',this.form).then(res=>res.data).then(res=>{
            console.log(res)
            if(res.code==200){
              this.$message({
                message: '操作成功',
                type: 'success'
              });
              sessionStorage.setItem("CurUser",JSON.stringify(this.form))
              this.centerDialogVisible = false;
              this.init()
            }else{
              this.$message.error('操作失败');
            } 
        })
          } else {
            console.log('error submit!!');
            return false;
          }
        });

      },
  },
  beforeMount(){
      this.init();
    }
};
</script>

<style>
.personal-container {
  padding: 20px;
}

.personal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.personal-title {
  font-size: 32px;
  font-weight: bold;
}

.personal-username {
  font-size: 24px;
}

.personal-card {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.personal-form {
  font-size: 16px;
}
</style>
