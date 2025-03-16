import axios from 'axios'

const state = {
  orders: [],
  total: 0,
  currentOrder: null,
  orderDetails: []
}

const mutations = {
  SET_ORDERS(state, { records, total }) {
    state.orders = records
    state.total = total
  },
  SET_CURRENT_ORDER(state, order) {
    state.currentOrder = order
  },
  SET_ORDER_DETAILS(state, details) {
    state.orderDetails = details
  }
}

const actions = {
  // 获取订单列表
  async getOrders({ commit }, { pageNum, pageSize, params }) {
    try {
      const res = await axios.post('http://localhost:8008/order/page', {
        pageNum,
        pageSize,
        param: params || {}
      })
      if (res.data.code === 200) {
        console.log('订单列表数据:', res.data.data);
        commit('SET_ORDERS', {
          records: res.data.data,
          total: res.data.total
        })
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('获取订单列表失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 创建订单
  async createOrder({ dispatch }, order) {
    try {
      // 创建订单（包含订单详情）
      const res = await axios.post('http://localhost:8008/order/create', order)
      if (res.data.code === 200) {
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('创建订单失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 审核订单
  async reviewOrder({ dispatch }, { orderId, reviewerId, status }) {
    try {
      const res = await axios.post(`http://localhost:8008/order/review?orderId=${orderId}&reviewerId=${reviewerId}&status=${status}`)
      if (res.data.code === 200) {
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('审核订单失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 确认订单
  async confirmOrder({ dispatch }, { orderId, confirmerId }) {
    try {
      const res = await axios.post(`http://localhost:8008/order/confirm?orderId=${orderId}&confirmerId=${confirmerId}`)
      if (res.data.code === 200) {
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('确认订单失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 完成订单
  async completeOrder({ dispatch }, orderId) {
    try {
      const res = await axios.post(`http://localhost:8008/order/complete?orderId=${orderId}`)
      if (res.data.code === 200) {
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('完成订单失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 获取订单详情
  async getOrderDetails({ commit }, orderId) {
    try {
      const res = await axios.get(`http://localhost:8008/orderDetail/getDetailVO?orderId=${orderId}`)
      if (res.data.code === 200) {
        commit('SET_ORDER_DETAILS', res.data.data)
        return res.data
      }
      return res.data
    } catch (error) {
      console.error('获取订单详情失败', error)
      return { code: 500, msg: error.message }
    }
  },
  
  // 添加订单详情
  async addOrderDetails({ dispatch }, details) {
    try {
      const res = await axios.post('http://localhost:8008/orderDetail/addBatch', details)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('添加订单详情失败', error)
    }
  },
  
  // 更新订单详情
  async updateOrderDetail({ dispatch }, detail) {
    try {
      const res = await axios.post('http://localhost:8008/orderDetail/update', detail)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('更新订单详情失败', error)
    }
  },
  
  // 删除订单详情
  async deleteOrderDetail({ dispatch }, id) {
    try {
      const res = await axios.get(`http://localhost:8008/orderDetail/delete?id=${id}`)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('删除订单详情失败', error)
    }
  }
}

const getters = {
  getOrderById: (state) => (id) => {
    return state.orders.find(order => order.orderId === id)
  },
  getOrderDetails: (state) => {
    return state.orderDetails
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
