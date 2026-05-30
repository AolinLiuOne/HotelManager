<template>
    <div class="page-container">
        <div class="carousel">
            <el-carousel height="300px">
                <el-carousel-item>
                    <img src="@/assets/宣传.png" class="carousel-img" />
                </el-carousel-item>
                <el-carousel-item>
                    <img src="@/assets/宣传3.png" class="carousel-img" />
                </el-carousel-item>
                <el-carousel-item>
                    <img src="@/assets/酒店国王房.jpg" class="carousel-img" />
                </el-carousel-item>
            </el-carousel>
        </div>
        
        <div class="section">
            <div class="section-title">
                客房展示
            </div>
            <div class="section-list">

                <div v-for="item in categoryList" :key="item.id" class="section-item" @click="$router.push(`/front/room?categoryId=${item.id}`)">
                    <!-- 房间类别图片 -->
                    <img :src="$baseFileUrl+item.photo" class="hotel-img">
                    <!-- 房间类别名称 -->
                    <div class="hotel-title">{{ item.categoryName }}</div>
                    <div class="flex justify-between item-center w-full">
                    <!-- 房间类别价格 -->
                        <div class="hotel-price">￥{{ item.price }}</div>
                    </div>
                </div>

                <!-- 多写两个标签解决flex最后一行对齐问题 -->
                <div class="section-item-empty"></div>
                <div class="section-item-empty"></div>
            </div>
        </div>
        <div class="section">
            <div class="section-title">
                最新公告
            </div>
            <el-table class="my-20" :data="noticeList" border fit highlight-current-row size="small">
                <el-table-column align="center" label="ID" prop="id"></el-table-column>
                <el-table-column align="center" label="标题" prop="title" show-overflow-tooltip></el-table-column>
                <el-table-column align="center" label="发布时间" prop="createTime"></el-table-column>
                <el-table-column fixed="right" label="操作" width="100">
                    <template slot-scope="scope">
                        <el-button @click="$router.push(`/front/noticeDetails/${scope.row.id}`)" type="text" size="small">查看</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script>
import { getTop5NoticeAPI } from '@/api/notice'
import {findAllCategoryAPI} from '@/api/category'
export default {
    name: 'Hotely5dWebHome',

    data() {
        return {
            // 公告列表数据
            noticeList: [],
            // 房间类别列表数据   
            categoryList: []
        };
    },

    async mounted() {
        // 组件挂载后执行的异步操作
        // 获取前5条公告数据
        const { data: noticeList } = await getTop5NoticeAPI()
        this.noticeList = noticeList
        // 获取所有房间类别数据
        const {data: categoryList} = await findAllCategoryAPI()
        this.categoryList = categoryList
    },

    methods: {

    },
};
</script>

<style lang="less" scoped>
.carousel {
    // 轮播样式
    margin-bottom: 20px;

    .carousel-img {
        width: 100%;
        height: 300px;
        background-size: cover;
    }
}

.page-container {
    // 页面容器样式
    width: 1200px;
    margin: 20px auto;


    .section {
        // 区块样式
        .section-title {
            background-color: #303643;
            color: azure;
            padding: 16px;
            border-radius: 6px;
            text-align: center;
        }

        .section-list {
            margin: 20px 0;
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
                cursor: pointer;

                // margin-left: 10px;
                // 等差数列(n-1)*4+1：1 4 9 13 左边距0 每行的第一个元素
                &:nth-child(4n-3) {
                    // background-color: deepskyblue;
                    margin-left: 0 !important;
                }

                .hotel-img {
                    width: 100%;
                    height: 200px;
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


            }
        }
    }

    .hotel-introduction {
        // 酒店简介样式
        display: flex;
        justify-content: space-around;
        align-items: center;

        .hotel-img {
            flex: 1;
            margin: 20px 10px;

            img {
                width: 100%;
                height: 350px;
                border-radius: 6px;
            }
        }

        .introduction {
            height: 350px;
            flex: 1;
            line-height: 1.5;
            color: #303643;
            font-size: 18px;
        }
    }
}

.el-carousel__item h3 {
    color: #475669;
    font-size: 14px;
    opacity: 0.75;
    line-height: 150px;
    margin: 0;
}

.el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
}
</style>