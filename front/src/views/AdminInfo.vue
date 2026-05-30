<template>
    <div class="page-container">
        <el-card class="box-card">
            <div slot="header" class="clearfix">
                <span>个人信息</span>
            </div>
            <el-form label-position="left" :model="form" label-width="80px">
                <el-form-item label="账号">
                    <el-input disabled  v-model="form.username" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                    <el-input type="password" placeholder="请输入密码" v-model="form.password" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input placeholder="请输入姓名" v-model="form.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="性别">
                    <el-select v-model="form.gender" placeholder="请选择">
                        <el-option v-for="item in genderOptions" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleModify">修改</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script>
import { modifyAdminAPI } from "@/api/admin";
import { mapMutations } from 'vuex'
import { getUserInfoByTokenAPI } from '@/api/system'
export default {
    name: 'Hotely5dWebAdminInfo',

    data() {
        return {
            form: {}, // 表单数据
            genderOptions: [ // 性别选项
                {
                    label: '男',
                    value: 1
                },
                {
                    label: '女',
                    value: 2
                }
            ],
        };
    },

    async mounted() {
        //请求用户信息
        const result = await getUserInfoByTokenAPI()
        // 将获取到的用户信息赋值给表单数据
        this.form = result.data
    },

    methods: {
        ...mapMutations(['setUser']),
        async handleModify() {
            // 调用修改管理员信息的 API
            const res = await modifyAdminAPI(this.form);

            // 显示提示消息
            this.$message({
                message: res.message,
                type: res.flag ? "success" : "error",
            });
            if (res.flag) {
                // 更新用户信息
                this.setUser(this.form)
                // 刷新页面
                setTimeout(() => {
                    this.$router.go(0)
                }, 1000);
            }

        },
    },
};
</script>

<style lang="less" scoped>
.page-container {
    width: 600px;
    margin: 20px auto;
}
</style>