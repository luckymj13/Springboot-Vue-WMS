<template>
  <div>
    <div style="margin-bottom: 5px">
      <el-input v-model="nameText" placeholder="请输入仓库名" style="width: 200px;"
        @keyup.enter.native="loadPost"/>
      <el-button @click="loadPost" size="small" type="primary" icon="el-icon-search" circle style="margin-left: 5px"></el-button>
      <el-button @click="reset" size="small" type="danger" icon="el-icon-delete" circle></el-button>
      <el-button @click="add" size="small" type="primary" icon="el-icon-plus" circle></el-button>
    </div>
  <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="50">
        </el-table-column>
        <el-table-column prop="name" label="仓库名" width="200">
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="800" align="center">
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

        <el-form-item label="仓库名" prop="name">
          <el-col :span="19">
            <el-input v-model="form.name"></el-input>
          </el-col>  
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-col :span="19">
            <el-input type="textarea" v-model="form.remark"></el-input>
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
    name: "StorageManage",
    data() {
      let checkDuplicate = (rule,value,callback)=>{
        if(this.form.id){
          return callback();
        }
        this.$axios.get(this.$httpUrl+"/storage/findByName?name="+this.form.name).then(res=>res.data).then(res=>{
          if(res.code!=200){
            callback()
          }else{
            callback(new Error('该仓库名称已经存在'))
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
          name:'',
          remark:'',
        },
        rules: {
          name: [
            { required: true, message: '请输入仓库名', trigger: 'blur' },
            {validator: checkDuplicate,trigger: 'blur'}
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
          this.form.name = row.name;
          this.form.remark = row.remark;
        })
      },
      del(id){
        this.$axios.get(this.$httpUrl+'/storage/del?id='+id).then(res=>res.data).then(res=>{
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
        this.$axios.post(this.$httpUrl+'/storage/add',this.form).then(res=>res.data).then(res=>{
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
        this.$axios.post(this.$httpUrl+'/storage/mod',this.form).then(res=>res.data).then(res=>{
            console.log(res)
            if(res.code==200){
              this.$message({
                message: '操作成功',
                type: 'success'
              });
              this.centerDialogVisible = false;
              this.loadPost()
            }else{
              this.$message.error('仓库名称已存在');
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
        this.$axios.post(this.$httpUrl+'/storage/getPage',{
          pageNum:this.pageNum,
          pageSize:this.pageSize,
          param:{
            name: this.nameText,
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