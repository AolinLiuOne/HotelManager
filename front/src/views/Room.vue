<template>
  <div class="app-container">
    <el-card class="box-table">
      <el-form :inline="true" class="demo-form-inline" :model="searchForm">
        <el-form-item label="房间号">
          <el-input placeholder="请输入房间号" v-model="searchForm.roomNum" autocomplete="off" clearable ></el-input>
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="searchForm.categoryId" placeholder="请选择" clearable>
            <el-option v-for="item in categoryList" :key="item.id" :label="item.categoryName" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row
        size="small">
        <el-table-column align="center" label="ID" prop="id"></el-table-column>
        <el-table-column align="center" label="房间号" prop="roomNum"></el-table-column>
        <el-table-column align="center" label="房型" prop="category.categoryName"></el-table-column>
        <el-table-column align="center" label="状态" prop="status">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.status == 1">正常</el-tag>
            <el-tag type="danger" v-if="scope.row.status == 2">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="100">
          <template slot-scope="scope">
            <el-button @click="handleEdit(scope.row)" type="text" size="small">编辑</el-button>
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
    <!-- 添加/修改的弹框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible">
      <el-form label-position="left" :model="dialogForm" label-width="80px">
        <el-form-item label="房间号">
          <el-input placeholder="请输入房间号" v-model="dialogForm.roomNum" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="dialogForm.categoryId" placeholder="请选择">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.categoryName" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="dialogForm.status" placeholder="请选择">
            <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
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
  findRoomPageAPI,
  addRoomAPI,
  modifyRoomAPI,
  removeRoomAPI,
} from "@/api/room";
import {findAllCategoryAPI, findCategoryAPI, findHotelCategoryAPI} from '@/api/category'
export default {
  data() {
    return {
      list: null,
      listLoading: true,
      pageSize: 10,
      pageNum: 1,
      total: 0,
      hotelId: '',
      searchForm: {
        hotelId: ''
      },
      dialogTitle: "添加",
      dialogFormVisible: false,
      dialogForm: {
        hotelId: ''
      },
      categoryList: [],
      statusList: [
        { label: '正常', value: 1 },
        { label: '停用', value: 2 },
      ]
    };
  },
  created() {
    const vuexStr = localStorage.getItem("vuex")
    const vuexState = JSON.parse(vuexStr);
    this.searchForm.hotelId = vuexState.user.hotelId
    this.dialogForm.hotelId =  vuexState.user.hotelId
    this.fetchData();
    this.getCategoryList()
  },
  methods: {
    async getCategoryList() {
      const { data } = await findHotelCategoryAPI(
          JSON.parse(localStorage.getItem("vuex"))
              .user
              .hotelId
      )
      this.categoryList = data
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
      let response = await findRoomPageAPI(
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
      this.dialogForm = {
        hotelId:'',
        categoryId: this.categoryList[0].id,
        status: this.statusList[0].value
      };
      this.dialogForm.hotelId = JSON.parse(localStorage.getItem("vuex"))
          .user
          .hotelId
    },
    handleEdit(row) {//点击修改按钮-显示弹框
      this.dialogTitle = "修改";
      this.dialogFormVisible = true;
      this.dialogForm = { ...row };
    },
    async dialogConfirm() {//点击弹框确定按钮
      let res = null;
      if (this.dialogTitle == "新增") {
        res = await addRoomAPI(this.dialogForm);
      } else {
        res = await modifyRoomAPI(this.dialogForm);
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
      const res = await removeRoomAPI(row.id);
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
