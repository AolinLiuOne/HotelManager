<template>
  <div :class="{ home: true, openMenus: !isCollapse, closeMenus: isCollapse }">
    <div class="menus">
      <div class="text-center font-bold text-white pt-20 pb-10 flex items-center justify-center">
        <img src="@/assets/logo.jpg" height="30px"/>{{ $sysTitle }}后台
      </div>
      <el-menu :collapse="isCollapse" default-active="home" background-color="#304156" text-color="#fff"
        active-text-color="#409eff" router>
        <el-menu-item index="/admin">
          <i class="el-icon-s-home"></i>
          <span slot="title">首页</span>
        </el-menu-item>
        <el-menu-item index="/admin/admin">
          <i class="el-icon-user"></i>
          <span slot="title">管理员管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/member">
          <i class="el-icon-user-solid"></i>
          <span slot="title">用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/category">
          <i class="el-icon-s-promotion"></i>
          <span slot="title">房型管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/room">
          <i class="el-icon-s-cooperation"></i>
          <span slot="title">房间管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/appointment">
          <i class="el-icon-s-flag"></i>
          <span slot="title">预约管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <i class="el-icon-s-order"></i>
          <span slot="title">入住管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/data">
          <i class="el-icon-document"></i>
          <span slot="title">数据统计</span>
        </el-menu-item>
<!--        <el-menu-item index="/admin/message">-->
<!--          <i class="el-icon-warning"></i>-->
<!--          <span slot="title">留言管理</span>-->
<!--        </el-menu-item>-->
        <el-menu-item index="/admin/notice">
          <i class="el-icon-bell"></i>
          <span slot="title">公告管理</span>
        </el-menu-item>

        <!-- <el-submenu index>
          <template slot="title">
            <i class="el-icon-location"></i>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/account">
          <i class="el-icon-setting"></i>
          <span slot="title">查询用户</span>
        </el-menu-item>
        </el-submenu> -->
      </el-menu>
    </div>
    <div class="content">
      <div class="navbar">
        <div class="navbar-btn" @click="isCollapse = !isCollapse">
          <i class="el-icon-s-fold" v-if="!isCollapse"></i>
          <i class="el-icon-s-unfold" v-else></i>
        </div>
        <div class="font-bold text-lg">{{ hotelName }}</div>
        <div class="navbar-right">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              {{ user.name }}<i class="el-icon-caret-bottom el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item icon="el-icon-s-custom" @click.native="$router.push('/admin/adminInfo')">个人信息</el-dropdown-item>
              <el-dropdown-item icon="el-icon-caret-left" @click.native="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <!-- 内容部分 -->
      <div class="section">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState,mapMutations } from 'vuex'
import { removeToken } from '@/utils/auth'
import request from "@/utils/request";
export default {
  name: "Home",
  computed: {
    ...mapState(['user'])  // 映射 Vuex 中的 user 状态到当前组件的计算属性
  },
  data() {
    return {
      hotelName:'',
      isCollapse: false,  // 控制菜单栏是否折叠的状态
    };
  },
  created() {
    this.fetchHotelName();
  },
  methods: {
    ...mapMutations(['setUser', 'setToken']),
    async fetchHotelName() {
      try {
        const res = await request.get(`/admin/${this.user.hotelId}`);
        this.hotelName = res.data.hotel;
      } catch (e) {
        console.error("获取酒店名称失败", e);
      }
    },
    logout() {
      this.$confirm('确认退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeToken()  // 移除用户 token
        this.setUser({})  // 清空用户信息
        this.setToken('')  // 清空 token
        this.$router.replace('/front')  // 跳转到前台页面
      }).catch(() => { });
    }
  },
};
</script>

<style lang="less" scoped>
.openMenus {
  .menus {
    height: 100vh;
    width: 200px;
    overflow-y: auto;
    position: fixed;
    left: 0;
    top: 0;
    background-color: #304156;
    transition: all 0.5s;

    .el-menu {
      border-right: none;
    }
  }

  .content {
    // background: darkcyan;
    margin-left: 200px;
    width: calc(100% - 200px);
    height: 100vh;
    transition: all 0.5s;
  }
}

.closeMenus {
  .menus {
    height: 100vh;
    width: 64px;
    overflow-y: auto;
    position: fixed;
    left: 0;
    top: 0;
    transition: all 0.5s;
    background-color: #304156;

    .el-menu {
      border-right: none;
    }
  }

  .content {
    // background: darkcyan;
    margin-left: 64px;
    width: calc(100% - 64px);
    height: 100vh;
    transition: all 0.5s;
  }
}

.home {
  .navbar {
    height: 60px;
    // background-color: darkcyan;
    display: flex;
    align-items: center;
    padding: 0 10px;
    justify-content: space-between;
    border-bottom: 1px solid #eee;

    .navbar-btn {
      // width: 60px;
      height: 40px;
      line-height: 40px;
      text-align: center;
      cursor: pointer;
      // background-color: darkblue;
    }

    .navbar-right {
      min-width: 60px;
      height: 40px;
      display: flex;
      align-items: center;
      cursor: pointer;
      // background-color: darkmagenta;
    }
  }

  .content {
    .section {
      padding: 16px;
    }
  }
}
</style>
