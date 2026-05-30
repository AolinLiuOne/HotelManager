<template>
  <div class="app-container">
    <el-card class="box-table">
      <el-form :inline="true" class="demo-form-inline" :model="searchForm">
        <el-form-item label="房间类型">
          <el-input placeholder="请输入房间类型" v-model="searchForm.categoryName"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row
        size="small">
        <el-table-column align="center" label="ID" prop="id"></el-table-column>
        <el-table-column align="center" label="房间类型" prop="categoryName"></el-table-column>
        <el-table-column align="center" label="房间图片" prop="photo">
          <template slot-scope="scope">
            <el-image style="width: 60px; height: 60px" :src="$baseFileUrl + scope.row.photo" fit="fit"></el-image>
          </template>
        </el-table-column>
        <el-table-column align="center" label="价格" prop="price"></el-table-column>
        <el-table-column align="center" label="可住人数" prop="liveNum"></el-table-column>
        <el-table-column align="center" label="床位数" prop="bedNum"></el-table-column>
        <el-table-column align="center" label="面积" prop="area"></el-table-column>
        <el-table-column align="center" label="介绍" prop="introduce"></el-table-column>
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
        <el-form-item label="房间类型">
          <el-input placeholder="请输入房间类型" v-model="dialogForm.categoryName" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="房间图片">
          <el-upload name="avatar" class="upload-demo" action="#" :http-request="uploadFile" :file-list="fileList"
            list-type="picture">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="价格">
          <el-input placeholder="请输入价格" v-model="dialogForm.price" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="可住人数">
          <el-input placeholder="请输入可住人数" v-model="dialogForm.liveNum" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="床位数">
          <el-input placeholder="请输入床位数" v-model="dialogForm.bedNum" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="面积">
          <el-input placeholder="请输入面积" v-model="dialogForm.area" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="介绍">
          <el-input type="textarea" placeholder="请输入介绍" v-model="dialogForm.introduce" autocomplete="off"></el-input>
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
  findCategoryPageAPI,  // 导入查询分类列表的 API
  addCategoryAPI,  // 导入新增分类的 API
  modifyCategoryAPI,  // 导入修改分类的 API
  removeCategoryAPI,  // 导入删除分类的 API
} from "@/api/category";
import request from '@/utils/request'  // 导入请求工具函数
export default {
  data() {
    return {
      list: null,  // 分类列表数据
      listLoading: true,  // 列表加载状态
      pageSize: 10,  // 每页显示条数
      pageNum: 1,  // 当前页码
      total: 0,  // 总条数
      searchForm: {
        hotelId: ''
      },  // 搜索表单数据
      dialogTitle: "添加",  // 弹框标题，默认为添加
      dialogFormVisible: false,  // 弹框显示状态
      dialogForm: {
        hotelId:''
      },  // 弹框表单数据
      fileList: [] // 文件列表
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
      let response = await findCategoryPageAPI(
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
        hotelId: ''
      };
      const vuexStr = localStorage.getItem("vuex")
      const vuexState = JSON.parse(vuexStr);
      this.dialogForm.hotelId = vuexState.user.hotelId
      this.fileList = []
    },
    handleEdit(row) {//点击修改按钮-显示弹框
      this.dialogTitle = "修改";
      this.dialogFormVisible = true;
      this.dialogForm = { ...row };
      this.fileList = [{
        name: this.dialogForm.photo,
        url: this.$baseFileUrl + this.dialogForm.photo
      }]
    },
    async dialogConfirm() {//点击弹框确定按钮
      let res = null;
      this.dialog
      if (this.dialogTitle == "新增") {
        res = await addCategoryAPI(this.dialogForm);
      } else {
        res = await modifyCategoryAPI(this.dialogForm);
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
      const res = await removeCategoryAPI(row.id,JSON.parse(localStorage.getItem('vuex')).user.hotelId);
      this.$message({
        message: res.message,
        type: res.flag ? "success" : "error",
      });
      this.fetchData();
    },
    uploadFile(item) {
      let fileObj = item.file;
      const form = new FormData(); // FormData 对象
      form.append("photo", fileObj); // 文件对象  'photo'是后台接收的参数名
      request.post("/file/uploadPhoto", form, "form").then((resp) => {
        // console.log(resp.data)
        this.dialogForm.photo = resp.data.filename
        this.fileList = [{
          name: this.dialogForm.photo,
          url: this.$baseFileUrl + this.dialogForm.photo
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
