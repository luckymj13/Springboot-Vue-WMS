<template>
  <div class="store-analysis-container">
    <!-- 加载状态指示器 -->
    <el-loading :visible="loading" fullscreen text="数据加载中..."></el-loading>
    
    <!-- 顶部卡片区域 -->
    <div class="stats-cards">
      <el-card class="stats-card" shadow="hover">
        <div slot="header" class="card-header">
          <span>今日出库总量</span>
        </div>
        <div class="card-content">
          <h2>{{ daySUM || 0 }}</h2>
        </div>
      </el-card>
      
      <el-card class="stats-card" shadow="hover">
        <div slot="header" class="card-header">
          <span>商品出库MAX</span>
        </div>
        <div class="card-content">
          <h2>{{ goodMAX || 0 }}</h2>
        </div>
      </el-card>
      
      <el-card class="stats-card" shadow="hover">
        <div slot="header" class="card-header">
          <span>商品出库MIN</span>
        </div>
        <div class="card-content">
          <h2>{{ goodMIN || 0 }}</h2>
        </div>
      </el-card>
      
      <el-card class="stats-card top-card" shadow="hover">
        <div slot="header" class="card-header">
          <span>本月出库量TOP1</span>
        </div>
        <div class="card-content">
          <h2>{{ Top || '暂无数据' }}</h2>
          <div class="growth-stats">
            <div class="growth-item">
              <span>今日占比:</span>
              <span :class="{'positive': zb > 0}">{{ zb }}%</span>
            </div>
            <div class="growth-item">
              <span>同比增长:</span>
              <span :class="{'positive': tb > 0, 'negative': tb < 0}">{{ tb }}%</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 出库量柱状图 -->
    <el-card class="chart-card" shadow="hover">
      <div slot="header" class="card-header">
        <span>本月总出库记录</span>
        <el-button size="mini" icon="el-icon-refresh" circle @click="refreshData"></el-button>
      </div>
      <div class="chart-container" ref="chart"></div>
    </el-card>
  </div>
</template>

