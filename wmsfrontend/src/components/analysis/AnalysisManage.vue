<template>
  <div class="analysis-container">
    <!-- 加载状态指示器 -->
    <el-loading :visible="loading" fullscreen text="数据加载中..."></el-loading>
    
    <div class="header-section">
      <el-select 
        v-model="goodstype" 
        placeholder="请选择分类" 
        class="category-select"
        @change="handleCategoryChange">
        <el-option
          v-for="item in goodstypeData"
          :key="item.id"
          :label="item.name"
          :value="item.id">
        </el-option>
      </el-select>
    </div>
    
    <!-- 上半部分：出库量占比和30天内物品出库数据 -->
    <div class="top-charts">
      <div class="chart-box left-chart">
        <div class="chart-title">出库量占比</div>
        <div class="chart-container" ref="chart"></div>
      </div>
      
      <div class="chart-box right-chart">
        <div class="chart-header">
          <div class="search-section">
            <el-input 
              v-model="nameText" 
              placeholder="请输入商品名" 
              class="goods-input"
              @keyup.enter.native="getThe">
            </el-input>
            <div class="button-group">
              <el-button @click="getThe" size="small" type="primary" icon="el-icon-search" circle></el-button>
              <el-button @click="reset" size="small" type="danger" icon="el-icon-delete" circle></el-button>
            </div>
          </div>
        </div>
        <div class="chart-title">{{option2.title && option2.title.text || '30天内物品出库数据'}}</div>
        <div class="chart-container" ref="chart2"></div>
      </div>
    </div>
    
    <!-- 下半部分：七日增长率和明天预计出货数量 -->
    <div class="bottom-charts">
      <div class="chart-box left-chart">
        <div class="chart-title">七日增长率</div>
        <div class="chart-container" ref="chart3"></div>
      </div>
      
      <div class="chart-box right-chart">
        <div class="chart-title">明天预计出货数量</div>
        <div class="chart-container" ref="chart4"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
    name: "AnalysisManage",
    data(){
    return {
      loading: false,
      chart: null,
      chart2: null,
      chart3: null,
      chart4: null,
      nameText: null,
      goodstypeData: [],
      goodstype: 1,
      number: 0,
      defaultGoodsId: null, // 默认显示的商品ID
      // 配置可视化图形
      option1: {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          top: 'center',
          data: []
        },
        color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272'],
        series: [
          {
            name: '出库量',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: []
          }
        ]
      },
      option2: {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
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
          boundaryGap: false,
          data: [],
          axisLabel: {
            rotate: 30,
            interval: 'auto'
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '出库数量',
            type: 'line',
            stack: 'Total',
            areaStyle: {},
            emphasis: {
              focus: 'series'
            },
            data: [],
            smooth: true,
            lineStyle: {
              width: 3,
              color: '#5470c6'
            },
            itemStyle: {
              color: '#5470c6'
            }
          }
        ]
      },
      option3: {
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            let result = params[0].name + '<br/>';
            params.forEach(param => {
              if (!param.value && param.value !== 0) return;
              // 转换为百分比显示
              const value = (param.value * 100).toFixed(2);
              const color = value >= 0 ? '#67C23A' : '#F56C6C';
              result += `<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:${param.color};"></span>`;
              result += `${param.seriesName}: <span style="color:${color}">${value}%</span><br/>`;
            });
            return result;
          }
        },
        legend: {
          data: [],
          top: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['1', '2', '3', '4', '5', '6', '7']
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: function(value) {
              return (value * 100).toFixed(1) + '%';
            }
          }
        },
        series: [
          {
            name: '',
            type: 'line',
            data: [],
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 2
            },
            itemStyle: {
              borderWidth: 2
            }
          },
          {
            name: '',
            type: 'line',
            data: [],
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 2
            },
            itemStyle: {
              borderWidth: 2
            }
          },
          {
            name: '',
            type: 'line',
            data: [],
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 2
            },
            itemStyle: {
              borderWidth: 2
            }
          },
          {
            name: '',
            type: 'line',
            data: [],
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 2
            },
            itemStyle: {
              borderWidth: 2
            }
          },
          {
            name: '',
            type: 'line',
            data: [],
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 2
            },
            itemStyle: {
              borderWidth: 2
            }
          },
          {
            name: '',
            type: 'line',
            data: [],
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 2
            },
            itemStyle: {
              borderWidth: 2
            }
          }
        ]
      },
      option4: {
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
            barWidth: '40%',
            itemStyle: {
              color: '#5470c6'
            },
            label: {
              show: true,
              position: 'top',
              formatter: '{c}',
              fontSize: 12,
              color: '#666'
            }
          }
        ]
      },
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initData();
    });
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
    // 销毁图表实例，避免内存泄漏
    if (this.chart) this.chart.dispose();
    if (this.chart2) this.chart2.dispose();
    if (this.chart3) this.chart3.dispose();
    if (this.chart4) this.chart4.dispose();
  },
  methods: {
    // 初始化数据
    initData() {
      this.loading = true;
      Promise.all([
        this.loadGoodsType(),
        this.load()
      ]).then(() => {
        this.loading = false;
      }).catch(error => {
        console.error('数据加载失败:', error);
        this.loading = false;
        this.$message.error('数据加载失败: ' + (error.message || '未知错误'));
      });
    },
    
    // 处理窗口大小变化
    handleResize() {
      this.$nextTick(() => {
        if (this.chart) this.chart.resize();
        if (this.chart2) this.chart2.resize();
        if (this.chart3) this.chart3.resize();
        if (this.chart4) this.chart4.resize();
      });
    },
    
    // 处理分类变化
    handleCategoryChange() {
      this.load();
    },
    
    // 加载默认商品的30天出库数据
    loadDefaultGoodsData() {
      if (!this.defaultGoodsId) return Promise.resolve();
      
      return this.$axios.get(this.$httpUrl+'/analysis/getOut?goodsid='+this.defaultGoodsId)
        .then(res => res.data)
        .then(res => {
          // 清空现有数据
          this.option2.xAxis.data = [];
          this.option2.series[0].data = [];
          
          // 填充新数据
          if (res.data && res.data.length > 0) {
            res.data.forEach((item, index) => {
              this.option2.xAxis.data[index] = item.date;
              this.option2.series[0].data[index] = item.value;
            });
            
            // 设置标题为商品名称
            const defaultGoodsName = this.option1.series[0].data.find(item => item.goodsid == this.defaultGoodsId)?.name || '商品';
            this.option2.title = {
              text: defaultGoodsName
            };
            
            this.$nextTick(() => {
              this.getPage2();
            });
          }
        })
        .catch(error => {
          console.error('加载默认商品数据失败:', error);
        });
    },
    
    // 搜索特定商品
    getThe() {
      if (!this.nameText || this.nameText.trim() === '') {
        this.$message.warning('请输入商品名称');
        return;
      }
      
      this.loading = true;
      this.$axios.get(this.$httpUrl+'/analysis/getOutThe?goodsname='+this.nameText)
        .then(res => res.data)
        .then(res => {
          if (res.code == 400) {
            this.$message.error('该商品不存在');
            this.loading = false;
            return;
          }
          
          // 清空现有数据
          this.option2.xAxis.data = [];
          this.option2.series[0].data = [];
          
          // 填充新数据
          if (res.data && res.data.length > 0) {
            res.data.forEach((item, index) => {
              this.option2.xAxis.data[index] = item.date;
              this.option2.series[0].data[index] = item.value;
            });
            
            this.option2.title = {
              text: res.data[0].name
            };
            
            this.$nextTick(() => {
              this.getPage2();
            });
            
            // 获取七日增长率
            return this.$axios.get(this.$httpUrl+'/analysis/getOne?goodsname='+this.nameText);
          }
        })
        .then(res => {
          if (res && res.data) {
            const data = res.data;
            if (data.code != 400 && data.data) {
              // 清空现有数据
              this.option3.series.forEach(series => {
                series.name = "";
                series.data = Array(7).fill(0);
              });
              
              // 填充新数据
              if (data.data.length > 0) {
                for (let k = 0; k < Math.min(7, data.data.length); k++) {
                  this.option3.series[0].data[k] = parseFloat(data.data[k].value[0]);
                }
                this.option3.series[0].name = this.nameText;
                this.option3.legend.data = [this.nameText];
                
                this.$nextTick(() => {
                  this.getPage3();
                });
              }
            }
          }
          this.loading = false;
        })
        .catch(error => {
          console.error('搜索数据加载失败:', error);
          this.loading = false;
          this.$message.error('数据加载失败: ' + (error.message || '未知错误'));
        });
    },
    
    // 重置搜索
    reset() {
      this.nameText = "";
      
      // 如果有默认商品ID，加载默认商品数据
      if (this.defaultGoodsId) {
        this.loadDefaultGoodsData();
      } else {
        // 清空图表数据
        this.option2.xAxis.data = [];
        this.option2.series[0].data = [];
        this.option2.title = {
          text: '30天内物品出库数据'
        };
        
        this.$nextTick(() => {
          this.getPage2();
        });
      }
      
      // 重新加载七日增长率数据
      this.loading = true;
      this.$axios.get(this.$httpUrl+'/analysis/getFive1?goodstype='+this.goodstype)
        .then(res => res.data)
        .then(res => {
          this.handleGrowthRateData(res);
          this.loading = false;
        })
        .catch(error => {
          console.error('重置数据加载失败:', error);
          this.loading = false;
          this.$message.error('数据加载失败: ' + (error.message || '未知错误'));
        });
    },
    
    // 饼图点击事件
    handlePieClick(param) {
      if (!param || !param.data) return;
      
      this.chart.dispatchAction({ type: 'highlight', dataIndex: param.dataIndex });
      
      if (param.dataIndex !== this.number) {
        this.chart.dispatchAction({ type: 'downplay', dataIndex: this.number });
      }
      
      this.number = param.dataIndex;
      this.defaultGoodsId = param.data.goodsid; // 设置为默认商品ID
      
      // 获取商品出库数据
      this.loading = true;
      this.$axios.get(this.$httpUrl+'/analysis/getOut?goodsid='+param.data.goodsid)
        .then(res => res.data)
        .then(res => {
          // 清空现有数据
          this.option2.xAxis.data = [];
          this.option2.series[0].data = [];
          
          // 填充新数据
          if (res.data && res.data.length > 0) {
            res.data.forEach((item, index) => {
              this.option2.xAxis.data[index] = item.date;
              this.option2.series[0].data[index] = item.value;
            });
            
            this.option2.title = {
              text: param.data.name
            };
            
            this.$nextTick(() => {
              this.getPage2();
            });
          }
          this.loading = false;
        })
        .catch(error => {
          console.error('饼图点击数据加载失败:', error);
          this.loading = false;
          this.$message.error('数据加载失败: ' + (error.message || '未知错误'));
        });
    },
    
    // 初始化图表
    getPage1() {
      if (!this.$refs.chart) {
        console.warn('图表容器不存在: chart');
        return;
      }
      
      try {
        if (this.chart) {
          this.chart.dispose();
        }
        this.chart = this.$echarts.init(this.$refs.chart);
        this.chart.setOption(this.option1);
        this.chart.off('click');
        this.chart.on('click', this.handlePieClick);
      } catch (error) {
        console.error('初始化饼图失败:', error);
      }
    },
    
    getPage2() {
      if (!this.$refs.chart2) {
        console.warn('图表容器不存在: chart2');
        return;
      }
      
      try {
        if (this.chart2) {
          this.chart2.dispose();
        }
        this.chart2 = this.$echarts.init(this.$refs.chart2);
        this.chart2.setOption(this.option2);
      } catch (error) {
        console.error('初始化折线图失败:', error);
      }
    },
    
    getPage3() {
      if (!this.$refs.chart3) {
        console.warn('图表容器不存在: chart3');
        return;
      }
      
      try {
        if (this.chart3) {
          this.chart3.dispose();
        }
        this.chart3 = this.$echarts.init(this.$refs.chart3);
        
        // 确保图表配置有效
        if (!this.option3.series[0].data || this.option3.series[0].data.length === 0) {
          console.warn('七日增长率数据为空，使用默认数据');
          this.option3.series[0].data = [0, 0, 0, 0, 0, 0, 0];
        }
        
        // 确保所有系列都有数据
        this.option3.series.forEach((series, index) => {
          if (!series.data || series.data.length === 0) {
            series.data = [0, 0, 0, 0, 0, 0, 0];
          }
          
          // 确保数据是数值类型
          series.data = series.data.map(value => {
            if (typeof value === 'string') {
              try {
                return parseFloat(value);
              } catch (e) {
                return 0;
              }
            }
            return value || 0;
          });
        });
        
        console.log('初始化七日增长率图表，配置:', JSON.stringify(this.option3));
        this.chart3.setOption(this.option3);
      } catch (error) {
        console.error('初始化增长率图失败:', error);
      }
    },
    
    getPage4() {
      if (!this.$refs.chart4) {
        console.warn('图表容器不存在: chart4');
        return;
      }
      
      try {
        if (this.chart4) {
          this.chart4.dispose();
        }
        this.chart4 = this.$echarts.init(this.$refs.chart4);
        
        // 确保图表配置有效
        if (!this.option4.series[0].data || this.option4.series[0].data.length === 0) {
          console.warn('预测数据为空，使用默认数据');
          this.option4.xAxis.data = ['暂无数据'];
          this.option4.series[0].data = [0];
        }
        
        console.log('初始化预测图表，配置:', JSON.stringify(this.option4));
        this.chart4.setOption(this.option4);
      } catch (error) {
        console.error('初始化柱状图失败:', error);
      }
    },
    
    // 加载商品分类数据
    loadGoodsType() {
      return this.$axios.get(this.$httpUrl+'/goodstype/list')
        .then(res => res.data)
        .then(res => {
          if (res.code == 200 && res.data) {
            this.goodstypeData = res.data;
          } else {
            this.$message.error('获取分类数据失败');
          }
        })
        .catch(error => {
          console.error('加载分类数据失败:', error);
          throw error;
        });
    },
    
    // 处理七日增长率数据
    handleGrowthRateData(data) {
      console.log('七日增长率原始数据:', JSON.stringify(data));
      
      if (!data || !data.data || !Array.isArray(data.data)) {
        console.warn('七日增长率数据格式不正确:', data);
        return;
      }
      
      // 清空现有数据
      this.option3.series.forEach(series => {
        series.name = "";
        series.data = Array(7).fill(0);
      });
      this.option3.legend.data = [];
      
      // 填充新数据
      data.data.forEach((item, i) => {
        console.log(`处理第${i+1}个商品数据:`, item);
        
        if (i < this.option3.series.length) {
          this.option3.series[i].name = item.name;
          this.option3.legend.data.push(item.name);
          
          // 注意：后端返回的value是字符串数组
          if (item.value && Array.isArray(item.value)) {
            console.log(`商品${item.name}的增长率数据:`, item.value);
            
            for (let j = 0; j < Math.min(item.value.length, 7); j++) {
              // 确保数据是数值类型，并处理null或undefined
              const value = item.value[j];
              // 转换为数值并确保有效
              let numValue = 0;
              try {
                // 字符串可能是百分比格式，如"0.1234"，需要转换为浮点数
                numValue = value !== null && value !== undefined ? parseFloat(value) : 0;
                // 检查是否是有效数字
                if (isNaN(numValue)) numValue = 0;
              } catch (e) {
                console.error('数据转换错误:', e, value);
                numValue = 0;
              }
              this.option3.series[i].data[j] = numValue;
            }
          } else {
            console.warn(`商品${item.name}没有有效的增长率数据`);
          }
        }
      });
      
      // 确保至少有一条数据
      if (this.option3.legend.data.length === 0) {
        console.warn('没有有效的七日增长率数据，使用默认数据');
        this.option3.legend.data = ['暂无数据'];
        this.option3.series[0].name = '暂无数据';
      }
      
      console.log('处理后的七日增长率图表配置:', JSON.stringify(this.option3));
      
      this.$nextTick(() => {
        this.getPage3();
      });
    },
    
    // 处理明天预计出货量数据
    handleTomorrowData(data) {
      console.log('明天预计出货量原始数据:', JSON.stringify(data));
      
      if (!data || !data.data) {
        console.warn('明天预计出货量数据格式不正确:', data);
        return;
      }
      
      // 清空现有数据
      this.option4.xAxis.data = [];
      this.option4.series[0].data = [];
      
      // 填充新数据
      if (Array.isArray(data.data) && data.data.length > 0) {
        data.data.forEach((item, index) => {
          console.log(`处理第${index+1}个预测数据:`, item);
          
          if (item && item.name) {
            // 确保value是有效数值
            let value = 0;
            if (item.value !== null && item.value !== undefined) {
              try {
                value = parseFloat(item.value);
                if (isNaN(value)) value = 0;
              } catch (e) {
                console.error('数据转换错误:', e, item.value);
                value = 0;
              }
            }
            this.option4.xAxis.data.push(item.name);
            this.option4.series[0].data.push(value);
          } else {
            console.warn('预测数据项缺少name属性:', item);
          }
        });
      } else {
        console.warn('没有有效的预测数据');
      }
      
      // 如果没有数据，添加默认数据
      if (this.option4.xAxis.data.length === 0) {
        console.warn('没有有效的预测数据，使用默认数据');
        this.option4.xAxis.data = ['暂无数据'];
        this.option4.series[0].data = [0];
      }
      
      console.log('处理后的预测图表配置:', JSON.stringify(this.option4));
      
      this.$nextTick(() => {
        this.getPage4();
      });
    },
    
    // 加载主要数据
    load() {
      this.loading = true;
      
      // 获取出库量占比数据
      const promise1 = this.$axios.get(this.$httpUrl+'/analysis/getsix?goodstype='+this.goodstype)
        .then(res => res.data)
        .then(res => {
          // 清空现有数据
          this.option1.series[0].data = [];
          this.option1.legend.data = [];
          
          // 填充新数据
          if (res.data && res.data.length > 0) {
            res.data.forEach((item) => {
              this.option1.series[0].data.push({
                value: item.value,
                name: item.name,
                goodsid: item.goodsid
              });
              this.option1.legend.data.push(item.name);
            });
            
            // 设置第一个商品为默认商品
            if (res.data.length > 0 && !this.defaultGoodsId) {
              this.defaultGoodsId = res.data[0].goodsid;
            }
          }
          
          this.$nextTick(() => {
            this.getPage1();
            
            // 加载默认商品的30天出库数据
            return this.loadDefaultGoodsData();
          });
        })
        .catch(error => {
          console.error('加载饼图数据失败:', error);
          throw error;
        });
      
      // 获取七日增长率数据
      const promise2 = this.$axios.get(this.$httpUrl+'/analysis/getFive1?goodstype='+this.goodstype)
        .then(res => res.data)
        .then(res => {
          this.handleGrowthRateData(res);
        })
        .catch(error => {
          console.error('加载增长率数据失败:', error);
          throw error;
        });
      
      // 获取明天预计出货数据
      const promise3 = this.$axios.get(this.$httpUrl+'/analysis/getTomorrow?goodstype='+this.goodstype)
        .then(res => res.data)
        .then(res => {
          this.handleTomorrowData(res);
        })
        .catch(error => {
          console.error('加载预测数据失败:', error);
          throw error;
        });
      
      // 等待所有请求完成
      return Promise.all([promise1, promise2, promise3])
        .then(() => {
          this.loading = false;
        })
        .catch(error => {
          console.error('加载数据失败:', error);
          this.loading = false;
          this.$message.error('数据加载失败: ' + (error.message || '未知错误'));
          throw error;
        });
    }
  }
};
</script>

<style scoped>
.analysis-container {
  padding: 15px;
  height: 100%;
  min-height: 600px;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.header-section {
  margin-bottom: 15px;
}

.category-select {
  width: 150px;
}

.top-charts,
.bottom-charts {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  height: calc(50% - 30px);
  min-height: 300px;
}

.chart-box {
  flex: 1;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 15px;
  display: flex;
  flex-direction: column;
  min-height: 300px;
}

.chart-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
  text-align: center;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.search-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.goods-input {
  width: 180px;
}

.button-group {
  display: flex;
  gap: 5px;
}

.chart-container {
  flex: 1;
  width: 100%;
  height: 100%;
  min-height: 250px;
}

/* 响应式调整 */
@media screen and (max-width: 1200px) {
  .top-charts,
  .bottom-charts {
    flex-direction: column;
    height: auto;
  }
  
  .chart-box {
    height: 350px;
    margin-bottom: 15px;
  }
}

@media screen and (max-width: 768px) {
  .chart-box {
    height: 300px;
  }
  
  .search-section {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .goods-input {
    width: 100%;
  }
}
</style>
