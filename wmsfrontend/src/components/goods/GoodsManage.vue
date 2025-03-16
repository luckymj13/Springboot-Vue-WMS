<template>
  <div class="goods-manage-container">
    <!-- 搜索区域优化：使用flex布局，更加紧凑 -->
    <div class="search-container">
      <el-input v-model="nameText" placeholder="请输入物品名" style="width: 180px;"
        @keyup.enter.native="loadPost"/>

      <el-select v-model="storage" placeholder="请选择仓库" style="width: 130px; margin-left: 5px;">
        <el-option
          v-for="item in storageData"
          :key="item.id"
          :label="item.name"
          :value="item.id">
        </el-option>
      </el-select>

      <el-select v-model="goodstype" placeholder="请选择分类" style="width: 130px; margin-left: 5px;">
        <el-option
          v-for="item in goodstypeData"
          :key="item.id"
          :label="item.name"
          :value="item.id">
        </el-option>
      </el-select>

      <div class="button-group">
        <el-button @click="loadPost" size="small" type="primary" icon="el-icon-search" circle></el-button>
        <el-button @click="reset" size="small" type="danger" icon="el-icon-delete" circle></el-button>
        <el-button @click="add" size="small" type="primary" icon="el-icon-plus" circle v-if="user.grade==0"></el-button>
      </div>
    </div>

    <!-- 表格区域优化：移除固定宽度，使用响应式布局 -->
    <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="50"></el-table-column>
        <el-table-column prop="name" label="货物名" min-width="120"></el-table-column>
        <el-table-column prop="storage" label="仓库" min-width="100" :formatter="formatStorage"></el-table-column>
        <el-table-column prop="goodstype" label="分类" min-width="100" :formatter="formatGoodsType"></el-table-column>
        <el-table-column prop="count" label="数量" min-width="80"></el-table-column>
        <el-table-column prop="price" label="价格" min-width="80"></el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="operate" label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="mod(scope.row)">编辑</el-button>
            <el-popconfirm title="确定删除吗？" @confirm="del(scope.row.id)" style="margin-left: 5px">
              <el-button size="mini" slot="reference" type="danger">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
    </el-table>

    <!-- 分页区域优化：增加页面大小选择器 -->
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page.sync="pageNum"
        :page-sizes="[5, 10, 15, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>

    <!-- 对话框优化：增加响应式宽度 -->
    <el-dialog
      title="物品信息"
      :visible.sync="centerDialogVisible"
      :width="dialogWidth"
      center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="物品名" prop="name">
              <el-input v-model="form.name"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <el-form-item label="仓库" prop="storage">
              <el-select v-model="form.storage" placeholder="请选择仓库" style="width: 100%">
                <el-option
                  v-for="item in storageData"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :xs="24" :sm="12">
            <el-form-item label="分类" prop="goodstype">
              <el-select v-model="form.goodstype" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="item in goodstypeData"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="价格" prop="price">
              <el-input v-model="form.price" type="number" min="0" step="0.01"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input type="textarea" v-model="form.remark" :rows="3"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
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
    name: "GoodsManage",
    data() {
       let checkDuplicate = (rule,value,callback)=>{
         if(this.form.id){
           return callback();
         }
         this.$axios.get(this.$httpUrl+"/goods/findByName?name="+this.form.name).then(res=>res.data).then(res=>{
           if(res.code!=200){
            console.log(res.code+"aaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAA")
             callback()
           }else{
             callback(new Error('该物品名称已经存在'))
           }
         })
       };
      // let checkCount = (rule ,value ,callback) => {
      //   if(value>9999){
      //     callback(new Error('输入数量过大'));
      //   }else{
      //     callback();
      //   }
      // };
      return {
        user: JSON.parse(sessionStorage.getItem('CurUser')),
        goodstypeData:[],
        storageData:[],
        tableData:[],
        pageNum:1,
        pageSize:10,
        total:0,
        nameText:'',
        centerDialogVisible: false,
        storage:'',
        goodstype:'',
        dialogWidth: '500px',
        form:{
          id:'',
          name:'',
          storage:'',
          goodstype:'',
          count:'',
          price:'',
          remark:'',
        },
        rules: {
          name: [
            { required: true, message: '请输入物品名', trigger: 'blur' },
            {validator: checkDuplicate,trigger: 'blur'}
          ],
          storage: [
            { required: true, message: '请选择仓库', trigger: 'blur' },
            // {validator: checkDuplicate,trigger: 'blur'}
          ],
          goodstype: [
            { required: true, message: '请选择分类', trigger: 'blur' },
            // {validator: checkDuplicate,trigger: 'blur'}
          ],
          price: [
            { required: true, message: '请输入价格', trigger: 'blur' },
            { pattern: /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/, message: '价格必须为正数，最多两位小数', trigger: 'blur' }
          ]
          // count:[
          //   {required: true, message: '请输入数量', trigger: 'blur'},
          //   {pattern: /^([1-9][0-9]*){1,4}$/, message: '数量必须为正整数',trigger: 'blur'},
          //   {validator: checkCount, trigger: 'blur'}
          // ]
        }
      }
    },
    methods:{
      formatStorage(row){
        let temp = this.storageData.find(item=>{
          return item.id == row.storage
        })
        return temp && temp.name
      },
      formatGoodsType(row){
        let temp = this.goodstypeData.find(item=>{
          return item.id == row.goodstype
        })
        return temp && temp.name
      },
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
        // 先获取原始的物品信息，包括备注字段
        this.$axios.get(this.$httpUrl+'/goods/getById?id='+row.id).then(res=>res.data).then(res=>{
          if(res.code==200){
            const originalGoods = res.data;
            this.$nextTick(()=>{
              this.form.id = originalGoods.id;
              this.form.name = originalGoods.name;
              this.form.storage = originalGoods.storage;
              this.form.goodstype = originalGoods.goodstype;
              this.form.count = originalGoods.count;
              this.form.price = originalGoods.price;
              this.form.remark = originalGoods.remark;
            });
          } else {
            // 如果获取失败，则使用表格中的数据
            this.$nextTick(()=>{
              this.form.id = row.id;
              this.form.name = row.name;
              this.form.storage = row.storage;
              this.form.goodstype = row.goodstype;
              this.form.count = row.count;
              this.form.price = row.price;
              this.form.remark = row.remark;
            });
          }
        }).catch(error => {
          // 如果请求出错，则使用表格中的数据
          this.$nextTick(()=>{
            this.form.id = row.id;
            this.form.name = row.name;
            this.form.storage = row.storage;
            this.form.goodstype = row.goodstype;
            this.form.count = row.count;
            this.form.price = row.price;
            this.form.remark = row.remark;
          });
        });
      },
      del(id){
        this.$axios.get(this.$httpUrl+'/goods/del?id='+id).then(res=>res.data).then(res=>{
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
        this.$axios.post(this.$httpUrl+'/goods/add',this.form).then(res=>res.data).then(res=>{
            console.log(res)
            if(res.code==200){
              this.$message({
                message: '操作成功',
                type: 'success'
              });
              this.centerDialogVisible = false;
              this.loadPost()
              this.resetForm()
            }else{
              this.$message.error('操作失败');
            } 
        })
      },
      doMod(){
        this.$axios.post(this.$httpUrl+'/goods/mod',this.form).then(res=>res.data).then(res=>{
            console.log(res)
            if(res.code==200){
              this.$message({
                message: '操作成功',
                type: 'success'
              });
              this.centerDialogVisible = false;
              this.loadPost()
              this.resetForm()
            }else{
              this.$message.error('该物品名称已存在');
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
        this.pageNum = 1;
        this.pageSize = val;
        this.loadPost();
      },
      handleCurrentChange(val) {
        console.log(`当前页: ${val}`);
        this.pageNum = val;
        this.loadPost();
      },
      reset(){
        this.nameText='',
        this.storage='',
        this.goodstype=''
      },
      loadPost(){
        this.$axios.post(this.$httpUrl+'/goods/getPage',{
          pageNum:this.pageNum,
          pageSize:this.pageSize,
          param:{
            name: this.nameText,
            goodstype: this.goodstype,
            storage:this.storage
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
      },
      loadStorage(){
        this.$axios.get(this.$httpUrl+'/storage/list').then(res=>res.data).then(res=>{
          console.log(res)
          if(res.code==200){
            this.storageData=res.data;
          }else{
            alert('获取数据失败')
          }
        })
      },
      loadGoodsType(){
        this.$axios.get(this.$httpUrl+'/goodstype/list').then(res=>res.data).then(res=>{
          console.log(res)
          if(res.code==200){
            this.goodstypeData=res.data;
          }else{
            alert('获取数据失败')
          }
        })
      },
      // 根据窗口大小调整对话框宽度
      adjustDialogWidth() {
        const windowWidth = window.innerWidth;
        if (windowWidth < 768) {
          this.dialogWidth = '95%';
        } else if (windowWidth < 992) {
          this.dialogWidth = '70%';
        } else {
          this.dialogWidth = '500px';
        }
      }
    },
    beforeMount(){
      this.loadStorage();
      this.loadGoodsType();
      this.loadPost();
    },
    mounted() {
      // 初始调整对话框宽度
      this.adjustDialogWidth();
      // 监听窗口大小变化
      window.addEventListener('resize', this.adjustDialogWidth);
    },
    beforeDestroy() {
      // 移除事件监听器
      window.removeEventListener('resize', this.adjustDialogWidth);
    }
}
</script>

<style scoped>
.goods-manage-container {
  padding: 10px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.search-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 15px;
  gap: 5px;
}

.button-group {
  margin-left: auto;
  display: flex;
  gap: 5px;
}

.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .search-container {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-container .el-input,
  .search-container .el-select {
    width: 100% !important;
    margin-left: 0 !important;
    margin-bottom: 5px;
  }
  
  .button-group {
    margin-left: 0;
    margin-top: 5px;
  }
}
</style>