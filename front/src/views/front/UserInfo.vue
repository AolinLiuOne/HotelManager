<template>
    <div class="page-container">
        <el-card class="box-card">
            <div slot="header" class="clearfix">
                <span>个人信息</span>
            </div>
            <el-form label-position="left" :model="form" label-width="80px">
                <el-form-item label="账号">
                    <el-input disabled placeholder="请输入账号" v-model="form.username" autocomplete="off"></el-input>
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
                <el-form-item label="手机">
                    <el-input placeholder="请输入手机" v-model="form.phone" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="身份证">
                    <el-input placeholder="请输入身份证" v-model="form.idcard" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="头像">
                    <el-upload name="avatar" class="upload-demo" action="#" :http-request="uploadHeadFile"
                        :file-list="fileList" list-type="picture">
                        <el-button size="small" type="primary">点击上传</el-button>
                    </el-upload>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleModify">修改</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script>
import request from '@/utils/request'
import { modifyMemberAPI } from "@/api/member";
import { mapMutations } from 'vuex'
import { getUserInfoByTokenAPI } from '@/api/system'
export default {
    name: 'Hotely5dWebMemberInfo',

    data() {
        return {
            genderOptions: [
                {
                    label: '男',
                    value: 1
                },
                {
                    label: '女',
                    value: 2
                }
            ],
            form: {},
            fileList: []
        };
    },

    async mounted() {
        //请求用户信息
        const result = await getUserInfoByTokenAPI()
        this.form = result.data
        this.fileList = [{
            name: this.form.head,
            url: this.$baseFileUrl + this.form.head
        }]
    },

methods: {
        ...mapMutations(['setUser']),

        async handleModify() {
            // 验证表单
            if (!this.validateForm()) {
                this.$message.error('修改信息不完整');
                return;
            }

            const res = await modifyMemberAPI(this.form);

            this.$message({
                message: res.message,
                type: res.flag ? "success" : "error",
            });
            if (res.flag) {
                this.setUser(this.form)
                setTimeout(() => {
                    this.$router.go(0)
                }, 1000);
            }
        },

        validateForm() {
            const form = this.form;
            for (let key in form) {
                if (!form[key]) {
                    return false;
                }
            }
            return true;
        },
        
        // 上传头像文件
        uploadHeadFile(item) {
            let fileObj = item.file;
            const form = new FormData(); // FormData 对象
            form.append("photo", fileObj); // 文件对象  'photo'是后台接收的参数名
            request.post("/file/uploadPhoto", form, "form").then((resp) => {
                // console.log(resp.data)
                this.form.head = resp.data.filename
                this.fileList = [{
                    name: this.form.head,
                    url: this.$baseFileUrl + this.form.head
                }]
            })
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