import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import 'normalize.css/normalize.css' //样式重置 https://www.jianshu.com/p/f50bf86597cb
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/css/common.css'
import * as echarts from 'echarts'

Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.prototype.$baseFileUrl = process.env.VUE_APP_BASE_FILE_URL
Vue.prototype.$sysTitle = process.env.VUE_APP_SYSTITLE
Vue.prototype.$echarts = echarts;
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
