<template>
  <div>
    <div style="margin-bottom: 5px">
      <el-input v-model="nameText" placeholder="请输入名字" style="width: 200px;"
        @keyup.enter.native="loadPost"/>
      <el-button @click="loadPost" size="small" type="primary" icon="el-icon-search" circle style="margin-left: 5px"></el-button>
      <el-button @click="reset" size="small" type="danger" icon="el-icon-delete" circle></el-button>
      <el-button @click="add" size="small" type="primary" icon="el-icon-plus" circle></el-button>
    </div>
  <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="50">
        </el-table-column>
        <el-table-column prop="num" label="账号" width="200">
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="200">
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="100" align="center">
        </el-table-column>
        <el-table-column prop="sex" label="性别" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.sex === 1 ? 'primary' : 'success'"
            disable-transitions>{{scope.row.sex === 1 ? '男' : '女'}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="200" align="center">
        </el-table-column>
        <el-table-column prop="grade" label="权限" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.grade === 0 ? 'danger' : (scope.row.grade === 1 ? 'primary' : 'success')"
            disable-transitions>{{scope.row.grade === 0 ? '超级管理员' : (scope.row.grade === 1 ? '管理员' : '用户')}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operate" label="操作" align="center">
          <template slot-scope="scope">
            <el-button size="small" type="primary" @click="mod(scope.row)">编辑</el-button>
            <el-popconfirm title="确定删除吗？" @confirm="del(scope.row.id)" style="margin-left: 5px">
              <el-button size="small" slot="reference" type="danger">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
  </el-table>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page.sync="pageNum"
      :page-size="9"
      layout="prev, pager, next, jumper"
      :total="total">
    </el-pagination>

    <el-dialog
      title="提示"
      :visible.sync="centerDialogVisible"
      width="30%"
      center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="账号" prop="num">
          <el-col :span="19">
            <el-input v-model="form.num"></el-input>
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
        <el-button type="primary" @click="save">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>

export default {
    name: "UserManage",
    data() {
      let checkAge = (rule, value, callback) => {
        if(value>150){
          callback(new Error('年龄输入过大'));
        }else{
          callback();
        }
      };
      let checkDuplicate = (rule,value,callback)=>{
        if(this.form.id){
          return callback();
        }
        this.$axios.get(this.$httpUrl+"/user/findByNum?num="+this.form.num).then(res=>res.data).then(res=>{
          if(res.code!=200){
            callback()
          }else{
            callback(new Error('账号已经存在'))
          }
        })
      };
      return {
        tableData:[],
        pageNum:1,
        pageSize:9,
        total:0,
        nameText:'',
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
          num: [
            { required: true, message: '请输入账号', trigger: 'blur' },
            { min: 3, max: 8, message: '长度在 3 到 8 个字符', trigger: 'blur' },
            {validator: checkDuplicate,trigger: 'blur'}
          ],
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
      }
    },
    methods:{
      resetForm(){
        this.$refs.form.resetFields();
      },
      add(){
        
        this.centerDialogVisible = true;
        this.$nextTick(()=>{
          this.resetForm();
          //reset后form中存在id不为空的问题
          this.form.id = '';
        })
      },
      mod(row){
        this.centerDialogVisible = true;
        this.$nextTick(()=>{
          this.form.id = row.id;
          this.form.num = row.num;
          this.form.name = row.name;
          this.form.password = row.password;
          this.form.age = row.age+'';
          this.form.sex = row.sex+'';
          this.form.phone = row.phone;
          this.form.grade = row.grade;
        })
      },
      del(id){
        this.$axios.get(this.$httpUrl+'/user/del?id='+id).then(res=>res.data).then(res=>{
            console.log(res)
            if(res.code==200){
              this.$message({
                message: '操作成功',
                type: 'success'
              });
              this.loadPost()
            }else{
              this.$message.error('操作失败');
            } 
        })
      },
      doSave(){
        this.$axios.post(this.$httpUrl+'/user/add',this.form).then(res=>res.data).then(res=>{
            console.log(res)
            if(res.code==200){
              this.$message({
                message: '操作成功',
                type: 'success'
              });
              this.centerDialogVisible = false;
              this.loadPost()
            }else{
              this.$message.error('操作失败');
            } 
        })
      },
      doMod(){
        this.$axios.post(this.$httpUrl+'/user/mod',this.form).then(res=>res.data).then(res=>{
            console.log(res)
            if(res.code==200){
              this.$message({
                message: '操作成功',
                type: 'success'
              });
              this.centerDialogVisible = false;
              this.loadPost()
            }else{
              this.$message.error('操作失败');
            } 
        })
      },
      save(){
        this.$refs.form.validate((valid) => {
          if (valid) {
            if(this.form.id){
              this.doMod();
            }else{
              this.doSave()
            }
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      handleSizeChange(val) {
        console.log(`每页 ${val} 条`);
        this.pageNum=1;
        // this.pageSize=val;
        this.loadPost()
      },
      handleCurrentChange(val) {
        console.log(`当前页: ${val}`);
        // this.pageNum=val;
        this.loadPost()
      },
      reset(){
        this.nameText=''
      },
      loadPost(){
        this.$axios.post(this.$httpUrl+'/user/getPage',{
          pageNum:this.pageNum,
          pageSize:this.pageSize,
          param:{
            name: this.nameText,
            grade:'2'
          }
          
          }).then(res=>res.data).then(res=>{
          console.log(res)
          if(res.code==200){
            this.tableData=res.data;
          this.total=res.total
          }else{
            alert('获取数据失败')
          }
          
        })
      }
    },
    beforeMount(){
      this.loadPost();
    }
}
</script>

<style scoped>

.el-input{
    justify-content: center;
    align-items: center;
}

</style>