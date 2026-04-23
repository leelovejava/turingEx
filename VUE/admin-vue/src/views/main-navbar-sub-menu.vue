
<template>
  <!-- :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"  -->
  <div :class="returnClass(menu)" >
    <el-submenu  class="ddd"
      v-if="menu.list && menu.list.length >= 1"
      :index="menu.menuId + ''"
      :popper-append-to-body="false"  
      >
      <!--  class="site-sidebar__menu-icon" -->
      <template slot="title">
        <icon-svg :name="menu.icon || ''" ></icon-svg>
        <span>
            {{ menu.name }}
        </span>
        <el-badge class="mark" :value="countNum(menu.name+'_'+menu.type)" v-if="countNum(menu.name+'_'+menu.type) > 0"/>
      </template>
      <main-navbar-sub-menu
        v-for="item in menu.list" 
        :key="item.menuId"
        :menu="item"
        :type=1
        :dynamicMenuRoutes="dynamicMenuRoutes">{{ item.name }}
      </main-navbar-sub-menu>
    </el-submenu>
    <el-menu-item v-else-if="!(menu.name.indexOf('ROOT')>=0&&userName!='root')" :index="menu.menuId + ''"  :popper-append-to-body="false"  @click="gotoRouteHandle(menu)" style="background-color: rgb(255, 255, 255);">
      <icon-svg :name="menu.icon || ''" class="site-sidebar__menu-icon"></icon-svg>
      <span>{{ menu.name}}</span>
      <el-badge class="mark" :value="countNum(menu.name+'_'+menu.type)" v-if="countNum(menu.name+'_'+menu.type) > 0"/>
    </el-menu-item>
  </div>
  <!-- <el-submenu :index="menu.menuId+''">   && (type == 1 || menu.orderNum > 4)
    <template slot="title">标题</template>
    <el-menu-item index="2-1">选项1</el-menu-item>
    <el-menu-item index="2-2">选项2</el-menu-item>
    <el-menu-item index="2-3">选项3</el-menu-item>
  </el-submenu> -->
</template>

<script>

  import MainNavbarSubMenu from './main-navbar-sub-menu'
  export default {
    name: 'main-navbar-sub-menu',
    props: {
      menu: {
        type: Object,
        required: true
      },
      dynamicMenuRoutes: {
        type: Array,
        required: true
      },
      type:0
    },
    components: {
      MainNavbarSubMenu
    },
    computed: {
      sidebarLayoutSkin: {
        get () { return this.$store.state.common.sidebarLayoutSkin }
      },
      userName: {
        get() {
          return this.$store.state.user.name;
        },
      },
    },
    methods: {
      // 通过menuId与动态(菜单)路由进行匹配跳转至指定路由
      gotoRouteHandle (menu) {
        var route = this.dynamicMenuRoutes.filter(item => item.meta.menuId === menu.menuId)
        console.log(menu)
        if (route.length >= 1) {
          this.$router.push({ name: route[0].name })
        }
      },
      countNum(name){
        if(!this.menuMap){
          // console.log(name + "-->");
          return 0;
        }
        let type = this.menuMap[name];
        let num = this.main.tips[type]
        if(isNaN(num)){
          num = 0
        }
        // console.log(name + "-->" + type + "-->" + num);
        return num;
      },
      returnClass(menu){
        //一级
        if(menu.parentId == 0){
          return 'div0'
        }
        //二级
        if(menu.list && menu.list.length >= 1){
          return 'div0-1'
        }
        //目录
        if(menu.type == 0){
          return 'div0-2'
        }
        //三级
        return 'div1';
      },
      returnClass2(menu){
        //一级
        if(menu.parentId == 0){
          return 'ddd'
        }
        //二级
        if(menu.list && menu.list.length >= 1){
          return 'ddd'
        }
        //三级
        return 'ddd';
      }
    },
    isOneType(){
      return this.type == 1
    }
  }
</script>

<!-- scoped -->
<style lang="scss" scoped>
.div0-1{
  // display:inline-block;
  // margin-left: 300px ;
  display:block
}

.div0-2{
  // display:inline-block;
  // margin-left: 300px ;
  display:block;
  border-top:1px solid rgb(237 238 239);
  font-weight: 600;
}

// .div0-1:hover .el-menu-item{
//   background: rgba(2,161,233,0.1) !important;
//   color: #02A1E9 !important;
// ;
// }

// .div0-1:hover .el-menu-item{
//   background: rgba(2,161,233,0.1) !important;
//   color: #0230e9 !important;
// ;
// }

.div0{
  display:inline-block
}
.div1{
  display:block
}
.div1:hover .el-menu-item{
  background: rgba(2,161,233,0.1) !important;
  color: #02A1E9 !important;
}
.ddd{
  ::v-deep .el-submenu__title{
    color: #fff;
  }
  ::v-deep .el-submenu__icon-arrow{
    right: 5px;
    top: 56%;
    color: #fff;
  }
}
::v-deep .el-submenu__title:hover{
  background: rgba(2,161,233,0.1) !important;
  color: #02A1E9 !important;
}
::v-deep .el-menu--horizontal{
  // left: 483px !important;
}
.ddd0-1{
  ::v-deep .el-submenu__title{
    color: #02A1E9 !important;
  }
  ::v-deep .el-submenu__icon-arrow{
    right: 5px;
    top: 56%;
    color: #fff;
  }
}

.ddd0-2{
  ::v-deep .el-submenu__title{
    color: #02A1E9 !important;
  }
  ::v-deep .el-submenu__icon-arrow{
    right: 5px;
    top: 56%;
    color: #fff;
  }
}
// .div0{
//   width: 120px;
// }
/* .ddd [class*=" el-icon-arrow"], [class^=el-icon-arrow]{
  display: none !important;
} */
</style>
