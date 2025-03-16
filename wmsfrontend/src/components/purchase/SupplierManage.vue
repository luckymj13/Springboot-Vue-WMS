<template>
  <div>
    <div style="margin-bottom: 5px">
      <el-input v-model="supplierName" placeholder="请输入供应商名称" style="width: 200px;"
        @keyup.enter.native="loadPost"/>
      
      <el-input v-model="contactPerson" placeholder="请输入联系人" style="width: 200px; margin-left: 5px"
        @keyup.enter.native="loadPost"/>
        
      <el-button @click="loadPost" size="small" type="primary" icon="el-icon-search" circle style="margin-left: 5px"></el-button>
      <el-button @click="reset" size="small" type="danger" icon="el-icon-delete" circle></el-button>
      <el-button @click="add" size="small" type="primary" icon="el-icon-plus" circle></el-button>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="supplierId" label="ID" width="50"></el-table-column>
      <el-table-column prop="supplierCode" label="供应商编码" width="120"></el-table-column>
      <el-table-column prop="supplierName" label="供应商名称" width="180"></el-table-column>
      <el-table-column prop="contactPerson" label="联系人" width="120"></el-table-column>
      <el-table-column prop="contactPhone" label="联系电话" width="150"></el-table-column>
      <el-table-column prop="address" label="地址" width="180"></el-table-column>
      <el-table-column prop="creditLevel" label="信用等级" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.creditLevel === 1 ? 'success' : 'warning'">
            {{ scope.row.creditLevel === 1 ? '优质' : '一般' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="cooperationStatus" label="合作状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.cooperationStatus === 1 ? 'success' : 'info'">
            {{ scope.row.cooperationStatus === 1 ? '合作中' : '暂停' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operate" label="操作" align="center" width="180">
        <template slot-scope="scope">
          <el-button size="small" type="primary" @click="edit(scope.row)">编辑</el-button>
          <el-popconfirm 
            title="确定删除吗？" 
            @confirm="del(scope.row)" 
            style="margin-left: 5px">
            <el-button size="small" slot="reference" type="danger">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page.sync="pageNum"
      :page-size="pageSize"
      layout="prev, pager, next, jumper"
      :total="total">
    </el-pagination>

    <!-- 新增/编辑供应商对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="40%"
      center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="供应商编码" prop="supplierCode">
          <el-input v-model="form.supplierCode"></el-input>
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="form.supplierName"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address"></el-input>
        </el-form-item>
        <el-form-item label="银行账号" prop="bankAccount">
          <el-input v-model="form.bankAccount"></el-input>
        </el-form-item>
        <el-form-item label="信用等级">
          <el-select v-model="form.creditLevel" placeholder="请选择信用等级">
            <el-option label="优质" :value="1"></el-option>
            <el-option label="一般" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="合作状态">
          <el-select v-model="form.cooperationStatus" placeholder="请选择合作状态">
            <el-option label="合作中" :value="1"></el-option>
            <el-option label="暂停" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'

export default {
  name: 'SupplierManage',
  data() {
    return {
      supplierName: '',
      contactPerson: '',
      pageNum: 1,
      pageSize: 10,
      dialogVisible: false,
      dialogTitle: '新增供应商',
      form: {
        supplierId: null,
        supplierCode: '',
        supplierName: '',
        contactPerson: '',
        contactPhone: '',
        address: '',
        bankAccount: '',
        creditLevel: 2,
        cooperationStatus: 1,
        remark: ''
      },
      rules: {
        supplierCode: [
          { required: true, message: '请输入供应商编码', trigger: 'blur' }
        ],
        supplierName: [
          { required: true, message: '请输入供应商名称', trigger: 'blur' }
        ],
        contactPerson: [
          { required: true, message: '请输入联系人', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapState({
      tableData: state => state.supplier.suppliers,
      total: state => state.supplier.total
    })
  },
  methods: {
    ...mapActions('supplier', [
      'getSuppliers',
      'addSupplier',
      'updateSupplier',
      'deleteSupplier'
    ]),

    async loadPost() {
      const params = {}
      if (this.supplierName) params.supplierName = this.supplierName
      if (this.contactPerson) params.contactPerson = this.contactPerson

      const res = await this.getSuppliers({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        params
      })
      
      if (!res || res.code !== 200) {
        this.$message.error('获取供应商列表失败')
      }
    },

    reset() {
      this.supplierName = ''
      this.contactPerson = ''
      this.loadPost()
    },

    add() {
      this.dialogTitle = '新增供应商'
      this.form = {
        supplierId: null,
        supplierCode: '',
        supplierName: '',
        contactPerson: '',
        contactPhone: '',
        address: '',
        bankAccount: '',
        creditLevel: 2,
        cooperationStatus: 1,
        remark: ''
      }
      this.dialogVisible = true
    },

    edit(row) {
      this.dialogTitle = '编辑供应商'
      this.form = { ...row }
      this.dialogVisible = true
    },

    async save() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          let res
          if (this.form.supplierId) {
            res = await this.updateSupplier(this.form)
          } else {
            res = await this.addSupplier(this.form)
          }
          
          if (res.code === 200) {
            this.$message.success(this.form.supplierId ? '更新成功' : '添加成功')
            this.dialogVisible = false
            this.loadPost()
          } else {
            this.$message.error(this.form.supplierId ? '更新失败' : '添加失败')
          }
        }
      })
    },

    async del(row) {
      const res = await this.deleteSupplier(row.supplierId)
      if (res.code === 200) {
        this.$message.success('删除成功')
        this.loadPost()
      } else {
        this.$message.error('删除失败')
      }
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.loadPost()
    },

    handleCurrentChange(val) {
      this.pageNum = val
      this.loadPost()
    }
  },
  created() {
    this.loadPost()
  }
}
</script>

<style scoped>
</style> 