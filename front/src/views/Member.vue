<template>
  <div class="app-container">
    <el-card class="box-table">
      <el-form :inline="true" class="demo-form-inline" :model="searchForm">
        <el-form-item label="账号">
          <el-input placeholder="请输入账号" v-model="searchForm.username"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input placeholder="请输入姓名" v-model="searchForm.name"></el-input>
        </el-form-item>
        <el-form-item label="手机">
          <el-input placeholder="请输入手机" v-model="searchForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="身份证">
          <el-input placeholder="请输入身份证" v-model="searchForm.idcard"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
      <el-table
        v-loading="listLoading"
        :data="list"
        element-loading-text="Loading"
        border
        fit
        highlight-current-row
        size="small"
      >
        <el-table-column align="center" label="ID" prop="id"></el-table-column>
        <el-table-column align="center" label="账号" prop="username"></el-table-column>
        <el-table-column align="center" label="姓名" prop="name"></el-table-column>
        <el-table-column align="center" label="性别" prop="gender">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.gender == 1">男</el-tag>
            <el-tag type="danger" v-else>女</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="手机" prop="phone"></el-table-column>
        <el-table-column align="center" label="身份证" prop="idcard"></el-table-column>
        <el-table-column align="center" label="头像" prop="head">
          <template slot-scope="scope">
            <el-image style="width: 60px; height: 60px" :src="$baseFileUrl + scope.row.head" fit="fit"></el-image>
          </template>
        </el-table-column>
        <el-table-column align="center" label="注册时间" prop="createTime"></el-table-column>
        <el-table-column fixed="right" label="操作" width="100">
          <template slot-scope="scope">
            <el-button @click="handleEdit(scope.row)" type="text" size="small"
              >编辑</el-button
            >
            <el-popconfirm
              title="确定删除吗？"
              @confirm="deleteCofirm(scope.row)"
            >
              <el-button type="text" slot="reference" size="small"
                >删除</el-button
              >
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

       <!-- 分页组件 -->
      <el-pagination
        class="page-box"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      >
      </el-pagination>
    </el-card>
    <!-- 添加/修改的弹框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible">
      <el-form label-position="left" :model="dialogForm" label-width="80px">
        <el-form-item label="账号">
          <el-input placeholder="请输入账号" v-model="dialogForm.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" placeholder="请输入密码" v-model="dialogForm.password" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input placeholder="请输入姓名" v-model="dialogForm.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="dialogForm.gender" placeholder="请选择">
            <el-option v-for="item in genderOptions" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机">
          <el-input placeholder="请输入手机" v-model="dialogForm.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="身份证">
          <el-input placeholder="请输入身份证" v-model="dialogForm.idcard" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload name="avatar" class="upload-demo" action="#" :http-request="uploadHeadFile" :file-list="fileList"
            list-type="picture">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <!-- 弹框底部按钮 -->
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogConfirm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  findMemberPageAPI,
  addMemberAPI,
  modifyMemberAPI,
  removeMemberAPI,
} from "@/api/member";
import request from '@/utils/request'
export default {
  data() {
    return {
      list: null,
      listLoading: true,
      pageSize: 10,
      pageNum: 1,
      total: 0,
      searchForm: {
        hotelId: ''
      },
      dialogTitle: "添加",
      dialogFormVisible: false,
      dialogForm: {
        hotelId: ''
      },
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
      fileList: []
    };
  },
  created() {
    const vuexStr = localStorage.getItem("vuex")
    const vuexState = JSON.parse(vuexStr);
    this.searchForm.hotelId = vuexState.user.hotelId
    this.dialogForm.hotelId = vuexState.user.hotelId
    this.fetchData();
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
      let response = await findMemberPageAPI(
        this.pageNum,
        this.pageSize,
        this.searchForm,
      );
      this.list = response.data.records;
      this.total = response.data.total;
      this.listLoading = false;
    },
    handleAdd() {//点击新增按钮-显示弹框
      this.dialogTitle = "新增";
      this.dialogFormVisible = true;
      this.dialogForm = {gender: 1};
      this.fileList = []
    },
    handleEdit(row) {//点击修改按钮-显示弹框
      this.dialogTitle = "修改";
      this.dialogFormVisible = true;
      this.dialogForm = { ...row };
      this.fileList = [{
          name: this.dialogForm.head,
          url: this.$baseFileUrl + this.dialogForm.head
        }]
    },
    async dialogConfirm() {//点击弹框确定按钮
      let res = null;
      if (this.dialogTitle == "新增") {
        res = await addMemberAPI(this.dialogForm);
      } else {
        res = await modifyMemberAPI(this.dialogForm);
      }
      if (res.flag) {
        this.dialogFormVisible = false;
      }
      this.$message({
        message: res.message,
        type: res.flag  ? "success" : "error",
      });
      this.fetchData();
    },
    async deleteCofirm(row) {//点击确定删除按钮
      this.pageNum = 1;
      const res = await removeMemberAPI(row.id);
      this.$message({
        message: res.message,
        type: res.flag ? "success" : "error",
      });
      this.fetchData();
    },
    uploadHeadFile(item) {
      let fileObj = item.file;
      const form = new FormData(); // FormData 对象
      form.append("photo", fileObj); // 文件对象  'photo'是后台接收的参数名
      request.post("/file/uploadPhoto", form, "form").then((resp) => {
        // console.log(resp.data)
        this.dialogForm.head = resp.data.filename
        this.fileList = [{
          name: this.dialogForm.head,
          url: this.$baseFileUrl + this.dialogForm.head
        }]
      })
    },
  },
};
</script>

<style lang="less" scoped>
.page-box {
  margin-top: 20px;
}
</style>
