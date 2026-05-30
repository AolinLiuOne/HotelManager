<template>
    <div class="container">
        <div class="login-box">
            <h2 class="title" @click="$router.push('/front')">{{ $sysTitle }}-登录</h2>
            <el-form :model="loginForm" label-position="left">
                <el-form-item>
                    <el-input v-model="loginForm.username" placeholder="请输入账户"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
                </el-form-item>
              <el-form-item class="radio-center">
                <el-radio-group v-model="loginForm.role">
                  <el-radio :label="1">管理员</el-radio>
                  <el-radio :label="2">用户</el-radio>
                </el-radio-group>
              </el-form-item>
                <el-form-item >
                    <el-button type="primary" style="width: 400px;" @click="login">登录</el-button>
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                    <div class="AdminLogin">
                      <router-link to="/adminRegister">管理员注册</router-link>
                    </div>
                    <div class="text-right">
                      没有账号?去<router-link to="/register">注册</router-link>
                    </div>
                  </div>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
import { loginAPI, getUserInfoByTokenAPI } from '@/api/system'
import { mapMutations } from 'vuex'
import { setToken } from '@/utils/auth'
export default {
    name: 'Login',
    data() {
        return {
            loginForm: {
                role: 1,
                username: '',
                password: "1"
            }
        }
    },
    methods: {
        ...mapMutations(['setUser','setRole','setToken']),
        async login() {
            const res = await loginAPI(this.loginForm)
            this.$message({
                type: res.flag ? 'success' : 'error',
                message: res.message
            })
            if (res.flag) {
                //设置token
                setToken(res.data)
                this.setToken(res.data)
                //请求用户信息
                const result = await getUserInfoByTokenAPI()
                this.setUser(result.data)
                this.setRole(this.loginForm.role)
                //跳转
                this.$router.replace(this.loginForm.role == 1?"/admin":"/front")
            }
        }
    }
}
</script>

<style lang="less" scoped>
.radio-center {
  display: flex;
  justify-content: center;
}
.container {
    width: 100vw;
    height: 100vh;
    background-color: #282c35;

    background-size: 100%;
    display: flex;

    .login-box {
        padding: 20px;
        border-radius: 10px;
        min-width: 300px;
        min-height: 200px;
        background-color: rgba(250,250,250,0.8);
        margin: auto;

        .title {
            text-align: center;
            cursor: pointer;
            &:hover{
                color: #409eff;
            }
        }
    }
}
</style>
