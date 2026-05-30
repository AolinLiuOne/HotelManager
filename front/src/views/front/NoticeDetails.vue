<template>
    <!-- 页面容器 -->
    <div class="page-container">
        <!-- 卡片 -->
        <el-card>
            <!-- 卡片头部 -->
            <div slot="header" class="clearfix">
                <span>公告详情</span>
            </div>
            <h1 class="text-center">{{ notice.title }}</h1>
            <div class="text-gray text-center">{{ notice.createTime }}</div>
            <div class="content">
                {{ notice.content }}
            </div>
        </el-card>
    </div>
</template>

<script>
import { getNoticeByIdAPI } from '@/api/notice'
export default {
    name: 'FrontNoticeDetails',

    data() {
        return {
            notice: {}
        };
    },

    async mounted() {
        // 获取公告详情数据
        const {data:notice} = await getNoticeByIdAPI(this.$route.params.id)
        this.notice = notice
    },

    methods: {

    },
};
</script>

<style lang="less" scoped>
.page-container {
    width: 1200px;
    margin: 20px auto;
    .content{
        padding: 10px;
        line-height: 1.5;
    }
}
</style>