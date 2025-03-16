<template>
  <div>
    <div style="margin-bottom: 5px">
      <el-input v-model="orderNumber" placeholder="请输入订单编号" style="width: 200px;"
        @keyup.enter.native="loadPost"/>
      
      <el-select v-model="supplierId" placeholder="供应商" style="margin-left: 5px; width: 200px" clearable>
        <el-option
          v-for="item in supplierOptions"
          :key="item.supplierId"
          :label="item.supplierName"
          :value="item.supplierId">
        </el-option>
      </el-select>

      <el-select v-model="status" placeholder="订单状态" style="margin-left: 5px; width: 120px" clearable>
        <el-option label="待审核" value="1"></el-option>
        <el-option label="审核通过" value="2"></el-option>
        <el-option label="已确认" value="3"></el-option>
        <el-option label="已完成" value="4"></el-option>
        <el-option label="已取消" value="5"></el-option>
        <el-option label="审核驳回" value="6"></el-option>
      </el-select>

      <el-button @click="loadPost" size="small" type="primary" icon="el-icon-search" circle style="margin-left: 5px"></el-button>
      <el-button @click="reset" size="small" type="danger" icon="el-icon-delete" circle></el-button>
      <el-button @click="add" size="small" type="primary" icon="el-icon-plus" circle></el-button>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="orderId" label="ID" width="50"></el-table-column>
      <el-table-column prop="orderNumber" label="订单编号" width="180"></el-table-column>
      <el-table-column prop="supplierName" label="供应商" width="150"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{getStatusText(scope.row.status)}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="creatorName" label="创建人" width="100"></el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="120"></el-table-column>
      <el-table-column prop="createdTime" label="创建时间" width="180"></el-table-column>
      <el-table-column prop="operate" label="操作" align="center">
        <template slot-scope="scope">
          <el-button size="small" type="primary" @click="viewDetails(scope.row)">详情</el-button>
          <el-button 
            v-if="scope.row.status === '1' && hasReviewPermission" 
            size="small" 
            type="success" 
            @click="review(scope.row)">审核</el-button>
          <el-button 
            v-if="scope.row.status === '2'" 
            size="small" 
            type="warning" 
            @click="confirm(scope.row)">确认</el-button>
          <el-button 
            v-if="scope.row.status === '3'" 
            size="small" 
            type="info" 
            @click="complete(scope.row)">入库</el-button>
          <el-popconfirm 
            v-if="scope.row.status === '1'"
            title="确定取消吗？" 
            @confirm="cancel(scope.row)" 
            style="margin-left: 5px">
            <el-button size="small" slot="reference" type="danger">取消</el-button>
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

    <!-- 订单详情对话框 -->
    <el-dialog
      title="采购订单详情"
      :visible.sync="detailsDialogVisible"
      width="60%"
      center>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{currentOrder.orderNumber}}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{currentOrder.supplierName}}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{currentOrder.creatorName || '-'}}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{currentOrder.createdTime}}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{currentOrder.reviewerName || '-'}}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{currentOrder.reviewTime || '-'}}</el-descriptions-item>
        <el-descriptions-item label="确认人">{{currentOrder.confirmerName || '-'}}</el-descriptions-item>
        <el-descriptions-item label="确认时间">{{currentOrder.confirmTime || '-'}}</el-descriptions-item>
        <el-descriptions-item label="状态">{{getStatusText(currentOrder.status)}}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{currentOrder.totalAmount}}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{currentOrder.remark || '-'}}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px; display: flex; justify-content: space-between; align-items: center;">
        <h3 style="margin: 0;">订单明细</h3>
        <el-button 
          v-if="orderDetails.length === 0" 
          size="small" 
          type="primary" 
          @click="createSampleDetails">
          创建示例明细
        </el-button>
      </div>

      <el-table :data="orderDetails" border style="margin-top: 10px">
        <el-table-column prop="goodsName" label="商品名称"></el-table-column>
        <el-table-column prop="quantity" label="数量"></el-table-column>
        <el-table-column prop="unitPrice" label="单价"></el-table-column>
        <el-table-column prop="totalPrice" label="总价"></el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
      </el-table>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog
      title="采购订单审核"
      :visible.sync="reviewDialogVisible"
      width="30%"
      center>
      <el-form ref="reviewForm" :model="reviewForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="reviewForm.status">
            <el-radio label="2">通过</el-radio>
            <el-radio label="6">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReview">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 新增订单对话框 -->
    <el-dialog
      title="新增采购订单"
      :visible.sync="addDialogVisible"
      width="60%"
      center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="供应商" prop="relatedPartyId">
          <el-select v-model="form.relatedPartyId" placeholder="请选择供应商" style="width: 100%">
            <el-option
              v-for="item in supplierOptions"
              :key="item.supplierId"
              :label="item.supplierName"
              :value="item.supplierId">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark"></el-input>
        </el-form-item>

        <!-- 订单明细 -->
        <div class="order-details">
          <div class="details-header">
            <span>订单明细</span>
            <el-button type="text" @click="addDetail">添加明细</el-button>
          </div>
          <el-table :data="form.details" border>
            <el-table-column label="商品">
              <template slot-scope="scope">
                <el-select v-model="scope.row.goodsId" placeholder="请选择商品" @change="handleGoodsChange($event, scope.$index)">
                  <el-option
                    v-for="item in goodsOptions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                  </el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="数量">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" @change="calculateTotal(scope.$index)" class="quantity-input"></el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="单价">
              <template slot-scope="scope">
                <el-input v-model="scope.row.unitPrice" type="number" @input="calculateTotal(scope.$index)"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="总价" prop="totalPrice"></el-table-column>
            <el-table-column label="备注">
              <template slot-scope="scope">
                <el-input v-model="scope.row.remark" placeholder="请输入备注"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button type="text" @click.prevent="removeDetail(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 入库对话框 -->
    <el-dialog
      title="采购入库"
      :visible.sync="storageDialogVisible"
      width="60%"
      center>
      <el-form ref="storageForm" :model="storageForm" label-width="80px">
        <el-form-item label="仓库" prop="storageId">
          <div v-if="storageOptions.length === 0" style="color: red;">暂无仓库数据</div>
          <el-select v-model="storageForm.storageId" placeholder="请选择仓库" style="width: 100%">
            <el-option
              v-for="item in storageOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              {{item.name}}
            </el-option>
          </el-select>
          <div style="margin-top: 5px; font-size: 12px; color: #666;">
            可选仓库: 
            <span v-for="(item, index) in storageOptions" :key="index" style="margin-right: 10px;">
              {{item.name}} (ID: {{item.id}})
            </span>
          </div>
        </el-form-item>
        
        <el-table :data="orderDetails" border style="margin-top: 20px">
          <el-table-column prop="goodsName" label="商品名称"></el-table-column>
          <el-table-column prop="quantity" label="数量"></el-table-column>
          <el-table-column prop="unitPrice" label="单价"></el-table-column>
          <el-table-column prop="totalPrice" label="总价"></el-table-column>
          <el-table-column prop="remark" label="备注"></el-table-column>
        </el-table>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="storageDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitStorage">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'