<script>
export default {
    name: "StoreAnalysisManage",
    data() {
        return {
            loading: false,
            daySUM: 0,
            goodMAX: 0,
            goodMIN: 0,
            Top: 0,
            zb: 0,
            tb: 0,
            chart: null,
            option: {
              title: {
                text: '本月总出库记录',
                subtext: '',
                left: 'center'
              },
              tooltip: {
                trigger: 'axis',
                axisPointer: {
                  type: 'shadow'
                }
              },
              grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
              },
              xAxis: {
                type: 'category',
                data: [],
                axisLabel: {
                  interval: 0,
                  rotate: 30
                }
              },
              yAxis: {
                type: 'value'
              },
              series: [
                {
                  data: [],
                  type: 'bar',
                  itemStyle: {
                    color: '#409EFF'
                  },
                  label: {
                    show: true,
                    position: 'top'
                  }
                }
              ]
            },
        }
    },
    mounted() {
        this.loadData();
        window.addEventListener('resize', this.handleResize);
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.handleResize);
    },
    methods: {
        // 加载所有数据
        loadData() {
            this.loading = true;
            
            Promise.all([
                this.fetchDaySum(),
                this.fetchDayMax(),
                this.fetchDayMin(),
                this.fetchTop(),
                this.fetchZb(),
                this.fetchTb(),
                this.fetchSum()
            ])
            .then(() => {
                this.loading = false;
            })
            .catch(error => {
                this.loading = false;
                this.$message.error('数据加载失败: ' + error.message);
            });
        },
        
        // 刷新数据
        refreshData() {
            this.loadData();
        },
        
        // 处理窗口大小变化
        handleResize() {
            if (this.chart) {
                this.chart.resize();
            }
        },
        
        // 获取今日出库总量
        fetchDaySum() {
            return this.$axios.get(this.$httpUrl+'/analysis/getDaySum')
                .then(res => res.data)
                .then(res => {
                    if (res.code === 200) {
                        this.daySUM = res.data || 0;
                    } else {
                        this.$message.warning('获取今日出库总量失败');
                    }
                });
        },
        
        // 获取今日出库最大值
        fetchDayMax() {
            return this.$axios.get(this.$httpUrl+'/analysis/getDayMax')
                .then(res => res.data)
                .then(res => {
                    if (res.code === 200) {
                        this.goodMAX = res.data || 0;
                    } else {
                        this.$message.warning('获取今日出库最大值失败');
                    }
                });
        },
        
        // 获取今日出库最小值
        fetchDayMin() {
            return this.$axios.get(this.$httpUrl+'/analysis/getDayMin')
                .then(res => res.data)
                .then(res => {
                    if (res.code === 200) {
                        this.goodMIN = res.data || 0;
                    } else {
                        this.$message.warning('获取今日出库最小值失败');
                    }
                });
        },
        
        // 获取本月出库量TOP1
        fetchTop() {
            return this.$axios.get(this.$httpUrl+'/analysis/getTop')
                .then(res => res.data)
                .then(res => {
                    if (res.code === 200) {
                        this.Top = res.data || 0;
                    } else {
                        this.$message.warning('获取本月出库量TOP1失败');
                    }
                });
        },
        
        // 获取今日占比
        fetchZb() {
            return this.$axios.get(this.$httpUrl+'/analysis/zb')
                .then(res => res.data)
                .then(res => {
                    if (res.code === 200) {
                        this.zb = res.data || 0;
                    } else {
                        this.$message.warning('获取今日占比失败');
                    }
                });
        },
        
        // 获取同比增长
        fetchTb() {
            return this.$axios.get(this.$httpUrl+'/analysis/tb')
                .then(res => res.data)
                .then(res => {
                    if (res.code === 200) {
                        this.tb = res.data || 0;
                    } else {
                        this.$message.warning('获取同比增长失败');
                    }
                });
        },
        
        // 获取本月总出库记录
        fetchSum() {
            return this.$axios.get(this.$httpUrl+'/analysis/getSum')
                .then(res => res.data)
                .then(res => {
                    if (res.code === 200 && res.data) {
                        // 清空现有数据
                        this.option.xAxis.data = [];
                        this.option.series[0].data = [];
                        
                        // 填充新数据
                        res.data.forEach((item, i) => {
                            this.option.xAxis.data[i] = item.date;
                            this.option.series[0].data[i] = item.value;
                        });
                        
                        this.$nextTick(() => {
                            this.initChart();
                        });
                    } else {
                        this.$message.warning('获取本月总出库记录失败');
                    }
                });
        },
        
        // 初始化图表
        initChart() {
            if (!this.$refs.chart) return;
            
            if (!this.chart) {
                this.chart = this.$echarts.init(this.$refs.chart);
            }
            
            this.chart.setOption(this.option);
        }
    }
}
</script>

<style scoped>
.store-analysis-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.stats-card {
  flex: 1;
  min-width: 200px;
}

.top-card {
  flex: 2;
  min-width: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 0;
}

.card-content h2 {
  margin: 10px 0;
  font-size: 24px;
  color: #303133;
}

.growth-stats {
  display: flex;
  justify-content: space-around;
  width: 100%;
  margin-top: 10px;
}

.growth-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.positive {
  color: #67C23A;
}

.negative {
  color: #F56C6C;
}

.chart-card {
  flex: 1;
}

.chart-container {
  width: 100%;
  height: 400px;
}

/* 响应式调整 */
@media screen and (max-width: 1200px) {
  .stats-cards {
    flex-wrap: wrap;
  }
  
  .stats-card {
    flex-basis: calc(50% - 10px);
  }
}

@media screen and (max-width: 768px) {
  .stats-card {
    flex-basis: 100%;
  }
  
  .chart-container {
    height: 300px;
  }
}
</style>