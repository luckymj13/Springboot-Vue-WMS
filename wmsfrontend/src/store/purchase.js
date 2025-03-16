import axios from 'axios'

const state = {
  purchaseOrders: [],
  total: 0,
  currentPurchaseOrder: null
}

const mutations = {
  SET_PURCHASE_ORDERS(state, { records, total }) {
    state.purchaseOrders = records
    state.total = total
  },
  SET_CURRENT_PURCHASE_ORDER(state, order) {
    state.currentPurchaseOrder = order
  }
}

const actions = {
  // 获取采购订单列表
  async getPurchaseOrders({ commit }, { pageNum, pageSize, params }) {
    try {
      const res = await axios.post('http://localhost:8008/order/page', {
        pageNum,
        pageSize,
        param: {
          ...params,
          orderType: '1' // 采购订单
        }
      })
      if (res.data.code === 200) {
        console.log('采购订单列表数据:', res.data.data); // 添加日志，查看返回的数据结构
        commit('SET_PURCHASE_ORDERS', {
          records: res.data.data,
          total: res.data.total
        })
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('获取采购订单列表失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 创建采购订单
  async createPurchaseOrder({ dispatch }, order) {
    try {
      // 设置订单类型为采购订单，关联方类型为供应商
      const purchaseOrder = { 
        ...order, 
        orderType: '1',
        relatedPartyType: 'supplier' // 设置关联方类型为供应商
      }
      
      // 创建订单（包含订单详情）
      const res = await axios.post('http://localhost:8008/order/create', purchaseOrder)
      if (res.data.code === 200) {
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('创建采购订单失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 采购入库操作
  async processPurchaseIn({ dispatch }, { orderId, storageId }) {
    try {
      const res = await axios.post(`http://localhost:8008/order/purchaseIn?orderId=${orderId}&storageId=${storageId}`)
      if (res.data.code === 200) {
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('采购入库操作失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 获取供应商列表（用于选择供应商）
  async getSupplierOptions() {
    try {
      const res = await axios.post('http://localhost:8008/supplier/page', {
        pageNum: 1,
        pageSize: 1000,
        param: { cooperationStatus: 1 } // 只获取合作状态正常的供应商
      })
      if (res.data.code === 200) {
        return res.data.data
      }
      return []
    } catch (error) {
      console.error('获取供应商选项失败', error)
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
  getPurchaseOrderById: (state) => (id) => {
    return state.purchaseOrders.find(order => order.orderId === id)
  },
  getPendingPurchaseOrders: (state) => {
    return state.purchaseOrders.filter(order => order.status === '1' || order.status === '2')
  },
  getCompletedPurchaseOrders: (state) => {
    return state.purchaseOrders.filter(order => order.status === '4')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