export default {
  name: 'PurchaseManage',
  data() {
    return {
      orderNumber: '',
      supplierId: '',
      status: '',
      pageNum: 1,
      pageSize: 10,
      detailsDialogVisible: false,
      reviewDialogVisible: false,
      addDialogVisible: false,
      storageDialogVisible: false,
      currentOrder: {},
      orderDetails: [],
      reviewForm: {
        status: '2'
      },
      form: {
        orderType: '1', // 采购订单
        relatedPartyId: '',
        remark: '',
        details: []
      },
      storageForm: {
        storageId: '',
        orderId: ''
      },
      rules: {
        relatedPartyId: [
          { required: true, message: '请选择供应商', trigger: 'change' }
        ]
      },
      supplierOptions: [],
      goodsOptions: [],
      storageOptions: [],
      user: JSON.parse(sessionStorage.getItem('CurUser'))
    }
  },
  computed: {
    ...mapState({
      tableData: state => state.purchase.purchaseOrders,
      total: state => state.purchase.total
    }),
    hasReviewPermission() {
      return this.user && (this.user.grade === 0 || this.user.grade === 1)
    }
  },
  methods: {
    ...mapActions('purchase', [
      'getPurchaseOrders',
      'createPurchaseOrder',
      'processPurchaseIn'
    ]),
    ...mapActions('order', [
      'reviewOrder',
      'confirmOrder',
      'getOrderDetails'
    ]),
    ...mapActions('supplier', ['getSuppliers']),

    async loadPost() {
      const params = {}
      if (this.orderNumber) params.orderNumber = this.orderNumber
      if (this.supplierId) params.supplierId = this.supplierId
      if (this.status) params.status = this.status

      const res = await this.getPurchaseOrders({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        params
      })
      
      if (!res || res.code !== 200) {
        this.$message.error('获取采购订单列表失败')
      }
    },

    reset() {
      this.orderNumber = ''
      this.supplierId = ''
      this.status = ''
      this.pageNum = 1
      this.loadPost()
    },

    async add() {
      this.form = {
        relatedPartyId: '',
        remark: '',
        details: []
      }
      try {
        await this.loadSupplierOptions()
        await this.loadGoodsOptions()
        this.addDialogVisible = true
      } catch (error) {
        console.error('加载选项失败', error)
        this.$message.error('加载选项失败: ' + error.message)
      }
    },

    async loadSupplierOptions() {
      try {
        const res = await this.getSuppliers({ 
          pageNum: 1, 
          pageSize: 1000,
          params: { cooperationStatus: 1 } // 只获取合作状态正常的供应商
        })
        if (res && res.code === 200) {
          this.supplierOptions = res.data
          console.log('供应商数据:', this.supplierOptions) // 添加日志，查看加载的数据
        } else {
          this.$message.error('获取供应商列表失败')
        }
      } catch (error) {
        console.error('加载供应商选项失败', error)
        this.$message.error('加载供应商选项失败: ' + error.message)
      }
    },

    async loadGoodsOptions() {
      try {
        const res = await this.$axios.post(this.$httpUrl + '/goods/listPage', {
          pageNum: 1,
          pageSize: 1000
        })
        if (res.data.code === 200) {
          this.goodsOptions = res.data.data
        } else {
          this.$message.error('获取商品列表失败')
        }
      } catch (error) {
        console.error('加载商品选项失败', error)
        this.$message.error('加载商品选项失败: ' + error.message)
      }
    },

    async loadStorageOptions() {
      try {
        // 使用GET方法调用/storage/list接口
        const res = await this.$axios.get(this.$httpUrl + '/storage/list')
        console.log('仓库API响应:', res); // 添加日志，查看完整的API响应
        
        if (res.data.code === 200) {
          console.log('仓库数据:', res.data.data); // 添加日志，查看返回的数据结构
          
          // 确保storageOptions是一个数组
          if (Array.isArray(res.data.data)) {
            this.storageOptions = res.data.data;
          } else {
            console.error('仓库数据不是数组:', res.data.data);
            this.$message.error('获取仓库列表失败: 数据格式不正确');
          }
        } else {
          console.error('获取仓库列表失败:', res.data);
          this.$message.error('获取仓库列表失败: ' + (res.data.msg || '未知错误'));
        }
      } catch (error) {
        console.error('加载仓库选项失败', error)
        this.$message.error('加载仓库选项失败: ' + error.message)
      }
    },

    addDetail() {
      this.form.details.push({
        goodsId: '',
        quantity: 1,
        unitPrice: 0,
        totalPrice: 0,
        remark: ''
      })
    },

    removeDetail(index) {
      this.form.details.splice(index, 1)
    },

    calculateTotal(index) {
      const detail = this.form.details[index]
      detail.totalPrice = (detail.quantity * detail.unitPrice).toFixed(2)
      
      // Calculate the total amount for the entire order
      this.form.totalAmount = this.form.details.reduce((sum, item) => {
        return sum + parseFloat(item.totalPrice || 0)
      }, 0).toFixed(2)
    },

    handleGoodsChange(goodsId, index) {
      const goods = this.goodsOptions.find(item => item.id === goodsId)
      if (goods) {
        this.form.details[index].unitPrice = goods.price || 0
        this.calculateTotal(index)
      }
    },

    async save() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          if (this.form.details.length === 0) {
            this.$message.warning('请添加订单明细')
            return
          }

          // Calculate the total amount from all details
          const totalAmount = this.form.details.reduce((sum, item) => {
            return sum + parseFloat(item.totalPrice || 0)
          }, 0).toFixed(2)
          
          const orderData = {
            orderType: '1', // 采购订单
            creatorId: this.user.id,
            relatedPartyId: this.form.relatedPartyId,
            totalAmount: totalAmount,
            remark: this.form.remark,
            details: this.form.details.map(detail => ({
              goodsId: detail.goodsId,
              quantity: detail.quantity,
              unitPrice: detail.unitPrice,
              totalPrice: detail.totalPrice,
              remark: detail.remark
            }))
          }
          
          try {
            const res = await this.createPurchaseOrder(orderData)
            if (res && res.code === 200) {
              this.$message.success('创建成功')
              this.addDialogVisible = false
              this.loadPost()
            } else {
              this.$message.error('创建失败: ' + (res ? res.msg : '未知错误'))
            }
          } catch (error) {
            console.error('创建采购订单失败', error)
            this.$message.error('创建失败: ' + error.message)
          }
        }
      })
    },

    async viewDetails(row) {
      // 调用新的API获取带有操作人姓名的订单详情
      this.$axios.get(this.$httpUrl + '/order/detail/' + row.orderId)
        .then(res => res.data)
        .then(res => {
          if (res.code === 200) {
            console.log('订单详情数据:', res.data); // 添加日志，查看返回的数据结构
            this.currentOrder = res.data;
            this.orderDetails = res.data.details || [];
            this.detailsDialogVisible = true;
            
            // 如果没有订单明细，显示提示信息
            if (!this.orderDetails || this.orderDetails.length === 0) {
              this.$message.info('该订单暂无明细数据，您可以点击"创建示例明细"按钮创建一些示例数据');
            }
          } else {
            this.$message.error(res.msg || '获取订单详情失败');
          }
        })
        .catch(error => {
          console.error('获取订单详情失败', error);
          this.$message.error('获取订单详情失败: ' + error.message);
          // 即使获取失败，也显示对话框，但显示空数据
          this.orderDetails = [];
          this.detailsDialogVisible = true;
        });
    },

    async review(row) {
      this.currentOrder = row
      this.reviewForm.status = '2'
      this.reviewDialogVisible = true
    },

    async submitReview() {
      const res = await this.reviewOrder({
        orderId: this.currentOrder.orderId,
        reviewerId: this.user.id,
        status: this.reviewForm.status
      })
      if (res.code === 200) {
        this.$message.success('审核成功')
        this.reviewDialogVisible = false
        this.loadPost()
      } else {
        this.$message.error('审核失败')
      }
    },

    async confirm(row) {
      const res = await this.confirmOrder({
        orderId: row.orderId,
        confirmerId: this.user.id
      })
      if (res.code === 200) {
        this.$message.success('确认成功')
        this.loadPost()
      } else {
        this.$message.error('确认失败')
      }
    },

    async complete(row) {
      this.currentOrder = row
      // 使用新的API获取带有操作人姓名的订单详情
      try {
        const res = await this.$axios.get(this.$httpUrl + '/order/detail/' + row.orderId)
        if (res.data.code === 200) {
          this.currentOrder = res.data.data
          
          // 使用orderDetail/getDetailVO接口获取带有商品名称的订单明细
          const detailRes = await this.$axios.get(this.$httpUrl + '/orderDetail/getDetailVO?orderId=' + row.orderId)
          if (detailRes.data.code === 200) {
            this.orderDetails = detailRes.data.data || []
            console.log('订单明细数据(带商品名称):', this.orderDetails)
          } else {
            this.orderDetails = res.data.data.details || []
            console.log('使用原始订单明细数据:', this.orderDetails)
          }
          
          // 先加载仓库选项
          await this.loadStorageOptions()
          
          // 只有在成功加载仓库选项后，才显示对话框
          this.storageForm = {
            storageId: '',
            orderId: row.orderId
          }
          this.storageDialogVisible = true
        } else {
          this.$message.error(res.data.msg || '获取订单详情失败')
        }
      } catch (error) {
        console.error('获取订单详情失败', error)
        this.$message.error('获取订单详情失败: ' + error.message)
      }
    },

    async submitStorage() {
      if (!this.storageForm.storageId) {
        this.$message.warning('请选择仓库')
        return
      }

      try {
        const res = await this.processPurchaseIn({
          orderId: this.currentOrder.orderId,
          storageId: this.storageForm.storageId
        })
        if (res && res.code === 200) {
          this.$message.success('入库成功')
          this.storageDialogVisible = false
          this.loadPost()
        } else {
          // 处理已经入库的情况
          if (res && res.msg && res.msg.includes('已经处理过')) {
            this.$message.warning('该订单已经完成入库操作')
            this.storageDialogVisible = false
            this.loadPost()
          } else {
            this.$message.error('入库失败: ' + (res ? res.msg : '未知错误'))
          }
        }
      } catch (error) {
        console.error('入库操作失败', error)
        // 处理已经入库的情况
        if (error.message && error.message.includes('已经处理过')) {
          this.$message.warning('该订单已经完成入库操作')
          this.storageDialogVisible = false
          this.loadPost()
        } else {
          this.$message.error('入库失败: ' + error.message)
        }
      }
    },

    async cancel(row) {
      try {
        const res = await this.$axios.post(this.$httpUrl + '/order/cancel?orderId=' + row.orderId)
        if (res.data.code === 200) {
          this.$message.success('取消成功')
          this.loadPost()
        } else {
          this.$message.error('取消失败: ' + (res.data.msg || '未知错误'))
        }
      } catch (error) {
        console.error('取消订单失败', error)
        this.$message.error('取消失败: ' + error.message)
      }
    },

    async createSampleDetails() {
      try {
        const res = await this.$axios.post(this.$httpUrl + '/order/createSampleDetails?orderId=' + this.currentOrder.orderId)
        if (res.data.code === 200) {
          this.$message.success('创建示例明细成功')
          // 重新获取订单详情
          this.$axios.get(this.$httpUrl + '/order/detail/' + this.currentOrder.orderId)
            .then(res => res.data)
            .then(res => {
              if (res.code === 200) {
                console.log('更新后的订单详情:', res.data); // 添加日志，查看返回的数据结构
                this.currentOrder = res.data;
                this.orderDetails = res.data.details || [];
              }
            });
        } else {
          this.$message.error('创建示例明细失败: ' + (res.data.msg || '未知错误'))
        }
      } catch (error) {
        console.error('创建示例明细失败', error)
        this.$message.error('创建示例明细失败: ' + error.message)
      }
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.loadPost()
    },

    handleCurrentChange(val) {
      this.pageNum = val
      this.loadPost()
    },

    getStatusType(status) {
      const types = {
        '1': 'info',
        '2': 'warning',
        '3': 'primary',
        '4': 'success',
        '5': 'danger',
        '6': 'danger'
      }
      return types[status] || 'info'
    },

    getStatusText(status) {
      const texts = {
        '1': '待审核',
        '2': '审核通过',
        '3': '已确认',
        '4': '已完成',
        '5': '已取消',
        '6': '审核驳回'
      }
      return texts[status] || '未知'
    }
  },
  async created() {
    await this.loadSupplierOptions()
    await this.loadStorageOptions()
    this.loadPost()
  }
}
</script>

<style scoped>
.order-details {
  margin-top: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.details-header span {
  font-weight: bold;
  font-size: 14px;
}

.el-select {
  width: 100%;
}

/* 修复数量输入框加号按钮被遮挡的问题 */
.quantity-input {
  width: 100%;
}
.quantity-input .el-input-number__increase,
.quantity-input .el-input-number__decrease {
  z-index: 1;
}
</style> 