<template>
    <div class="page-container">
        <el-card>
            <div class="section">
                <div slot="header" class="clearfix">
                    <span>办理入住</span>
                </div>
                <el-form :inline="true" :model="formInline" class="mt-40">
                    <el-form-item label="入住日期">
                        <el-date-picker size="mini" disabled :clearable="false" :picker-options="pickerOptions"
                            v-model="formInline.startDate" type="date" placeholder="入住日期" value-format="yyyy-MM-dd"
                            format="yyyy-MM-dd">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="房型">
                        <el-select size="mini" v-model="formInline.categoryId" placeholder="房型" clearable>
                            <el-option v-for="item in categoryList" :label="item.categoryName" :value="item.id"
                                :key="item.id"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select size="mini" v-model="formInline.canUse" placeholder="状态" clearable>
                            <el-option label="可入住" :value="true"></el-option>
                            <el-option label="已满房" :value="false"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button size="mini" type="primary" @click="onSubmit">查询</el-button>
                    </el-form-item>
                </el-form>
                <div class="section-list">

                    <div v-for="item in roomList" :key="item.id" class="section-item">
                        <img :src="$baseFileUrl + item.category.photo" class="hotel-img">
                        <div class="hotel-title">{{ item.category.categoryName }}-{{ item.roomNum }}</div>
                        <div class="flex justify-between item-center w-full">
                            <el-tag size="mini" type="success" v-if="item.canUse">可入住</el-tag>
                            <el-tag size="mini" type="danger" v-else>已满房</el-tag>
                            <div class="hotel-price">￥{{ item.category.price }}</div>
                        </div>
                        <div class="text-right w-full">
                            <el-button v-if="item.canUse" type="warning" size="mini" @click="handleAdd(item)">入住</el-button>
                        </div>
                    </div>


                    <!-- 多写两个标签解决flex最后一行对齐问题 -->
                    <div class="section-item-empty"></div>
                    <div class="section-item-empty"></div>
                </div>
            </div>
        </el-card>

        <el-dialog title="办理入住" :visible.sync="dialogVisible" width="500px" :before-close="handleClose">
            <el-form ref="form" :model="form" label-width="80px">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="房型">
                            <el-tag size="mini" type="info">{{ form.categoryName }}</el-tag>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="房号">
                            <el-tag size="mini" type="info">{{ form.roomNum }}</el-tag>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="入住时间">
                            <el-tag size="mini" type="info">今天</el-tag>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="居住天数">
                            <el-input type="number" size="mini" v-model="form.days"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="姓名">
                            <el-input size="mini" v-model="form.name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="性别">
                            <el-select size="mini" v-model="form.gender" placeholder="请选择">
                                <el-option v-for="item in genderOptions" :key="item.value" :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="电话">
                            <el-input size="mini" v-model="form.phone"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="身份证">
                            <el-input size="mini" v-model="form.idcard"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="已付金额">
                            <el-tag size="mini" type="info">{{ form.money }}</el-tag>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button size="mini" type="primary" @click="handleConfirm">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import {findAllCategoryAPI, findHotelCategoryAPI} from '@/api/category'
import { findRoomListAPI } from '@/api/room'
import { addOrdersAPI } from '@/api/orders'
import dayjs from 'dayjs'
export default {
    name: 'FrontCategory',

    data() {
        return {
            pickerOptions: {
                disabledDate: this.disabledDate
            },
            formInline: {
                startDate: dayjs().format('YYYY-MM-DD'),
                categoryId: null,
                canUse: null,
                hotelId: JSON.parse(localStorage.getItem('vuex')).user.hotelId
            },
            categoryList: [],
            roomList: [],
            form: {  // 添加这个属性
                gender: 1,
                categoryName: '',
                days: 1,
                name: '',
                phone: '',
                idcard: '',
                money: '',
                roomId: '',
                roomNum: '',
                hotelId:''
            },
            dialogVisible: false,
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
        };
    },

    async mounted() {
      const vuexStr = localStorage.getItem("vuex")
      const vuexState = JSON.parse(vuexStr);
      this.form.hotelId = vuexState.user.hotelId
        if (this.$route.query?.categoryId) {
            this.formInline.categoryId = parseInt(this.$route.query?.categoryId)
        }
        this.getCategoryList()
        this.getList()
    },

    methods: {
        handleClose() {
            this.dialogVisible = false
            this.form = {
                gender: 1,
                categoryName: '',
                days: 1
            }
        },
        disabledDate(time) {
            //禁用今天之前的日期
            return dayjs(time).isBefore(dayjs(), 'day')
        },
        async getCategoryList() {
            const { data } = await findHotelCategoryAPI(JSON.parse(localStorage.getItem('vuex')).user.hotelId)
            this.categoryList = data
        },
        async getList() {
            const { data } = await findRoomListAPI(this.formInline)
            this.roomList = data;
        },
        onSubmit() {
            this.getList()
        },
        //点击入住按钮
        handleAdd({ category, roomNum, id }) {
            this.form.money = category.price * this.form.days;
            this.form.roomId = id
            this.form.roomNum = roomNum
            this.form.categoryName = category.categoryName
            this.dialogVisible = true
        },
        //点击弹框中的确认
        async handleConfirm() {
            if(this.form.days == null || this.form.days == ""){
                this.$message({
                    message: "居住天数不能为空",
                    type: "error",
                });
                return
            }
            if(this.form.name == null || this.form.name == ""){
                this.$message({
                    message: "姓名不能为空",
                    type: "error",
                });
                return
            }
            if(this.form.phone == null || this.form.phone == ""){
                this.$message({
                    message: "电话不能为空",
                    type: "error",
                });
                return
            }
            if(this.form.idcard == null || this.form.idcard == ""){
                this.$message({
                    message: "身份证不能为空",
                    type: "error",
                });
                return
            }
            const res = await addOrdersAPI(this.form)
            this.$message({
                message: res.message,
                type: res.flag ? "success" : "error",
            });
            if (res.flag) {
                this.dialogVisible = false
                setTimeout(() => {
                    this.$router.push('/admin/orders')
                }, 1000);
            }
        }
    },
};
</script>

<style lang="less" scoped>
.page-container {
    margin: 20px auto;

    .section {
        .section-title {
            background-color: #303643;
            color: #fff;
            padding: 16px;
            border-radius: 6px;
            text-align: center;
        }

        .section-list {
            margin-bottom: 20px;
            display: flex;
            flex-wrap: wrap;
            justify-content: space-between;

            // //重要 解决最后一行两端对齐的问题
            &::after {
                content: '';
                // flex: 1;
                width: 290px;
            }

            .section-item-empty {
                width: 290px;
            }

            .section-item {
                // width: 290px;
                width: 290px;
                height: 320px;
                padding: 16px;
                box-sizing: border-box;
                border-radius: 6px;
                margin-bottom: 10px;
                // background-color: antiquewhite;

                border: 1px solid #DCDFE6;
                display: flex;
                flex-direction: column;
                align-items: center;

                // margin-left: 10px;
                // 等差数列(n-1)*4+1：1 4 9 13 左边距0 每行的第一个元素
                &:nth-child(4n-3) {
                    // background-color: deepskyblue;
                    margin-left: 0 !important;
                }

                .hotel-img {
                    width: 100%;
                    height: 180px;
                }

                .hotel-title {
                    font-weight: bold;
                    width: 100%;
                    margin: 10px;
                    text-overflow: ellipsis;
                    overflow: hidden;
                    white-space: nowrap;
                }

                .hotel-price {
                    color: red;
                    width: 100%;
                    margin: 4px;
                    text-align: right;
                }

            }
        }
    }
}
</style>