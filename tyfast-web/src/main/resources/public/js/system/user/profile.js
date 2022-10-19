const homeName = '默认首页';
// 初始化Vue
let app = new Vue({
  el: "#app",
  mixins,
  data: {
    menuName: "个人设置",
    formData: {
      password: null,
      newPassword: null
    },
    confirmPassword: null,

    homeName,
    homeAction: null,
    parentMenuId: 0,
    myMenuList: [],
    selectedMenuId: null,

    // 抽屉窗口
    winDrawer: false,
    winDrawerWidth: 800,

    // 树菜单配置项
    openItems: [],
    activeItems: []
  },

  computed: {
    disableClear() {
      return !('string' === typeof(this.homeAction) && this.homeAction.length > 0);
    }
  },

  watch: {
    homeAction(val) {
      this.selectedMenuId = val;
    }
  },

  mounted() {
    this.inactiveNavMenuItem();
  },

  methods: {
    /*
     * 修改密码
     */
    doSubmit() {
      this.posting = true;
      doAjax(ctx + "system/user/password/update", this.formData, data => {
        if (data.state) {
          this.toast(this.$t("密码修改成功"));
          this.resetForm();
        } else {
          this.toast(data.message, 'warning');
        }
      });
    },

    /*
     * 清除默认首页
     */
    clearHomeSettings() {
      doAjaxGet(ctx + "system/user/profile/myhome/clear", null, () => {
        this.toast(this.$t("设置清除成功"));
        this.homeName = homeName;
        this.homeAction = null;
      });
    },

    /*
     * 查询默认首页
     */
    queryHomeSettings() {
      this.loading = true;
      doAjaxGet(ctx + "system/user/profile/myhome", null, (data) => {
        let myHome = data.data;
        if (myHome) {
          this.homeName = myHome.menuAlias || myHome.menuName || homeName;
          this.homeAction = myHome.menuId;
          this.parentMenuId = myHome.parentId || 0;
        } else {
          this.homeName = homeName;
          this.homeAction = null;
          this.parentMenuId = 0;
        }
      });
    },

    /*
     * 更新默认首页
     */
    updateHomeSettings() {
      if (this.selectedMenuId) {
        doAjaxGet(ctx + "system/user/profile/myhome/update/" + this.selectedMenuId, null, () => {
          this.toast(this.$t("默认首页设置成功"));
          this.closeWinDrawer();
          this.queryHomeSettings();
        });
      }
    },

    /*
     * 打开抽屉窗口
     */
    openWinDrawer() {
      this.winDrawer = true;
      this.reloadNavMenus((navMenus) => {
        this.myMenuList = navMenus;

        // 选中当前的默认首页
        if (this.homeAction) {
          this.activeItems = [this.homeAction];
          this.openItems = [this.parentMenuId];
        } else {
          this.activeItems = [];
          this.openItems = [];
        }
      });
    },

    /*
     * 关闭抽屉窗口
     */
    closeWinDrawer() {
      this.winDrawer = false;
    },

    /*
     * 菜单树Active事件
     */
    treeActiveEvent(items) {
      this.selectedMenuId = items.length > 0? items[0] : null;
    }
  }
});