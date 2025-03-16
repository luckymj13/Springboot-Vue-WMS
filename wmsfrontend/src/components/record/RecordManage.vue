<template>
  <div>
    <div style="margin-bottom: 5px">
      <el-input v-model="nameText" placeholder="请输入物品名" style="width: 200px;"
        @keyup.enter.native="loadPost"/>

      <el-select v-model="storage" placeholder="请选择仓库" style="margin-left: 5px; width: 150px">
        <el-option
          v-for="item in storageData"
          :key="item.id"
          :label="item.name"
          :value="item.id">
        </el-option>
      </el-select>

      <el-select v-model="goodstype" placeholder="请选择分类" style="margin-left: 5px; width: 150px">
        <el-option
          v-for="item in goodstypeData"
          :key="item.id"
          :label="item.name"
          :value="item.id">
        </el-option>
      </el-select>

      <!-- <el-date-picker
        v-model="date"
        type="date"
        placeholder="选择日期">
      </el-date-picker> -->
      <el-input v-model="date" placeholder="请输入时间" style="width: 150px; margin-left: 5px;"/>

      <el-button @click="loadPost" size="small" type="primary" icon="el-icon-search" circle style="margin-left: 5px"></el-button>
      <el-button @click="reset" size="small" type="danger" icon="el-icon-delete" circle></el-button>
      <!-- <el-button @click="add" size="small" type="primary" icon="el-icon-plus" circle></el-button> -->
    </div>
  <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="50">
        </el-table-column>
        <el-table-column prop="goodsname" label="货物名" width="150">
        </el-table-column>
        <el-table-column prop="storagename" label="仓库" width="170">
        </el-table-column>
        <el-table-column prop="goodstypename" label="分类" width="150">
        </el-table-column>
        <el-table-column prop="username" label="操作人" width="150">
        </el-table-column>
        <el-table-column prop="count" label="数量" width="140">
        </el-table-column>
        <el-table-column prop="createtime" label="时间" width="163">
        </el-table-column>
        <el-table-column prop="remark" label="备注" align="center">
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

    

  </div>
</template>

<script>

export default {
    name: "RecordManage",
    data() {
      
      return {
        user: JSON.parse(sessionStorage.getItem('CurUser')),
        goodstypeData:[],
        storageData:[],
        tableData:[],
        pageNum:1,
        pageSize:9,
        total:0,
        nameText:'',
        centerDialogVisible: false,
        storage:'',
        goodstype:'',
        date:'',
        form:{
          id:'',
          name:'',
          storage:'',
          goodstype:'',
          count:'',
          remark:'',
        },
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
        this.nameText='',
        this.storage='',
        this.goodstype='',
        this.date=''
      },
      loadPost(){
        this.$axios.post(this.$httpUrl+'/record/getPage',{
          pageNum:this.pageNum,
          pageSize:this.pageSize,
          param:{
            name: this.nameText,
            goodstype: this.goodstype,
            storage: this.storage,
            date: this.date,
            grade:this.user.grade,
            id:this.user.id,
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
      }
    },
    beforeMount(){
      this.loadStorage();
      this.loadGoodsType();
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