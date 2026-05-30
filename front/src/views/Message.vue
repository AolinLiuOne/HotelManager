<template>
  <div class="app-container">
    <el-card class="box-table">
      <el-form :inline="true" class="demo-form-inline" :model="searchForm">
        <el-form-item label="留言">
          <el-input placeholder="请输入留言" v-model="searchForm.comment"></el-input>
        </el-form-item>
        <el-form-item label="回复">
          <el-input placeholder="请输入回复" v-model="searchForm.reply"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row
        size="small">
        <el-table-column align="center" label="ID" prop="id"></el-table-column>
        <el-table-column align="center" label="用户" prop="member.name"></el-table-column>
        <el-table-column align="center" label="留言" prop="comment"></el-table-column>
        <el-table-column align="center" label="回复" prop="reply"></el-table-column>
        <el-table-column align="center" label="留言时间" prop="createTime"></el-table-column>
        <el-table-column align="center" label="回复时间" prop="replyTime"></el-table-column>
        <el-table-column align="center" label="状态" prop="status">
          <template slot-scope="scope">
            <el-tag type="danger" v-if="scope.row.status == 1">待回复</el-tag>
            <el-tag type="success" v-else>已回复</el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="100">
          <template slot-scope="scope">
            <el-button @click="handleEdit(scope.row)" type="text" size="small">回复</el-button>
            <el-popconfirm title="确定删除吗？" @confirm="deleteCofirm(scope.row)">
              <el-button type="text" slot="reference" size="small">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="page-box" background @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="pageNum" :page-sizes="[10, 20]" :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </el-card>
    <!-- 回复留言的弹框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible">
      <el-form label-position="left" :model="dialogForm" label-width="80px">
        <el-form-item label="留言">
          <el-input disabled type="textarea" :rows="4" placeholder="请输入留言" v-model="dialogForm.comment"
            autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="回复">
          <el-input type="textarea" :rows="4" placeholder="请输入回复" v-model="dialogForm.reply"
            autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogConfirm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  findMessagePageAPI,
  addMessageAPI,
  modifyMessageAPI,
  removeMessageAPI,
} from "@/api/message";

export default {
  data() {
    return {
      list: null,
      listLoading: true,
      pageSize: 10,
      pageNum: 1,
      total: 0,
      searchForm: {},  // 搜索表单数据
      dialogTitle: "添加",  // 弹框标题
      dialogFormVisible: false,  // 弹框可见性
      dialogForm: {},  // 弹框表单数据
    };
  },
  created() {
    this.fetchData();  // 初始化时获取数据
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
      let response = await findMessagePageAPI(
        this.pageNum,
        this.pageSize,
        this.searchForm
      );
      this.list = response.data.records;
      this.total = response.data.total;
      this.listLoading = false;
    },
    handleAdd() {//点击新增按钮-显示弹框
      this.dialogTitle = "新增";
      this.dialogFormVisible = true;
      this.dialogForm = {};
    },
    handleEdit(row) {//点击修改按钮-显示弹框
      this.dialogTitle = "修改";
      this.dialogFormVisible = true;
      this.dialogForm = { ...row };
    },
    async dialogConfirm() {//点击弹框确定按钮
      let res = null;
      if (this.dialogTitle == "新增") {
        res = await addMessageAPI(this.dialogForm);
      } else {
        res = await modifyMessageAPI(this.dialogForm);
      }
      if (res.flag) {
        this.dialogFormVisible = false;
      }
      this.$message({
        message: res.message,
        type: res.flag ? "success" : "error",
      });
      this.fetchData();
    },
    async deleteCofirm(row) {//点击确定删除按钮
      this.pageNum = 1;
      const res = await removeMessageAPI(row.id);
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
.page-box {
  margin-top: 20px;
}
</style>
