<template>
  <div class="app-container">
    <el-card class="box-table">
      <el-form :inline="true" class="demo-form-inline" :model="searchForm">
        <el-form-item label="备注">
          <el-input placeholder="请输入备注" v-model="searchForm.remark" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" clearable>
            <el-option label="已预订" :value="1"></el-option>
            <el-option label="已入住" :value="2"></el-option>
            <el-option label="已退房" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户">
          <el-select v-model="searchForm.memberId" placeholder="请选择" clearable filterable>
            <el-option v-for="item in memberList" :label="item.name + '-' + item.idcard" :value="item.id"
              :key="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="success" @click="$router.push('/admin/addOrders')">新增入住</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row
        size="small">
        <el-table-column align="center" label="ID" prop="id"></el-table-column>
        <el-table-column align="center" label="用户" prop="member.name"></el-table-column>
        <el-table-column align="center" label="电话" prop="member.phone"></el-table-column>
        <el-table-column align="center" label="身份证" prop="member.idcard" show-overflow-tooltip></el-table-column>
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
            <el-tag v-if="scope.row.status == 1" type="warning">已预订</el-tag>
            <el-tag v-if="scope.row.status == 2" type="success">已入住</el-tag>
            <el-tag v-if="scope.row.status == 3" type="danger">已退房</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="备注" prop="remark" show-overflow-tooltip></el-table-column>
        <el-table-column align="center" label="实付金额" prop="money">
          <template slot-scope="scope">
            ￥{{  scope.row.money *  scope.row.days  }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="100">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status == 1" @click="confirmOrder(scope.row)" type="text"
              size="small">确认入住</el-button>
            <el-button v-if="scope.row.status != 3" @click="checkOutOrder(scope.row)" type="text" size="small">退房</el-button>
            <el-button type="text" size="small" v-if="scope.row.status != 3" @click="openChangeRoomDialog(scope.row)">换房</el-button>
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
    <el-dialog title="退房" :visible.sync="showCheckOutDialog" :before-close="handleCheckOutDialogClose">
      <el-input type="textarea" :rows="4" placeholder="请输入备注(非必须)" v-model="checkoutDialogForm.remark">
      </el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showCheckOutDialog = false">取 消</el-button>
        <el-button type="primary" @click="checkOutDialogConfirm">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="换房" :visible.sync="showChangeRoomDialog" width="400px">
      <el-form :model="changeRoomForm" label-width="80px">
        <el-form-item label="新房间">
          <el-select v-model="changeRoomForm.roomId" placeholder="请选择房间" filterable clearable>
            <el-option
                v-for="room in roomList.filter(r => r.canUse)"
                :key="room.id"
                :label="room.roomNum + ' - ' + room.category.categoryName"
                :value="room.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="showChangeRoomDialog = false">取 消</el-button>
    <el-button type="primary" @click="submitChangeRoom">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>
import {findOrdersPageAPI, removeOrdersAPI, confirmAPI, checkOutAPI, modifyOrdersAPI} from "@/api/orders";
import { findAllMemberAPI } from '@/api/member'
import {findRoomListAPI, modifyRoomAPI} from "@/api/room";
import dayjs from 'dayjs';
export default {
  data() {
    return {
      list: null,
      showChangeRoomDialog: false,
      roomList: [],
      changeRoomForm: {
        id: null,
        roomId: null,
      },
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
        hotelId:''
      },
      memberList: [],
      checkoutDialogForm: {},
      showCheckOutDialog: false
    };
  },
  async created() {
    const vuexStr = localStorage.getItem("vuex")
    const vuexState = JSON.parse(vuexStr);
    this.searchForm.hotelId = vuexState.user.hotelId
    this.dialogForm.hotelId = vuexState.user.hotelId
    this.fetchData();
    //查询所有用户
    const res = await findAllMemberAPI()
    this.memberList = res.data
  },
  methods: {
    handleSizeChange(val) {//每页显示条数改变
      this.pageSize = val;
      this.fetchData();
    },
    async openChangeRoomDialog(row) {
      this.changeRoomForm = {
        id: row.id,
        startDate: dayjs(row.startTime).format('YYYY-MM-DD'),
        days: row.days,
        hotelId : JSON.parse(localStorage.getItem('vuex')).user.hotelId
      }
      this.showChangeRoomDialog = true
      // 获取房间列表（可换的房间）
      const res = await findRoomListAPI(this.changeRoomForm)
      console.log(res.data)
      this.roomList = res.data
    },
    async submitChangeRoom() {
      if (!this.changeRoomForm.roomId) {
        this.$message.warning("请选择新房间")
        return
      }
      const res = await modifyOrdersAPI(this.changeRoomForm)
      this.$message({
        message: res.message,
        type: res.flag ? "success" : "error",
      })
      if (res.flag) {
        this.showChangeRoomDialog = false
        this.fetchData()
      }
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
      let response = await findOrdersPageAPI(
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
      const res = await removeOrdersAPI(row.id);
      this.$message({
        message: res.message,
        type: res.flag ? "success" : "error",
      });
      this.fetchData();
    },
    //确认入住
    confirmOrder(row) {
      this.$confirm('确定入住吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await confirmAPI(row.id)
        this.$message({
          message: res.message,
          type: res.flag ? "success" : "error",
        });
        this.fetchData();
      }).catch(() => { });
    },
    handleCheckOutDialogClose(){
      this.showCheckOutDialog = false
      this.checkoutDialogForm.remark = ''
    },
    //点击退房
    checkOutOrder({id}) {
      this.showCheckOutDialog = true
      this.checkoutDialogForm.id = id
    },
    //确认退房
    async checkOutDialogConfirm() {
      const res = await checkOutAPI(this.checkoutDialogForm)
      this.$message({
        message: res.message,
        type: res.flag ? "success" : "error",
      });
      if(res.flag){
        this.showCheckOutDialog = false
        this.fetchData();
      }
    }
  },
};
</script>

<style lang="less" scoped>
.page-box {
  margin-top: 20px;
}
</style>
