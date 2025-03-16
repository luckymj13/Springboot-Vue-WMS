import axios from 'axios'

const state = {
  salesOrders: [],
  total: 0,
  currentSalesOrder: null
}

const mutations = {
  SET_SALES_ORDERS(state, { records, total }) {
    state.salesOrders = records
    state.total = total
  },
  SET_CURRENT_SALES_ORDER(state, order) {
    state.currentSalesOrder = order
  }
}

const actions = {
  // 获取销售订单列表
  async getSalesOrders({ commit }, { pageNum, pageSize, params }) {
    try {
      const res = await axios.post('http://localhost:8008/order/page', {
        pageNum,
        pageSize,
        param: {
          ...params,
          orderType: '2' // 销售订单
        }
      })
      if (res.data.code === 200) {
        console.log('销售订单列表数据:', res.data.data); // 添加日志，查看返回的数据结构
        commit('SET_SALES_ORDERS', {
          records: res.data.data,
          total: res.data.total
        })
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('获取销售订单列表失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 创建销售订单
  async createSalesOrder({ dispatch }, order) {
    try {
      // 设置订单类型为销售订单，关联方类型为客户
      const salesOrder = { 
        ...order, 
        orderType: '2',
        relatedPartyType: 'customer' // 设置关联方类型为客户
      }
      
      // 创建订单（包含订单详情）
      const res = await axios.post('http://localhost:8008/order/create', salesOrder)
      if (res.data.code === 200) {
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('创建销售订单失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 销售出库操作
  async processSaleOut({ dispatch }, { orderId, storageId }) {
    try {
      const res = await axios.post(`http://localhost:8008/order/saleOut?orderId=${orderId}&storageId=${storageId}`)
      if (res.data.code === 200) {
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('销售出库操作失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 获取客户列表（用于选择客户）
  async getCustomerOptions() {
    try {
      const res = await axios.post('http://localhost:8008/customer/page', {
        pageNum: 1,
        pageSize: 1000
      })
      if (res.data.code === 200) {
        return res.data.data
      }
      return []
    } catch (error) {
      console.error('获取客户选项失败', error)
      return []
    }
  },
  
  // 获取商品列表（用于选择商品）
  async getGoodsOptions() {
    try {
      const res = await axios.post('http://localhost:8008/goods/listPage', {
        pageNum: 1,
        pageSize: 1000
      })
      if (res.data.code === 200) {
        return res.data.data
      }
      return []
    } catch (error) {
      console.error('获取商品选项失败', error)
      return []
    }
  }
}

const getters = {
  getSalesOrderById: (state) => (id) => {
    return state.salesOrders.find(order => order.orderId === id)
  },
  getPendingSalesOrders: (state) => {
    return state.salesOrders.filter(order => order.status === '1' || order.status === '2')
  },
  getCompletedSalesOrders: (state) => {
    return state.salesOrders.filter(order => order.status === '4')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
