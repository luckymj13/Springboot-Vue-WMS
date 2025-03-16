import axios from 'axios'

const state = {
  suppliers: [],
  total: 0,
  currentSupplier: null
}

const mutations = {
  SET_SUPPLIERS(state, { records, total }) {
    state.suppliers = records
    state.total = total
  },
  SET_CURRENT_SUPPLIER(state, supplier) {
    state.currentSupplier = supplier
  }
}

const actions = {
  // 获取供应商列表
  async getSuppliers({ commit }, { pageNum, pageSize, params }) {
    try {
      const res = await axios.post('http://localhost:8008/supplier/page', {
        pageNum,
        pageSize,
        param: params || {}
      })
      if (res.data.code === 200) {
        commit('SET_SUPPLIERS', {
          records: res.data.data,
          total: res.data.total
        })
        return res.data
      }
    } catch (error) {
      console.error('获取供应商列表失败', error)
    }
  },
  
  // 添加供应商
  async addSupplier({ dispatch }, supplier) {
    try {
      const res = await axios.post('http://localhost:8008/supplier/add', supplier)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('添加供应商失败', error)
    }
  },
  
  // 更新供应商
  async updateSupplier({ dispatch }, supplier) {
    try {
      const res = await axios.post('http://localhost:8008/supplier/update', supplier)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('更新供应商失败', error)
    }
  },
  
  // 删除供应商
  async deleteSupplier({ dispatch }, id) {
    try {
      const res = await axios.get(`http://localhost:8008/supplier/delete?id=${id}`)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('删除供应商失败', error)
    }
  },
  
  // 更新供应商合作状态
  async updateSupplierStatus({ dispatch }, { supplierId, status }) {
    try {
      const res = await axios.post(`http://localhost:8008/supplier/updateStatus?supplierId=${supplierId}&status=${status}`)
      if (res.data.code === 200) {
        return res.data
      }
    } catch (error) {
      console.error('更新供应商状态失败', error)
    }
  }
}

const getters = {
  getSupplierById: (state) => (id) => {
    return state.suppliers.find(supplier => supplier.supplierId === id)
  },
  getActiveSuppliers: (state) => {
    return state.suppliers.filter(supplier => supplier.cooperationStatus === 1)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
