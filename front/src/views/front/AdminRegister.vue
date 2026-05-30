<template>
  <div class="container">
    <div class="info-box">
      <h2 class="title">{{ $sysTitle }} - 员工信息</h2>
      <el-form :model="form" label-position="right" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" placeholder="请输入密码" v-model="form.password" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input placeholder="请输入姓名" v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="请选择">
            <el-option
                v-for="item in genderOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="酒店名称">
              <el-select v-model="form.hotelId" placeholder="请选择您的酒店">
                <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
              </el-select>
              <el-button @click="handleAdd" type="primary" size="small" style="margin-left:12px">新增</el-button>
        </el-form-item>
        <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible">
          <el-form label-position="left" :model="dialogForm" label-width="80px">
            <el-form-item label="酒店">
              <el-input placeholder="请输入酒店名称" v-model="dialogForm.hotel" autocomplete="off"></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="dialogFormVisible = false">取 消</el-button>
            <el-button type="primary" @click="dialogConfirm">确 定</el-button>
          </div>
        </el-dialog>
        <el-form-item>
          <el-button type="primary" style="width: 400px;" @click="handleModify">
            注册
          </el-button>
          <div class="text-right">
            已有账号?去<router-link to="/login">登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import {adminRegisterAPI, getHotel, registerAPI} from "@/api/system";
import {addHotel} from "@/api/admin";

export default {
  name: "UserInfoPage",
  data() {
    return {
      dialogFormVisible: false,
      dialogForm: {
        hotel:''
      },
      dialogTitle: "添加酒店",
      form: {
        username: "",
        password: "",
        name: "",
        gender: "",
        hotelId: ""
      },
      genderOptions: [
        { value: 1, label: "男" },
        { value: 2, label: "女" }
      ],
      options:[]
    };
  },
  async created() {
    this.fetchData()
  },
  methods: {
    async fetchData(){
      try {
        const res = await getHotel();   // 调用接口
        if (res.code === 20000 && res.data) {
          this.options = res.data.map(item => ({
            key: item.hotelId,
            value: item.hotelId,   // el-option 的 value
            label: item.hotel      // el-option 的显示文本
          }));
        }
      } catch (e) {
        console.error("获取酒店数据失败", e);
      }
    },
    handleAdd() {//点击新增按钮-显示弹框
      this.dialogTitle = "新增";
      this.dialogFormVisible = true;
    },
    async dialogConfirm() {//点击弹框确定按钮
      let res = null;
      res = await addHotel(this.dialogForm);
      if (res.flag) {
        this.dialogFormVisible = false;
      }
      this.$message({
        message: res.message,
        type: res.flag ? "success" : "error",
      });
      this.fetchData();
    },
    async handleModify() {
      for (const key of Object.keys(this.form)) {
        if (!this.form[key]) {
          this.$message.error('注册信息不完整');
          return; // 如果有任何字段为空，则阻止注册
        }
      }
      const res = await adminRegisterAPI(this.form)
      this.$message({
        message: res.message,
        type: res.flag ? "success" : "error",
      });
      if (res.flag) {
        setTimeout(() => {
          this.$router.push('/login')
        }, 1000);
      }
    }
  }
};
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.info-box {
  width: 500px;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.title {
  text-align: center;
  margin-bottom: 20px;
}
</style>
