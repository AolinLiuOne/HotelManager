<template>
    <div class="page-container">
        <div class="section">
            <div class="section-title">
                预约入住
            </div>
            <el-form :inline="true" :model="formInline" class="mt-20">
                <el-form-item label="入住日期">
                    <el-date-picker :clearable="false" :picker-options="pickerOptions" v-model="formInline.startDate"
                        type="date" placeholder="入住日期" value-format="yyyy-MM-dd" format="yyyy-MM-dd">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="房型">
                    <el-select v-model="formInline.categoryId" placeholder="房型" clearable>
                        <el-option v-for="item in categoryList" :label="item.categoryName" :value="item.id"
                            :key="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="formInline.canUse" placeholder="状态" clearable>
                        <el-option label="可预订" :value="true"></el-option>
                        <el-option label="已满房" :value="false"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit">查询</el-button>
                </el-form-item>
            </el-form>
            <div class="section-list">

                <div v-for="item in roomList" :key="item.id" class="section-item"
                    @click="$router.push(`/front/roomDetails/${item.id}`)">
                    <img :src="$baseFileUrl + item.category.photo" class="hotel-img">
                    <div class="hotel-title">{{ item.category.categoryName }}-{{ item.roomNum }}</div>
                    <div class="flex justify-between item-center w-full">
                        <el-tag type="success" v-if="item.canUse">可预订</el-tag>
                        <el-tag type="danger" v-else>已满房</el-tag>
                        <div class="hotel-name">{{ item.hotelName }}</div>
                        <div class="hotel-price">￥{{ item.category.price }}</div>
                    </div>
                </div>


                <!-- 多写两个标签解决flex最后一行对齐问题 -->
                <div class="section-item-empty"></div>
                <div class="section-item-empty"></div>
            </div>
        </div>
    </div>
</template>

<script>
import { findAllCategoryAPI } from '@/api/category'
import { findRoomListAPI } from '@/api/room'
import dayjs from 'dayjs'
export default {
    name: 'FrontCategory',

    data() {
        return {
            pickerOptions: {
                disabledDate: this.disabledDate
            },
            formInline: {
                startDate: dayjs().format('YYYY-MM-DD'),
                categoryId: null,
                canUse: null
            },
            categoryList: [],
            roomList: []
        };
    },

    async mounted() {
        // 从路由中获取分类ID，若存在则设置到表单中
        if (this.$route.query?.categoryId) {
            this.formInline.categoryId = parseInt(this.$route.query?.categoryId)
        }
        // 获取分类列表和客房列表
        this.getCategoryList()
        this.getList()
    },

    methods: {
        disabledDate(time) {
            //禁用今天之前的日期
            return dayjs(time).isBefore(dayjs(), 'day')
        },
        // 获取分类列表
        async getCategoryList() {
            const { data } = await findAllCategoryAPI(null)
            console.log(data)
            this.categoryList = data
        },
        // 获取客房列表
        async getList() {
            const { data } = await findRoomListAPI(this.formInline)
            this.roomList = data;
        },
        // 表单提交时重新获取客房列表
        onSubmit() {
            this.getList()
        }
    },
};
</script>

<style lang="less" scoped>
.page-container {
    width: 1200px;
    margin: 20px auto;

    .section {
        .section-title {
            background-color: #303643;
            color: #fff;
            padding: 16px;
            border-radius: 6px;
            text-align: center;
        }

        .section-list {
            margin-bottom: 20px;
            display: flex;
            flex-wrap: wrap;
            justify-content: space-between;

            // //重要 解决最后一行两端对齐的问题
            &::after {
                content: '';
                // flex: 1;
                width: 290px;
            }

            .section-item-empty {
                width: 290px;
            }

            .section-item {
                // width: 290px;
                width: 290px;
                height: 300px;
                padding: 16px;
                box-sizing: border-box;
                border-radius: 6px;
                margin-bottom: 10px;
                // background-color: antiquewhite;

                border: 1px solid #DCDFE6;
                display: flex;
                flex-direction: column;
                align-items: center;
                cursor: pointer;

                // margin-left: 10px;
                // 等差数列(n-1)*4+1：1 4 9 13 左边距0 每行的第一个元素
                &:nth-child(4n-3) {
                    // background-color: deepskyblue;
                    margin-left: 0 !important;
                }

                .hotel-img {
                    width: 100%;
                    height: 180px;
                }

                .hotel-title {
                    font-weight: bold;
                    width: 100%;
                    margin: 10px;
                    text-overflow: ellipsis;
                    overflow: hidden;
                    white-space: nowrap;
                }

                .hotel-price {
                    color: red;
                    width: 100%;
                    margin: 4px;
                    text-align: right;
                }

                .hotel-name {
                  color: #493e3e;
                  width: 100%;
                  margin: 4px;
                  text-align: right;
                }

            }
        }
    }
}
</style>