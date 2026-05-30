<template>
  <div class="app-container">
    <el-card class="box-table">
      <!-- 查询表单 -->
      <el-form :inline="true" class="demo-form-inline" :model="searchForm">
        <el-form-item label="备注" >
          <el-input placeholder="请输入备注" v-model="searchForm.remark" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态" >
          <el-select v-model="searchForm.status" clearable>
            <el-option label="待确认" :value="1"></el-option>
            <el-option label="预订成功" :value="2"></el-option>
            <el-option label="预订失败" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户" >
          <el-select v-model="searchForm.memberId" placeholder="请选择" clearable filterable >
            <!-- 用户列表选项 -->
            <el-option v-for="item in memberList" :label="item.name+'-'+item.idcard" :value="item.id" :key="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row
        size="small">
        <!-- 列定义 -->
        <el-table-column align="center" label="ID" prop="id"></el-table-column>
        <el-table-column align="center" label="用户" prop="member.name"></el-table-column>
        <el-table-column align="center" label="电话" prop="member.phone"></el-table-column>
        <el-table-column align="center" label="身份证" prop="member.idcard"></el-table-column>
        <el-table-column align="center" label="性别" prop="member.gender">
          <!-- 性别显示 -->
          <template slot-scope="scope">
            <el-tag v-if="scope.row.member.gender == 1" type="success">男</el-tag>
            <el-tag v-if="scope.row.member.gender == 2" type="danger">女</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="房型" prop="room.category.categoryName"></el-table-column>
        <el-table-column align="center" label="房号" prop="room.roomNum"></el-table-column>
        <el-table-column align="center" label="入住时间" prop="startTime">
          <!-- 入住时间格式化 -->
          <template slot-scope="scope">
            {{ scope.row.startTime.slice(0,10) }}
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
          <!-- 已付金额计算 -->
          <template slot-scope="scope">
            ￥{{  scope.row.money *  scope.row.days }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="100">
          <!-- 操作按钮 -->
          <template slot-scope="scope">
            <el-button v-if="scope.row.status == 1" @click="handleCofirmAppointemnt(scope.row)" type="text" size="small">确认</el-button>
            <el-button v-if="scope.row.status == 1" @click="handleCancelAppointemnt(scope.row)" type="text" size="small">取消</el-button>
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

    <!-- 取消预订弹框 -->
    <el-dialog title="取消预订" :visible.sync="dialogFormVisible">
      <el-form label-position="left" :model="dialogForm" label-width="80px">
        <el-form-item label="备注">
          <el-input type="textarea" placeholder="请输入取消的备注信息" v-model="dialogForm.remark" autocomplete="off"></el-input>
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
  findAppointmentPageAPI,
  cancelAppointmentAPI,
  confirmAppointmentAPI,
  removeAppointmentAPI,
} from "@/api/appointment";
import { findAllMemberAPI } from '@/api/member'

export default {
  data() {
    return {
      list: null,  // 列表数据
      listLoading: true,  // 列表加载状态
      pageSize: 10,  // 每页显示条数
      pageNum: 1,  // 当前页码
      total: 0,  // 总条数
      searchForm: {
        hotelId:  ''
      },  // 搜索表单数据
      dialogFormVisible: false,  // 取消预订弹框显示状态
      dialogForm: {},  // 取消预订弹框表单数据
      memberList: []  // 用户列表数据
    };
  },
  async created() {
    const vuexStr = localStorage.getItem("vuex")
    const vuexState = JSON.parse(vuexStr);
    this.searchForm.hotelId = vuexState.user.hotelId
    this.fetchData();
    //查询所有用户
    const res = await findAllMemberAPI()
    this.memberList = res.data
  },
  methods: {
    //确认预订
    handleCofirmAppointemnt(row) {
      this.$confirm('此操作将同意用户预订, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await confirmAppointmentAPI(row.id,this.searchForm.hotelId)
        this.$message({
          message: res.message,
          type: res.flag ? "success" : "error",
        });
        this.fetchData()
      }).catch(() => { });
    },
    //点击取消按钮
    handleCancelAppointemnt(row){
      this.dialogFormVisible = true;
      this.dialogForm = { ...row };
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
      let response = await findAppointmentPageAPI(
        this.pageNum,
        this.pageSize,
        this.searchForm
      );
      this.list = response.data.records;
      this.total = response.data.total;
      this.listLoading = false;
    },
    async dialogConfirm() {//点击取消弹框中的确定按钮
      this.dialogForm.remark = '抱歉，此房间已满请重新预订其它房间'
      const res = await cancelAppointmentAPI({...this.dialogForm })
      this.$message({
        message: res.message,
        type: res.flag ? "success" : "error",
      });
      if(res.flag){
        this.dialogFormVisible = false
        this.fetchData();
      }
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
.page-box {
  margin-top: 20px;
}
</style>
