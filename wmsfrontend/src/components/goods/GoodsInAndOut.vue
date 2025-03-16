<template>
  <div class="goods-inout-container">
    <!-- 搜索区域优化：使用flex布局，更加紧凑 -->
    <div class="search-container">
      <div class="search-inputs">
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
      </div>

      <div class="button-group">
        <el-button @click="loadPost" size="small" type="primary" icon="el-icon-search" circle></el-button>
        <el-button @click="reset" size="small" type="danger" icon="el-icon-delete" circle></el-button>
        <el-button @click="inGoods" size="small" type="primary">入库</el-button>
        <el-button @click="outGoods" size="small" type="danger">出库</el-button>
      </div>
    </div>

    <!-- 表格区域优化：移除固定宽度，使用响应式布局 -->
    <el-table :data="tableData" border highlight-current-row @current-change="selectCurrentChange" style="width: 100%">
      <el-table-column prop="id" label="ID" width="50"></el-table-column>
      <el-table-column prop="name" label="货物名" min-width="120"></el-table-column>
      <el-table-column prop="storage" label="仓库" min-width="100" :formatter="formatStorage"></el-table-column>
      <el-table-column prop="goodstype" label="分类" min-width="100" :formatter="formatGoodsType"></el-table-column>
      <el-table-column prop="count" label="数量" min-width="80"></el-table-column>
      <el-table-column prop="price" label="价格" min-width="80"></el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip></el-table-column>
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

    <!-- 入库对话框优化 -->
    <el-dialog
      title="物品入库"
      :visible.sync="inDialogVisible"
      :width="dialogWidth"
      center>
      <el-form ref="formIn" :rules="rulesIn" :model="formIn" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="物品名">
              <el-input v-model="formIn.goodsname" readonly></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="数量" prop="count">
              <el-input v-model="formIn.count"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input type="textarea" v-model="formIn.remark" :rows="2"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>  
      <span slot="footer" class="dialog-footer">
        <el-button @click="inDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="insave">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 出库对话框优化 -->
    <el-dialog
      title="物品出库"
      :visible.sync="outDialogVisible"
      :width="dialogWidth"
      center>
      <el-form ref="formOut" :rules="rulesOut" :model="formOut" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="物品名">
              <el-input v-model="formOut.goodsname" readonly></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="数量" prop="count">
              <el-input v-model="formOut.count"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input type="textarea" v-model="formOut.remark" :rows="2"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>  
      <span slot="footer" class="dialog-footer">
        <el-button @click="outDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="outsave">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>

export default {
    name: "GoodsManage",
    data() {
      let checkCount = (rule ,value ,callback) => {
        if(value>this.countMax){
          callback(new Error('输入数量过大'));
        }else{
          callback();
        }
      };
      return {
        user: JSON.parse(sessionStorage.getItem('CurUser')),
        goodstypeData:[],
        storageData:[],
        tableData:[],
        pageNum:1,
        pageSize:10,
        total:0,
        nameText:'',
        storage:'',
        goodstype:'',
        inDialogVisible: false,
        outDialogVisible: false,
        currentRow:{},
        countMax:0,
        dialogWidth: '500px',
        formIn:{
            goods:'',
            goodsname:'',
            count:'',
            userid:'',
            remark:''
        },
        formOut:{
            goods:'',
            goodsname:'',
            count:'',
            userid:'',
            remark:''
        },
        rulesIn: {
          count:[
            {required: true, message: '请输入数量', trigger: 'blur'},
            {pattern: /^([1-9][0-9]*){1,4}$/, message: '数量必须为正整数',trigger: 'blur'},
          ]
        },
        rulesOut: {
          count:[
            {required: true, message: '请输入数量', trigger: 'blur'},
            {pattern: /^([1-9][0-9]*){1,4}$/, message: '数量必须为正整数',trigger: 'blur'},
            {validator: checkCount, trigger: 'blur'}
          ]
        }
      }
    },
    methods:{
      selectCurrentChange(val){
          this.currentRow=val;
      },
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
      resetInForm(){
        this.$refs.formIn.resetFields();
      },
      resetOutForm(){
        this.$refs.formOut.resetFields();
      },
      inGoods(){
        if(!this.currentRow.id){
            alert('请选择记录');
            return;
        }
        this.inDialogVisible = true;
        this.$nextTick(()=>{
          this.resetInForm();
        })
        this.formIn.goodsname = this.currentRow.name;
        this.formIn.goods = this.currentRow.id;
        this.formIn.userid = this.user.id
      },
      doinGoods(){
        this.$axios.post(this.$httpUrl+'/record/insave',this.formIn).then(res=>res.data).then(res=>{
            console.log(res)
            if(res.code==200){
              this.$message({
                message: '操作成功',
                type: 'success'
              });
              this.inDialogVisible = false;
              this.loadPost()
              this.resetInForm()
            }else{
              this.$message.error('操作失败');
            } 
        })
      },
      outGoods(){
        if(!this.currentRow.id){
            alert('请选择记录');
            return;
        }
        this.outDialogVisible = true;
        this.$nextTick(()=>{
          this.resetOutForm();
        })
        this.formOut.goodsname = this.currentRow.name;
        this.formOut.goods = this.currentRow.id;
        this.formOut.userid = this.user.id
        this.countMax = this.currentRow.count;
      },
      dooutGoods(){
        this.$axios.post(this.$httpUrl+'/record/outsave',this.formOut).then(res=>res.data).then(res=>{
            console.log(res)
            if(res.code==200){
              this.$message({
                message: '操作成功',
                type: 'success'
              });
              this.outDialogVisible = false;
              this.loadPost()
              this.resetOutForm()
            }else{
              this.$message.error('操作失败');
            } 
        })
      },
      outsave(){
        this.$refs.formOut.validate((valid) => {
          if (valid) {
            this.dooutGoods();
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      insave(){
        this.$refs.formIn.validate((valid) => {
          if (valid) {
            this.doinGoods();
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
            this.total=res.total;
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
.goods-inout-container {
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
  gap: 10px;
  justify-content: space-between;
}

.search-inputs {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 5px;
}

.button-group {
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
  
  .search-inputs {
    flex-direction: column;
    width: 100%;
  }
  
  .search-inputs .el-input,
  .search-inputs .el-select {
    width: 100% !important;
    margin-left: 0 !important;
    margin-bottom: 5px;
  }
  
  .button-group {
    width: 100%;
    justify-content: flex-end;
    margin-top: 5px;
  }
}
</style>