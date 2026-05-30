<template>
  <div class="app-container">
    <el-card class="box-card">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;">
        <div>
          <span class="title">月收入统计</span>
        </div>
      </div>

      <!-- 注意：不要写自闭合 div，这里用标准写法 -->
      <div ref="mains" style="width: 830px; height: 300px;"></div>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { getMonthlyIncome } from '@/api/data.js';

export default {
  name: 'ChatHistoryAnalysis',
  data() {
    return {
      chartInstance: null,
      dateList: [],   // x 轴数据（字符串数组）
      valueList: []   // y 轴数据（数字数组）
    };
  },
  mounted() {
    // 读取 hotelId（从 localStorage 或默认 1）
    const hotelId = JSON.parse(localStorage.getItem('vuex')).user.hotelId;
    this.fetchData(hotelId);

    // 窗口缩放时调整图表
    window.addEventListener('resize', this.resizeChart);
  },
  beforeDestroy() {
    // 清理
    window.removeEventListener('resize', this.resizeChart);
    if (this.chartInstance) {
      this.chartInstance.dispose();
      this.chartInstance = null;
    }
  },
  methods: {
    // 获取并处理数据
    async fetchData(hotelId) {
      try {
        console.log("开始获取");
        const res = await getMonthlyIncome(hotelId);
        console.log("res返回是这个：",res)

        const list = Array.isArray(res) ? res
            : Array.isArray(res && res.data) ? res.data
                : Array.isArray(res?.data?.list) ? res.data.list
                    : [];

        // 填充 x,y 数组
        this.dateList = list.map(item => String(item?.month ?? item?.name ?? ''));
        this.valueList = list.map(item => Number(item?.income ?? 0));

        // 初始化图表
        this.initEcharts();
      } catch (err) {
           console.error('获取月收入失败：', err);
      }
    },

    // 初始化 ECharts
    initEcharts() {
      if (!this.$refs.mains) return;

      // 释放旧实例
      if (this.chartInstance) {
        this.chartInstance.dispose();
        this.chartInstance = null;
      }

      this.chartInstance = echarts.init(this.$refs.mains);

      const xData = this.dateList.length ? this.dateList : ['无数据'];
      const yData = this.valueList.length ? this.valueList : [0];

      const option = {
        title: {
          text: '月收入',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' } // 鼠标悬浮显示阴影
        },
        xAxis: {
          type: 'category',
          data: xData,
          axisTick: { alignWithLabel: true }
        },
        yAxis: {
          type: 'value'
        },
        grid: { top: '15%', left: '3%', right: '4%', bottom: '3%', containLabel: true },
        series: [
          {
            name: '收入',
            type: 'bar',
            data: yData,
            barWidth: '50%',
            itemStyle: {
              color: '#409EFF' // 蓝色柱子，可自定义
            }
          }
        ]
      };

      try {
        this.chartInstance.setOption(option);
      } catch (e) {
        console.error('ECharts 渲染错误：', e);
      }
    },


    // 窗口缩放调整图表
    resizeChart() {
      if (this.chartInstance) this.chartInstance.resize();
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 12px;
}
.box-card {
  padding: 12px;
}
.title {
  font-size: 16px;
  font-weight: 600;
}
</style>
