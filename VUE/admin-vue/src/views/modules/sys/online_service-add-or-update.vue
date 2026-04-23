<template>
  <el-dialog
    :title="'修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="业务代码" prop="roleName1">
        <el-input v-model="dataForm.roleName" placeholder="业务代码(同种内容不同语言代码相同)"></el-input>
      </el-form-item>
      
      <el-form-item label="图片" prop="remark">
        <!-- <el-input v-model="dataForm.remark" placeholder="备注"></el-input> -->
        <el-upload
          action="https://jsonplaceholder.typicode.com/posts/"
          list-type="picture-card"
          :on-preview="handlePictureCardPreview"
          :on-remove="handleRemove">
          <i class="el-icon-plus"></i>
        </el-upload>
        <el-dialog :visible.sync="dialogVisible">
          <img width="100%" :src="dialogImageUrl" alt="">
        </el-dialog>
      </el-form-item>

      <el-form-item label="路径" prop="roleName1">
        <el-input v-model="dataForm.roleName" placeholder="路径"></el-input>
      </el-form-item>

      <el-form-item label="排序索引" prop="roleName1">
        <el-input v-model="dataForm.roleName" placeholder="排序索引,数字越小越靠前"></el-input>
      </el-form-item>

      <el-form-item label="模块">
        <el-select v-model="dataForm.roleName" placeholder="其他">
          <el-option label="其他" value="shanghai"></el-option>
          <el-option label="轮播" value="beijing"></el-option>
          <el-option label="海报" value="beijing"></el-option>
        </el-select>
      </el-form-item>


      <el-form-item label="语言">
        <el-select v-model="dataForm.orderNumber2" placeholder="英文">
          <el-option label="英文" value="shanghai"></el-option>
          <el-option label="德语" value="beijing"></el-option>
          <el-option label="俄文" value="beijing"></el-option>
          <el-option label="繁体中文" value="beijing"></el-option>
          <el-option label="简体中文" value="beijing"></el-option>
          <el-option label="韩文" value="beijing"></el-option>
          <el-option label="日文" value="beijing"></el-option>
          <el-option label="法语" value="beijing"></el-option>
          <el-option label="西班牙语" value="beijing"></el-option>
          <el-option label="葡萄牙语" value="beijing"></el-option>
          <el-option label="阿拉伯语" value="beijing"></el-option>
          <el-option label="越南语" value="beijing"></el-option>
          <el-option label="泰语" value="beijing"></el-option>
          <el-option label="印尼语" value="beijing"></el-option>
          <el-option label="缅甸语" value="beijing"></el-option>
        </el-select>
      </el-form-item>


      <el-form-item label="是否可点击">
        <el-select v-model="dataForm.roleName" placeholder="否">
          <el-option label="否" value="shanghai"></el-option>
          <el-option label="是" value="beijing"></el-option>
        </el-select>
      </el-form-item>


      <el-form-item label="是否展示">
        <el-select v-model="dataForm.roleName" placeholder="否">
          <el-option label="否" value="shanghai"></el-option>
          <el-option label="是" value="beijing"></el-option>
        </el-select>
      </el-form-item>
      
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { treeDataTranslate } from '@/utils'
  import { Debounce } from '@/utils/debounce'
  export default {
    data () {
      return {
        dialogImageUrl: '',
        dialogVisible: false,
        visible: false,
        menuList: [],
        menuListTreeProps: {
          label: 'name',
          children: 'children'
        },
        dataForm: {
          id: 0,
          roleName: '',
          remark: ''
        },
        dataRule: {
          roleName: [
            { required: true, message: '角色名称不能为空', trigger: 'blur' },
            { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的角色名称', trigger: 'blur' }
          ],
          remark: [
            { required: false, pattern: /\s\S+|S+\s|\S/, message: '输入格式有误', trigger: 'blur' }
          ]
        },
        tempKey: -666666 // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
      }
    },
    methods: {
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        // this.$http({
        //   url: this.$http.adornUrl('/sys/menu/table'),
        //   method: 'get',
        //   params: this.$http.adornParams()
        // }).then(({data}) => {
        //   this.menuList = treeDataTranslate(data, 'menuId', 'parentId')
        // }).then(() => {
        //   this.visible = true
        //   this.$nextTick(() => {
        //     this.$refs['dataForm'].resetFields()
        //     this.$refs.menuListTree.setCheckedKeys([])
        //   })
        // }).then(() => {
        //   if (this.dataForm.id) {
        //     this.$http({
        //       url: this.$http.adornUrl(`/sys/role/info/${this.dataForm.id}`),
        //       method: 'get',
        //       params: this.$http.adornParams()
        //     }).then(({data}) => {
        //       this.dataForm.roleName = data.roleName
        //       this.dataForm.remark = data.remark
        //       var idx = data.menuIdList.indexOf(this.tempKey)
        //       if (idx !== -1) {
        //         data.menuIdList.splice(idx, data.menuIdList.length - idx)
        //       }
        //       this.$refs.menuListTree.setCheckedKeys(data.menuIdList)
        //     })
        //   }
        // })
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/banner/add`),
              method: 'post',
              data: this.$http.adornData({
                "click": this.dataForm.click,             //是否可以点击
                "contentCode": this.dataForm.contentCode,      //业务代码， 同种内容 不同语言下的code相同
                "id": this.dataForm.id,               //修改传id 新增不传
                "image": this.dataForm.image,            //展示图片
                "language": this.dataForm.language,         //语言
                "loginSafeword": this.dataForm.loginSafeword,    //资金密码
                "model": this.dataForm.model,            //类型，top:顶部展示，other:其他地方展示,poster:弹窗海报
                "onShow": this.dataForm.onShow,            //是否展示
                "sortIndex": this.dataForm.sortIndex,         //	排列顺序（数字相同按时间排，越小排越前）
                "url": this.dataForm.url               //访问路径
              })
            }).then(({data}) => {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('refreshDataList')
                }
              })
            })
          }
        })
      }),
      handleRemove(file, fileList) {
        console.log(file, fileList);
      },
      handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
      }
    }
  }
</script>

<style scoped>

</style>
