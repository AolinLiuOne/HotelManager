<template>
    <!-- 页面容器 -->
    <div class="page-container">
        <!-- 卡片 -->
        <el-card class="box-card">
            <!-- 卡片头部 -->
            <div slot="header" class="clearfix">
                <span>我的预约</span>
            </div>
            <!-- 表单 -->
            <el-form :inline="true" class="demo-form-inline" :model="searchForm">
                <el-form-item label="备注">
                    <el-input placeholder="请输入备注" v-model="searchForm.remark" clearable></el-input>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="searchForm.status" clearable>
                        <el-option label="待确认" :value="1"></el-option>
                        <el-option label="预订成功" :value="2"></el-option>
                        <el-option label="预订失败" :value="3"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">查询</el-button>
                </el-form-item>
            </el-form>
            <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row
                size="small">
                <el-table-column align="center" label="ID" prop="id"></el-table-column>
                <el-table-column align="center" label="用户" prop="member.name"></el-table-column>
                <el-table-column align="center" label="电话" prop="member.phone"></el-table-column>
                <el-table-column align="center" label="身份证" prop="member.idcard"></el-table-column>
                <el-table-column align="center" label="性别" prop="member.gender">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.member.gender == 1" type="success">男</el-tag>
                        <el-tag v-if="scope.row.member.gender == 2" type="danger">女</el-tag>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="房型" prop="room.category.categoryName"></el-table-column>
                <el-table-column align="center" label="房号" prop="room.roomNum"></el-table-column>
                <el-table-column align="center" label="入住时间" prop="startTime">
                    <template slot-scope="scope">
                        {{ scope.row.startTime.slice(0, 10) }}
                    </template>
                </el-table-column>
                <el-table-column align="center" label="居住天数" prop="days"></el-table-column>
                <el-table-column align="center" label="状态" prop="status">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.status == 1" type="warning">待确认</el-tag>
                        <el-tag v-if="scope.row.status == 2" type="success">预订成功</el-tag>
                        <el-tag v-if="scope.row.status == 3" type="danger">预订失败</el-tag>
                    </template>

                </el-table-column>
                <el-table-column align="center" label="备注" prop="remark" show-overflow-tooltip></el-table-column>
                <el-table-column align="center" label="已付金额" prop="money">
                    <template slot-scope="scope">
                        ￥{{ scope.row.money *  scope.row.days }}
                    </template>
                </el-table-column>
                <!-- 操作列 -->
                <el-table-column fixed="right" label="操作" width="100">
                    <template slot-scope="scope">
                        <!-- 删除按钮 -->
                        <el-popconfirm title="确定删除吗？" @confirm="deleteCofirm(scope.row)">
                            <el-button type="text" slot="reference" size="small">删除</el-button>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页器 -->
            <el-pagination class="page-box" background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="pageNum" :page-sizes="[10, 20]" :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="total">
            </el-pagination>
        </el-card>
    </div>
</template>

<script>
import {
    findAppointmentPageAPI,
    cancelAppointmentAPI,
    removeAppointmentAPI,
} from "@/api/appointment";
export default {
    // 组件名称
    name: 'MyAppointment',

    data() {
        return {
            list: null,     // 预约列表
            listLoading: true,  // 是否正在加载数据
            pageSize: 10,  // 每页显示条数
            pageNum: 1,  // 当前页码
            total: 0,   // 总预约数
            searchForm: {},   // 查询表单
        };
    },

    mounted() {
        this.fetchData();   // 获取数据
    },

    methods: {
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
            let response = await findAppointmentPageAPI(
                this.pageNum,
                this.pageSize,
                this.searchForm
            );
            this.list = response.data.records;
            this.total = response.data.total;
            this.listLoading = false;
        },
        async deleteCofirm(row) {//点击确定删除按钮
            this.pageNum = 1;
            const res = await removeAppointmentAPI(row.id);
            this.$message({
                message: res.message,
                type: res.flag ? "success" : "error",
            });
            this.fetchData();
        },
    },
};
</script>

<style lang="less" scoped>
.page-container {
    width: 1200px;
    margin: 20px auto;
}

.page-box {
    margin-top: 20px;
}
</style>