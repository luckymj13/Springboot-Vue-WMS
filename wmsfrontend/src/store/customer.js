import axios from 'axios'

const state = {
  customers: [],
  total: 0,
  currentCustomer: null
}

const mutations = {
  SET_CUSTOMERS(state, { records, total }) {
    state.customers = records
    state.total = total
  },
  SET_CURRENT_CUSTOMER(state, customer) {
    state.currentCustomer = customer
  }
}

const actions = {
  // 获取客户列表
  async getCustomers({ commit }, { pageNum, pageSize, params }) {
    try {
      const res = await axios.post('http://localhost:8008/customer/page', {
        pageNum,
        pageSize,
        param: params || {}
      })
      if (res.data.code === 200) {
        commit('SET_CUSTOMERS', {
          records: res.data.data,
          total: res.data.total
        })
        return res.data
      }
    } catch (error) {
      console.error('获取客户列表失败', error)
    }
  },
  
  // 添加客户
  async addCustomer({ dispatch }, customer) {
    try {
      const res = await axios.post('http://localhost:8008/customer/add', customer)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('添加客户失败', error)
    }
  },
  
  // 更新客户
  async updateCustomer({ dispatch }, customer) {
    try {
      const res = await axios.post('http://localhost:8008/customer/update', customer)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('更新客户失败', error)
    }
  },
  
  // 删除客户
  async deleteCustomer({ dispatch }, id) {
    try {
      const res = await axios.get(`http://localhost:8008/customer/delete?id=${id}`)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('删除客户失败', error)
    }
  },
  
  // 更新客户等级
  async updateCustomerLevel({ dispatch }, { customerId, level }) {
    try {
      const res = await axios.post(`http://localhost:8008/customer/updateLevel?customerId=${customerId}&level=${level}`)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('更新客户等级失败', error)
    }
  },
  
  // 更新客户信用等级
  async updateCreditLevel({ dispatch }, { customerId, creditLevel }) {
    try {
      const res = await axios.post(`http://localhost:8008/customer/updateCreditLevel?customerId=${customerId}&creditLevel=${creditLevel}`)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('更新客户信用等级失败', error)
    }
  }
}

const getters = {
  getCustomerById: (state) => (id) => {
    return state.customers.find(customer => customer.customerId === id)
  },
  getVipCustomers: (state) => {
    return state.customers.filter(customer => customer.customerLevel === 1)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
