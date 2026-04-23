<template>
  <div class="mod-transport">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:user:root')"
          @click.stop="getUpdateSuperGoogleAuth()"
        >
          超级谷歌验证器
        </el-button>
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:user:root')"
          @click.stop="getAdminGoogleAuth()"
        >
          admin谷歌验证器
        </el-button>
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:user:root')"
          @click.stop="backupDB()"
        >
          备份数据库
        </el-button>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:user:update')"
          @click.stop="addOrUpdateHandle(scope.row, scope.$index)"
        >
          编辑
        </el-button>
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改  -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      :dataList="dataList"
      :currentPage="page.currentPage"
      :pageSize="page.pageSize"
      @refreshDataList="refreshDataList"
    ></add-or-update>
     <!-- 谷歌验证 -->
     <add-or-gogle
      v-if="UpdateGogle"
      ref="UpdateGogle"
      @refreshDataList="getDataList"
    ></add-or-gogle>
  </div>
</template>

<script>
import { tableOption } from "@/crud/sys/root";
import AddOrUpdate from "./root-sys-config-add-or-update";
import AddOrGogle from "./root-sys-googleAuthCode";
export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      UpdateGogle: false,
      addOrUpdateVisible: false,
      tableOption: tableOption,
      adminGoog:'',
      rootGoog:'',
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      searchParams: {}, // 搜索条件
    };
  },
  components: {
    AddOrUpdate,
    AddOrGogle
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      this.dataListLoading = true;
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.$http({
        url: this.$http.adornUrl("/normal/adminSysparaAction!/list.action"),
        method: "get",
        params: this.$http.adornParams(params),
      }).then(({ data }) => {
        this.dataList = data.data.records;
        this.page.total = data.data.total;

        // 更新当前页，确保与实际数据一致
        if (page != null) {
          this.page.currentPage = page.currentPage;
        }

        this.dataListLoading = false;

        // 激活事件，发送数据
        this.$bus.$emit("root2-sys-config", {});

        if (done) {
          done();
        }
      });
    },
    // 刷新数据列表
    refreshDataList() {
      this.getDataList(this.page);
    },
    // 条件查询
    searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      this.searchParams = params;
      this.getDataList(this.page, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    // 新增 / 修改
    addOrUpdateHandle(data, index) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(data, index);
      });
    },
    addOrUpdateGogle(googleAuthBind,n,m) { // n判断是admin还是超级谷歌 m true=解绑  false = 绑定
      this.UpdateGogle = true;
      this.$nextTick(() => {
        this.$refs.UpdateGogle.init(googleAuthBind,n,m);
      });
    },
    getUpdateSuperGoogleAuth() {
      this.$http({
        url: this.$http.adornUrl( //获取系统配置-超级谷歌验证码绑定状态
          "/adminGoogleAuthAction/getUpdateSuperGoogleAuth"
        ),
        method: "get",
        params: this.$http.adornParams({}),
      }).then(({ data }) => {
        this.rootGoog = data.data.googleAuthBind
        this.goBind(this.rootGoog,'超级')
      });
    },
    getAdminGoogleAuth() {
      this.$http({
        url: this.$http.adornUrl( //获取admin谷歌验证器绑定状态
          "/adminGoogleAuthAction/getAdminGoogleAuth"
        ),
        method: "get",
        params: this.$http.adornParams({}),
      }).then(({ data }) => {
        if(data.code == 0){
          this.adminGoog = data.data.googleAuthBind
          this.goBind(this.adminGoog,'admin')
        }
      });
    },
    goBind(googleAuthBind,n){
      if (googleAuthBind) {
        this.$confirm(n + "谷歌验证器已绑定", "谷歌验证器", {
          distinguishCancelAndClose: true,
          confirmButtonText: "确定",
          cancelButtonText: "解绑",
          type: "success",
        })
          .then(() => {})
          .catch((action) => {
            if (action === "cancel") {
              this.addOrUpdateGogle(googleAuthBind,n,true);
            }
          });
      } else {
        this.addOrUpdateGogle(googleAuthBind,n,false);
      }
    },
    backupDB() {
      this.$confirm(
        `确定备份数据库操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/normal/adminSysparaAction!/backupDB"),
            method: "post",
            data: this.$http.adornData({}),
          }).then(({ data }) => {
            if(data.code == 0){
              this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
              }
            });
            }else{
              this.$message({
              message:data.msg,
              type: "error",
              duration: 1500,
              onClose: () => {
              }
            });
            }
          });
        })
        .catch(() => {});
    }
  },
};
</script>
