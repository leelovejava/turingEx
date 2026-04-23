<template>
  <div>

    <el-container class="editor-container">
      <div class="no-padding textarea main-editor padding0">
        <div style="min-width: 38px;width: 38px;height: 38px;background-color: #fff;cursor: pointer;" 
          @click="$refs.restFile.click()">
          <img src="~@/assets/img/upload.png" style="width: 38px;height: 38px;"/>
        </div>
        <textarea class="box-sizing1"
          ref="textarea"
          v-model.trim="editorText"
          rows="1"
          placeholder="请输入消息..."
          @keydown="keydownEvent($event)"
          @input="inputEvent($event)"
        ></textarea>
        <div style="min-width: 38px;width: 38px;height: 38px;background-color: #fff;cursor: pointer;"
          @click="SendText()">
          <img v-if="this.$store.state.talks.sendStatus == 0" src="~@/assets/img/send.png" style="width: 38px;height: 38px;"/>
          <div v-if="this.$store.state.talks.sendStatus == 1" class="loading"></div>
        </div>
      </div>
      <form
          enctype="multipart/form-data"
          style="display: none"
          ref="fileFrom"
        >
        <input
          type="file"
          ref="restFile"
          accept="image/*"
          @change="uploadImageChange"
        />
        <input type="file" ref="restFile2" @change="uploadFileChange" />
      </form>
    </el-container>
  </div>
</template>

<script>
// import MeEditorEmoticon from './MeEditorEmoticon'
// import MeEditorFileManage from './MeEditorFileManage'
import MeEditorImageView from './MeEditorImageView'
// import MeEditorRecorder from './MeEditorRecorder'
// import MeEditorVote from './MeEditorVote'
// import MeEditorLocation from './MeEditorLocation'
// import TalkCodeBlock from '@/components/chat/TalkCodeBlock'
import { getPasteImgs, getDragPasteImg } from '@/utils/editor'
import { findTalk } from '@/utils/talk'
import store from '@/store'
import {
  ServeSendTalkCodeBlock,
  ServeSendTalkImage,
  ServeSendEmoticon,
} from '@/api/chat'

