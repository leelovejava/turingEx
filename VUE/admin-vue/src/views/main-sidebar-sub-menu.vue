<template>
 <!-- 左侧菜单组件 -->
  <el-submenu 
    v-if="menu.list && menu.list.length >= 1"
    :index="menu.menuId + ''"
    :popper-append-to-body="false"
    :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'">
    <template slot="title">
      <icon-svg :name="menu.icon || ''" class="site-sidebar__menu-icon"></icon-svg>
      <span>{{ menu.name }}</span>
      <el-badge class="mark" :value="countNum(menu.name+'_'+menu.type)" v-if="countNum(menu.name+'_'+menu.type) > 0"/>
    </template>
    <sub-menu
      v-for="item in menu.list" 
      :key="item.menuId"
      :menu="item"
      :dynamicMenuRoutes="dynamicMenuRoutes">
    </sub-menu>
  </el-submenu>
  <el-menu-item v-else :index="menu.menuId + ''" @click="gotoRouteHandle(menu)">
    <icon-svg :name="menu.icon || ''" class="site-sidebar__menu-icon"></icon-svg>
    <span>{{ menu.name }}</span>
    <el-badge class="mark" :value="countNum(menu.name+'_'+menu.type)" v-if="countNum(menu.name+'_'+menu.type) > 0"/>
  </el-menu-item>

</template>

<script>
  import SubMenu from './main-sidebar-sub-menu'
  export default {
    name: 'sub-menu',
    props: {
      menu: {
        type: Object,
        required: true
      },
      dynamicMenuRoutes: {
        type: Array,
        required: true
      }
    },
    components: {
      SubMenu
    },
    computed: {
      sidebarLayoutSkin: {
        get () { return this.$store.state.common.sidebarLayoutSkin }
      }
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
      }
    }
  }
</script>
<style scoped>
::v-deep .el-menu--popup-right-start{
  margin-left: 0px !important;
  border: none !important;
}
/* ::v-deep .el-submenu__title{
    background: rgba(44, 44, 44, 0);
    color:#BBCAFF !important;
  } */
  /* ::v-deep .el-menu-item{
    color: white;
  } */
</style>
