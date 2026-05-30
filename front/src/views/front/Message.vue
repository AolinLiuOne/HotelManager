<template>
    <!-- 用户留言页面容器 -->
    <div class="page-container">
        <!-- 卡片 -->
        <el-card>
            <div slot="header" class="clearfix">
                <!-- 标题 -->
                <span>用户留言（{{ total }}）</span>
                <!-- 添加留言按钮 -->
                <el-button @click="handleAddClick" style="float: right; padding: 3px 0" type="text">吐槽一下</el-button>
            </div>

            <!-- 留言列表 -->
            <div class="message-list" v-loading="listLoading">

                <div class="message-item" v-for="item in list" :key="item.id">
                    <div class="comment-head">
                        <img :src="$baseFileUrl + item.member.head" class="head" />
                    </div>
                    <div class="comment-detail p-10">
                        <!-- 评论内容 -->
                        <div class="text-bold">{{ item.member.name }}</div>
                        <div class="comment-content font-14 my-10">{{ item.comment }}</div>
                        <div class="text-gray mb-10 font-14">{{ item.createTime }}</div>
                        <!-- 回复内容 -->
                        <div class="reply p-y" v-show="item?.reply">
                            <div class="reply-head">
                                <img src="@/assets/head.jpg" class="head" />
                            </div>
                            <div class="reply-detail">
                                <div class="text-bold">管理员回复</div>
                                <div class="reply-content font-14 my-10">{{ item.reply }}</div>
                                <div class="text-gray font-14">{{ item.replyTime }}</div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

            <!-- 分页器 -->
            <el-pagination class="page-box" background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="pageNum" :page-sizes="[10, 20]" :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="total">
            </el-pagination>
        </el-card>
        <!-- 留言对话框 -->
        <el-dialog title="留言" :visible.sync="showDialog" :before-close="handleDialogClose">
            <!-- 留言输入框 -->
            <el-input type="textarea" :rows="4" placeholder="请输入你的留言" v-model="dialogForm.comment">
            </el-input>
            <!-- 留言对话框底部 -->
            <span slot="footer" class="dialog-footer">
                <el-button @click="showDialog = false">取 消</el-button>
                <el-button type="primary" @click="dialogConfirm">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { findMessagePageAPI, addMessageAPI } from "@/api/message";
import { mapState } from 'vuex'
export default {
    name: 'FrontMessage',

    data() {
        return {
            list: [],
            listLoading: false,
            pageSize: 10,
            pageNum: 1,
            total: 0,
            showDialog: false,
            dialogForm: {}
        };
    },

    created() {
        this.fetchData();
    },

    computed: {
        ...mapState(['user', 'role', 'token'])
    },

    methods: {
        handleDialogClose() {
            this.showDialog = false
        },
        handleSizeChange(val) {//每页显示条数改变
            this.pageSize = val;
            this.fetchData();
        },
        handleCurrentChange(val) {//当前页码改变
            this.pageNum = val;
            this.fetchData();
        },
        handleSearch() {//点击搜索按钮
            this.fetchData();
        },
        async fetchData() {//查询数据
            this.listLoading = true;
            let response = await findMessagePageAPI(
                this.pageNum,
                this.pageSize,
                this.searchForm
            );
            this.list = response.data.records;
            this.total = response.data.total;
            this.listLoading = false;
        },
        //点击吐槽按钮
        handleAddClick() {
            if (!this.token) {
                this.$message({ type: 'error', message: '请先登录！' })
                setTimeout(() => this.$router.push('/login'), 1000)
                return
            }
            this.showDialog = true
        },
        //点击吐槽弹框确认按钮
        async dialogConfirm() {
            if(this.dialogForm.comment == '' || this.dialogForm.comment == null){
                this.$message({
                    message: "内容不能为空！",
                    type: "error",
                });
                return
            }
            const res = await addMessageAPI(this.dialogForm)
            this.$message({
                message: res.message,
                type: res.flag ? "success" : "error",
            });
            if(res.flag){
                this.showDialog = false
                this.fetchData()
            }
            
        }
    },
};
</script>

<style lang="less" scoped>
.page-container {
    width: 1200px;
    margin: 20px auto;

    .message-list {
        .message-item {
            display: flex;
            line-height: 1.5;
            margin-bottom: 10px;
            border-bottom: 1px solid #ccc;
            font-size: 14px;

            &:nth-last-child(1) {
                border-bottom: none;
            }

            .head {
                width: 40px;
                height: 40px;
                border-radius: 50%;
            }

            .reply {
                display: flex;
            }
        }
    }
}
</style>