export default {
  name: 'MeEditor',
  components: {
    MeEditorImageView,
  },
  computed: {
    talkUser() {
      return this.$store.state.dialogue.index_name
    },
    documentClientHeight: {
      get () { return this.$store.state.common.documentClientHeight },
      set (val) { this.$store.commit('common/updateDocumentClientHeight', val) }
    },
  },
  watch: {
    talkUser(n_index_name) {
      this.$refs.filesManager.clear()
      this.editorText = this.getDraftText(n_index_name)
    },
  },
  data() {
    return {
      // 当前编辑的内容
      editorText: '',

      // 图片查看器相关信息
      imageViewer: {
        isShow: false,
        file: null,
      },

      codeBlock: {
        isShow: false,
        editMode: true,
      },

      filesManager: {
        isShow: false,
      },

      vote: {
        isShow: false,
      },

      // 录音器
      recorder: false,

      // 上次发送消息的时间
      sendtime: 0,

      // 发送间隔时间（默认1秒）
      interval: 1000,
    }
  },
  created() {
  },
  mounted () {
    this.resetDocumentClientHeight()
  },
  methods: {
    // 读取对话编辑草稿信息
    getDraftText(index_name) {
      return findTalk(index_name).draft_text || ''
    },

    //复制粘贴图片回调方法
    pasteImage(e) {
      let files = getPasteImgs(e)
      if (files.length == 0) return

      this.openImageViewer(files[0])
    },

    //拖拽上传图片回调方法
    dragPasteImage(e) {
      let files = getDragPasteImg(e)
      if (files.length == 0) return

      this.openImageViewer(files[0])
    },

    inputEvent(e) {
      this.$emit('keyboard-event', e.target.value)
    },

    // 键盘按下监听事件
    keydownEvent(e) {
      return;
      if (e.keyCode == 13 && this.editorText == '') {
        e.preventDefault()
      }

      // 回车发送消息
      if (e.keyCode == 13 && e.shiftKey == false && this.editorText != '') {
        let currentTime = new Date().getTime()

        if (this.sendtime > 0) {
          // 判断 1秒内只能发送一条消息
          if (currentTime - this.sendtime < this.interval) {
            e.preventDefault()
            return false
          }
        }

        this.$emit('send', this.editorText)
        this.editorText = ''
        this.sendtime = currentTime
        e.preventDefault()
      }
    },
    SendText(){
      if(this.$store.state.talks.sendStatus == 1){return;}
      this.$store.commit('SET_SEND_STATUS', 1)
      let timer = setTimeout(() => {
            clearTimeout(timer);
            this.$store.commit('SET_SEND_STATUS', 0);
        },5000);
      let currentTime = new Date().getTime()

        if (this.sendtime > 0) {
          // 判断 1秒内只能发送一条消息
          if (currentTime - this.sendtime < this.interval) {
            // e.preventDefault()
            return false
          }
        }

        this.$emit('send', this.editorText)
        this.editorText = ''
        this.sendtime = currentTime
        // e.preventDefault()
    },

    // 选择图片文件后回调方法
    uploadImageChange(e) {
      let file = e.target.files[0]
      console.log(file)


      const isLt2M = file.size / 1024 / 1024 < 10;
        if (!isLt2M) {
            Toast.fail('上传图片大小不能超过 10MB!');
            return false;
        }

      let fileData = new FormData()
      fileData.append('file', file)
      //
      this.$http({
        url: this.$http.adornUrl('/api/uploadFile'),
        method: 'post',
        data: fileData,
        headers: {
          'Content-Type': 'multipart/form-data' // 设置请求头为 multipart/form-data
        }
      }).then(({ data }) => {
        //
        console.log("uploaded image = " + JSON.stringify(data));
        if(data.code == 0){
          this.$bus.$emit('SendMessage',{type:"img",content:""+data.data.httpUrl})
        }else{
          this.$message(''+data.msg)
        }
        //
      })
      .finally(() => {
      })
      //
      // this.openImageViewer(file)
      // this.openNewImageViewer(e);
      //
      // let file = e.target.files[0]
      // console.log("file = " + file);
      // this.imageViewer.isShow = true
      // this.imageViewer.file = file
      // store.commit('SET_IMAGE_VIEWER', this.imageViewer)
      // this.$bus.$emit('SET_IMAGE_VIEWER', {file:file,isShow:true})
      //
      // this.$refs.restFile.value = null
    },

    // 选择文件回调事件
    uploadFileChange(e) {
      let maxsize = 100 * 1024 * 1024
      if (e.target.files.length == 0) {
        return false
      }

      let file = e.target.files[0]
      if (/\.(gif|jpg|jpeg|png|webp|GIF|JPG|PNG|WEBP)$/.test(file.name)) {
        this.openImageViewer(file)
        return
      }

      if (file.size > maxsize) {
        this.$notify.info({
          title: '消息',
          message: '上传文件不能大于100M',
        })
        return
      }

      this.filesManager.isShow = true
      this.$refs.restFile2.value = null
      this.$refs.filesManager.upload(file)
    },

    // 打开图片查看器
    openImageViewer(file) {
      console.log("file = " + file);
      this.imageViewer.isShow = true
      this.imageViewer.file = file
      // store.commit('SET_IMAGE_VIEWER', this.imageViewer)
      this.$bus.$emit('SET_IMAGE_VIEWER', {file:file,isShow:true})
    },

    // 打开图片查看器
    openNewImageViewer(e) {
      console.log("e = " + JSON.stringify(e));
      let file = e.target.files[0]
      console.log("file = " + file);
      this.imageViewer.isShow = true
      this.imageViewer.file = file
      // store.commit('SET_IMAGE_VIEWER', this.imageViewer)
      this.$bus.$emit('SET_IMAGE_VIEWER', {file:file,isShow:true})
    },

    sendRecorder(file) {
      this.recorder = false
      this.$refs.filesManager.upload(file)
    },

    // 代码块编辑器确认完成回调事件
    confirmCodeBlock(data) {
      const { talk_type, receiver_id } = this.$store.state.dialogue
      ServeSendTalkCodeBlock({
        talk_type,
        receiver_id,
        code: data.code,
        lang: data.language,
      }).then(res => {
        if (res.code == 200) {
          this.codeBlock.isShow = false
        } else {
          this.$notify({
            title: '友情提示',
            message: res.message,
            type: 'warning',
          })
        }
      })
    },

    // 确认上传图片消息回调事件
    confirmUploadImage() {
      const { talk_type, receiver_id } = this.$store.state.dialogue

      let fileData = new FormData()
      fileData.append('talk_type', talk_type)
      fileData.append('receiver_id', receiver_id)
      fileData.append('image', this.imageViewer.file)

      let ref = this.$refs.imageViewer

      ServeSendTalkImage(fileData)
        .then(res => {
          ref.loading = false
          if (res.code == 200) {
            ref.closeBox()
          } else {
            this.$notify({
              title: '友情提示',
              message: res.message,
              type: 'warning',
            })
          }
        })
        .finally(() => {
          ref.loading = false
        })
    },

    // 选中表情包回调事件
    selecteEmoticon(data) {
      if (data.type == 1) {
        let value = this.editorText
        let el = this.$refs.textarea
        let startPos = el.selectionStart
        let endPos = el.selectionEnd
        let newValue =
          value.substring(0, startPos) +
          data.value +
          value.substring(endPos, value.length)

        this.editorText = newValue

        if (el.setSelectionRange) {
          // setTimeout(() => {
          //   let index = startPos + data.value.length
          //   el.setSelectionRange(index, index)
          //   el.focus()
          // }, 0)
        }
      } else {
        const { talk_type, receiver_id } = this.$store.state.dialogue
        ServeSendEmoticon({
          talk_type,
          receiver_id,
          emoticon_id: data.value,
        })
      }

      this.$refs.popoverEmoticon.doClose()
    },

    sendGeoLocation() {},


    siteContentViewHeight () {
      var height = 1280 - 50
     
      return { minHeight: height + 'px' }
    },
    // 重置窗口可视高度
    resetDocumentClientHeight () {
      this.documentClientHeight = document.documentElement['clientHeight'] - 80
      window.onresize = () => {
        this.documentClientHeight = document.documentElement['clientHeight'] - 80
      }
      document.documentElement.clientHeight
    },
  },
}
</script>
<style scoped>
.editor-container {
  height: 90px;
  width: 100%;
  background-color: #EDEDED;
}

