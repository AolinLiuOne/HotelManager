<template>
    <el-card class="box-card">
        <div slot="header" class="clearfix">
            <span>客房详情</span>
        </div>
        <div class="page-container">
            <div class="left-section">
                <img :src="$baseFileUrl + category.photo" class="hotel-img">
            </div>
            <div class="right-section">
                <el-descriptions class="margin-top" :column="1" border>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-office-building"></i>
                            房型
                        </template>
                        {{ category.categoryName }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-user"></i>
                            房号
                        </template>
                        {{ room.roomNum }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-mobile-phone"></i>
                            价格
                        </template>
                        {{ category.price }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-location-outline"></i>
                            可住人数
                        </template>
                        {{ category.liveNum }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-tickets"></i>
                            床位数
                        </template>
                        {{ category.bedNum }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-office-building"></i>
                            面积
                        </template>
                        {{ category.area }}平方
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-office-building"></i>
                            今日状态
                        </template>
                        <el-tag v-if="room.canUse" type="success">可预订</el-tag>
                        <el-tag v-else type="danger">已满房</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-location-outline"></i>
                            介绍
                        </template>
                        {{ category.introduce }}
                    </el-descriptions-item>
                </el-descriptions>
                <p v-if="!token" class="text-red">温馨提示：请登录后再预订</p>
                <el-form inline ref="form" :model="form" class="my-10">
                    <el-form-item>
                        <el-date-picker size="mini" :clearable="false" :picker-options="pickerOptions" v-model="form.startTime"
                        type="date" placeholder="入住日期" value-format="yyyy-MM-dd" format="yyyy-MM-dd">
                    </el-date-picker>
                    </el-form-item>
                    <el-form-item>
                       <el-input type="number" min="1" placeholder="居住天数" size="mini" v-model="form.days"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button size="mini" v-if="role != 1" class="ml-10" @click="handleAppointment" type="primary">预订</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>

    </el-card>
</template>

<script>
import { findRoomByIdAPI } from '@/api/room'
import { addAppointmentAPI } from '@/api/appointment'
import { mapState } from 'vuex'
import dayjs from 'dayjs'
export default {
    name: 'FrontCategoryDetails',

    data() {
        return {
            pickerOptions: {
                disabledDate: this.disabledDate  // 日期选择器的禁用日期方法
            },
            room: {},  // 当前客房信息
            category: {},  // 当前客房所属分类信息
            form: {
                roomId: this.$route.params.id,  // 表单数据：客房ID
                startTime: null,  // 表单数据：入住时间
                days: null  // 表单数据：居住天数
            },
            timeArr: null
        };
    },

    computed: {
        ...mapState(['user', 'role', 'token'])  // 计算属性：映射 Vuex 中的状态
    },

    async mounted() {
        // 组件挂载后获取客房信息
        const { data } = await findRoomByIdAPI(this.$route.params.id)
        this.room = data
        this.category = data.category
    },

    methods: {
        disabledDate(time) {
            //禁用今天之前的日期
            if(dayjs(time).isBefore(dayjs(), 'day')){
                return true
            }
            //禁用其它日期
            return this.room.notUseDateList.includes(dayjs(time).format('YYYY-MM-DD') )
        },
        //预订
        async handleAppointment() {
            // 未登录时提示用户登录并跳转登录页面
            if (!this.token) {
                this.$message({ type: 'error', message: '请先登录！' })
                setTimeout(() => this.$router.push('/login'), 1000)
                return
            }
            // 未选择入住时间时提示用户选择
            if (null == this.form.startTime || this.form.startTime == '') {
                this.$message({ type: 'error', message: '请选择入住时间！' })
                return
            }
            // 未选择居住天数时提示用户选择
            if (null == this.form.days || this.form.days == '') {
                this.$message({ type: 'error', message: '请选择居住天数！' })
                return
            }
            // 调用预订接口，传递表单数据
            const res = await addAppointmentAPI({
                ...this.form,
                startTime: this.form.startTime+" 00:00:00"
            })
            // 显示预订结果信息
            this.$message({
                message: res.message,
                type: res.flag ? "success" : "error",
            });
            if(res.flag){
                // 预订成功后跳转到客房列表页面
                this.$router.push("/front/room")
            }
            
        }
    },
};
</script>

<style lang="less" scoped>
.box-card {
    width: 1200px;
    margin: 20px auto;
}

.page-container {
    display: flex;

    .left-section {
        min-height: 500px;
        padding: 14px;

        .hotel-img {
            width: 80%;
            height: 300px;

        }
    }

    .right-section {
        padding: 14px;
        flex: 1;
    }
}
</style>