.editor-container .toolbar {
  line-height: 35px;
  border-bottom: 1px solid #f5f0f0;
  border-top: 1px solid #f5f0f0;
}

.editor-container .toolbar li {
  list-style: none;
  float: left;
  width: 35px;
  margin-left: 3px;
  cursor: pointer;
  text-align: center;
  line-height: 35px;
  position: relative;
  color: #8d8d8d;
}

.editor-container .toolbar li .tip-title {
  display: none;
  position: absolute;
  top: 38px;
  left: 0px;
  height: 26px;
  line-height: 26px;
  background-color: rgba(31, 35, 41, 0.9);
  color: white;
  min-width: 30px;
  font-size: 10px;
  padding: 0 5px;
  border-radius: 2px;
  white-space: pre;
  text-align: center;
  user-select: none;
  z-index: 1;
}

.editor-container .toolbar li:hover .tip-title {
  display: block;
}

.editor-container .toolbar li:hover {
  background-color: #f7f5f5;
}

.editor-container .toolbar .text-tips {
  float: right;
  margin-right: 15px;
  font-size: 12px;
  color: #ccc;
}

.editor-container .toolbar .text-tips i {
  font-size: 14px;
  cursor: pointer;
  margin-left: 5px;
  color: rgb(255, 181, 111);
}

.editor-container .textarea {
  overflow: hidden;
  position: relative;
}

textarea {
  /* width: calc(100% - 80px);
  width: -moz-calc(100% - 80px);
  width: -webkit-calc(100% - 80px);
  height: calc(100% - 10px);
  height: -moz-calc(100% - 10px);
  height: -webkit-calc(100% - 10px); */
  border: 0 none;
  outline: none;
  resize: none;
  font-size: 15px;
  /* overflow-y: auto; */
  color: #464545;
  /* padding: 5px; */
  position: relative;
  /* margin-top: -20px; */
  width: calc(100vw - 80px);
  height: 90px;

}

textarea::-webkit-scrollbar {
  width: 4px;
  height: 1px;
}

textarea::-webkit-scrollbar-thumb {
  background: #d5cfcf;
}

textarea::-webkit-scrollbar-track {
  background: #ededed;
}

textarea::-webkit-input-placeholder {
  color: #dccdcd;
  font-size: 12px;
  font-weight: 400;
}

/* 编辑器文档说明 --- start */
.editor-books .books-title {
  font-size: 16px;
  height: 30px;
  line-height: 22px;
  margin-top: 10px;
  margin-bottom: 10px;
  border-bottom: 1px solid #cbcbcb;
  color: #726f6f;
  font-weight: 400;
  margin-left: 11px;
}

.editor-books p {
  text-indent: 10px;
  font-size: 12px;
  height: 30px;
  line-height: 30px;
  color: #7f7c7c;
}

input::-webkit-input-placeholder,
textarea::-webkit-input-placeholder {
    color: #AFB1B5;
    font-size: 14px; 
    height: 90px;line-height: 90px; 
}
input::-moz-placeholder, 
textarea::-moz-placeholder {  /* Mozilla Firefox 19+ */
    color: #AFB1B5;
    font-size: 14px;  
    height: 90px;line-height: 90px;
}
input:-moz-placeholder, 
textarea:-moz-placeholder {  /* Mozilla Firefox 4 to 18 */
    color: #AFB1B5;
    font-size: 14px;
    height: 90px;line-height: 90px;
}
input:-ms-input-placeholder, 
textarea:-ms-input-placeholder {  /* Internet Explorer 10-11 */
    color: #AFB1B5;
    font-size: 14px;  
    height: 90px;line-height: 90px;
}

.main-editor{
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fff;
  width: auto;
}

.padding0{
  padding: 0;
}

.box-sizing1 {
  box-sizing: none; /* 1 */
}


*,
*:before,
*:after {
  box-sizing: none;
}

/* 编辑器文档说明 --- end */

.loading {
  position: relative;
  left: 0%;
  top: 0%;
  width: 34px;
  height: 34px;
  border: 2px solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;

  animation: circle infinite 0.75s linear;
}

@keyframes circle {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}

</style>
